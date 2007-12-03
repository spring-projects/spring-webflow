package org.springframework.faces.ui;

import javax.faces.component.UICommand;
import javax.faces.component.UIForm;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKitFactory;

import org.springframework.faces.webflow.JSFMockHelper;

import junit.framework.TestCase;

public class AjaxViewRootTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	UIViewRoot testTree = new UIViewRoot();

	protected void setUp() throws Exception {
		jsf.setUp();

		UIForm form = new UIForm();
		form.setId("foo");
		testTree.getChildren().add(form);
		UIPanel panel = new UIPanel();
		panel.setId("bar");
		form.getChildren().add(panel);
		UICommand command = new UICommand();
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
		jsf.externalContext().getRequestParameterMap().put("renderIds", "foo:bar, foo:baz");

		AjaxViewRoot ajaxRoot = new AjaxViewRoot(testTree);

		ajaxRoot.processDecodes(jsf.facesContext());

		assertEquals(1, ajaxRoot.getProcessIds().length);
		assertEquals(1, ajaxRoot.getRenderIds().length);
	}

}
