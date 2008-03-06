package org.springframework.faces.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

/**
 * Special action for resolving and rendering static resources from within a JAR file.
 * 
 * @author Jeremy Grelle
 */
public class ResourceServlet extends HttpServlet {

	private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String HTTP_LAST_MODIFIED_HEADER = "Last-Modified";

	private static final String HTTP_EXPIRES_HEADER = "Expires";

	private static final String HTTP_CACHE_CONTROL_HEADER = "Cache-Control";

	private static final Log log = LogFactory.getLog(ResourceServlet.class);

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String localResourcePath = request.getPathInfo();

		if (log.isDebugEnabled()) {
			log.debug("Attempting to GET resource: " + localResourcePath);
		}

		URL resource = getRequestResourceURL(localResourcePath);

		if (resource == null) {
			if (log.isDebugEnabled()) {
				log.debug("Resource not found: " + localResourcePath);
			}
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		URLConnection resourceConn = resource.openConnection();
		long lastModified = resourceConn.getLastModified();

		String mimeType = getServletContext().getMimeType(resource.getPath());
		if (mimeType == null) {
			String extension = resource.getPath().substring(resource.getPath().lastIndexOf('.'));
			mimeType = (String) defaultMimeTypes.get(extension);
		}
		response.setContentType(mimeType);

		response.setHeader(HTTP_CONTENT_LENGTH_HEADER, Long.toString(resourceConn.getContentLength()));
		response.setDateHeader(HTTP_LAST_MODIFIED_HEADER, lastModified);
		configureCaching(response, 31556926);

		InputStream in = resourceConn.getInputStream();
		OutputStream out = response.getOutputStream();
		try {
			byte[] buffer = new byte[1024];
			while (in.available() > 0) {
				int len = in.read(buffer);
				out.write(buffer, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	protected long getLastModified(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("Checking last modified of resource: " + request.getPathInfo());
		}
		URL resource;
		try {
			resource = getRequestResourceURL(request.getPathInfo());
		} catch (MalformedURLException e) {
			return -1;
		}

		if (resource == null) {
			return -1;
		}

		URLConnection resourceConn;
		try {
			resourceConn = resource.openConnection();
		} catch (IOException e) {
			return -1;
		}
		return resourceConn.getLastModified();
	}

	private URL getRequestResourceURL(String localResourcePath) throws MalformedURLException {

		String jarResourcePath = "META-INF" + localResourcePath;

		URL resource;

		resource = getServletContext().getResource(localResourcePath);
		if (resource == null) {
			if (log.isDebugEnabled()) {
				log.debug("Searching classpath for resource: " + jarResourcePath);
			}
			resource = ClassUtils.getDefaultClassLoader().getResource(jarResourcePath);
		}

		return resource;
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
}
