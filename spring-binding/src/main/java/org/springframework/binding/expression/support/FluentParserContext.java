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
package org.springframework.binding.expression.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;

/**
 * Default implementation of the ParserContext interface that has a fluent API for building parser context attributes.
 * @author Keith Donald
 */
public class FluentParserContext implements ParserContext {

	private Class evaluationContextType;

	private Class evaluationResultType;

	private List expressionVariables;

	private boolean template;

	/**
	 * Create a new parser context, initially with all context attributes as null. Post construction, call one or more
	 * of the fluent builder methods to configure this context.
	 * @see #evaluate(Class)
	 * @see #expectResult(Class)
	 * @see #variable(ExpressionVariable)
	 * @see #template()
	 */
	public FluentParserContext() {
		init();
	}

	public Class getEvaluationContextType() {
		return evaluationContextType;
	}

	public Class getExpectedEvaluationResultType() {
		return evaluationResultType;
	}

	public ExpressionVariable[] getExpressionVariables() {
		return (ExpressionVariable[]) expressionVariables.toArray(new ExpressionVariable[expressionVariables.size()]);
	}

	public boolean isTemplate() {
		return template;
	}

	/**
	 * Configure the evaluationContextType attribute with the value provided.
	 * @param contextType the type of context object the parsed expression will evaluate in
	 * @return this
	 */
	public FluentParserContext evaluate(Class contextType) {
		evaluationContextType = contextType;
		return this;
	}

	/**
	 * Configure the expectedEvaluationResult attribute with the value provided.
	 * @param resultType the type of result object the parsed expression should return when evaluated
	 * @return this
	 */
	public FluentParserContext expectResult(Class resultType) {
		evaluationResultType = resultType;
		return this;
	}

	/**
	 * Add an expression variable that can be referenced by the expression.
	 * @param variable the expression variable
	 * @return this
	 */
	public FluentParserContext variable(ExpressionVariable variable) {
		expressionVariables.add(variable);
		return this;
	}

	/**
	 * Add an array of expression variables that can be referenced by the expression.
	 * @param variables the expression variables
	 * @return this
	 */
	public FluentParserContext variables(ExpressionVariable[] variables) {
		expressionVariables.addAll(Arrays.asList(variables));
		return this;
	}

	/**
	 * Sets a flag indicating the expression to parse is a template.
	 * @return this
	 */
	public FluentParserContext template() {
		template = true;
		return this;
	}

	private void init() {
		expressionVariables = new ArrayList();
	}
}
