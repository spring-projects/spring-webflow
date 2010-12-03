package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;
import org.springframework.webflow.test.MockExternalContext;

public class FlowExecutorBeanDefinitionParserTests extends TestCase {
	private ClassPathXmlApplicationContext context;

	public void setUp() {
		context = new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-executor.xml");
	}

	public void testConfigOk() {
		FlowExecutor executor = context.getBean("flowExecutor", FlowExecutor.class);
		executor.launchExecution("flow", null, new MockExternalContext());
		FlowExecutor executor2 = context.getBean("flowExecutorSimpleRepo", FlowExecutor.class);
		executor2.launchExecution("flow", null, new MockExternalContext());
	}

	public void testCustomConversationManager() {
		FlowExecutorImpl executor = context.getBean("flowExecutor", FlowExecutorImpl.class);
		try {
			executor.getExecutionRepository().parseFlowExecutionKey("e1s1");
			fail("ExceptionThrowingConversationManager would have raised an exception");
		} catch (UnsupportedOperationException e) {
		}
	}

	public static class ConfigurationListener extends FlowExecutionListenerAdapter {
		public void sessionCreating(RequestContext context, FlowDefinition definition) {
			assertEquals(3, context.getFlowExecutionContext().getAttributes().size());
			assertEquals(Boolean.FALSE,
					context.getFlowExecutionContext().getAttributes().getBoolean("alwaysRedirectOnPause"));
			assertEquals("bar", context.getFlowExecutionContext().getAttributes().get("foo"));
			assertEquals(new Integer(2), context.getFlowExecutionContext().getAttributes().get("bar"));
		}
	}

	public static class ExceptionThrowingConversationManager implements ConversationManager {

		public Conversation beginConversation(ConversationParameters conversationParameters)
				throws ConversationException {
			throw new UnsupportedOperationException();
		}

		public Conversation getConversation(ConversationId id) throws ConversationException {
			throw new UnsupportedOperationException();
		}

		public ConversationId parseConversationId(String encodedId) throws ConversationException {
			throw new UnsupportedOperationException();
		}

	}

}
