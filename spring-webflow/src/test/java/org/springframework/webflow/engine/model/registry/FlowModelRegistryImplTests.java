package org.springframework.webflow.engine.model.registry;

import junit.framework.TestCase;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.model.FlowModel;

public class FlowModelRegistryImplTests extends TestCase {

	private FlowModelRegistryImpl registry = new FlowModelRegistryImpl();

	private FlowModel fooFlow;

	private FlowModel barFlow;

	protected void setUp() {
		fooFlow = new FlowModel();
		barFlow = new FlowModel();
	}

	public void testNoSuchFlowDefinition() {
		try {
			registry.getFlowModel("bogus");
			fail("Should've bombed with NoSuchFlow");
		} catch (NoSuchFlowModelException e) {

		}
	}

	public void testRegisterFlow() {
		registry.registerFlowModel("foo", new StaticFlowModelHolder(fooFlow));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterFlowSameIds() {
		registry.registerFlowModel("foo", new StaticFlowModelHolder(fooFlow));
		FlowModel newFlow = new FlowModel();
		registry.registerFlowModel("foo", new StaticFlowModelHolder(newFlow));
		assertSame(newFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterMultipleFlows() {
		registry.registerFlowModel("foo", new StaticFlowModelHolder(fooFlow));
		registry.registerFlowModel("bar", new StaticFlowModelHolder(barFlow));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
		assertEquals(barFlow, registry.getFlowModel("bar"));
	}

	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowModelRegistryImpl child = new FlowModelRegistryImpl();
		child.setParent(registry);
		FlowModel fooFlow = new FlowModel();
		child.registerFlowModel("foo", new StaticFlowModelHolder(fooFlow));
		assertSame(fooFlow, child.getFlowModel("foo"));
		assertEquals(barFlow, child.getFlowModel("bar"));
	}

	private static class StaticFlowModelHolder implements FlowModelHolder {

		private FlowModel model;

		public StaticFlowModelHolder(FlowModel model) {
			this.model = model;
		}

		public FlowModel getFlowModel() {
			return model;
		}

		public Resource getFlowModelResource() {
			return null;
		}

		public boolean hasFlowModelChanged() {
			return false;
		}

		public void refresh() {
		}

	}
}
