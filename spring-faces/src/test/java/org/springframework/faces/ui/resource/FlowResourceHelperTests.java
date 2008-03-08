package org.springframework.faces.ui.resource;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.shale.test.mock.MockResponseWriter;
import org.springframework.faces.webflow.JSFMockHelper;

public class FlowResourceHelperTests extends TestCase {

	ResourceHelper resourceHelper = new ResourceHelper();

	StringWriter writer = new StringWriter();

	JSFMockHelper jsf = new JSFMockHelper();

	protected void setUp() throws Exception {
		jsf.setUp();
		// TODO figure out how to set the context path
		jsf.facesContext().setResponseWriter(new MockResponseWriter(writer, "text/html", "UTF-8"));
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public final void testRenderScriptLink() throws IOException {

		String scriptPath = "/dojo/dojo.js";
		String expectedUrl = "null/resources/dojo/dojo.js";

		resourceHelper.renderScriptLink(jsf.facesContext(), scriptPath);
		resourceHelper.renderScriptLink(jsf.facesContext(), scriptPath);

		String expectedOutput = "<script type=\"text/javascript\" src=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, writer.toString());

	}

	public final void testRenderStyleLink() throws IOException {

		String scriptPath = "/dijit/themes/dijit.css";
		String expectedUrl = "null/resources/dijit/themes/dijit.css";

		resourceHelper.renderStyleLink(jsf.facesContext(), scriptPath);
		resourceHelper.renderStyleLink(jsf.facesContext(), scriptPath);

		String expectedOutput = "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, writer.toString());
	}
}
