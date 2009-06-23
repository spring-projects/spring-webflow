package org.springframework.webflow.engine.model.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindingResult;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.action.FormActionTests.TestBeanValidator;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.model.FlowModelFlowBuilder;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.BindingModel;
import org.springframework.webflow.engine.model.ExceptionHandlerModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.engine.model.registry.FlowModelRegistry;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

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
		assertEquals("conversation-foo", ((VarModel) flow.getVars().get(1)).getName());
	}

	public void testViewStateVariable() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-var.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		assertEquals("foo", ((VarModel) ((ViewStateModel) flow.getStates().get(0)).getVars().get(0)).getName());
	}

	public void testViewStateModelBinding() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-model-binding.xml", getClass());
		FlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		builder.init();
		builder.build();
		FlowModel flow = builder.getFlowModel();
		ViewStateModel model = (ViewStateModel) flow.getStates().get(0);
		assertEquals("formObject", model.getModel());
		assertEquals("objectProperty", ((BindingModel) model.getBinder().getBindings().get(0)).getProperty());
		assertEquals("customConverter", ((BindingModel) model.getBinder().getBindings().get(0)).getConverter());
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
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent", new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent,
				registry)));
		FlowModel flow = registry.getFlowModel("child");
		assertEquals(1, flow.getGlobalTransitions().size());
		assertEquals(2, flow.getStates().size());
		assertEquals("view", ((AbstractStateModel) flow.getStates().get(0)).getId());
	}

	public void testMergeParentNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-parent.xml", getClass());
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent-id-not-matching", new DefaultFlowModelHolder(new XmlFlowModelBuilder(
				resourceParent, registry)));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelBuilderException was expected");
		} catch (FlowModelBuilderException e) {
			// we want this
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
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent", new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent,
				registry)));
		FlowModel flow = registry.getFlowModel("child");
		assertEquals(1, flow.getStates().size());
		assertEquals("otherview", ((ViewStateModel) flow.getStates().get(0)).getView());
	}

	public void testStateMergeInvalidParentSyntax() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-invalid-parent-syntax.xml",
				getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent", new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent,
				registry)));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelConstructionException was expected");
		} catch (FlowModelBuilderException e) {
			// we want this
		}
	}

	public void testStateMergeParentFlowNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent-id-not-matching", new DefaultFlowModelHolder(new XmlFlowModelBuilder(
				resourceParent, registry)));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelBuilderException was expected");
		} catch (FlowModelBuilderException e) {
			// we want this
		}
	}

	public void testStateMergeParentStateNotFound() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-empty.xml", getClass());
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent", new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent,
				registry)));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelBuilderException was expected");
		} catch (FlowModelBuilderException e) {
			// we want this
		}
	}

	public void testStateMergeParentStateIncompatable() {
		ClassPathResource resourceChild = new ClassPathResource("flow-inheritance-state-child-alt.xml", getClass());
		ClassPathResource resourceParent = new ClassPathResource("flow-inheritance-state-parent.xml", getClass());
		registry.registerFlowModel("child",
				new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceChild, registry)));
		registry.registerFlowModel("parent", new DefaultFlowModelHolder(new XmlFlowModelBuilder(resourceParent,
				registry)));
		try {
			registry.getFlowModel("child");
			fail("A FlowModelBuilderException was expected");
		} catch (FlowModelBuilderException e) {
			// we want this
		}
	}

	public void testParseFlowExceptionHandler() {
		ClassPathResource res = new ClassPathResource("flow-exception-handler.xml", getClass());
		XmlFlowModelBuilder builder = new XmlFlowModelBuilder(res);
		DefaultFlowModelHolder holder = new DefaultFlowModelHolder(builder);
		FlowModel model = holder.getFlowModel();
		assertEquals("foo1", ((ExceptionHandlerModel) model.getExceptionHandlers().get(0)).getBean());
		assertEquals("foo2", ((ExceptionHandlerModel) model.getStateById("state1").getExceptionHandlers().get(0))
				.getBean());
		assertEquals("foo3", ((ExceptionHandlerModel) model.getStateById("state2").getExceptionHandlers().get(0))
				.getBean());
		assertEquals("foo4", ((ExceptionHandlerModel) model.getStateById("state3").getExceptionHandlers().get(0))
				.getBean());
		assertEquals("foo5", ((ExceptionHandlerModel) model.getStateById("state4").getExceptionHandlers().get(0))
				.getBean());
		assertEquals("foo6", ((ExceptionHandlerModel) model.getStateById("state5").getExceptionHandlers().get(0))
				.getBean());
	}

	public void testFormActionValidatorMethod() {
		ClassPathResource resource = new ClassPathResource("flow-formaction-validatormethod.xml", getClass());
		XmlFlowModelBuilder builder = new XmlFlowModelBuilder(resource, registry);
		DefaultFlowModelHolder holder = new DefaultFlowModelHolder(builder);
		FlowModelFlowBuilder flowBuilder = new FlowModelFlowBuilder(holder);
		FlowAssembler assembler = new FlowAssembler(flowBuilder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(new FlowExecutionListenerAdapter() {
			public void viewRendering(RequestContext context, View view, StateDefinition viewState) {
				if (context.getCurrentEvent() != null && context.getCurrentEvent().getId().equals("submit")) {
					BindingResult result = (BindingResult) context.getFlashScope().get(
							"org.springframework.validation.BindingResult.formBean");
					assertEquals(1, result.getErrorCount());
				}
			}

			public void viewRendered(RequestContext context, View view, StateDefinition viewState) {
				if (context.getCurrentEvent() != null && context.getCurrentEvent().getId().equals("submit")) {
					BindingResult result = (BindingResult) context.getFlashScope().get(
							"org.springframework.validation.BindingResult.formBean");
					assertNull(result);
				}
			}
		}));
		FlowExecution execution = factory.createFlowExecution(flow);
		FormAction action = (FormAction) flow.getApplicationContext().getBean("formAction");
		assertFalse(((TestBeanValidator) action.getValidator()).getInvoked());
		execution.start(null, new MockExternalContext());
		MockExternalContext context = new MockExternalContext();
		context.setEventId("submit");
		execution.resume(context);
		assertTrue(((TestBeanValidator) action.getValidator()).getInvoked());
	}
}
