package org.springframework.faces.ui.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.ClassUtils;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Special action for resolving and rendering static resources from within a JAR file.
 * 
 * @author Jeremy Grelle
 */
public class RenderResourceAction implements Action {

	private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

	private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String HTTP_LAST_MODIFIED_HEADER = "Last-Modified";

	private static final String HTTP_EXPIRES_HEADER = "Expires";

	private static final String HTTP_CACHE_CONTROL_HEADER = "Cache-Control";

	private Map<String, String> defaultMimeTypes = new HashMap<String, String>();
	{
		defaultMimeTypes.put(".css", "text/css");
		defaultMimeTypes.put(".gif", "image/gif");
		defaultMimeTypes.put(".ico", "image/vnd.microsoft.icon");
		defaultMimeTypes.put(".jpeg", "image/jpeg");
		defaultMimeTypes.put(".jpg", "image/jpeg");
		defaultMimeTypes.put(".js", "text/javascript");
		defaultMimeTypes.put(".png", "image/png");
	}

	public Event execute(RequestContext context) throws Exception {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();

		String localResourcePath = context.getExternalContext().getRequestPath().toString();
		String jarResourcePath = "META-INF" + context.getExternalContext().getRequestPath().toString();

		URL resource;

		resource = servletContext.getResource(localResourcePath);
		if (resource == null) {
			resource = ClassUtils.getDefaultClassLoader().getResource(jarResourcePath);
			if (resource == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return new Event(this, "success");
			}
		}

		URLConnection resourceConn = resource.openConnection();
		long lastModified = resourceConn.getLastModified();

		long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE_HEADER);
		if (ifModifiedSince > 0 && lastModified / 1000 <= ifModifiedSince / 1000) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return new Event(this, "success");
		}

		String mimeType = servletContext.getMimeType(jarResourcePath);
		if (mimeType == null) {
			String extension = jarResourcePath.substring(jarResourcePath.lastIndexOf('.'));
			mimeType = defaultMimeTypes.get(extension);
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
		return new Event(this, "success");
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
