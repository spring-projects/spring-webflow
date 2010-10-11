/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.js.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HttpServletBean;

/**
 * Special resource servlet for efficiently resolving and rendering static resources from within a JAR file.
 * 
 * @author Jeremy Grelle
 * @author Scott Andrews
 * 
 * @deprecated Deprecated in favor of the new resource handling mechanism available in Spring 3.0.4. See the
 * documentation on the custom mvc namespace, specifically the new resources element.
 */
public class ResourceServlet extends HttpServletBean {

	private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String HTTP_LAST_MODIFIED_HEADER = "Last-Modified";

	private static final String HTTP_EXPIRES_HEADER = "Expires";

	private static final String HTTP_CACHE_CONTROL_HEADER = "Cache-Control";

	private static final Log log = LogFactory.getLog(ResourceServlet.class);

	private final String protectedPath = "/?WEB-INF/.*";

	private String jarPathPrefix = "META-INF";

	private String springJsJarPathPrefix = "META-INF/web-resources";

	private boolean gzipEnabled = true;

	private Set allowedResourcePaths = new HashSet();
	{
		allowedResourcePaths.add("/**/*.css");
		allowedResourcePaths.add("/**/*.gif");
		allowedResourcePaths.add("/**/*.ico");
		allowedResourcePaths.add("/**/*.jpeg");
		allowedResourcePaths.add("/**/*.jpg");
		allowedResourcePaths.add("/**/*.js");
		allowedResourcePaths.add("/**/*.png");
		allowedResourcePaths.add("META-INF/**/*.css");
		allowedResourcePaths.add("META-INF/**/*.gif");
		allowedResourcePaths.add("META-INF/**/*.ico");
		allowedResourcePaths.add("META-INF/**/*.jpeg");
		allowedResourcePaths.add("META-INF/**/*.jpg");
		allowedResourcePaths.add("META-INF/**/*.js");
		allowedResourcePaths.add("META-INF/**/*.png");
	};

	private Map defaultMimeTypes = new HashMap();
	{
		defaultMimeTypes.put(".css", "text/css");
		defaultMimeTypes.put(".gif", "image/gif");
		defaultMimeTypes.put(".ico", "image/vnd.microsoft.icon");
		defaultMimeTypes.put(".jpeg", "image/jpeg");
		defaultMimeTypes.put(".jpg", "image/jpeg");
		defaultMimeTypes.put(".js", "text/javascript");
		defaultMimeTypes.put(".png", "image/png");
	}

	private Set compressedMimeTypes = new HashSet();
	{
		compressedMimeTypes.add("text/*");
	}

	private int cacheTimeout = 31556926;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String rawResourcePath = request.getPathInfo();

		if (log.isDebugEnabled()) {
			log.debug("Attempting to GET resource: " + rawResourcePath);
		}

		URL[] resources = getRequestResourceURLs(request);

		if (resources == null || resources.length == 0) {
			if (log.isDebugEnabled()) {
				log.debug("Resource not found: " + rawResourcePath);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		prepareResponse(response, resources, rawResourcePath);

		OutputStream out = selectOutputStream(request, response);

		try {
			for (int i = 0; i < resources.length; i++) {
				URLConnection resourceConn = resources[i].openConnection();
				InputStream in = resourceConn.getInputStream();
				try {
					byte[] buffer = new byte[1024];
					int bytesRead = -1;
					while ((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}
				} finally {
					in.close();
				}
			}
		} finally {
			out.close();
		}
	}

	private OutputStream selectOutputStream(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String acceptEncoding = request.getHeader("Accept-Encoding");
		String mimeType = response.getContentType();

		if (gzipEnabled && StringUtils.hasText(acceptEncoding) && acceptEncoding.indexOf("gzip") > -1
				&& matchesCompressedMimeTypes(mimeType)) {
			log.debug("Enabling GZIP compression for the current response.");
			return new GZIPResponseStream(response);
		} else {
			return response.getOutputStream();
		}
	}

	private boolean matchesCompressedMimeTypes(String mimeType) {
		PathMatcher pathMatcher = new AntPathMatcher();
		Iterator compressedMimeTypesIt = compressedMimeTypes.iterator();
		while (compressedMimeTypesIt.hasNext()) {
			String compressedMimeType = (String) compressedMimeTypesIt.next();
			if (pathMatcher.match(compressedMimeType, mimeType)) {
				return true;
			}
		}
		return false;
	}

	private void prepareResponse(HttpServletResponse response, URL[] resources, String rawResourcePath)
			throws IOException {
		long lastModified = -1;
		int contentLength = 0;
		String mimeType = null;
		for (int i = 0; i < resources.length; i++) {
			URLConnection resourceConn = resources[i].openConnection();
			if (resourceConn.getLastModified() > lastModified) {
				lastModified = resourceConn.getLastModified();
			}

			String currentMimeType = getServletContext().getMimeType(resources[i].getPath());
			if (currentMimeType == null) {
				String extension = resources[i].getPath().substring(resources[i].getPath().lastIndexOf('.'));
				currentMimeType = (String) defaultMimeTypes.get(extension);
			}
			if (mimeType == null) {
				mimeType = currentMimeType;
			} else if (!mimeType.equals(currentMimeType)) {
				throw new MalformedURLException("Combined resource path: " + rawResourcePath
						+ " is invalid. All resources in a combined resource path must be of the same mime type.");
			}
			contentLength += resourceConn.getContentLength();
		}

		response.setContentType(mimeType);
		response.setHeader(HTTP_CONTENT_LENGTH_HEADER, Long.toString(contentLength));
		response.setDateHeader(HTTP_LAST_MODIFIED_HEADER, lastModified);
		if (cacheTimeout > 0) {
			configureCaching(response, cacheTimeout);
		}
	}

	protected long getLastModified(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("Checking last modified of resource: " + request.getPathInfo());
		}
		URL[] resources;
		try {
			resources = getRequestResourceURLs(request);
		} catch (MalformedURLException e) {
			return -1;
		}

		if (resources == null || resources.length == 0) {
			return -1;
		}

		long lastModified = -1;

		for (int i = 0; i < resources.length; i++) {
			URLConnection resourceConn;
			try {
				resourceConn = resources[i].openConnection();
			} catch (IOException e) {
				return -1;
			}
			if (resourceConn.getLastModified() > lastModified) {
				lastModified = resourceConn.getLastModified();
			}
		}
		return lastModified;
	}

	private URL[] getRequestResourceURLs(HttpServletRequest request) throws MalformedURLException {

		String rawResourcePath = request.getPathInfo();
		String appendedPaths = request.getParameter("appended");
		if (StringUtils.hasText(appendedPaths)) {
			rawResourcePath = rawResourcePath + "," + appendedPaths;
		}
		String[] localResourcePaths = StringUtils.delimitedListToStringArray(rawResourcePath, ",");
		URL[] resources = new URL[localResourcePaths.length];
		for (int i = 0; i < localResourcePaths.length; i++) {
			String localResourcePath = localResourcePaths[i];
			if (!isAllowed(localResourcePath)) {
				if (log.isWarnEnabled()) {
					log.warn("An attempt to access a protected resource at " + localResourcePath + " was disallowed.");
				}
				return null;
			}
			URL resource = getServletContext().getResource(localResourcePath);
			if (resource == null) {
				resource = getJarResource(springJsJarPathPrefix, localResourcePath);
			}
			if (resource == null) {
				resource = getJarResource(jarPathPrefix, localResourcePath);
			}
			if (resource == null) {
				if (resources.length > 1) {
					log.debug("Combined resource not found: " + localResourcePath);
				}
				return null;
			} else {
				resources[i] = resource;
			}
		}
		return resources;
	}

	private URL getJarResource(String jarPrefix, String resourcePath) {
		String jarResourcePath = jarPrefix + resourcePath;
		if (!isAllowed(jarResourcePath)) {
			if (log.isWarnEnabled()) {
				log.warn("An attempt to access a protected resource at " + jarResourcePath + " was disallowed.");
			}
			return null;
		}
		if (jarResourcePath.startsWith("/")) {
			jarResourcePath = jarResourcePath.substring(1);
		}
		if (log.isDebugEnabled()) {
			log.debug("Searching classpath for resource: " + jarResourcePath);
		}
		return ClassUtils.getDefaultClassLoader().getResource(jarResourcePath);
	}

	private boolean isAllowed(String resourcePath) {
		if (resourcePath.matches(protectedPath)) {
			return false;
		}
		PathMatcher pathMatcher = new AntPathMatcher();
		Iterator allowedResourcePathsIt = allowedResourcePaths.iterator();
		while (allowedResourcePathsIt.hasNext()) {
			String pattern = (String) allowedResourcePathsIt.next();
			if (pathMatcher.match(pattern, resourcePath)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set HTTP headers to allow caching for the given number of seconds.
	 * @param seconds number of seconds into the future that the response should be cacheable for
	 */
	private void configureCaching(HttpServletResponse response, int seconds) {
		// HTTP 1.0 header
		response.setDateHeader(HTTP_EXPIRES_HEADER, System.currentTimeMillis() + seconds * 1000L);
		// HTTP 1.1 header
		response.setHeader(HTTP_CACHE_CONTROL_HEADER, "max-age=" + seconds);
	}

	private class GZIPResponseStream extends ServletOutputStream {

		private ByteArrayOutputStream byteStream = null;

		private GZIPOutputStream gzipStream = null;

		private boolean closed = false;

		private HttpServletResponse response = null;

		private ServletOutputStream servletStream = null;

		public GZIPResponseStream(HttpServletResponse response) throws IOException {
			super();
			closed = false;
			this.response = response;
			this.servletStream = response.getOutputStream();
			byteStream = new ByteArrayOutputStream();
			gzipStream = new GZIPOutputStream(byteStream);
		}

		public void close() throws IOException {
			if (closed) {
				throw new IOException("This output stream has already been closed");
			}
			gzipStream.finish();

			byte[] bytes = byteStream.toByteArray();

			response.setContentLength(bytes.length);
			response.addHeader("Content-Encoding", "gzip");
			servletStream.write(bytes);
			servletStream.flush();
			servletStream.close();
			closed = true;
		}

		public void flush() throws IOException {
			if (closed) {
				throw new IOException("Cannot flush a closed output stream");
			}
			gzipStream.flush();
		}

		public void write(int b) throws IOException {
			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}
			gzipStream.write((byte) b);
		}

		public void write(byte b[]) throws IOException {
			write(b, 0, b.length);
		}

		public void write(byte b[], int off, int len) throws IOException {
			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}
			gzipStream.write(b, off, len);
		}

		public boolean closed() {
			return (this.closed);
		}

		public void reset() {
			// noop
		}
	}

	/**
	 * Set whether to apply gzip compression to resources if the requesting client supports it.
	 */
	public void setGzipEnabled(boolean gzipEnabled) {
		this.gzipEnabled = gzipEnabled;
	}

	/**
	 * Set allowed resources as an comma separated String of URL patterns, e.g. "META-INF/** /*.js", The paths may be
	 * any Ant-style pattern parsable by AntPathMatcher.
	 * 
	 * @see AntPathMatcher
	 */
	public void setAllowedResourcePaths(String allowedResourcePaths) {
		this.allowedResourcePaths = new HashSet(Arrays.asList(StringUtils.tokenizeToStringArray(allowedResourcePaths,
				",", true, true)));
	}

	/**
	 * Set comma separated MIME types that should have gzip compression applied. Typically, gzip compression is only
	 * useful for text based content. Ant-style patterns are supported, e.g. "text/*".
	 * 
	 * @see AntPathMatcher
	 */
	public void setCompressedMimeTypes(String compressedMimeTypes) {
		this.compressedMimeTypes = new HashSet(Arrays.asList(StringUtils.tokenizeToStringArray(compressedMimeTypes,
				",", true, true)));
	}

	/**
	 * Set the default path prefix to apply to resources being served from jar files. Default is "META-INF".
	 */
	public void setJarPathPrefix(String jarPathPrefix) {
		this.jarPathPrefix = jarPathPrefix;
	}

	/**
	 * Set the number of seconds resources should be cached by the client. Zero disables caching. Default is one year.
	 */
	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

}
