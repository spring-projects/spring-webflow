package org.springframework.faces.config;

import org.springframework.binding.convert.ConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

public class FacesFlowBuilderServicesJavaConfigTests extends AbstractFacesFlowBuilderServicesConfigurationTests {


	@Override
	protected ApplicationContext initApplicationContext() {
		return new AnnotationConfigApplicationContext(FacesFlowConfig.class);
	}


	static class FacesFlowConfig extends AbstractFacesFlowConfiguration {

		@Bean
		public FlowBuilderServices flowBuilderServicesDefault() {
			return getFlowBuilderServicesBuilder().build();
		}

		@Bean
		public FlowBuilderServices flowBuilderServicesLegacy() {
			return getFlowBuilderServicesBuilder().setEnableManagedBeans(true).build();
		}

		@Bean
		public FlowBuilderServices flowBuilderServicesAllCustom() {
			return getFlowBuilderServicesBuilder()
					.setExpressionParser(customExpressionParser())
					.setViewFactoryCreator(customViewFactoryCreator())
					.setConversionService(customConversionService())
					.setValidator(new EmptySpringValidator())
					.setValidationHintResolver(new MyBeanValidationHintResolver())
					.setDevelopmentMode(true)
					.build();
		}

		@Bean
		public FlowBuilderServices flowBuilderServicesConversionServiceCustom() {
			return getFlowBuilderServicesBuilder().setConversionService(customConversionService()).build();
		}

		@Bean
		public WebFlowSpringELExpressionParser customExpressionParser() {
			return new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		}

		@Bean
		public ViewFactoryCreator customViewFactoryCreator() {
			return new TestViewFactoryCreator();
		}

		@Bean
		public ConversionService customConversionService() {
			return new TestConversionService();
		}

	}

}
