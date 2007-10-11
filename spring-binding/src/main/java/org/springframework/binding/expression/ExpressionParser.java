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
	 * Is the provided expression string an "eval" expression: meaning an expression that validates to a dynamic value,
	 * and not a literal expression? "Eval" expressions are normally enclosed in delimiters like #{}, where literal
	 * expressions are not delimited.
	 * @param string the string
	 * @return true if the expression is an eval expression string, false otherwise.
	 */
	public boolean isEvalExpressionString(String string);

	/**
	 * Parse the raw string into an "eval" expression string that when parsed produces a dynamic value when evaluated
	 * against a target object. For example, the raw expression string "person.id" might become #{person.id}. If the
	 * string is already an eval expression string, the string argument is returned unchanged. If the string is an
	 * composite expression string that mixes eval and literal expressions, a parser exception is thrown.
	 * @param string the raw string to be transformed into a parseable eval expression string
	 * @return the eval expression spring
	 * @throws ParserException an exception occurred during parsing
	 */
	public String parseEvalExpressionString(String string) throws ParserException;

	/**
	 * Parse the provided expression string, returning an expression evaluator capable of evaluating it. The expression
	 * string may be a literal expression string like "foo", an eval-expression string like #{foo}, or a
	 * composite-expression string like "foo#{foo}bar#{bar}".
	 * @param expressionString the parseable expression string
	 * @param expressionTargetType the class of target object this expression can successfully evaluate; for example,
	 * <code>Map.class</code> for an expression that is expected to evaluate against Maps.
	 * @param expectedEvaluationResultType the class of object this expression is expected to return or set: for
	 * example, <code>Boolean.class</code> for an expression that is expected to get or set a boolean value.
	 * @param expressionVariables variables providing aliases for this expression during evaluation parsing. Optional.
	 * @return the evaluator for the parsed expression
	 * @throws ParserException an exception occurred during parsing
	 */
	public Expression parseExpression(String expressionString, Class expressionTargetType,
			Class expectedEvaluationResultType, ExpressionVariable[] expressionVariables) throws ParserException;
}