package org.springframework.webflow.engine.model.registry;

import junit.framework.TestCase;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;

public class DefaultFlowModelHolderTests extends TestCase {
	private DefaultFlowModelHolder holder;
	private FlowModelBuilder builder;

	protected void setUp() {
		builder = new SimpleFlowBuilder();
		holder = new DefaultFlowModelHolder(builder);
	}

	public void testGetFlowDefinition() {
		FlowModel flow = holder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public void testGetFlowDefinitionWithChangesRefreshed() {
		FlowModel flow = holder.getFlowModel();
		holder.refresh();
		flow = holder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public class SimpleFlowBuilder implements FlowModelBuilder {

		public FlowModel getFlowModel() throws FlowModelBuilderException {
			FlowModel flow = new FlowModel();
			flow.addEndState(new EndStateModel("end"));
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
