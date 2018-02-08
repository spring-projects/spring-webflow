package org.springframework.faces.webflow;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.event.ActionEvent;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

public class FlowActionListenerTests extends TestCase {

	FlowActionListener listener;

	JSFMockHelper jsfMock = new JSFMockHelper();

	RequestContext context = EasyMock.createMock(RequestContext.class);

	protected void setUp() throws Exception {
		this.jsfMock.setUp();

		this.listener = new FlowActionListener(this.jsfMock.application().getActionListener());
		RequestContextHolder.setRequestContext(this.context);
		LocalAttributeMap<Object> flash = new LocalAttributeMap<>();
		EasyMock.expect(this.context.getFlashScope()).andStubReturn(flash);
		EasyMock.expect(this.context.getCurrentState()).andStubReturn(new MockViewState());
		EasyMock.replay(new Object[] { this.context });
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public final void testProcessAction() {

		String outcome = "foo";
		MethodBinding binding = new MethodBindingStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setAction(binding);
		ActionEvent event = new ActionEvent(commandButton);

		this.listener.processAction(event);

		assertTrue("The event was not signaled",
				this.jsfMock.externalContext().getRequestMap().containsKey(JsfView.EVENT_KEY));
		assertEquals("The event should be " + outcome, outcome,
				this.jsfMock.externalContext().getRequestMap().get(JsfView.EVENT_KEY));
	}

	public final void testProcessAction_NullOutcome() {

		String outcome = null;
		MethodBinding binding = new MethodBindingStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setAction(binding);
		ActionEvent event = new ActionEvent(commandButton);

		this.listener.processAction(event);

		assertFalse("An unexpected event was signaled",
				this.jsfMock.externalContext().getRequestMap().containsKey(JsfView.EVENT_KEY));
	}

	private class MethodBindingStub extends MethodBinding {

		String result;

		public MethodBindingStub(String result) {
			this.result = result;
		}

		public Class<?> getType(FacesContext context) throws MethodNotFoundException {
			return String.class;
		}

		public Object invoke(FacesContext context, Object... args) throws EvaluationException, MethodNotFoundException {
			return this.result;
		}

	}

	private class MockViewState extends ViewState {

		public MockViewState() {
			super(new Flow("mockFlow"), "mockView", new ViewFactory() {

				public View getView(RequestContext context) {
					throw new UnsupportedOperationException();
				}
			});
		}
	}
}
