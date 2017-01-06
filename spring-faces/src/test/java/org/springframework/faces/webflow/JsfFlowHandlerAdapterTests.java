package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.springframework.webflow.context.servlet.AjaxHandler;
import org.springframework.webflow.context.servlet.DefaultAjaxHandler;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

public class JsfFlowHandlerAdapterTests extends TestCase {

	private JsfFlowHandlerAdapter handlerAdapter;

	protected void setUp() throws Exception {
		StaticWebApplicationContext context = new StaticWebApplicationContext();
		context.setServletContext(new MockServletContext());
		this.handlerAdapter = new JsfFlowHandlerAdapter();
		this.handlerAdapter.setApplicationContext(context);
		this.handlerAdapter.setFlowExecutor(new StubFlowExecutor());
	}

	public void testAjaxHandlerNotProvided() throws Exception {
		this.handlerAdapter.afterPropertiesSet();
		assertNotNull(this.handlerAdapter.getAjaxHandler());
		assertTrue(this.handlerAdapter.getAjaxHandler() instanceof JsfAjaxHandler);
	}

	public void testAjaxHandlerProvided() throws Exception {
		AjaxHandler myAjaxHandler = new DefaultAjaxHandler();
		this.handlerAdapter.setAjaxHandler(myAjaxHandler);
		this.handlerAdapter.afterPropertiesSet();
		assertTrue(myAjaxHandler == this.handlerAdapter.getAjaxHandler());
	}

	private final class StubFlowExecutor implements FlowExecutor {
		public FlowExecutionResult resumeExecution(String flowExecutionKey, ExternalContext context)
				throws FlowException {
			throw new UnsupportedOperationException("Not expected");
		}

		public FlowExecutionResult launchExecution(String flowId, MutableAttributeMap<?> input, ExternalContext context)
				throws FlowException {
			throw new UnsupportedOperationException("Not expected");
		}
	}

}
