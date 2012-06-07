package org.springframework.faces.ui;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIForm;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKitFactory;

import junit.framework.TestCase;

import org.apache.myfaces.test.mock.MockResponseWriter;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.MockViewHandler;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.View;

public class AjaxViewRootTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	UIViewRoot testTree = new UIViewRoot();

	private final StringWriter output = new StringWriter();

	protected void setUp() throws Exception {
		this.jsf.setUp();
		this.jsf.facesContext().getApplication().setViewHandler(new MockViewHandler());
		this.jsf.facesContext().setResponseWriter(new MockResponseWriter(this.output, null, null));

		UIForm form = new UIForm();
		form.setId("foo");
		this.testTree.getChildren().add(form);
		UIPanel panel = new UIPanel();
		panel.setId("bar");
		form.getChildren().add(panel);
		ProgressiveUICommand command = new ProgressiveUICommand();
		command.setId("baz");
		panel.getChildren().add(command);

		this.testTree.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);

		this.jsf.facesContext().setViewRoot(this.testTree);
	}

	protected void tearDown() throws Exception {
		this.jsf.tearDown();
	}

	public void testProcessDecodes() {
		this.jsf.externalContext().getRequestParameterMap().put("processIds", "foo:bar, foo:baz");

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(this.testTree);

		ajaxRoot.processDecodes(this.jsf.facesContext());

		assertEquals(1, ajaxRoot.getProcessIds().length);
	}

	public void testEncodeAll_NoRenderIds() throws IOException {
		this.jsf.externalContext().getRequestParameterMap().put("processIds", "foo:bar, foo:baz");

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(this.testTree);

		ajaxRoot.encodeAll(this.jsf.facesContext());

		assertEquals(1, ajaxRoot.getProcessIds().length);
		assertEquals(1, ajaxRoot.getRenderIds().length);
		assertEquals(StringUtils.arrayToCommaDelimitedString(ajaxRoot.getProcessIds()),
				StringUtils.arrayToCommaDelimitedString(ajaxRoot.getRenderIds()));
	}

	public void testEncodeAll_RenderIdsExpr() throws IOException {

		this.jsf.externalContext()
				.getRequestMap()
				.put(View.RENDER_FRAGMENTS_ATTRIBUTE,
						StringUtils.delimitedListToStringArray("foo:bar,foo:baz", ",", " "));

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(this.testTree);

		ajaxRoot.encodeAll(this.jsf.facesContext());

		assertEquals(1, ajaxRoot.getRenderIds().length);

		assertEquals("foo:bar", StringUtils.arrayToCommaDelimitedString(ajaxRoot.getRenderIds()));
	}
}
