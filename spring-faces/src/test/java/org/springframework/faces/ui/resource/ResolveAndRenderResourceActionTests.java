package org.springframework.faces.ui.resource;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

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

public class ResolveAndRenderResourceActionTests extends TestCase {

	ExternalContext externalContext = createMock(ExternalContext.class);
	RequestContext requestContext = createMock(RequestContext.class);
	ServletContext servletContext = new MimeAwareMockServletContext();
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockHttpServletRequest request = new MockHttpServletRequest();

	RequestPath requestPath;
	String[] requestElements;

	ResolveAndRenderResourceAction action;

	protected void setUp() throws Exception {
		action = new ResolveAndRenderResourceAction();

		expect(requestContext.getExternalContext()).andStubReturn(externalContext);
		expect(externalContext.getContext()).andStubReturn(servletContext);
		expect(externalContext.getResponse()).andStubReturn(response);
		expect(externalContext.getRequest()).andStubReturn(request);
		expect(externalContext.getResponseWriter()).andStubReturn(new PrintWriter(new StringWriter()));
	}

	public final void testExecute() throws Exception {

		requestPath = new RequestPath("/ext/ext.js");

		expect(externalContext.getRequestPath()).andStubReturn(requestPath);

		replay(new Object[] { requestContext, externalContext });

		action.execute(requestContext);
	}

	private class MimeAwareMockServletContext extends MockServletContext {

		public String getMimeType(String filePath) {
			return null;
		}
	}
}
