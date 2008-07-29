package org.springframework.webflow.config;

import java.util.Set;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;

public class FlowBuilderServicesBeanDefinitionParserTests extends TestCase {

	private ClassPathXmlApplicationContext context;
	private FlowBuilderServices builderServices;

	public void setUp() {
		context = new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-builder-services.xml");
	}

	public void testFlowBuilderServicesDefaultConfig() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesDefault");
		assertNotNull(builderServices);
		assertNotNull(builderServices.getExpressionParser());
		assertTrue(builderServices.getViewFactoryCreator() instanceof MvcViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof DefaultConversionService);
	}

	public void testFlowBuilderServicesCustomized() {
		builderServices = (FlowBuilderServices) context.getBean("flowBuilderServicesCustom");
		assertNotNull(builderServices);
		assertNotNull(builderServices.getExpressionParser());
		assertTrue(builderServices.getViewFactoryCreator() instanceof TestViewFactoryCreator);
		assertTrue(builderServices.getConversionService() instanceof TestConversionService);
	}

	public static class TestViewFactoryCreator implements ViewFactoryCreator {

		public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
				ConversionService conversionService, BinderConfiguration binderConfiguration) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getViewIdByConvention(String viewStateId) {
			return viewStateId;
		}

	}

	public static class TestConversionService implements ConversionService {

		public Object executeConversion(Object source, Class targetClass) throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
				throws ConversionExecutionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutor(String id, Class sourceClass, Class targetClass)
				throws ConversionExecutorNotFoundException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Set getConversionExecutors(Class sourceClass) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Class getClassForAlias(String alias) throws ConversionExecutionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

}