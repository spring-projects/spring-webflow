package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;

public class FlowExecutorBeanDefinitionParserTests extends TestCase {
	private ClassPathXmlApplicationContext context;

	public void setUp() {
		context = new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-executor.xml");
	}

	public void testConfigOk() {
		context.getBean("flowExecutor", FlowExecutor.class);
	}

	public static class ConfigurationListener extends FlowExecutionListenerAdapter {
		public void sessionCreating(RequestContext context, FlowDefinition definition) {
			if (!context.getFlowExecutionContext().isActive()) {
				assertEquals(3, context.getFlowExecutionContext().getAttributes().size());
				assertEquals(Boolean.FALSE, context.getFlowExecutionContext().getAttributes().getBoolean(
						"alwaysRedirectOnPause"));
				assertEquals("bar", context.getFlowExecutionContext().getAttributes().get("foo"));
				assertEquals(new Integer(2), context.getFlowExecutionContext().getAttributes().get("bar"));
			}
		}
	}

}
