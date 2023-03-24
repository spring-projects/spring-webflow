/*
 * Copyright 2004-2023 the original author or authors.
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
package org.springframework.faces.webflow;

import jakarta.faces.lifecycle.Lifecycle;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.validation.ValidationHintResolver;

/**
 * A {@link ViewFactoryCreator} implementation for creating instances of a JSF-specific {@link ViewFactory}.
 *
 * @author Jeremy Grelle
 */
public class JsfViewFactoryCreator implements ViewFactoryCreator {

	private static final String FACELETS_EXTENSION = ".xhtml";

	private Lifecycle lifecycle;

	public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration,
			Validator validator, ValidationHintResolver resolver) {
		return new JsfViewFactory(viewIdExpression, getLifecycle());
	}

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId + FACELETS_EXTENSION;
	}

	private Lifecycle getLifecycle() {
		if (this.lifecycle == null) {
			this.lifecycle = FlowLifecycle.newInstance();
		}
		return this.lifecycle;
	}

}
