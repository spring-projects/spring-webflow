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

		String requestPath = "/dojo/dojo.js";
		request.setPathInfo(requestPath);
		servlet.doGet(request, response);

		assertEquals(200, response.getStatus());
	}

	public final void testExecute_CombinedResources() throws Exception {

		String requestPath = "/dojo/dojo.js";
		request.setPathInfo(requestPath);
		Map params = new HashMap();
		params.put("appended", "/dijit/dijit.js,/dijit/Dialog.js");
		request.setParameters(params);
		servlet.doGet(request, response);

		assertEquals(200, response.getStatus());
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
