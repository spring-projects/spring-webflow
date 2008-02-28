package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
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

	public void testBuildFlowWithStartStateElement() {
		ClassPathResource resource = new ClassPathResource("flow-startstate-element.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, new MockFlowBuilderContext("flow"));
		Flow flow = assembler.assembleFlow();
		assertEquals("flow", flow.getId());
		assertEquals("end", flow.getStartState().getId());
	}

}
