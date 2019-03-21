/*
 * Copyright 2004-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.config;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.faces.webflow.FacesSpringELExpressionParser;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.validation.BeanValidationHintResolver;
import org.springframework.webflow.validation.ValidationHintResolver;

/**
 * A builder for {@link FlowBuilderServices} instances for use in JSF applications.
 * Designed for programmatic use in {@code @Bean} factory methods. For XML
 * configuration consider using the {@code webflow-config} and {@code faces-config}
 * XML namespaces.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class FlowBuilderServicesBuilder {

	private boolean enableManagedBeans = false;

	private ConversionService conversionService = new FacesConversionService();

	private ExpressionParser expressionParser;

	private ViewFactoryCreator viewFactoryCreator = new JsfViewFactoryCreator();

	private Validator validator;

	private ValidationHintResolver validationHintResolver;

	private boolean enableDevelopmentMode;


	/**
	 * Whether to enable access to JSF-managed beans from EL expressions.
	 * When this attribute is set to true, a special EL expression parser will be registered.
	 * @param enableManagedBeans whether to enable JSF managed bean resolution
	 */
	public FlowBuilderServicesBuilder setEnableManagedBeans(boolean enableManagedBeans) {
		this.enableManagedBeans = enableManagedBeans;
		return this;
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
	 * By default an {@link JsfViewFactoryCreator} instance is used.
	 * @param viewFactoryCreator the ViewFactory creator to use
	 */
	public FlowBuilderServicesBuilder setViewFactoryCreator(ViewFactoryCreator viewFactoryCreator) {
		this.viewFactoryCreator = viewFactoryCreator;
		return this;
	}

	/**
	 * Set the {@link Validator} to use for validating a model declared on a view state.
	 * By default no validator is set.
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
			Assert.isTrue(!this.enableManagedBeans,
					"Do not specify a custom expression-parser when enable-managed-beans is true");
			return this.expressionParser;
		}
		else {
			return (this.enableManagedBeans ?
					new FacesSpringELExpressionParser(new SpelExpressionParser(), this.conversionService) :
					new WebFlowSpringELExpressionParser(new SpelExpressionParser(), this.conversionService));
		}
	}

}