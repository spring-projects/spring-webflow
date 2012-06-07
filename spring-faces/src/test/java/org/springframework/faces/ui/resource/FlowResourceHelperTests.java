package org.springframework.faces.ui.resource;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.myfaces.test.mock.MockResponseWriter;
import org.springframework.faces.webflow.JSFMockHelper;

public class FlowResourceHelperTests extends TestCase {

	StringWriter writer = new StringWriter();

	JSFMockHelper jsf = new JSFMockHelper();

	protected void setUp() throws Exception {
		this.jsf.setUp();
		// TODO figure out how to set the context path
		this.jsf.facesContext().setResponseWriter(new MockResponseWriter(this.writer, "text/html", "UTF-8"));
	}

	protected void tearDown() throws Exception {
		this.jsf.tearDown();
	}

	public final void testRenderScriptLink() throws IOException {

		String scriptPath = "/dojo/dojo.js";
		String expectedUrl = "null/resources/dojo/dojo.js";

		ResourceHelper.renderScriptLink(this.jsf.facesContext(), scriptPath);
		ResourceHelper.renderScriptLink(this.jsf.facesContext(), scriptPath);

		String expectedOutput = "<script type=\"text/javascript\" src=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, this.writer.toString());

	}

	public final void testRenderStyleLink() throws IOException {

		String scriptPath = "/dijit/themes/dijit.css";
		String expectedUrl = "null/resources/dijit/themes/dijit.css";

		ResourceHelper.renderStyleLink(this.jsf.facesContext(), scriptPath);
		ResourceHelper.renderStyleLink(this.jsf.facesContext(), scriptPath);

		String expectedOutput = "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + expectedUrl + "\"/>";

		assertEquals(expectedOutput, this.writer.toString());
	}
}
