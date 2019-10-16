package org.springframework.webflow.execution.repository.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.test.MockExternalContext;

public class SerializedFlowExecutionSnapshotFactoryTests {
	private Flow flow;
	private SerializedFlowExecutionSnapshotFactory factory;
	private FlowExecutionImplFactory executionFactory;

	@BeforeEach
	public void setUp() {
		flow = new Flow("myFlow");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		FlowDefinitionLocator locator = flowId -> flow;
		executionFactory = new FlowExecutionImplFactory();
		executionFactory.setExecutionKeyFactory(null);
		factory = new SerializedFlowExecutionSnapshotFactory(executionFactory, locator);
	}

	@Test
	public void testCreateSnapshot() {
		FlowExecutionImpl flowExecution = (FlowExecutionImpl) executionFactory.createFlowExecution(flow);
		flowExecution.start(null, new MockExternalContext());
		flowExecution.getActiveSession().getScope().put("foo", "bar");
		FlowExecutionSnapshot snapshot = factory.createSnapshot(flowExecution);
		FlowExecutionImpl flowExecution2 = (FlowExecutionImpl) factory.restoreExecution(snapshot, "myFlow", null,
				flowExecution.getConversationScope(), null);
		assertNotSame(flowExecution, flowExecution2);
		assertEquals(flowExecution.getDefinition().getId(), flowExecution2.getDefinition().getId());
		assertEquals(flowExecution.getActiveSession().getScope().get("foo"), flowExecution2.getActiveSession()
				.getScope().get("foo"));
		assertEquals(flowExecution.getActiveSession().getState().getId(), flowExecution2.getActiveSession().getState()
				.getId());
		assertNull(flowExecution2.getKey());
		assertSame(flowExecution.getConversationScope(), flowExecution2.getConversationScope());
	}
}