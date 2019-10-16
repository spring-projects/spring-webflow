package org.springframework.webflow.engine.builder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.action.ExternalRedirectAction;
import org.springframework.webflow.action.FlowDefinitionRedirectAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.EvaluateModel;
import org.springframework.webflow.engine.model.ExceptionHandlerModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.InputModel;
import org.springframework.webflow.engine.model.OutputModel;
import org.springframework.webflow.engine.model.PersistenceContextModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.SetModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilderTests;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;
import org.springframework.webflow.engine.support.ActionExecutingViewFactory;
import org.springframework.webflow.execution.AnnotatedAction;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.security.SecurityRule;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockRequestContext;

public class FlowModelFlowBuilderTests {
	private FlowModel model;

	@BeforeEach
	public void setUp() {
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("bean", new Object());
		model = new FlowModel();
	}

	@Test
	public void testBuildIncompleteFlow() {
		try {
			getFlow(model);
			fail("Should have failed");
		} catch (FlowBuilderException e) {
		}
	}

	@SuppressWarnings("unchecked")
	private <T> LinkedList<T> asList(T... a) {
		return new LinkedList<>(Arrays.asList(a));
	}

	@Test
	public void testBuildFlowWithEndState() {
		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testBuildFlowWithDefaultStartState() {
		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testBuildFlowWithStartStateAttribute() {
		model.setStartStateId("end");
		model.setStates(asList(new EndStateModel("foo"), new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testCustomFlowAttribute() {
		AttributeModel attribute1 = new AttributeModel("foo", "bar");
		AttributeModel attribute2 = new AttributeModel("number", "1");
		attribute2.setType("integer");
		model.setAttributes(asList(attribute1, attribute2));

		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("bar", flow.getAttributes().get("foo"));
		assertEquals(1, flow.getAttributes().get("number"));
	}

	@Test
	public void testPersistenceContextFlow() {
		model.setPersistenceContext(new PersistenceContextModel());
		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertNotNull(flow.getAttributes().get("persistenceContext"));
		assertTrue((Boolean) flow.getAttributes().get("persistenceContext"));
	}

	@Test
	public void testFlowInputOutputMapping() {
		InputModel input1 = new InputModel("foo", "flowScope.foo");
		InputModel input2 = new InputModel("foo", "flowScope.bar");
		InputModel input3 = new InputModel("number", "flowScope.baz");
		input3.setType("integer");
		InputModel input4 = new InputModel("required", "flowScope.boop");
		input4.setRequired("true");
		model.setInputs(asList(input1, input2, input3, input4));

		OutputModel output1 = new OutputModel("differentName", "flowScope.bar");
		OutputModel output2 = new OutputModel("number", "flowScope.baz");
		output2.setType("integer");
		OutputModel output3 = new OutputModel("required", "flowScope.baz");
		output3.setType("integer");
		output3.setRequired("true");
		OutputModel output4 = new OutputModel("literal", "'a literal'");
		model.setOutputs(asList(output1, output2, output3, output4));

		EndStateModel end = new EndStateModel("end");
		end.setOutputs(asList(new OutputModel("foo", "flowScope.foo")));

		EndStateModel notReached = new EndStateModel("notReached");
		notReached.setOutputs(asList(new OutputModel("notReached", "flowScope.foo")));

		model.setStates(asList(end, notReached));

		Flow flow = getFlow(model);
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap<Object> map = new LocalAttributeMap<>();
		map.put("foo", "bar");
		map.put("number", "3");
		map.put("required", "9");
		execution.start(map, context);
		FlowExecutionOutcome outcome = execution.getOutcome();
		assertEquals("end", outcome.getId());
		assertEquals("bar", outcome.getOutput().get("foo"));
		assertEquals("bar", outcome.getOutput().get("differentName"));
		assertEquals(3, outcome.getOutput().get("number"));
		assertEquals(3, outcome.getOutput().get("required"));
		assertEquals("a literal", outcome.getOutput().get("literal"));
		assertNull(outcome.getOutput().get("notReached"));
	}

	@Test
	public void testFlowSecured() {
		model.setSecured(new SecuredModel("ROLE_USER"));
		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getAttributes().get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	@Test
	public void testFlowSecuredState() {
		EndStateModel end = new EndStateModel("end");
		end.setSecured(new SecuredModel("ROLE_USER"));
		model.setStates(asList(end));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getState("end").getAttributes()
				.get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	@Test
	public void testFlowSecuredTransition() {
		model.setStates(asList(new EndStateModel("end")));
		TransitionModel transition = new TransitionModel();
		transition.setTo("end");
		transition.setSecured(new SecuredModel("ROLE_USER"));
		model.setGlobalTransitions(asList(transition));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getGlobalTransitionSet().toArray()[0].getAttributes().get(
				SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	@Test
	public void testFlowVariable() {
		model.setVars(asList(new VarModel("flow-foo", "org.springframework.webflow.TestBean")));
		model.setStates(asList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow-foo", flow.getVariable("flow-foo").getName());
	}

	@Test
	public void testViewStateVariable() {
		ViewStateModel view = new ViewStateModel("view");
		view.setVars(asList(new VarModel("foo", "org.springframework.webflow.TestBean")));
		model.setStates(asList(view));
		Flow flow = getFlow(model);
		assertNotNull(((ViewState) flow.getStateInstance("view")).getVariable("foo"));
	}

	@Test
	public void testViewStateRedirect() {
		ViewStateModel view = new ViewStateModel("view");
		view.setRedirect("true");
		model.setStates(asList(view));
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getRedirect());
	}

	@Test
	public void testViewStatePopup() {
		ViewStateModel view = new ViewStateModel("view");
		view.setPopup("true");
		model.setStates(asList(view));
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getPopup());
	}

	@Test
	public void testViewStateFlowRedirect() {
		ViewStateModel state = new ViewStateModel("view");
		state.setView("flowRedirect:myFlow?input=#{flowScope.foo}");
		model.setStates(asList(state));
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof FlowDefinitionRedirectAction);
	}

	@Test
	public void testViewStateExternalRedirect() {
		ViewStateModel state = new ViewStateModel("view");
		state.setView("externalRedirect:https://www.paypal.com?_callbackUrl=#{flowExecutionUri}");
		model.setStates(asList(state));
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof ExternalRedirectAction);
	}

	@Test
	public void testResourceBackedFlowBuilder() {
		ClassPathResource resource = new ClassPathResource("flow-endstate.xml", XmlFlowModelBuilderTests.class);
		Flow flow = getFlow(resource);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	@Test
	public void testResourceBackedFlowBuilderWithMessages() {
		ClassPathResource resource = new ClassPathResource("resources/flow.xml", FlowModelFlowBuilderTests.class);
		Flow flow = getFlow(resource);
		assertNotNull(flow.getApplicationContext());
		assertEquals("bar", flow.getApplicationContext().getMessage("foo", null, null));
	}

	@Test
	public void testAbstractFlow() {
		model.setAbstract("true");
		try {
			getFlow(model);
			fail("FlowBuilderException expected");
		} catch (FlowBuilderException e) {
			// we want this
		}
	}

	@Test
	public void testExceptionHandlers() {
		FlowModel model = new FlowModel();
		model.setStates(asList(new EndStateModel("state")));
		model.setExceptionHandlers(asList(new ExceptionHandlerModel("exceptionHandler")));
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

	@Test
	public void testSetActionWithResultType() throws Exception {
		SetModel setModel = new SetModel("flowScope.stringArray", "intArray");
		setModel.setType("java.lang.String[]");
		model.setOnStartActions(asList(setModel));
		model.setStates(asList(new ViewStateModel("view")));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("intArray", new int[] { 1, 2 });
		action.execute(context);
		String[] expected = (String[]) context.getFlowScope().get("stringArray");
		assertEquals("1", expected[0]);
		assertEquals("2", expected[1]);
	}

	@Test
	public void testSetActionWithImplicitTypeConversion() throws Exception {
		SetModel setModel = new SetModel("testBean.stringArray", "intArray");
		model.setOnStartActions(asList(setModel));
		ViewStateModel state = new ViewStateModel("view");
		model.setStates(asList(state));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("testBean", new TestBean());
		context.getFlowScope().put("intArray", new int[] { 1, 2 });
		action.execute(context);
		TestBean expected = (TestBean) context.getFlowScope().get("testBean");
		assertEquals("1", expected.stringArray[0]);
		assertEquals("2", expected.stringArray[1]);
	}

	@Test
	public void testEvaluateActionWithResultType() throws Exception {
		EvaluateModel evaluateModel = new EvaluateModel("testBean.getIntegers()");
		evaluateModel.setResult("flowScope.stringArray");
		evaluateModel.setResultType("java.lang.String[]");
		model.setOnStartActions(asList(evaluateModel));
		model.setStates(asList(new ViewStateModel("view")));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("testBean", new TestBean());
		action.execute(context);
		String[] expected = (String[]) context.getFlowScope().get("stringArray");
		assertEquals("1", expected[0]);
		assertEquals("2", expected[1]);
	}

	@Test
	public void testEvaluateActionWithELExpression() throws Exception {
		EvaluateModel evaluateModel = new EvaluateModel("testBean.getIntegers()");
		evaluateModel.setResult("flowScope.stringArray");
		evaluateModel.setResultType("java.lang.String[]");
		model.setOnStartActions(asList(evaluateModel));
		model.setStates(asList(new ViewStateModel("view")));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("testBean", new TestBean());
		action.execute(context);
		String[] expected = (String[]) context.getFlowScope().get("stringArray");
		assertEquals("1", expected[0]);
		assertEquals("2", expected[1]);
	}

	private static class TestBean {
		public String[] stringArray;

		@SuppressWarnings("unused")
		public int[] getIntegers() {
			return new int[] { 1, 2 };
		}
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

		public Resource getFlowModelResource() {
			return new ClassPathResource("", getClass());
		}

		public boolean hasFlowModelChanged() {
			return false;
		}

		public void refresh() {
		}

	}
}
