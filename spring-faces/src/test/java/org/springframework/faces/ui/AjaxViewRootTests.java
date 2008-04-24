package org.springframework.faces.ui;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIForm;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKitFactory;

import junit.framework.TestCase;

import org.apache.shale.test.mock.MockResponseWriter;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.MockViewHandler;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.View;

public class AjaxViewRootTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	UIViewRoot testTree = new UIViewRoot();

	private StringWriter output = new StringWriter();

	protected void setUp() throws Exception {
		jsf.setUp();
		jsf.facesContext().getApplication().setViewHandler(new MockViewHandler());
		jsf.facesContext().setResponseWriter(new MockResponseWriter(output, null, null));

		UIForm form = new UIForm();
		form.setId("foo");
		testTree.getChildren().add(form);
		UIPanel panel = new UIPanel();
		panel.setId("bar");
		form.getChildren().add(panel);
		ProgressiveUICommand command = new ProgressiveUICommand();
		command.setId("baz");
		panel.getChildren().add(command);

		testTree.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);

		jsf.facesContext().setViewRoot(testTree);
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public void testProcessDecodes() {
		jsf.externalContext().getRequestParameterMap().put("processIds", "foo:bar, foo:baz");

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(testTree);

		ajaxRoot.processDecodes(jsf.facesContext());

		assertEquals(1, ajaxRoot.getProcessIds().length);
	}

	public void testEncodeAll_NoRenderIds() throws IOException {
		jsf.externalContext().getRequestParameterMap().put("processIds", "foo:bar, foo:baz");

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(testTree);

		ajaxRoot.encodeAll(jsf.facesContext());

		assertEquals(1, ajaxRoot.getProcessIds().length);
		assertEquals(1, ajaxRoot.getRenderIds().length);
		assertEquals(StringUtils.arrayToCommaDelimitedString(ajaxRoot.getProcessIds()), StringUtils
				.arrayToCommaDelimitedString(ajaxRoot.getRenderIds()));
	}

	public void testEncodeAll_RenderIdsExpr() throws IOException {

		jsf.externalContext().getRequestMap().put(View.RENDER_FRAGMENTS_ATTRIBUTE,
				StringUtils.delimitedListToStringArray("foo:bar,foo:baz", ",", " "));

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(testTree);

		ajaxRoot.encodeAll(jsf.facesContext());

		assertEquals(1, ajaxRoot.getRenderIds().length);

		assertEquals("foo:bar", StringUtils.arrayToCommaDelimitedString(ajaxRoot.getRenderIds()));
	}
}
