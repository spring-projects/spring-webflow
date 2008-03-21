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
package org.springframework.webflow.engine.model;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Model support for evaluate actions.
 * <p>
 * Evaluates an expression against the flow request context.
 * 
 * @author Scott Andrews
 */
public class EvaluateModel extends AbstractActionModel {
	private String expression;
	private String result;
	private String resultType;

	/**
	 * Create an evaluate action model
	 * @param expression the expression to evaluate
	 */
	public EvaluateModel(String expression) {
		setExpression(expression);
	}

	/**
	 * Create an evaluate action model
	 * @param expression the expression to evaluate
	 * @param result where to store the result of the expressions
	 */
	public EvaluateModel(String expression, String result) {
		setExpression(expression);
		setResult(result);
	}

	/**
	 * Create an evaluate action model
	 * @param expression the expression to evaluate
	 * @param result where to store the result of the expressions
	 * @param resultType the type of the result
	 */
	public EvaluateModel(String expression, String result, String resultType) {
		setExpression(expression);
		setResult(result);
		setResultType(resultType);
	}

	/**
	 * Merge properties
	 * @param model the evaluate action to merge into this evaluate
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			EvaluateModel evaluate = (EvaluateModel) model;
			setResult(merge(getResult(), evaluate.getResult()));
			setResultType(merge(getResultType(), evaluate.getResultType()));
		}
	}

	/**
	 * Tests if the model is able to be merged with this evaluate action
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof EvaluateModel)) {
			return false;
		}
		EvaluateModel evaluate = (EvaluateModel) model;
		return ObjectUtils.nullSafeEquals(getExpression(), evaluate.getExpression());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof EvaluateModel)) {
			return false;
		}
		EvaluateModel evaluate = (EvaluateModel) obj;
		if (evaluate == null) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getExpression(), evaluate.getExpression())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getResult(), evaluate.getResult())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getResultType(), evaluate.getResultType())) {
			return false;
		} else {
			return true;
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getExpression()) * 27 + ObjectUtils.nullSafeHashCode(getResult()) * 27
				+ ObjectUtils.nullSafeHashCode(getResultType()) * 27;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		if (StringUtils.hasText(expression)) {
			this.expression = expression;
		} else {
			this.expression = null;
		}
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		if (StringUtils.hasText(result)) {
			this.result = result;
		} else {
			this.result = null;
		}
	}

	/**
	 * @return the result type
	 */
	public String getResultType() {
		return resultType;
	}

	/**
	 * @param resultType the result type to set
	 */
	public void setResultType(String resultType) {
		if (StringUtils.hasText(resultType)) {
			this.resultType = resultType;
		} else {
			this.resultType = null;
		}
	}
}
