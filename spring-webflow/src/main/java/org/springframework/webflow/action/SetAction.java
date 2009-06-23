/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.action;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * An action that sets an attribute in a {@link ScopeType scope} when executed. Always returns the "success" event.
 * 
 * @author Keith Donald
 */
public class SetAction extends AbstractAction {

	/**
	 * The expression for setting the scoped attribute value.
	 */
	private Expression nameExpression;

	/**
	 * The expression for resolving the scoped attribute value.
	 */
	private Expression valueExpression;

	/**
	 * The expected value type.
	 */
	private Class expectedType;

	/**
	 * The service to perform the type conversion if the actual value type does not match the expected.
	 */
	private ConversionService conversionService;

	/**
	 * Creates a new set attribute action.
	 * @param nameExpression the name of the property to set (required)
	 * @param valueExpression the expression to obtain the new property value (required)
	 * @param expectedType the expected value type
	 * @param conversionService the service to perform the type conversion if the actual value type does not match the
	 * expected
	 */
	public SetAction(Expression nameExpression, Expression valueExpression, Class expectedType,
			ConversionService conversionService) {
		Assert.notNull(nameExpression, "The name expression is required");
		Assert.notNull(valueExpression, "The value expression is required");
		if (expectedType != null) {
			Assert.notNull(conversionService, "The conversion service is required if the expectedType is provided");
		}
		this.nameExpression = nameExpression;
		this.valueExpression = valueExpression;
		this.expectedType = expectedType;
		this.conversionService = conversionService;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		Object value = valueExpression.getValue(context);
		nameExpression.setValue(context, applyTypeConversionIfNecessary(value));
		return success();
	}

	/**
	 * Apply type conversion on the supplied value if necessary.
	 * @param value the raw value to be converted
	 */
	private Object applyTypeConversionIfNecessary(Object value) {
		if (value == null || expectedType == null) {
			return value;
		} else {
			return conversionService.getConversionExecutor(value.getClass(), expectedType).execute(value);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("name", nameExpression).append("value", valueExpression).append("type",
				expectedType).toString();
	}

}