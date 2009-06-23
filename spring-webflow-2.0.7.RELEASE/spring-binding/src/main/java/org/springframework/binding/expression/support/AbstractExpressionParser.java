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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.util.Assert;

/**
 * An expression parser that parses Ognl expressions.
 * 
 * @author Keith Donald
 */
public abstract class AbstractExpressionParser implements ExpressionParser {

	/**
	 * The expression prefix.
	 */
	private static final String DEFAULT_EXPRESSION_PREFIX = "${";

	/**
	 * The expression suffix.
	 */
	private static final String DEFAULT_EXPRESSION_SUFFIX = "}";

	/**
	 * The marked expression delimiter prefix.
	 */
	private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

	/**
	 * The marked expression delimiter suffix.
	 */
	private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

	/**
	 * Should we allow delimited eval expressions like "${foo.bar}"? If not, evalutable expressions must not be enclosed
	 * in delimiters like ${foo.bar} else an exception is thrown. Only here for compatability reasons, as Web Flow 1.0
	 * allows delimited eval expressions while 2.x does not.
	 */
	private boolean allowDelimitedEvalExpressions;

	/**
	 * Returns the configured expression delimiter prefix. Defaults to "${".
	 */
	public String getExpressionPrefix() {
		return expressionPrefix;
	}

	/**
	 * Sets the expression delimiter prefix.
	 */
	public void setExpressionPrefix(String expressionPrefix) {
		this.expressionPrefix = expressionPrefix;
	}

	/**
	 * Returns the expression delimiter suffix. Defaults to "}".
	 */
	public String getExpressionSuffix() {
		return expressionSuffix;
	}

	/**
	 * Sets the expression delimiter suffix.
	 */
	public void setExpressionSuffix(String expressionSuffix) {
		this.expressionSuffix = expressionSuffix;
	}

	/**
	 * Returns if this parser allows delimited eval expressions like <code>${foo.bar}</code>.
	 */
	public boolean getAllowDelimitedEvalExpressions() {
		return allowDelimitedEvalExpressions;
	}

	/**
	 * Sets if this parser allows eval expressions like ${foo.bar}.
	 */
	public void setAllowDelimitedEvalExpressions(boolean allowDelmitedEvalExpressions) {
		this.allowDelimitedEvalExpressions = allowDelmitedEvalExpressions;
	}

	// expression parser

	public Expression parseExpression(String expressionString, ParserContext context) throws ParserException {
		Assert.notNull(expressionString, "The expression string to parse is required");
		if (context == null) {
			context = NullParserContext.INSTANCE;
		}
		if (context.isTemplate()) {
			return parseTemplate(expressionString, context);
		} else {
			if (expressionString.startsWith(getExpressionPrefix()) && expressionString.endsWith(getExpressionSuffix())) {
				if (!allowDelimitedEvalExpressions) {
					throw new ParserException(
							expressionString,
							"The expression '"
									+ expressionString
									+ "' being parsed is expected be a standard OGNL expression. Do not attempt to enclose such expression strings in ${} delimiters--this is redundant. If you need to parse a template that mixes literal text with evaluatable blocks, set the 'template' parser context attribute to true.",
							null);
				} else {
					int lastIndex = expressionString.length() - getExpressionSuffix().length();
					String ognlExpression = expressionString.substring(getExpressionPrefix().length(), lastIndex);
					return doParseExpression(ognlExpression, context);
				}
			} else {
				return doParseExpression(expressionString, context);
			}
		}
	}

	private Expression parseTemplate(String expressionString, ParserContext context) throws ParserException {
		Assert.notNull(expressionString, "The expression string to parse is required");
		if (expressionString.length() == 0) {
			return parseEmptyExpressionString(context);
		}
		Expression[] expressions = parseExpressions(expressionString, context);
		if (expressions.length == 1) {
			return expressions[0];
		} else {
			return new CompositeStringExpression(expressions);
		}
	}

	// helper methods

	/**
	 * Helper that handles a empty expression string.
	 */
	private Expression parseEmptyExpressionString(ParserContext context) {
		if (allowDelimitedEvalExpressions) {
			// let the parser handle it
			return doParseExpression("", context);
		} else {
			// return a literal expression containing the empty string
			return new LiteralExpression("");
		}
	}

	/**
	 * Helper that parses given expression string using the configured parser. The expression string can contain any
	 * number of expressions all contained in "${...}" markers. For instance: "foo${expr0}bar${expr1}". The static
	 * pieces of text will also be returned as Expressions that just return that static piece of text. As a result,
	 * evaluating all returned expressions and concatenating the results produces the complete evaluated string.
	 * @param expressionString the expression string
	 * @return the parsed expressions
	 * @throws ParserException when the expressions cannot be parsed
	 */
	private Expression[] parseExpressions(String expressionString, ParserContext context) throws ParserException {
		List expressions = new LinkedList();
		int startIdx = 0;
		while (startIdx < expressionString.length()) {
			int prefixIndex = expressionString.indexOf(getExpressionPrefix(), startIdx);
			if (prefixIndex >= startIdx) {
				// a inner expression was found - this is a composite
				if (prefixIndex > startIdx) {
					expressions.add(new LiteralExpression(expressionString.substring(startIdx, prefixIndex)));
					startIdx = prefixIndex;
				}
				int nextPrefixIndex = expressionString.indexOf(getExpressionPrefix(), prefixIndex
						+ getExpressionPrefix().length());
				int suffixIndex;
				if (nextPrefixIndex == -1) {
					// this is the last expression in the expression string
					suffixIndex = expressionString.lastIndexOf(getExpressionSuffix());
				} else {
					// another expression exists after this one in the expression string
					suffixIndex = expressionString.lastIndexOf(getExpressionSuffix(), nextPrefixIndex);
				}
				if (suffixIndex < (prefixIndex + getExpressionPrefix().length())) {
					throw new ParserException(expressionString, "No ending suffix '" + getExpressionSuffix()
							+ "' for expression starting at character " + prefixIndex + ": "
							+ expressionString.substring(prefixIndex), null);
				} else if (suffixIndex == prefixIndex + getExpressionPrefix().length()) {
					throw new ParserException(expressionString, "No expression defined within delimiter '"
							+ getExpressionPrefix() + getExpressionSuffix() + "' at character " + prefixIndex, null);
				} else {
					String expr = expressionString.substring(prefixIndex + getExpressionPrefix().length(), suffixIndex);
					expressions.add(doParseExpression(expr, context));
					startIdx = suffixIndex + 1;
				}
			} else {
				if (startIdx == 0) {
					// treat the entire string as one expression
					if (allowDelimitedEvalExpressions) {
						expressions.add(doParseExpression(expressionString, context));
					} else {
						// treat entire string as a literal
						expressions.add(new LiteralExpression(expressionString));
					}
				} else {
					// no more ${expressions} found in string, add rest as static text
					expressions.add(new LiteralExpression(expressionString.substring(startIdx)));
				}
				startIdx = expressionString.length();
			}
		}
		return (Expression[]) expressions.toArray(new Expression[expressions.size()]);
	}

	protected Map parseVariableExpressions(ExpressionVariable[] variables) throws ParserException {
		if (variables == null || variables.length == 0) {
			return null;
		}
		Map variableExpressions = new HashMap(variables.length, 1);
		for (int i = 0; i < variables.length; i++) {
			ExpressionVariable var = variables[i];
			variableExpressions.put(var.getName(), parseExpression(var.getValueExpression(), var.getParserContext()));
		}
		return variableExpressions;
	}

	protected abstract Expression doParseExpression(String expressionString, ParserContext context)
			throws ParserException;

}