package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

public class JsfFlowHandlerAdapterTests extends TestCase {

	private JsfFlowHandlerAdapter handlerAdapter;

	protected void setUp() throws Exception {
		handlerAdapter = new JsfFlowHandlerAdapter();
		handlerAdapter.setFlowExecutor(new StubFlowExecutor());
	}

	public void testAjaxHandlerNotProvided() throws Exception {
		handlerAdapter.afterPropertiesSet();
		assertNotNull(handlerAdapter.getAjaxHandler());
		assertTrue(handlerAdapter.getAjaxHandler() instanceof JsfAjaxHandler);
	}

	public void testAjaxHandlerProvided() throws Exception {
		AjaxHandler myAjaxHandler = new SpringJavascriptAjaxHandler();
		handlerAdapter.setAjaxHandler(myAjaxHandler);
		handlerAdapter.afterPropertiesSet();
		assertTrue(myAjaxHandler == handlerAdapter.getAjaxHandler());
	}

	private final class StubFlowExecutor implements FlowExecutor {
		public FlowExecutionResult resumeExecution(String flowExecutionKey, ExternalContext context)
				throws FlowException {
			throw new UnsupportedOperationException("Not expected");
		}

		public FlowExecutionResult launchExecution(String flowId, MutableAttributeMap input, ExternalContext context)
				throws FlowException {
			throw new UnsupportedOperationException("Not expected");
		}
	}

}
