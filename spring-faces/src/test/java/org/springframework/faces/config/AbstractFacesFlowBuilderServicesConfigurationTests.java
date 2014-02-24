package org.springframework.faces.config;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.faces.webflow.FacesSpringELExpressionParser;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.validation.ValidationHintResolver;

public abstract class AbstractFacesFlowBuilderServicesConfigurationTests extends TestCase {

	protected ApplicationContext context;

	protected FlowBuilderServices builderServices;

	protected final JSFMockHelper jsf = new JSFMockHelper();


	public void setUp() throws Exception {
		this.jsf.setUp();
		this.context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();

	protected void tearDown() throws Exception {
		this.jsf.tearDown();
	}

	public void testConfigureDefaults() {
		this.builderServices = (FlowBuilderServices) this.context.getBean("flowBuilderServicesDefault");
		assertNotNull(this.builderServices);
		assertTrue(this.builderServices.getExpressionParser() instanceof SpringELExpressionParser);
		assertTrue(this.builderServices.getViewFactoryCreator() instanceof JsfViewFactoryCreator);
		assertTrue(this.builderServices.getConversionService() instanceof FacesConversionService);
		assertFalse(this.builderServices.getDevelopment());
	}

	public void testEnableManagedBeans() {
		this.builderServices = (FlowBuilderServices) this.context.getBean("flowBuilderServicesLegacy");
		assertNotNull(this.builderServices);
		assertTrue(this.builderServices.getExpressionParser() instanceof FacesSpringELExpressionParser);
		assertTrue(this.builderServices.getViewFactoryCreator() instanceof JsfViewFactoryCreator);
		assertTrue(this.builderServices.getConversionService() instanceof FacesConversionService);
		assertFalse(this.builderServices.getDevelopment());
	}

	public void testFlowBuilderServicesAllCustomized() {
		this.builderServices = (FlowBuilderServices) this.context.getBean("flowBuilderServicesAllCustom");
		assertNotNull(this.builderServices);
		assertTrue(this.builderServices.getExpressionParser() instanceof WebFlowSpringELExpressionParser);
		assertTrue(this.builderServices.getViewFactoryCreator() instanceof TestViewFactoryCreator);
		assertTrue(this.builderServices.getConversionService() instanceof TestConversionService);
		assertTrue(this.builderServices.getDevelopment());
	}

	public void testFlowBuilderServicesConversionServiceCustomized() {
		this.builderServices = (FlowBuilderServices) this.context.getBean("flowBuilderServicesConversionServiceCustom");
		assertNotNull(this.builderServices);
		assertTrue(this.builderServices.getConversionService() instanceof TestConversionService);
		assertTrue(this.builderServices.getExpressionParser() instanceof WebFlowSpringELExpressionParser);
		assertTrue(((SpringELExpressionParser) this.builderServices.getExpressionParser()).getConversionService() instanceof TestConversionService);
		assertTrue(this.builderServices.getViewFactoryCreator() instanceof JsfViewFactoryCreator);
		assertFalse(this.builderServices.getDevelopment());
	}

	public static class TestViewFactoryCreator implements ViewFactoryCreator {

		public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
				ConversionService conversionService, BinderConfiguration binderConfiguration,
				Validator validator, ValidationHintResolver validationHintResolver) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getViewIdByConvention(String viewStateId) {
			return viewStateId;
		}

	}

	public static class TestConversionService implements ConversionService {

		public Object executeConversion(Object source, Class<?> targetClass) throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Object executeConversion(String converterId, Object source, Class<?> targetClass) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutor(Class<?> sourceClass, Class<?> targetClass)
				throws ConversionExecutionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutor(String id, Class<?> sourceClass, Class<?> targetClass)
				throws ConversionExecutorNotFoundException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Class<?> getClassForAlias(String name) throws ConversionExecutionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public org.springframework.core.convert.ConversionService getDelegateConversionService() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}
	}
}
