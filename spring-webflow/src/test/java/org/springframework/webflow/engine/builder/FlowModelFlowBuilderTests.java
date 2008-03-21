package org.springframework.webflow.engine.builder;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.ExternalRedirectAction;
import org.springframework.webflow.action.FlowDefinitionRedirectAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowInputMappingException;
import org.springframework.webflow.engine.FlowOutputMappingException;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.ActionExecutingViewFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.InputModel;
import org.springframework.webflow.engine.model.OutputModel;
import org.springframework.webflow.engine.model.PersistenceContextModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilderTests;
import org.springframework.webflow.engine.model.registry.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.security.SecurityRule;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class FlowModelFlowBuilderTests extends TestCase {
	private FlowModel model;

	protected void setUp() {
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("bean", new Object());
		model = new FlowModel();
	}

	public void testBuildIncompleteFlow() {
		try {
			getFlow(model);
			fail("Should have failed");
		} catch (FlowBuilderException e) {
		}
	}

	public void testBuildFlowWithEndState() {
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithDefaultStartState() {
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithStartStateAttribute() {
		model.setStartStateId("end");
		model.addEndState(new EndStateModel("foo"));
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testCustomFlowAttribute() {
		model.addAttribute(new AttributeModel("foo", "bar"));
		model.addAttribute(new AttributeModel("number", "1", "integer"));
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("bar", flow.getAttributes().get("foo"));
		assertEquals(new Integer(1), flow.getAttributes().get("number"));
	}

	public void testPersistenceContextFlow() {
		model.setPersistenceContext(new PersistenceContextModel());
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertNotNull(flow.getAttributes().get("persistenceContext"));
		assertTrue(((Boolean) flow.getAttributes().get("persistenceContext")).booleanValue());
	}

	public void testFlowInputOutputMapping() {
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		model.addInput(new InputModel("number", "flowScope.baz", "integer", null));
		model.addInput(new InputModel("required", "flowScope.boop", null, "true"));
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		model.addOutput(new OutputModel("number", "flowScope.baz", "integer", null));
		model.addOutput(new OutputModel("required", "flowScope.baz", "integer", "true"));
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("foo", "bar");
		input.put("number", "3");
		input.put("required", "9");
		execution.start(input, context);
		Event outcome = execution.getOutcome();
		assertEquals("end", outcome.getId());
		assertEquals("bar", outcome.getAttributes().get("foo"));
		assertEquals("bar", outcome.getAttributes().get("differentName"));
		assertEquals(new Integer(3), outcome.getAttributes().get("number"));
		assertEquals(new Integer(3), outcome.getAttributes().get("required"));
		assertEquals("a literal", outcome.getAttributes().get("literal"));
		assertNull(outcome.getAttributes().get("notReached"));
	}

	public void testFlowRequiredInputMapping() {
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		model.addInput(new InputModel("number", "flowScope.baz", "integer", null));
		model.addInput(new InputModel("required", "flowScope.boop", null, "true"));
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		model.addOutput(new OutputModel("number", "flowScope.baz", "integer", null));
		model.addOutput(new OutputModel("required", "flowScope.baz", "integer", "true"));
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		try {
			execution.start(input, context);
			fail("Should have failed");
		} catch (FlowInputMappingException e) {
		}
	}

	public void testFlowRequiredOutputMapping() {
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		model.addInput(new InputModel("number", "flowScope.baz", "integer", null));
		model.addInput(new InputModel("required", "flowScope.boop", null, "true"));
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		model.addOutput(new OutputModel("number", "flowScope.baz", "integer", null));
		model.addOutput(new OutputModel("required", "flowScope.baz", "integer", "true"));
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("required", "yo");
		try {
			execution.start(input, context);
			fail("Should have failed");
		} catch (FlowOutputMappingException e) {
		}
	}

	public void testFlowSecured() {
		model.setSecured(new SecuredModel("ROLE_USER"));
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getAttributes().get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	public void testFlowSecuredState() {
		EndStateModel end = new EndStateModel("end");
		end.setSecured(new SecuredModel("ROLE_USER"));
		model.addEndState(end);
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getState("end").getAttributes().get(
				SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	public void testFlowSecuredTransition() {
		model.addEndState(new EndStateModel("end"));
		TransitionModel transition = new TransitionModel(null, "end");
		transition.setSecured(new SecuredModel("ROLE_USER"));
		model.addGlobalTransition(transition);
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getGlobalTransitionSet().toArray()[0].getAttributes().get(
				SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	public void testFlowVariable() {
		model.addVar(new VarModel("flow-foo", "org.springframework.webflow.TestBean"));
		model.addVar(new VarModel("conversation-foo", "org.springframework.webflow.TestBean", "conversation"));
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("flow-foo", flow.getVariable("flow-foo").getName());
		assertEquals(true, flow.getVariable("flow-foo").isLocal());
		assertEquals("conversation-foo", flow.getVariables()[1].getName());
		assertEquals(false, flow.getVariables()[1].isLocal());
	}

	public void testViewStateVariable() {
		ViewStateModel view = new ViewStateModel("view");
		view.addVar(new VarModel("foo", "org.springframework.webflow.TestBean"));
		model.addViewState(view);
		Flow flow = getFlow(model);
		assertNotNull(((ViewState) flow.getStateInstance("view")).getVariable("foo"));
	}

	public void testViewStateRedirect() {
		ViewStateModel view = new ViewStateModel("view");
		view.setRedirect("true");
		model.addViewState(view);
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getRedirect());
	}

	public void testViewStatePopup() {
		ViewStateModel view = new ViewStateModel("view");
		view.setPopup("true");
		model.addViewState(view);
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getPopup());
	}

	public void testViewStateFlowRedirect() {
		model.addViewState(new ViewStateModel("view", "flowRedirect:myFlow?input=#{flowScope.foo}"));
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof FlowDefinitionRedirectAction);
	}

	public void testViewStateExternalRedirect() {
		model.addViewState(new ViewStateModel("view",
				"externalRedirect:http://www.paypal.com?_callbackUrl=#{flowExecutionUri}"));
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof ExternalRedirectAction);
	}

	public void testResourceBackedFlowBuilder() {
		ClassPathResource resource = new ClassPathResource("flow-endstate.xml", XmlFlowModelBuilderTests.class);
		Flow flow = getFlow(resource);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	private Flow getFlow(FlowModel model) {
		FlowModelHolder holder = new DefaultFlowModelHolder(model, "flow");
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		return assembler.assembleFlow();
	}

	private Flow getFlow(ClassPathResource resource) {
		FlowModelHolder holder = new DefaultFlowModelHolder(new XmlFlowModelBuilder(resource,
				new FlowModelRegistryImpl()), "flow");
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder, resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		return assembler.assembleFlow();
	}

}
