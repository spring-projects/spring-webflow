package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.FacesException;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.apache.shale.test.mock.MockResponseWriter;
import org.apache.shale.test.mock.MockStateManager;
import org.easymock.EasyMock;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;

public class JsfViewTests extends TestCase {

	private static final String VIEW_ID = "testView.xhtml";

	private JsfView view;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private StringWriter output = new StringWriter();

	private RequestContext requestContext = (RequestContext) EasyMock.createMock(RequestContext.class);
	private FlowExecutionContext flowExecutionContext = (FlowExecutionContext) EasyMock
			.createMock(FlowExecutionContext.class);
	private MutableAttributeMap flashMap = (MutableAttributeMap) EasyMock.createMock(MutableAttributeMap.class);
	private MutableAttributeMap flowMap = (MutableAttributeMap) EasyMock.createMock(MutableAttributeMap.class);

	private FlowExecutionKey key = new FlowExecutionKey() {

		public String toString() {
			return "MOCK_KEY";
		}

		public boolean equals(Object o) {
			return true;
		}

		public int hashCode() {
			return 0;
		}
	};

	protected void setUp() throws Exception {

		jsfMock.setUp();
		jsfMock.facesContext().getApplication().setViewHandler(new MockViewHandler());
		jsfMock.application().setStateManager(new TestStateManager());
		jsfMock.facesContext().setResponseWriter(new MockResponseWriter(output, null, null));

		UIViewRoot viewToRender = new UIViewRoot();
		viewToRender.setRenderKitId("HTML_BASIC");
		viewToRender.setViewId(VIEW_ID);
		jsfMock.facesContext().setViewRoot(viewToRender);

		UIForm form = new HtmlForm();
		form.setId("myForm");

		UIInput input = new HtmlInputText();
		input.setId("foo");

		form.getChildren().add(input);
		viewToRender.getChildren().add(form);

		view = new JsfView(viewToRender, jsfMock.lifecycle(), requestContext);
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public final void testRender() throws IOException {

		EasyMock.expect(requestContext.getExternalContext()).andStubReturn(new MockExternalContext());
		EasyMock.expect(requestContext.getFlashScope()).andStubReturn(flashMap);
		EasyMock.expect(requestContext.getFlowScope()).andStubReturn(flowMap);
		EasyMock.expect(requestContext.getFlowExecutionContext()).andStubReturn(flowExecutionContext);
		EasyMock.expect(flowExecutionContext.getKey()).andStubReturn(key);
		EasyMock.expect(flashMap.put(EasyMock.matches("renderResponse"), EasyMock.anyObject())).andStubReturn(null);
		EasyMock.expect(flashMap.put(EasyMock.matches("responseComplete"), EasyMock.anyObject())).andStubReturn(null);

		EasyMock.replay(new Object[] { requestContext, flowExecutionContext, flowMap, flashMap });

		view.render();

		EasyMock.verify(new Object[] { requestContext, flowExecutionContext, flowMap, flashMap });
		assertNull("The FacesContext was not released", FacesContext.getCurrentInstance());
	}

	public final void testRenderException() throws IOException {

		EasyMock.expect(requestContext.getExternalContext()).andStubReturn(new MockExternalContext());
		EasyMock.expect(requestContext.getFlashScope()).andStubReturn(flashMap);
		EasyMock.expect(flashMap.put(EasyMock.matches("renderResponse"), EasyMock.anyObject())).andStubReturn(null);
		EasyMock.expect(flashMap.put(EasyMock.matches("responseComplete"), EasyMock.anyObject())).andStubReturn(null);

		EasyMock.replay(new Object[] { requestContext, flowExecutionContext, flashMap });

		jsfMock.application().setViewHandler(new ExceptionalViewHandler());

		try {
			view.render();
		} catch (FacesException ex) {
			assertNull("The FacesContext was not released", FacesContext.getCurrentInstance());
		}

	}

	private class ExceptionalViewHandler extends MockViewHandler {
		public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
			throw new IOException("Rendering blew up");
		}
	}

	private class TestStateManager extends MockStateManager {
		public SerializedView saveSerializedView(FacesContext context) {
			SerializedView state = new SerializedView(new Object[] { "tree_state" }, new Object[] { "component_state" });
			return state;
		}
	}
}
