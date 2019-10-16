package org.springframework.webflow.engine.model.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;

public class DefaultFlowModelHolderTests {
	private DefaultFlowModelHolder holder;
	private FlowModelBuilder builder;

	@BeforeEach
	public void setUp() {
		builder = new SimpleFlowBuilder();
		holder = new DefaultFlowModelHolder(builder);
	}

	@Test
	public void testGetFlowDefinition() {
		FlowModel flow = holder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", flow.getStates().get(0).getId());
	}

	@Test
	public void testGetFlowDefinitionWithChangesRefreshed() {
		FlowModel flow = holder.getFlowModel();
		holder.refresh();
		flow = holder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", flow.getStates().get(0).getId());
	}

	public class SimpleFlowBuilder implements FlowModelBuilder {

		public FlowModel getFlowModel() throws FlowModelBuilderException {
			FlowModel flow = new FlowModel();
			flow.setStates(new LinkedList<>(Collections.singletonList(new EndStateModel("end"))));
			return flow;
		}

		public void build() throws FlowModelBuilderException {
			// no-op
		}

		public void mergeParent() throws FlowModelBuilderException {
			// no-op
		}

		public void dispose() throws FlowModelBuilderException {
			// no-op
		}

		public void init() throws FlowModelBuilderException {
			// no-op
		}

		public Resource getFlowModelResource() {
			return null;
		}

		public boolean hasFlowModelResourceChanged() {
			return false;
		}

	}
}
