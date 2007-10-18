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
public class ResolveAndRenderResourceAction implements Action {

	private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

	private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String HTTP_LAST_MODIFIED_HEADER = "Last-Modified";

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

		String resourcePath = "META-INF" + context.getExternalContext().getRequestPath().toString();

		URL resource = ClassUtils.getDefaultClassLoader().getResource(resourcePath);

		if (resource == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new Event(this, "success");
		}

		URLConnection resourceConn = resource.openConnection();
		long lastModified = resourceConn.getLastModified();

		long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE_HEADER);
		if (ifModifiedSince > 0 && lastModified / 1000 <= ifModifiedSince / 1000) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return new Event(this, "success");
		}

		String mimeType = servletContext.getMimeType(resourcePath);
		if (mimeType == null) {
			String extension = resourcePath.substring(resourcePath.lastIndexOf('.'));
			mimeType = defaultMimeTypes.get(extension);
		}
		response.setContentType(mimeType);

		response.setHeader(HTTP_CONTENT_LENGTH_HEADER, Long.toString(resourceConn.getContentLength()));

		response.setDateHeader(HTTP_LAST_MODIFIED_HEADER, lastModified);

		// TODO - Should probably be setting cache and expires headers as well

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
}
