package org.springframework.webflow.engine.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class DefaultFlowHolderTests {
	private DefaultFlowHolder holder;
	private FlowAssembler assembler;

	@BeforeEach
	public void setUp() {
		MockFlowBuilderContext context = new MockFlowBuilderContext("flowId");
		context.getFlowBuilderServices().setApplicationContext(new StaticApplicationContext());
		FlowAssembler assembler = new FlowAssembler(new SimpleFlowBuilder(), context);
		holder = new DefaultFlowHolder(assembler);
	}

	@Test
	public void testGetFlowDefinition() {
		FlowDefinition flow = holder.getFlowDefinition();
		assertEquals("flowId", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testGetFlowDefinitionWithChangesRefreshed() {
		assembler = new FlowAssembler(new ChangeDetectableFlowBuilder(), new MockFlowBuilderContext("flowId"));
		holder = new DefaultFlowHolder(assembler);
		FlowDefinition flow = holder.getFlowDefinition();
		assertEquals("flowId", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testDestroyNotInitialized() {
		holder.destroy();
	}

	@Test
	public void testDestroy() {
		holder.getFlowDefinition();
		holder.destroy();
	}

	public class SimpleFlowBuilder extends AbstractFlowBuilder implements FlowBuilder {

		public void buildStates() throws FlowBuilderException {
			new EndState(getFlow(), "end");
		}

		protected Flow createFlow() {
			return Flow.create(getContext().getFlowId(), getContext().getFlowAttributes());
		}

	}

	public class ChangeDetectableFlowBuilder extends SimpleFlowBuilder {
		private FileSystemResource resource = new FileSystemResource("file.txt");

		public Resource getResource() {
			return resource;
		}
	}

}
