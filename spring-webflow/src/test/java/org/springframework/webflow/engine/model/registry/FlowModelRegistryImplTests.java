package org.springframework.webflow.engine.model.registry;

import junit.framework.TestCase;

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
		registry.registerFlowModel(new DefaultFlowModelHolder(fooFlow, "foo"));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterFlowSameIds() {
		registry.registerFlowModel(new DefaultFlowModelHolder(fooFlow, "foo"));
		FlowModel newFlow = new FlowModel();
		registry.registerFlowModel(new DefaultFlowModelHolder(newFlow, "foo"));
		assertSame(newFlow, registry.getFlowModel("foo"));
	}

	public void testRegisterMultipleFlows() {
		registry.registerFlowModel(new DefaultFlowModelHolder(fooFlow, "foo"));
		registry.registerFlowModel(new DefaultFlowModelHolder(barFlow, "bar"));
		assertEquals(fooFlow, registry.getFlowModel("foo"));
		assertEquals(barFlow, registry.getFlowModel("bar"));
	}

	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowModelRegistryImpl child = new FlowModelRegistryImpl();
		child.setParent(registry);
		FlowModel fooFlow = new FlowModel();
		child.registerFlowModel(new DefaultFlowModelHolder(fooFlow, "foo"));
		assertSame(fooFlow, child.getFlowModel("foo"));
		assertEquals(barFlow, child.getFlowModel("bar"));
	}

}
