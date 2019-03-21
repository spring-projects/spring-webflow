/*
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.binding.expression.spel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.binding.expression.Expression;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

/**
 * Creates a {@link StandardEvaluationContext} enabling the full power of SpEL.
 *
 * @author Rossen Stoyanchev
 * @since 2.4.8
 */
public class StandardEvaluationContextFactory implements EvaluationContextFactory {

	private final List<PropertyAccessor> propertyAccessors;

	private final ConversionService conversionService;

	private final Map<String, Expression> expressionVariables;


	public StandardEvaluationContextFactory(List<PropertyAccessor> propertyAccessors,
			ConversionService conversionService, Map<String, Expression> expressionVariables) {

		this.propertyAccessors = propertyAccessors;
		this.conversionService = conversionService;
		this.expressionVariables = expressionVariables;
	}


	@Override
	public EvaluationContext createContext(Object rootObject) {
		StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
		context.setVariables(getVariableValues(rootObject));
		context.setTypeConverter(new StandardTypeConverter(conversionService));
		context.getPropertyAccessors().addAll(propertyAccessors);
		return context;
	}

	/**
	 * Turn the map of variable-names-to-expressions into a map of variable-names-to-plain-objects
	 * by evaluating each object against the input rootObject.
	 * @param rootObject the Object to evaluate variable expressions against.
	 * @return a mapping between variables names and plain Object's.
	 */
	private Map<String, Object> getVariableValues(Object rootObject) {
		if (expressionVariables == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> variableValues = new HashMap<>(expressionVariables.size());
		for (Map.Entry<String, Expression> var : expressionVariables.entrySet()) {
			variableValues.put(var.getKey(), var.getValue().getValue(rootObject));
		}
		return variableValues;
	}
}
