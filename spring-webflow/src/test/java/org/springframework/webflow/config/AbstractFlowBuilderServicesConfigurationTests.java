package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.validation.ValidationHintResolver;

public abstract class AbstractFlowBuilderServicesConfigurationTests {

	protected ApplicationContext context;

	protected FlowBuilderServices builderServices;

	@BeforeEach
	public void setUp() {
		context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();

	@Test
	public void testFlowBuilderServicesDefaultConfig() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesDefault");
		assertNotNull(builderServices);
		assertTrue(builderServices.getExpressionParser() instanceof SpringELExpressionParser);
		assertTrue(builderServices.getViewFactoryCreator() instanceof MvcViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof DefaultConversionService);
		assertNull(builderServices.getValidator());
		assertFalse(builderServices.getDevelopment());
	}

	@Test
	public void testFlowBuilderServicesAllCustomized() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesAllCustom");
		assertNotNull(builderServices);
		assertTrue(builderServices.getExpressionParser() instanceof SpringELExpressionParser);
		assertTrue(builderServices.getViewFactoryCreator() instanceof TestViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof TestConversionService);
		assertTrue(builderServices.getValidator() instanceof EmptySpringValidator);
		assertTrue(builderServices.getValidationHintResolver() instanceof MyBeanValidationHintResolver);
		assertTrue(builderServices.getDevelopment());
	}

	@Test
	public void testFlowBuilderServicesConversionServiceCustomized() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesConversionServiceCustom");
		assertNotNull(builderServices);
		assertTrue(builderServices.getConversionService() instanceof TestConversionService);
		assertTrue(builderServices.getExpressionParser() instanceof SpringELExpressionParser);
		assertTrue(((SpringELExpressionParser) builderServices.getExpressionParser()).getConversionService() instanceof TestConversionService);
		assertTrue(builderServices.getViewFactoryCreator() instanceof MvcViewFactoryCreator);
		assertNull(builderServices.getValidator());
		assertNull(builderServices.getValidationHintResolver());
		assertFalse(builderServices.getDevelopment());
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

		public Set<ConversionExecutor> getConversionExecutors(Class<?> sourceClass) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Class<?> getClassForAlias(String alias) throws ConversionExecutionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public org.springframework.core.convert.ConversionService getDelegateConversionService() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

}
