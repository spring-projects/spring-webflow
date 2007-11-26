package org.springframework.faces.ui.resource;

import org.easymock.EasyMock;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.RequestPath;
import org.springframework.webflow.execution.RequestContext;

public class RenderResourceActionTests extends TestCase {

	ExternalContext externalContext = (ExternalContext) EasyMock.createMock(ExternalContext.class);
	RequestContext requestContext = (RequestContext) EasyMock.createMock(RequestContext.class);
	ServletContext servletContext = new ResourceTestMockServletContext();
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockHttpServletRequest request = new MockHttpServletRequest();

	RequestPath requestPath;
	String[] requestElements;

	RenderResourceAction action;

	protected void setUp() throws Exception {
		action = new RenderResourceAction();

		EasyMock.expect(requestContext.getExternalContext()).andStubReturn(externalContext);
		EasyMock.expect(externalContext.getContext()).andStubReturn(servletContext);
		EasyMock.expect(externalContext.getResponse()).andStubReturn(response);
		EasyMock.expect(externalContext.getRequest()).andStubReturn(request);
		EasyMock.expect(externalContext.getResponseWriter()).andStubReturn(new PrintWriter(new StringWriter()));
	}

	public final void testExecute() throws Exception {

		requestPath = new RequestPath("/ext/ext.js");

		EasyMock.expect(externalContext.getRequestPath()).andStubReturn(requestPath);

		EasyMock.replay(new Object[] { requestContext, externalContext });

		action.execute(requestContext);
	}

	public final void testExecute_ResourceNotFound() throws Exception {

		requestPath = new RequestPath("/xxx/xxx.js");

		EasyMock.expect(externalContext.getRequestPath()).andStubReturn(requestPath);

		EasyMock.replay(new Object[] { requestContext, externalContext });

		action.execute(requestContext);

		assertEquals(404, response.getStatus());
	}

	private class ResourceTestMockServletContext extends MockServletContext {

		public String getMimeType(String filePath) {
			return null;
		}

	}
}
