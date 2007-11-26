package org.springframework.faces.webflow;

import org.easymock.EasyMock;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.event.ActionEvent;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowActionListenerTests extends TestCase {

	FlowActionListener listener;

	JSFMockHelper jsfMock = new JSFMockHelper();

	RequestContext context = (RequestContext) EasyMock.createMock(RequestContext.class);

	protected void setUp() throws Exception {
		jsfMock.setUp();

		listener = new FlowActionListener(jsfMock.application().getActionListener());
		RequestContextHolder.setRequestContext(context);
		AttributeMap flash = new LocalAttributeMap();
		EasyMock.expect(context.getFlashScope()).andStubReturn(flash);
		EasyMock.replay(new Object[] { context });
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public final void testProcessAction() {

		String outcome = "foo";
		MethodBinding binding = new MethodBindingStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setAction(binding);
		ActionEvent event = new ActionEvent(commandButton);

		listener.processAction(event);

		assertTrue("The event was not signaled", jsfMock.externalContext().getRequestMap().containsKey(
				JsfView.EVENT_KEY));
		assertEquals("The event should be " + outcome, outcome, jsfMock.externalContext().getRequestMap().get(
				JsfView.EVENT_KEY));
	}

	public final void testProcessAction_NullOutcome() {

		String outcome = null;
		MethodBinding binding = new MethodBindingStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setAction(binding);
		ActionEvent event = new ActionEvent(commandButton);

		listener.processAction(event);

		assertFalse("An unexpected event was signaled", jsfMock.externalContext().getRequestMap().containsKey(
				JsfView.EVENT_KEY));
	}

	private class MethodBindingStub extends MethodBinding {

		String result;

		public MethodBindingStub(String result) {
			this.result = result;
		}

		public Class getType(FacesContext context) throws MethodNotFoundException {
			return String.class;
		}

		public Object invoke(FacesContext context, Object[] args) throws EvaluationException, MethodNotFoundException {
			return this.result;
		}

	}
}
