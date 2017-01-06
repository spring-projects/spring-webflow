/*
 * Copyright 2004-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.config;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.validation.BeanValidationHintResolver;
import org.springframework.webflow.validation.ValidationHintResolver;

/**
 * A builder for creating {@link FlowBuilderServices} instances designed for programmatic
 * use in {@code @Bean} factory methods. For XML configuration consider using the
 * {@code webflow-config} XML namespace.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class FlowBuilderServicesBuilder {

	private ConversionService conversionService = new DefaultConversionService();

	private ExpressionParser expressionParser;

	private ViewFactoryCreator viewFactoryCreator;

	private Validator validator;

	private ValidationHintResolver validationHintResolver;

	private boolean enableDevelopmentMode;


	public FlowBuilderServicesBuilder() {
		this.viewFactoryCreator = new MvcViewFactoryCreator();
	}

	/**
	 * Create a new instance with the given ApplicationContext.
	 * @deprecated as of 2.5 an ApplicationContext is no longer required
	 */
	public FlowBuilderServicesBuilder(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "applicationContext is required");
		this.viewFactoryCreator = new MvcViewFactoryCreator();
	}


	/**
	 * Set the {@link ConversionService} to use.
	 * By default a {@link DefaultConversionService} instance is used.
	 * @param conversionService the conversion service
	 */
	public FlowBuilderServicesBuilder setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
		return this;
	}

	/**
	 * Set the {@link ExpressionParser} to use.
	 * By default a {@link WebFlowSpringELExpressionParser} with SpEL expressions is used.
	 * @param expressionParser the expression parser to use
	 */
	public FlowBuilderServicesBuilder setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
		return this;
	}

	/**
	 * Set a custom {@link ViewFactoryCreator} to use for rendering.
	 * By default an {@link MvcViewFactoryCreator} instance is used.
	 * @param viewFactoryCreator the ViewFactory creator to use
	 */
	public FlowBuilderServicesBuilder setViewFactoryCreator(ViewFactoryCreator viewFactoryCreator) {
		this.viewFactoryCreator = viewFactoryCreator;
		return this;
	}

	/**
	 * Set the {@link Validator} to use for validating a model declared on a view state.
	 * By default bean validation (JSR-303) is enabled if a bean validation provider is
	 * present on the classpath.
	 * @param validator the validator to use
	 */
	public FlowBuilderServicesBuilder setValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	/**
	 * The {@link ValidationHintResolver} to use to resolve validation hints such as bean validation groups.
	 * By default a {@link BeanValidationHintResolver} is used.
	 * @param resolver the resolver to use
	 */
	public FlowBuilderServicesBuilder setValidationHintResolver(ValidationHintResolver resolver) {
		this.validationHintResolver = resolver;
		return this;
	}

	/**
	 * Put all flows in development mode. When set to {@code true}, changes to a flow
	 * definition are auto-detected and result in a flow refresh.
	 * By default this is set to {@code false}
	 * @param enableDevelopmentMode whether to enable development mode
	 */
	public FlowBuilderServicesBuilder setDevelopmentMode(boolean enableDevelopmentMode) {
		this.enableDevelopmentMode = enableDevelopmentMode;
		return this;
	}

	/**
	 * Create and return a {@link FlowBuilderServices} instance.
	 */
	public FlowBuilderServices build() {
		FlowBuilderServices flowBuilderServices = new FlowBuilderServices();
		flowBuilderServices.setConversionService(this.conversionService);
		flowBuilderServices.setExpressionParser(getExpressionParser());
		flowBuilderServices.setViewFactoryCreator(this.viewFactoryCreator);
		flowBuilderServices.setValidator(this.validator);
		flowBuilderServices.setValidationHintResolver(this.validationHintResolver);
		flowBuilderServices.setDevelopment(this.enableDevelopmentMode);
		return flowBuilderServices;
	}

	private ExpressionParser getExpressionParser() {
		if (this.expressionParser != null) {
			return this.expressionParser;
		}
		else {
			return new WebFlowSpringELExpressionParser(new SpelExpressionParser(), this.conversionService);
		}
	}

}
