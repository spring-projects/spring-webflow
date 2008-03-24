package org.springframework.webflow.engine.model.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.registry.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelConstructionException;
import org.springframework.webflow.engine.model.registry.FlowModelRegistry;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;

public class XmlFlowModelBuilderTests extends TestCase {

	private FlowModelRegistry registry;

	protected void setUp() {
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("bean", new Object());
		registry = new FlowModelRegistryImpl();
	}

	public void testBuildFlowWithEndState() {
		ClassPathResource resource = new ClassPathResource("flow-endstate.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public void testBuildFlowWithDefaultStartState() {
		ClassPathResource resource = new ClassPathResource("flow-startstate-default.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertNull(flow.getStartStateId());
		assertEquals("end", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public void testBuildFlowWithStartStateAttribute() {
		ClassPathResource resource = new ClassPathResource("flow-startstate-attribute.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("end", flow.getStartStateId());
	}

	public void testCustomFlowAttribute() {
		ClassPathResource resource = new ClassPathResource("flow-custom-attribute.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("bar", ((AttributeModel) flow.getAttributes().get(0)).getValue());
		assertEquals("number", ((AttributeModel) flow.getAttributes().get(1)).getName());
	}

	public void testPersistenceContextFlow() {
		ClassPathResource resource = new ClassPathResource("flow-persistencecontext.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertNotNull(flow.getPersistenceContext());
	}

	public void testFlowSecured() {
		ClassPathResource resource = new ClassPathResource("flow-secured.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		SecuredModel secured = flow.getSecured();
		assertNotNull(secured);
		assertEquals("ROLE_USER", secured.getAttributes());
	}

	public void testFlowSecuredState() {
		ClassPathResource resource = new ClassPathResource("flow-secured-state.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		SecuredModel secured = ((AbstractStateModel) flow.getStates().get(0)).getSecured();
		assertNotNull(secured);
		assertEquals("ROLE_USER", secured.getAttributes());
	}

	public void testFlowSecuredTransition() {
		ClassPathResource resource = new ClassPathResource("flow-secured-transition.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		SecuredModel secured = ((TransitionModel) flow.getGlobalTransitions().get(0)).getSecured();
		assertNotNull(secured);
		assertEquals("ROLE_USER", secured.getAttributes());
	}

	public void testFlowVariable() {
		ClassPathResource resource = new ClassPathResource("flow-var.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("flow-foo", ((VarModel) flow.getVars().get(0)).getName());
		assertEquals(null, ((VarModel) flow.getVars().get(0)).getScope());
		assertEquals("conversation-foo", ((VarModel) flow.getVars().get(1)).getName());
		assertEquals("conversation", ((VarModel) flow.getVars().get(1)).getScope());
	}

	public void testViewStateVariable() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-var.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("foo", ((VarModel) ((ViewStateModel) flow.getStates().get(0)).getVars().get(0)).getName());
	}

	public void testViewStateRedirect() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-redirect.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("true", ((ViewStateModel) flow.getStates().get(0)).getRedirect());
	}

	public void testViewStatePopup() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-popup.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("true", ((ViewStateModel) flow.getStates().get(0)).getPopup());
	}

	public void testMerge() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent"));
		FlowModel flow = registry.getFlowModel("child");
		assertEquals(1, flow.getGlobalTransitions().size());
		assertEquals(2, flow.getStates().size());
		assertEquals("view", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public void testMergeParentNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent-id-not-matching"));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelConstructionException was expected");
		} catch (FlowModelConstructionException e) {
			// we want this
			e.printStackTrace();
		}
	}

	public void testEvaluateAction() {
		ClassPathResource resource = new ClassPathResource("flow-action-evaluate-action.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals(4, flow.getOnStartActions().size());
	}

	public void testStateMerge() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent"));
		FlowModel flow = registry.getFlowModel("child");
		assertEquals(1, flow.getStates().size());
		assertEquals("otherview", ((ViewStateModel) flow.getStates().get(0)).getView());
	}

	public void testStateMergeDefault() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child-default.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent"));
		FlowModel flow = registry.getFlowModel("child");
		assertEquals(1, flow.getStates().size());
		assertEquals("mainview", ((ViewStateModel) flow.getStates().get(0)).getView());
	}

	public void testStateMergeParentFlowNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent-id-not-matching"));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelConstructionException was expected");
		} catch (FlowModelConstructionException e) {
			// we want this
		}
	}

	public void testStateMergeParentStateNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-empty.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent"));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelConstructionException was expected");
		} catch (FlowModelConstructionException e) {
			// we want this
		}
	}

	public void testStateMergeParentStateIncompatable() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child-alt.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry
				.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry), "child"));
		registry.registerFlowModel(new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent, registry),
				"parent"));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelConstructionException was expected");
		} catch (FlowModelConstructionException e) {
			// we want this
		}
	}

}
