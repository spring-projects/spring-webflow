package org.springframework.js.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

@SuppressWarnings("deprecation")
public class ResourceServletTests extends TestCase {

	ServletContext servletContext = new ResourceTestMockServletContext();
	ServletConfig config = new MockServletConfig(servletContext);
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockHttpServletRequest request = new MockHttpServletRequest();

	String[] requestElements;

	ResourceServlet servlet;

	protected void setUp() throws Exception {
		servlet = new ResourceServlet();
		servlet.init(config);
	}

	public final void testExecute() throws Exception {

		String requestPath = "/org/springframework/js/resource/TestResource1.js";
		request.setPathInfo(requestPath);
		servlet.doGet(request, response);

		assertEquals(200, response.getStatus());
		assertNull(response.getHeader("Content-Encoding"));
	}

	public final void testExecute_CombinedResources() throws Exception {

		String requestPath = "/org/springframework/js/resource/TestResource1.js";
		request.setPathInfo(requestPath);
		Map<String, String> params = new HashMap<String, String>();
		params.put("appended", "/org/springframework/js/resource/TestResource2.js");
		request.setParameters(params);
		servlet.doGet(request, response);

		assertEquals(200, response.getStatus());
	}

	public final void testExecute_CompressedResponse() throws Exception {

		String requestPath = "/org/springframework/js/resource/TestResource1.js";
		request.setPathInfo(requestPath);
		request.addHeader("Accept-Encoding", "gzip");
		servlet.doGet(request, response);

		assertEquals(200, response.getStatus());
		assertEquals("gzip", response.getHeader("Content-Encoding"));
	}

	public final void testExecute_ResourceNotFound() throws Exception {

		String requestPath = "/xxx/xxx.js";
		request.setPathInfo(requestPath);
		servlet.doGet(request, response);

		assertEquals(404, response.getStatus());
	}

	public final void testExecute_ProtectedPath() throws Exception {
		String requestPath = "/WEB-INF/web.xml";
		request.setPathInfo(requestPath);
		servlet.doGet(request, response);

		assertEquals(404, response.getStatus());
	}

	public final void testExecute_DisallowedPath() throws Exception {
		String requestPath = "/persistence.xml";
		request.setPathInfo(requestPath);
		servlet.doGet(request, response);

		assertEquals(404, response.getStatus());
	}

	private class ResourceTestMockServletContext extends MockServletContext {

		public String getMimeType(String filePath) {
			return null;
		}

	}
}
