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
 * Parses expression strings, returing a configured evaluator instance capable
 * of performing parsed expression evaluation in a thread safe way.
 * 
 * @author Keith Donald
 */
public interface ExpressionParser {

	/**
	 * Is this expression string delimited in a manner that indicates it is a
	 * parseable expression? For example "${expression}".
	 * @param expressionString the proposed expression string
	 * @return true if yes, false if not
	 */
	public boolean isDelimitedExpression(String expressionString);

	/**
	 * Parse the provided expression string, returning an evaluator capable of
	 * evaluating it against input.
	 * @param expressionString the parseable expression string
	 * @return the evaluator for the parsed expression
	 * @throws ParserException an exception occured during parsing
	 */
	public Expression parseExpression(String expressionString) throws ParserException;

	/**
	 * Parse the provided settable expression string, returning an evaluator
	 * capable of evaluating its value as well as setting its value.
	 * @param expressionString the parseable expression string
	 * @return the evaluator for the parsed expression
	 * @throws ParserException an exception occured during parsing
	 * @throws UnsupportedOperationException this parser does not support
	 * settable expressions
	 */
	public SettableExpression parseSettableExpression(String expressionString) throws ParserException,
			UnsupportedOperationException;

}