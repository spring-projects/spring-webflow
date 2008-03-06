package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.mapping.RequiredMappingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.ExternalRedirectAction;
import org.springframework.webflow.action.FlowDefinitionRedirectAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.ActionExecutingViewFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.security.SecurityRule;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class XmlFlowBuilderTests extends TestCase {
	private XmlFlowBuilder builder;

	protected void setUp() {
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("bean", new Object());
	}

	public void testBuildIncompleteFlow() {
		ClassPathResource resource = new ClassPathResource("flow-incomplete.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		try {
			assembler.assembleFlow();
			fail("Should have failed");
		} catch (FlowBuilderException e) {
		}
	}

	public void testBuildFlowWithEndState() {
		ClassPathResource resource = new ClassPathResource("flow-endstate.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithDefaultStartState() {
		ClassPathResource resource = new ClassPathResource("flow-startstate-default.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testBuildFlowWithStartStateAttribute() {
		ClassPathResource resource = new ClassPathResource("flow-startstate-attribute.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

	public void testCustomFlowAttribute() {
		ClassPathResource resource = new ClassPathResource("flow-custom-attribute.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("bar", flow.getAttributes().get("foo"));
		assertEquals(new Integer(1), flow.getAttributes().get("number"));
	}

	public void testPersistenceContextFlow() {
		ClassPathResource resource = new ClassPathResource("flow-persistencecontext.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertNotNull(flow.getAttributes().get("persistenceContext"));
		assertTrue(((Boolean) flow.getAttributes().get("persistenceContext")).booleanValue());
	}

	public void testFlowInputOutputMapping() {
		ClassPathResource resource = new ClassPathResource("flow-inputoutput.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
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
		ClassPathResource resource = new ClassPathResource("flow-inputoutput.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		try {
			execution.start(input, context);
			fail("Should have failed");
		} catch (FlowExecutionException e) {
			RequiredMappingException me = (RequiredMappingException) e.getRootCause();
		}
	}

	public void testFlowRequiredOutputMapping() {
		ClassPathResource resource = new ClassPathResource("flow-inputoutput.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("required", "yo");
		try {
			execution.start(input, context);
			fail("Should have failed");
		} catch (FlowExecutionException e) {
			RequiredMappingException me = (RequiredMappingException) e.getRootCause();
		}
	}

	public void testFlowSecured() {
		ClassPathResource resource = new ClassPathResource("flow-secured.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		SecurityRule rule = (SecurityRule) flow.getAttributes().get(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getRequiredAuthorities().size());
		assertTrue(rule.getRequiredAuthorities().contains("ROLE_USER"));
	}

	public void testFlowSecuredState() {
		ClassPathResource resource = new ClassPathResource("flow-secured-state.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		SecurityRule rule = (SecurityRule) flow.getState("end").getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getRequiredAuthorities().size());
		assertTrue(rule.getRequiredAuthorities().contains("ROLE_USER"));
	}

	public void testFlowSecuredTransition() {
		ClassPathResource resource = new ClassPathResource("flow-secured-transition.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		SecurityRule rule = (SecurityRule) flow.getGlobalTransitionSet().toArray()[0].getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		assertNotNull(rule);
		assertEquals(SecurityRule.COMPARISON_ANY, rule.getComparisonType());
		assertEquals(1, rule.getRequiredAuthorities().size());
		assertTrue(rule.getRequiredAuthorities().contains("ROLE_USER"));
	}

	public void testFlowVariable() {
		ClassPathResource resource = new ClassPathResource("flow-var.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("flow-foo", flow.getVariable("flow-foo").getName());
		assertEquals(true, flow.getVariable("flow-foo").isLocal());
		assertEquals("conversation-foo", flow.getVariables()[1].getName());
		assertEquals(false, flow.getVariables()[1].isLocal());
	}

	public void testViewStateVariable() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-var.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertNotNull(((ViewState) flow.getStateInstance("view")).getVariable("foo"));
	}

	public void testViewStateRedirect() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-redirect.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertTrue(((ViewState) flow.getStateInstance("view")).getRedirect());
	}

	public void testViewStatePopup() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-popup.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertTrue(((ViewState) flow.getStateInstance("view")).getPopup());
	}

	public void testViewStateFlowRedirect() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-flowredirect.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof FlowDefinitionRedirectAction);
	}

	public void testViewStateExternalRedirect() {
		ClassPathResource resource = new ClassPathResource("flow-viewstate-externalredirect.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		ViewFactory vf = ((ViewState) flow.getStateInstance("view")).getViewFactory();
		assertTrue(vf instanceof ActionExecutingViewFactory);
		ActionExecutingViewFactory avf = (ActionExecutingViewFactory) vf;
		assertTrue(avf.getAction() instanceof ExternalRedirectAction);
	}

}
