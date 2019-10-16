package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;
import org.springframework.webflow.test.MockExternalContext;

public abstract class AbstractFlowExecutorConfigurationTests {

	private ApplicationContext context;

	@BeforeEach
	public void setUp() {
		context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();


	@Test
	public void testConfigOk() {
		FlowExecutor executor = context.getBean("flowExecutor", FlowExecutor.class);
		executor.launchExecution("flow", null, new MockExternalContext());
		FlowExecutor executor2 = context.getBean("flowExecutorSimpleRepo", FlowExecutor.class);
		executor2.launchExecution("flow", null, new MockExternalContext());
	}

	@Test
	public void testCustomConversationManager() {
		FlowExecutorImpl executor = context.getBean("flowExecutor", FlowExecutorImpl.class);
		try {
			executor.getExecutionRepository().parseFlowExecutionKey("e1s1");
			fail("ExceptionThrowingConversationManager would have raised an exception");
		} catch (UnsupportedOperationException e) {
		}
	}

	public static class ConfigurationListener implements FlowExecutionListener {

		public void sessionCreating(RequestContext context, FlowDefinition definition) {
			AttributeMap<Object> attributes = context.getFlowExecutionContext().getAttributes();
			assertEquals(4, attributes.size());
			assertEquals(Boolean.FALSE, attributes.getBoolean("alwaysRedirectOnPause"));
			assertEquals(Boolean.TRUE, attributes.getBoolean("redirectInSameState"));
			assertEquals("bar", attributes.get("foo"));
			assertEquals(2, attributes.get("bar"));
		}
	}

	public static class ExceptionThrowingConversationManager implements ConversationManager {

		public Conversation beginConversation(ConversationParameters params) throws ConversationException {
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
