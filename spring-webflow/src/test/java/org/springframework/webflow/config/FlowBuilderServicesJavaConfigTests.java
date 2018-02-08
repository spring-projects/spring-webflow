package org.springframework.webflow.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

public class FlowBuilderServicesJavaConfigTests extends AbstractFlowBuilderServicesConfigurationTests {

	protected ApplicationContext initApplicationContext() {
		return new AnnotationConfigApplicationContext(WebFlowConfig.class);
	}


	static class WebFlowConfig extends AbstractFlowConfiguration {

		@Bean
		public FlowBuilderServices flowBuilderServicesDefault() {
			return getFlowBuilderServicesBuilder().build();
		}

		@Bean
		public FlowBuilderServices flowBuilderServicesAllCustom() {
			return getFlowBuilderServicesBuilder()
					.setExpressionParser(customExpressionParser())
					.setViewFactoryCreator(customViewFactoryCreator())
					.setConversionService(customConversionService())
					.setValidator(customValidator())
					.setValidationHintResolver(customValidationHintResolver())
					.setDevelopmentMode(true)
					.build();
		}

		@Bean
		public FlowBuilderServices flowBuilderServicesConversionServiceCustom() {
			return getFlowBuilderServicesBuilder()
					.setConversionService(customConversionService())
					.build();
		}

		@Bean
		public WebFlowSpringELExpressionParser customExpressionParser() {
			return new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		}

		@Bean
		public TestViewFactoryCreator customViewFactoryCreator() {
			return new TestViewFactoryCreator();
		}

		@Bean
		public TestConversionService customConversionService() {
			return new TestConversionService();
		}

		@Bean
		public EmptySpringValidator customValidator() {
			return new EmptySpringValidator();
		}

		@Bean
		public MyBeanValidationHintResolver customValidationHintResolver() {
			return new MyBeanValidationHintResolver();
		}

	}

}
