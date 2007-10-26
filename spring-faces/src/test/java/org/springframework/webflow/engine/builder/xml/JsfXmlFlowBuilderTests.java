package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class JsfXmlFlowBuilderTests extends TestCase {

	private XmlFlowBuilder builder;
	private JSFMockHelper jsf = new JSFMockHelper();

	protected void setUp() throws Exception {
		jsf.setUp();
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("bean", new Object());
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public final void testBuildJsfFlow() {
		ClassPathResource resource = new ClassPathResource("jsf-flow.xml", getClass());
		builder = new XmlFlowBuilder(resource);
		MockFlowBuilderContext builderContext = new MockFlowBuilderContext("jsf-flow");
		builderContext.getFlowBuilderServices().setViewFactoryCreator(new JsfViewFactoryCreator());
		FlowAssembler assembler = new FlowAssembler(builder, builderContext);
		Flow flow = assembler.assembleFlow();
		assertEquals("jsf-flow", flow.getId());
		assertEquals("viewState1", flow.getStartState().getId());
	}
}
