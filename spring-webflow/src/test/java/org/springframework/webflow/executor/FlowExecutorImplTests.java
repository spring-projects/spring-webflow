package org.springframework.webflow.executor;

import junit.framework.TestCase;

import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.repository.impl.DefaultFlowExecutionRepository;
import org.springframework.webflow.test.MockExternalContext;

public class FlowExecutorImplTests extends TestCase {
	private FlowDefinitionRegistryImpl definitionLocator;
	private FlowExecutionImplFactory executionFactory;
	private DefaultFlowExecutionRepository executionRepository;
	private FlowExecutorImpl executor;

	protected void setUp() {
		definitionLocator = new FlowDefinitionRegistryImpl();
		executionFactory = new FlowExecutionImplFactory();
		executionRepository = new DefaultFlowExecutionRepository(new SessionBindingConversationManager(),
				new FlowExecutionImplStateRestorer(definitionLocator));
		executionFactory.setExecutionKeyFactory(executionRepository);
		executor = new FlowExecutorImpl(definitionLocator, executionFactory, executionRepository);
	}

	public void testLaunchAndEnd() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		definitionLocator.registerFlowDefinition(flow);
		MockExternalContext context = new MockExternalContext();
		context.setFlowId("flow");

		ExternalContextHolder.setExternalContext(context);
		executor.execute(context);
		ExternalContextHolder.setExternalContext(null);

		assertNull(context.getFlowExecutionRedirectResult());
		assertNull(context.getPausedFlowExecutionKeyResult());
		assertNull(context.getExceptionResult());
	}

	public void testLaunchAndResume() {
		Flow flow = new Flow("flow");
		new ViewState(flow, "pause", new StubViewFactory());
		definitionLocator.registerFlowDefinition(flow);
		MockExternalContext context = new MockExternalContext();
		context.setFlowId("flow");

		ExternalContextHolder.setExternalContext(context);
		executor.execute(context);
		ExternalContextHolder.setExternalContext(null);

		assertNotNull(context.getPausedFlowExecutionKeyResult());
		assertNull(context.getExceptionResult());
		assertNull(context.getFlowExecutionRedirectResult());

		MockExternalContext context2 = new MockExternalContext();
		context2.setSessionMap(context.getSessionMap());
		context2.setFlowId("flow");
		context2.setFlowExecutionKey(context.getPausedFlowExecutionKeyResult());

		ExternalContextHolder.setExternalContext(context);
		executor.execute(context2);
		ExternalContextHolder.setExternalContext(null);
	}

	public void testLaunchAndException() {
		Flow flow = new Flow("flow");
		final UnsupportedOperationException e = new UnsupportedOperationException();
		new State(flow, "exception") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw e;
			}
		};
		definitionLocator.registerFlowDefinition(flow);
		MockExternalContext context = new MockExternalContext();
		context.setFlowId("flow");

		ExternalContextHolder.setExternalContext(context);
		executor.execute(context);
		ExternalContextHolder.setExternalContext(null);

		assertNull(context.getFlowExecutionRedirectResult());
		assertNull(context.getPausedFlowExecutionKeyResult());
		assertNotNull(context.getExceptionResult());
		assertSame(e, context.getExceptionResult().getCause());
	}
}
