package org.springframework.webflow.engine.builder.model;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.action.ExternalRedirectAction;
import org.springframework.webflow.action.FlowDefinitionRedirectAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.FlowInputMappingException;
import org.springframework.webflow.engine.FlowOutputMappingException;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.ExceptionHandlerModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.InputModel;
import org.springframework.webflow.engine.model.OutputModel;
import org.springframework.webflow.engine.model.PersistenceContextModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilderTests;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;
import org.springframework.webflow.engine.support.ActionExecutingViewFactory;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionOutcome;
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
		AttributeModel attribute = new AttributeModel("number", "1");
		attribute.setType("integer");
		model.addAttribute(attribute);
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
		InputModel input;
		OutputModel output;
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		input = new InputModel("number", "flowScope.baz");
		input.setType("integer");
		model.addInput(input);
		input = new InputModel("required", "flowScope.boop");
		input.setRequired("true");
		model.addInput(input);
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		output = new OutputModel("number", "flowScope.baz");
		output.setType("integer");
		model.addOutput(output);
		output = new OutputModel("required", "flowScope.baz");
		output.setType("integer");
		output.setRequired("true");
		model.addOutput(output);
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");
		map.put("number", "3");
		map.put("required", "9");
		execution.start(map, context);
		FlowExecutionOutcome outcome = execution.getOutcome();
		assertEquals("end", outcome.getId());
		assertEquals("bar", outcome.getOutput().get("foo"));
		assertEquals("bar", outcome.getOutput().get("differentName"));
		assertEquals(new Integer(3), outcome.getOutput().get("number"));
		assertEquals(new Integer(3), outcome.getOutput().get("required"));
		assertEquals("a literal", outcome.getOutput().get("literal"));
		assertNull(outcome.getOutput().get("notReached"));
	}

	public void testFlowRequiredInputMapping() {
		InputModel input;
		OutputModel output;
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		input = new InputModel("number", "flowScope.baz");
		input.setType("integer");
		model.addInput(input);
		input = new InputModel("required", "flowScope.boop");
		input.setRequired("true");
		model.addInput(input);
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		output = new OutputModel("number", "flowScope.baz");
		output.setType("integer");
		model.addOutput(output);
		output = new OutputModel("required", "flowScope.baz");
		output.setType("integer");
		output.setRequired("true");
		model.addOutput(output);
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap map = new LocalAttributeMap();
		try {
			execution.start(map, context);
			fail("Should have failed");
		} catch (FlowInputMappingException e) {
		}
	}

	public void testFlowRequiredOutputMapping() {
		InputModel input;
		OutputModel output;
		model.addInput(new InputModel("foo", "flowScope.foo"));
		model.addInput(new InputModel("foo", "flowScope.bar"));
		input = new InputModel("number", "flowScope.baz");
		input.setType("integer");
		model.addInput(input);
		input = new InputModel("required", "flowScope.boop");
		input.setRequired("true");
		model.addInput(input);
		EndStateModel end = new EndStateModel("end");
		end.addOutput(new OutputModel("foo", "flowScope.foo"));
		model.addEndState(end);
		EndStateModel notReached = new EndStateModel("notReached");
		notReached.addOutput(new OutputModel("notReached", "flowScope.foo"));
		model.addEndState(notReached);
		model.addOutput(new OutputModel("differentName", "flowScope.bar"));
		output = new OutputModel("number", "flowScope.baz");
		output.setType("integer");
		model.addOutput(output);
		output = new OutputModel("required", "flowScope.baz");
		output.setType("integer");
		output.setRequired("true");
		model.addOutput(output);
		model.addOutput(new OutputModel("literal", "'a literal'"));
		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap map = new LocalAttributeMap();
		map.put("required", "yo");
		try {
			execution.start(map, context);
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
		TransitionModel transition = new TransitionModel();
		transition.setTo("end");
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
		model.addEndState(new EndStateModel("end"));
		Flow flow = getFlow(model);
		assertEquals("flow-foo", flow.getVariable("flow-foo").getName());
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
		ViewStateModel state = new ViewStateModel("view");
		state.setView("flowRedirect:myFlow?input=#{flowScope.foo}");
		model.addViewState(state);
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof FlowDefinitionRedirectAction);
	}

	public void testViewStateExternalRedirect() {
		ViewStateModel state = new ViewStateModel("view");
		state.setView("externalRedirect:http://www.paypal.com?_callbackUrl=#{flowExecutionUri}");
		model.addViewState(state);
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

	public void testResourceBackedFlowBuilderWithMessages() {
		ClassPathResource resource = new ClassPathResource("resources/flow.xml", FlowModelFlowBuilderTests.class);
		Flow flow = getFlow(resource);
		assertNotNull(flow.getApplicationContext());
		assertEquals("bar", flow.getApplicationContext().getMessage("foo", null, null));
	}

	public void testAbstractFlow() {
		model.setAbstract("true");
		try {
			getFlow(model);
			fail("FlowBuilderException expected");
		} catch (FlowBuilderException e) {
			// we want this
		}
	}

	public void testExceptionHandlers() {
		FlowModel model = new FlowModel();
		model.addState(new EndStateModel("state"));
		model.addExceptionHandler(new ExceptionHandlerModel("exceptionHandler"));
		FlowExecutionExceptionHandler handler = new FlowExecutionExceptionHandler() {
			public boolean canHandle(FlowExecutionException exception) {
				return true;
			}

			public void handle(FlowExecutionException exception, RequestControlContext context) {
			}
		};
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(new StaticFlowModelHolder(model));
		MockFlowBuilderContext context = new MockFlowBuilderContext("foo");
		context.registerBean("exceptionHandler", handler);
		FlowAssembler assembler = new FlowAssembler(builder, context);
		Flow flow = assembler.assembleFlow();
		assertEquals(1, flow.getExceptionHandlerSet().size());
	}

	private Flow getFlow(FlowModel model) {
		FlowModelHolder holder = new StaticFlowModelHolder(model);
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		return assembler.assembleFlow();
	}

	private Flow getFlow(ClassPathResource resource) {
		FlowModelHolder holder = new DefaultFlowModelHolder(new XmlFlowModelBuilder(resource,
				new FlowModelRegistryImpl()));
		FlowModelFlowBuilder builder = new FlowModelFlowBuilder(holder);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		return assembler.assembleFlow();
	}

	private static class StaticFlowModelHolder implements FlowModelHolder {

		private FlowModel model;

		public StaticFlowModelHolder(FlowModel model) {
			this.model = model;
		}

		public FlowModel getFlowModel() {
			return model;
		}

		public String getFlowModelId() {
			return "flow";
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
