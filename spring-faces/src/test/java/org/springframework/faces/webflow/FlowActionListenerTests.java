package org.springframework.faces.webflow;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

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
		EasyMock.replay(this.context);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public final void testProcessAction() {

		String outcome = "foo";
		MethodExpression expression = new MethodExpressionStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setActionExpression(expression);
		ActionEvent event = new ActionEvent(commandButton);

		this.listener.processAction(event);

		assertTrue("The event was not signaled",
				this.jsfMock.externalContext().getRequestMap().containsKey(JsfView.EVENT_KEY));
		assertEquals("The event should be " + outcome, outcome,
				this.jsfMock.externalContext().getRequestMap().get(JsfView.EVENT_KEY));
	}

	public final void testProcessAction_NullOutcome() {

		String outcome = null;
		MethodExpression expression = new MethodExpressionStub(outcome);
		UICommand commandButton = new UICommand();
		commandButton.setActionExpression(expression);
		ActionEvent event = new ActionEvent(commandButton);

		this.listener.processAction(event);

		assertFalse("An unexpected event was signaled",
				this.jsfMock.externalContext().getRequestMap().containsKey(JsfView.EVENT_KEY));
	}

	private class MethodExpressionStub extends MethodExpression {

		String result;

		public MethodExpressionStub(String result) {
			this.result = result;
		}

		@Override
		public MethodInfo getMethodInfo(ELContext context) {
			return null;
		}

		@Override
		public Object invoke(ELContext context, Object[] params) {
			return this.result;
		}

		@Override
		public String getExpressionString() {
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			return false;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean isLiteralText() {
			return false;
		}
	}

	private class MockViewState extends ViewState {

		public MockViewState() {
			super(new Flow("mockFlow"), "mockView", context -> {
				throw new UnsupportedOperationException();
			});
		}
	}
}
