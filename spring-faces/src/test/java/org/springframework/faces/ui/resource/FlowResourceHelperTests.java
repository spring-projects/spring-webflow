package org.springframework.faces.ui.resource;

import org.easymock.EasyMock;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.shale.test.mock.MockResponseWriter;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.FlowDefinitionRequestInfo;
import org.springframework.webflow.context.RequestPath;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowResourceHelperTests extends TestCase {

	RequestContext requestContext = (RequestContext) EasyMock.createMock(RequestContext.class);
	ExternalContext externalContext = (ExternalContext) EasyMock.createMock(ExternalContext.class);
	FlowResourceHelper resourceHelper = new FlowResourceHelper();

	StringWriter writer = new StringWriter();

	JSFMockHelper jsf = new JSFMockHelper();

	protected void setUp() throws Exception {
		jsf.setUp();
		jsf.facesContext().setResponseWriter(new MockResponseWriter(writer, "text/html", "UTF-8"));
		EasyMock.expect(requestContext.getExternalContext()).andStubReturn(externalContext);
		RequestContextHolder.setRequestContext(requestContext);
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public final void testRenderScriptLink() throws IOException {

		String scriptPath = "/dojo/dojo.js";
		String expectedUrl = "/context/spring/resources/dojo/dojo.js";

		FlowDefinitionRequestInfo expectedRequest = new FlowDefinitionRequestInfo("resources", new RequestPath(
				scriptPath), null, null);

		EasyMock.expect(externalContext.buildFlowDefinitionUrl(requestInfoMatches(expectedRequest))).andReturn(
				expectedUrl);

		EasyMock.replay(new Object[] { requestContext, externalContext });

		resourceHelper.renderScriptLink(jsf.facesContext(), scriptPath);
		resourceHelper.renderScriptLink(jsf.facesContext(), scriptPath);

		EasyMock.verify(new Object[] { externalContext });

		String expectedOutput = "<script type=\"text/javascript\" src=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, writer.toString());

	}

	public final void testRenderStyleLink() throws IOException {

		String scriptPath = "/dijit/themes/dijit.css";
		String expectedUrl = "/context/spring/resources/dijit/themes/dijit.css";

		FlowDefinitionRequestInfo expectedRequest = new FlowDefinitionRequestInfo("resources", new RequestPath(
				scriptPath), null, null);

		EasyMock.expect(externalContext.buildFlowDefinitionUrl(requestInfoMatches(expectedRequest))).andReturn(
				expectedUrl);

		EasyMock.replay(new Object[] { requestContext, externalContext });

		resourceHelper.renderStyleLink(jsf.facesContext(), scriptPath);
		resourceHelper.renderStyleLink(jsf.facesContext(), scriptPath);

		EasyMock.verify(new Object[] { externalContext });

		String expectedOutput = "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, writer.toString());
	}

	static FlowDefinitionRequestInfo requestInfoMatches(FlowDefinitionRequestInfo info) {
		EasyMock.reportMatcher(new RequestInfoMatcher(info));
		return null;
	}
}
