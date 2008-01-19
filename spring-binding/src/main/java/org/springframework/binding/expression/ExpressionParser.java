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
	 * Is the provided string an explicitly delimited expression this parser knows how to parse? For example, this
	 * method may return true if the string provided is enclosed in "${}". It may also return true if the string
	 * provided is a mix of literal text and delimited expression syntax, for example "hello world ${name}!" The exact
	 * semantics are determined by the parser implementation.
	 * @param string the string
	 * @return true if the string is a delimited expression, false otherwise.
	 */
	public boolean hasDelimitedExpression(String string);

	/**
	 * Parse the provided expression string, returning an expression evaluator capable of evaluating it.
	 * @param expressionString the parseable expression string; cannot be null (required)
	 * @param context a context used to set attributes that influence expression parsing routine (optional)
	 * @return the evaluator for the parsed expression
	 * @throws ParserException an exception occurred during parsing
	 */
	public Expression parseExpression(String expressionString, ParserContext context) throws ParserException;

}