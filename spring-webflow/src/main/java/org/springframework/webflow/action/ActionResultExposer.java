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

import java.io.Serializable;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;

/**
 * Specifies how an action result value should be exposed to an executing flow. The return value is exposed as an
 * attribute in a configured scope.
 * 
 * @see EvaluateAction
 * 
 * @author Keith Donald
 */
public class ActionResultExposer implements Serializable {

	/**
	 * The expression to set the result to.
	 */
	private Expression resultExpression;

	/**
	 * The desired type to expose the result as
	 */
	private Class expectedResultType;

	/**
	 * The {@link ConversionService} to use to convert the result to the desired type
	 */
	private ConversionService conversionService;

	/**
	 * Creates a action result exposer
	 * @param resultExpression the result expression
	 * @param expectedResultType the expected result type
	 */
	public ActionResultExposer(Expression resultExpression, Class expectedResultType,
			ConversionService conversionService) {
		Assert.notNull(resultExpression, "The result expression is required");
		if (expectedResultType != null) {
			Assert.notNull(conversionService, "A conversionService is required with an expectedResultType");
		}
		this.resultExpression = resultExpression;
		this.expectedResultType = expectedResultType;
		this.conversionService = conversionService;
	}

	/**
	 * Returns name of the attribute to index the return value with.
	 */
	public Expression getNameExpression() {
		return resultExpression;
	}

	/**
	 * Returns the desired result type to be exposed
	 */
	public Class getExpectedResultType() {
		return expectedResultType;
	}

	/**
	 * Expose given bean method return value in given flow execution request context.
	 * @param result the return value
	 * @param context the request context
	 */
	public void exposeResult(Object result, RequestContext context) {
		resultExpression.setValue(context, applyTypeConversionIfNecessary(result));
	}

	/**
	 * Apply type conversion on the supplied value if necessary.
	 * @param value the raw value to be converted
	 */
	private Object applyTypeConversionIfNecessary(Object value) {
		if (value == null || expectedResultType == null) {
			return value;
		} else {
			return conversionService.getConversionExecutor(value.getClass(), expectedResultType).execute(value);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("result", resultExpression).append("resultType", expectedResultType)
				.toString();
	}
}