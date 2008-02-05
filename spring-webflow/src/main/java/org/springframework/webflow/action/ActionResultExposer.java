/*
 * Copyright 2004-2007 the original author or authors.
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
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * Specifies how an action result value should be exposed to an executing flow. The return value is exposed as an
 * attribute in a configured scope.
 * 
 * @see EvaluateAction
 * @see AbstractBeanInvokingAction
 * 
 * @author Keith Donald
 */
public class ActionResultExposer implements Serializable {

	/**
	 * The name of the attribute to index the return value with.
	 */
	private Expression nameExpression;

	/**
	 * The scope of the attribute indexing the return value.
	 */
	private ScopeType resultScope;

	/**
	 * The desired type to expose the result as
	 */
	private Class desiredResultType;

	/**
	 * The {@link ConversionService} to use to convert the result to the desired type
	 */
	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * Creates a action result exposer
	 * @param nameExpression the result name
	 * @param resultScope the result scope
	 * @param desiredResultType the desired result type
	 */
	public ActionResultExposer(Expression nameExpression, ScopeType resultScope, Class desiredResultType) {
		Assert.notNull(nameExpression, "The result name is required");
		this.nameExpression = nameExpression;
		this.resultScope = resultScope;
		this.desiredResultType = desiredResultType;
	}

	/**
	 * Returns name of the attribute to index the return value with.
	 */
	public Expression getNameExpression() {
		return nameExpression;
	}

	/**
	 * Returns the scope the attribute indexing the return value.
	 */
	public ScopeType getResultScope() {
		return resultScope;
	}

	/**
	 * Returns the desired result type to be exposed
	 */
	public Class getDesiredResultType() {
		return desiredResultType;
	}

	/**
	 * Expose given bean method return value in given flow execution request context.
	 * @param result the return value
	 * @param context the request context
	 */
	public void exposeResult(Object result, RequestContext context) {
		if (resultScope != null) {
			MutableAttributeMap scopeMap = resultScope.getScope(context);
			nameExpression.setValue(scopeMap, applyTypeConversion(result, desiredResultType));
		} else {
			nameExpression.setValue(context, applyTypeConversion(result, desiredResultType));
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("resultName", nameExpression).append("resultScope", resultScope)
				.toString();
	}

	/**
	 * Apply type conversion on the supplied value
	 * 
	 * @param value the raw value to be converted
	 * @param targetType the target type for the conversion
	 * @return the converted result
	 */
	protected Object applyTypeConversion(Object value, Class targetType) {
		if (value == null || targetType == null) {
			return value;
		}
		return conversionService.getConversionExecutor(value.getClass(), targetType).execute(value);
	}
}