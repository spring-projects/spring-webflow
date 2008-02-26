package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockExternalContext;

public class EnableScopesBeanDefinitionParserTests extends TestCase {

	private ClassPathXmlApplicationContext context;

	private FlowExecutor executor;

	public void setUp() {
		context = new ClassPathXmlApplicationContext("org/springframework/webflow/config/enable-flow-scopes.xml");
		executor = (FlowExecutor) context.getBean("flowExecutor");
	}

	public void testExecute() {
		MockExternalContext context = new MockExternalContext();
	}

	public static class ConfigurationListener extends FlowExecutionListenerAdapter {
		public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
			assertNotNull(session.getScope().get("user"));
		}
	}

}
