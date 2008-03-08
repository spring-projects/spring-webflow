package org.springframework.faces.config;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.faces.webflow.JsfManagedBeanAwareELExpressionParser;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;

public class FacesFlowBuilderServicesBeanDefinitionParserTests extends TestCase {

	private ClassPathXmlApplicationContext context;
	private FlowBuilderServices builderServices;
	private JSFMockHelper jsf = new JSFMockHelper();

	public void setUp() throws Exception {
		jsf.setUp();
		context = new ClassPathXmlApplicationContext("org/springframework/faces/config/flow-builder-services.xml");
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public void testConfigureDefaults() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesDefault");
		assertNotNull(builderServices);
		assertTrue(builderServices.getExpressionParser() instanceof WebFlowELExpressionParser);
		assertTrue(builderServices.getViewFactoryCreator() instanceof JsfViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof FacesConversionService);
	}

	public void testEnableManagedBeans() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesLegacy");
		assertNotNull(builderServices);
		assertTrue(builderServices.getExpressionParser() instanceof JsfManagedBeanAwareELExpressionParser);
		assertTrue(builderServices.getViewFactoryCreator() instanceof JsfViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof FacesConversionService);
	}
}
