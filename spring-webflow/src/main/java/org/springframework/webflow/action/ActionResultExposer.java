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
	 * Creates a action result exposer
	 * @param nameExpression the result name
	 * @param resultScope the result scope
	 */
	public ActionResultExposer(Expression nameExpression, ScopeType resultScope) {
		Assert.notNull(nameExpression, "The result name is required");
		this.nameExpression = nameExpression;
		this.resultScope = resultScope;
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
	 * Expose given bean method return value in given flow execution request context.
	 * @param result the return value
	 * @param context the request context
	 */
	public void exposeResult(Object result, RequestContext context) {
		if (resultScope != null) {
			MutableAttributeMap scopeMap = resultScope.getScope(context);
			nameExpression.setValue(scopeMap, result);
		} else {
			nameExpression.setValue(context, result);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("resultName", nameExpression).append("resultScope", resultScope)
				.toString();
	}
}