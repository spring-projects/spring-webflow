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
		registry.registerFlowModel(new StaticFlowModelHolder(fooFlow, "foo"));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterFlowSameIds() {
		registry.registerFlowModel(new StaticFlowModelHolder(fooFlow, "foo"));
		FlowModel newFlow = new FlowModel();
		registry.registerFlowModel(new StaticFlowModelHolder(newFlow, "foo"));
		assertSame(newFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterMultipleFlows() {
		registry.registerFlowModel(new StaticFlowModelHolder(fooFlow, "foo"));
		registry.registerFlowModel(new StaticFlowModelHolder(barFlow, "bar"));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
		assertEquals(barFlow, registry.getFlowModel("bar"));
	}

	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowModelRegistryImpl child = new FlowModelRegistryImpl();
		child.setParent(registry);
		FlowModel fooFlow = new FlowModel();
		child.registerFlowModel(new StaticFlowModelHolder(fooFlow, "foo"));
		assertSame(fooFlow, child.getFlowModel("foo"));
		assertEquals(barFlow, child.getFlowModel("bar"));
	}

	private static class StaticFlowModelHolder implements FlowModelHolder {

		private FlowModel model;
		private String id;

		public StaticFlowModelHolder(FlowModel model, String id) {
			this.model = model;
			this.id = id;
		}

		public FlowModel getFlowModel() throws FlowModelConstructionException {
			return model;
		}

		public String getFlowModelId() {
			return id;
		}

		public Resource getFlowModelResource() {
			return null;
		}

		public boolean hasFlowModelChanged() {
			return false;
		}

		public void refresh() throws FlowModelConstructionException {
		}

	}
}
