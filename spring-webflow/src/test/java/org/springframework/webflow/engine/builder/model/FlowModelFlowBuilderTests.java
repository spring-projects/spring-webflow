package org.springframework.webflow.engine.builder.model;

import java.util.LinkedList;

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
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.model.AjaxDrivenModel;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.EvaluateModel;
import org.springframework.webflow.engine.model.ExceptionHandlerModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.InputModel;
import org.springframework.webflow.engine.model.Model;
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

	private LinkedList singleList(Model model) {
		LinkedList list = new LinkedList();
		list.add(model);
		return list;
	}

	private LinkedList doubleList(Model model, Model model2) {
		LinkedList list = new LinkedList();
		list.add(model);
		list.add(model2);
		return list;
	}

	private LinkedList quadList(Model model, Model model2, Model model3, Model model4) {
		LinkedList list = new LinkedList();
		list.add(model);
		list.add(model2);
		list.add(model3);
		list.add(model4);
		return list;
	}

	public void testBuildFlowWithEndState() {
		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithDefaultStartState() {
		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithStartStateAttribute() {
		model.setStartStateId("end");
		model.setStates(doubleList(new EndStateModel("foo"), new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testCustomFlowAttribute() {
		AttributeModel attribute1 = new AttributeModel("foo", "bar");
		AttributeModel attribute2 = new AttributeModel("number", "1");
		attribute2.setType("integer");
		model.setAttributes(doubleList(attribute1, attribute2));

		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("bar", flow.getAttributes().get("foo"));
		assertEquals(new Integer(1), flow.getAttributes().get("number"));
	}

	public void testPersistenceContextFlow() {
		model.setPersistenceContext(new PersistenceContextModel());
		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertNotNull(flow.getAttributes().get("persistenceContext"));
		assertTrue(((Boolean) flow.getAttributes().get("persistenceContext")).booleanValue());
	}

	public void testAjaxDrivenFlow() {
		model.setAjaxDriven(new AjaxDrivenModel());
		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertNotNull(flow.getAttributes().get("ajaxDriven"));
		assertTrue(((Boolean) flow.getAttributes().get("ajaxDriven")).booleanValue());
	}

	public void testFlowInputOutputMapping() {
		InputModel input1 = new InputModel("foo", "flowScope.foo");
		InputModel input2 = new InputModel("foo", "flowScope.bar");
		InputModel input3 = new InputModel("number", "flowScope.baz");
		input3.setType("integer");
		InputModel input4 = new InputModel("required", "flowScope.boop");
		input4.setRequired("true");
		model.setInputs(quadList(input1, input2, input3, input4));

		OutputModel output1 = new OutputModel("differentName", "flowScope.bar");
		OutputModel output2 = new OutputModel("number", "flowScope.baz");
		output2.setType("integer");
		OutputModel output3 = new OutputModel("required", "flowScope.baz");
		output3.setType("integer");
		output3.setRequired("true");
		OutputModel output4 = new OutputModel("literal", "'a literal'");
		model.setOutputs(quadList(output1, output2, output3, output4));

		EndStateModel end = new EndStateModel("end");
		end.setOutputs(singleList(new OutputModel("foo", "flowScope.foo")));

		EndStateModel notReached = new EndStateModel("notReached");
		notReached.setOutputs(singleList(new OutputModel("notReached", "flowScope.foo")));

		model.setStates(doubleList(end, notReached));

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

	public void testFlowSecured() {
		model.setSecured(new SecuredModel("ROLE_USER"));
		model.setStates(singleList(new EndStateModel("end")));
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
		model.setStates(singleList(end));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getState("end").getAttributes()
				.get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	public void testFlowSecuredTransition() {
		model.setStates(singleList(new EndStateModel("end")));
		TransitionModel transition = new TransitionModel();
		transition.setTo("end");
		transition.setSecured(new SecuredModel("ROLE_USER"));
		model.setGlobalTransitions(singleList(transition));
		Flow flow = getFlow(model);
		SecurityRule rule = (SecurityRule) flow.getGlobalTransitionSet().toArray()[0].getAttributes().get(
				SecurityRule.SECURITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getAttributes().size());
		assertTrue(rule.getAttributes().contains("ROLE_USER"));
	}

	public void testFlowVariable() {
		model.setVars(singleList(new VarModel("flow-foo", "org.springframework.webflow.TestBean")));
		model.setStates(singleList(new EndStateModel("end")));
		Flow flow = getFlow(model);
		assertEquals("flow-foo", flow.getVariable("flow-foo").getName());
	}

	public void testViewStateVariable() {
		ViewStateModel view = new ViewStateModel("view");
		view.setVars(singleList(new VarModel("foo", "org.springframework.webflow.TestBean")));
		model.setStates(singleList(view));
		Flow flow = getFlow(model);
		assertNotNull(((ViewState) flow.getStateInstance("view")).getVariable("foo"));
	}

	public void testViewStateRedirect() {
		ViewStateModel view = new ViewStateModel("view");
		view.setRedirect("true");
		model.setStates(singleList(view));
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getRedirect());
	}

	public void testViewStatePopup() {
		ViewStateModel view = new ViewStateModel("view");
		view.setPopup("true");
		model.setStates(singleList(view));
		Flow flow = getFlow(model);
		assertTrue(((ViewState) flow.getStateInstance("view")).getPopup());
	}

	public void testViewStateFlowRedirect() {
		ViewStateModel state = new ViewStateModel("view");
		state.setView("flowRedirect:myFlow?input=#{flowScope.foo}");
		model.setStates(singleList(state));
		Flow flow = getFlow(model);
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof FlowDefinitionRedirectAction);
	}

	public void testViewStateExternalRedirect() {
		ViewStateModel state = new ViewStateModel("view");
		state.setView("externalRedirect:http://www.paypal.com?_callbackUrl=#{flowExecutionUri}");
		model.setStates(singleList(state));
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
		model.setStates(singleList(new EndStateModel("state")));
		model.setExceptionHandlers(singleList(new ExceptionHandlerModel("exceptionHandler")));
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

	public void testSetActionWithResultType() throws Exception {
		SetModel setModel = new SetModel("flowScope.stringArray", "intArray");
		setModel.setType("java.lang.String[]");
		model.setOnStartActions(singleList(setModel));
		model.setStates(singleList(new ViewStateModel("view")));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("intArray", new int[] { 1, 2 });
		action.execute(context);
		String[] expected = (String[]) context.getFlowScope().get("stringArray");
		assertEquals("1", expected[0]);
		assertEquals("2", expected[1]);
	}

	public void testSetActionWithImplicitTypeConversion() throws Exception {
		SetModel setModel = new SetModel("testBean.stringArray", "intArray");
		model.setOnStartActions(singleList(setModel));
		ViewStateModel state = new ViewStateModel("view");
		model.setStates(singleList(state));
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

	public void testEvaluateActionWithResultType() throws Exception {
		EvaluateModel evaluateModel = new EvaluateModel("testBean.getIntegers()");
		evaluateModel.setResult("flowScope.stringArray");
		evaluateModel.setResultType("java.lang.String[]");
		model.setOnStartActions(singleList(evaluateModel));
		model.setStates(singleList(new ViewStateModel("view")));
		Flow flow = getFlow(model);
		AnnotatedAction action = (AnnotatedAction) flow.getStartActionList().get(0);
		MockRequestContext context = new MockRequestContext(flow);
		context.getFlowScope().put("testBean", new TestBean());
		action.execute(context);
		String[] expected = (String[]) context.getFlowScope().get("stringArray");
		assertEquals("1", expected[0]);
		assertEquals("2", expected[1]);
	}

	public void testEvaluateActionWithELExpression() throws Exception {
		EvaluateModel evaluateModel = new EvaluateModel("testBean.getIntegers()");
		evaluateModel.setResult("flowScope.stringArray");
		evaluateModel.setResultType("java.lang.String[]");
		model.setOnStartActions(singleList(evaluateModel));
		model.setStates(singleList(new ViewStateModel("view")));
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

		public String getFlowModelId() {
			return "flow";
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
