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
package org.springframework.binding.expression.ognl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.CompositeStringExpression;
import org.springframework.binding.expression.support.NullParserContext;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.util.Assert;

/**
 * An expression parser that parses Ognl expressions.
 * 
 * @author Keith Donald
 */
public class OgnlExpressionParser implements ExpressionParser {

	/**
	 * The expression prefix.
	 */
	private static final String DEFAULT_EXPRESSION_PREFIX = "${";

	/**
	 * The expression suffix.
	 */
	private static final String DEFAULT_EXPRESSION_SUFFIX = "}";

	/**
	 * The marked expression delimter prefix.
	 */
	private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

	/**
	 * The marked expression delimiter suffix.
	 */
	private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

	/**
	 * Should we allow undelimited OGNL eval expressions like "foo.bar"? If not, evalutable OGNL expressions must be
	 * enclosed in delimiters like ${foo.bar} else they are treated as literal expressions. Mainly here for
	 * compatability reasons, as Web Flow 1.0 allows undelimited OGNL eval expressions by default.
	 */
	private boolean allowUndelimitedEvalExpressions;

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
	 * Returns if this parser should we allow undelimited OGNL eval expressions like <code>foo.bar</code>.
	 */
	public boolean getAllowUndelimitedEvalExpressions() {
		return allowUndelimitedEvalExpressions;
	}

	/**
	 * Sets if this parser should allow undelimited OGNL eval expressions like "foo.bar"? If not, evalutable OGNL
	 * expressions must be enclosed in delimiters like ${foo.bar}, else they are treated as literal expressions.
	 */
	public void setAllowUndelimitedEvalExpressions(boolean allowUndelmitedEvalExpressions) {
		this.allowUndelimitedEvalExpressions = allowUndelmitedEvalExpressions;
	}

	public static String getDEFAULT_EXPRESSION_PREFIX() {
		return DEFAULT_EXPRESSION_PREFIX;
	}

	public static String getDEFAULT_EXPRESSION_SUFFIX() {
		return DEFAULT_EXPRESSION_SUFFIX;
	}

	/**
	 * Add a property access strategy for the given class.
	 * @param clazz the class that contains properties needing access
	 * @param propertyAccessor the property access strategy
	 */
	public void addPropertyAccessor(Class clazz, PropertyAccessor propertyAccessor) {
		OgnlRuntime.setPropertyAccessor(clazz, propertyAccessor);
	}

	// expression parser

	public Expression parseExpression(String expressionString, ParserContext context) throws ParserException {
		Assert.notNull(expressionString, "The expression string to parse is required");
		Expression[] expressions = parseExpressions(expressionString, context);
		if (expressions.length == 1) {
			return expressions[0];
		} else {
			return new CompositeStringExpression(expressions);
		}
	}

	/**
	 * Is the provided string a template expression this parser can parse? Always returns <code>true</code> if this
	 * OGNL expression parser is configured to <b>not</b> allow undelimited OGNL expressions. If undelimited OGNL
	 * expressions are allowed like "foo.bar", this method only returns true if an explicitly delimited expression is
	 * present in the string like "hello my name is ${name}" or "${foo.bar}".
	 * 
	 * In general, a template expression is either:
	 * <ol>
	 * <li>static literal text like "hello world". In this case, evaluating the expression simply returns the literal
	 * text.
	 * <li>a single eval expression like ${requestParameters.foo}. In this case, evaluating the expression returns the
	 * evaluated value.
	 * <li>a mix of literal text with one or more eval expressions like "hello #{name}". In this case, evaluating the
	 * expression returns a string with the result of #{name} evaluated (often called a composite expression).
	 * </ol>
	 * 
	 * This method and the {@link #getAllowUndelimitedEvalExpressions()} flag primarily exist for compatibility reasons.
	 * The OgnlExpressionParser in SWF 1.0 does not treat literal text like "hello world" as a template expression, but
	 * rather a standard, evaluatable OGNL expression. Therefore, callers expecting standard template evaluation
	 * semantics are expected to work with these literal string values themselves, and not pass those strings to
	 * {@link #parseExpression(String, ParserContext)}.
	 * 
	 * @param string the string
	 * @return true if the string is a template expression, false otherwise.
	 */
	public boolean isTemplateExpression(String string) {
		if (!allowUndelimitedEvalExpressions) {
			// every string provided is a "template" style expression - return true
			return true;
		}
		// only returns true when there is ${} somewhere in the string
		// this is version 1.0 semantics, there for compatability reasons
		int prefixIndex = string.indexOf(getExpressionPrefix());
		if (prefixIndex == -1) {
			return false;
		}
		int suffixIndex = string.indexOf(getExpressionSuffix(), prefixIndex);
		if (suffixIndex == -1) {
			return false;
		} else {
			// make sure there is actually something inside the ${}
			if (suffixIndex == prefixIndex + getExpressionPrefix().length()) {
				return false;
			} else {
				return true;
			}
		}
	}

	// helper methods

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
					expressions.add(new StaticExpression(expressionString.substring(startIdx, prefixIndex)));
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
					if (allowUndelimitedEvalExpressions) {
						expressions.add(doParseExpression(expressionString, context));
					} else {
						// treat entire string as a literal
						expressions.add(new StaticExpression(expressionString));
					}
				} else {
					// no more ${expressions} found in string, add rest as static text
					expressions.add(new StaticExpression(expressionString.substring(startIdx)));
				}
				startIdx = expressionString.length();
			}
		}
		return (Expression[]) expressions.toArray(new Expression[expressions.size()]);
	}

	private Expression doParseExpression(String expressionString, ParserContext context) throws ParserException {
		if (context == null) {
			context = NullParserContext.INSTANCE;
		}
		try {
			return new OgnlExpression(Ognl.parseExpression(expressionString), parseVariableExpressions(context
					.getExpressionVariables()), context.getExpectedEvaluationResultType());
		} catch (OgnlException e) {
			throw new ParserException(expressionString, e);
		}
	}

	private Map parseVariableExpressions(ExpressionVariable[] variables) throws OgnlException {
		if (variables == null || variables.length == 0) {
			return null;
		}
		Map variableExpressions = new HashMap(variables.length, 1);
		for (int i = 0; i < variables.length; i++) {
			ExpressionVariable var = variables[i];
			variableExpressions.put(var.getName(), parseExpression(var.getValueExpression(), null));
		}
		return variableExpressions;
	}
}