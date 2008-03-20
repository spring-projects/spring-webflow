package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.view.MvcViewFactoryCreator;

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

		public Action createRenderViewAction(Expression viewId, ResourceLoader viewResourceLoader) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ViewFactory createViewFactory(Expression viewId, ResourceLoader viewResourceLoader) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getViewIdByConvention(String viewStateId) {
			return viewStateId;
		}

	}

	public static class TestConversionService implements ConversionService {

		public Class getClassByAlias(String alias) throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
				throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor getConversionExecutorByTargetAlias(Class sourceClass, String targetAlias)
				throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ConversionExecutor[] getConversionExecutorsForSource(Class sourceClass) throws ConversionException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

}
