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
package org.springframework.webflow.engine.model;

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
