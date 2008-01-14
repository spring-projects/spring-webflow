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
package org.springframework.binding.expression;

/**
 * Parses expression strings, returning a configured evaluator instance capable of performing parsed expression
 * evaluation in a thread safe way.
 * 
 * @author Keith Donald
 */
public interface ExpressionParser {

	/**
	 * Parse the provided expression string, returning an expression evaluator capable of evaluating it.
	 * @param expressionString the parseable expression string; cannot be null
	 * @param expressionTargetType the class of target object this expression can successfully evaluate; for example,
	 * <code>Map.class</code> for an expression that is expected to evaluate against Maps.
	 * @param expectedEvaluationResultType the class of object this expression is expected to return or set: for
	 * example, <code>Boolean.class</code> for an expression that is expected to get or set a boolean value. Typically
	 * used to facilitate type conversion by the expression evaluator; for example, if a evaluated expression equates to
	 * a String value 'true', with an expected Boolean result the string value could be converted to a typed Boolean
	 * value (required). If the type of the evaluation result cannot be determined, use Object.class.
	 * @param expressionVariables variables providing aliases for this expression during evaluation (optional).
	 * @return the evaluator for the parsed expression
	 * @throws ParserException an exception occurred during parsing
	 */
	public Expression parseExpression(String expressionString, Class expressionTargetType,
			Class expectedEvaluationResultType, ExpressionVariable[] expressionVariables) throws ParserException;
}