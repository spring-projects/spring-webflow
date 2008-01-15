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
package org.springframework.webflow.core.expression;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserException;

/**
 * Static helper factory that creates instances of the default expression parser used by Spring Web Flow when requested.
 * Marked final with a private constructor to prevent subclassing.
 * <p>
 * The default is an OGNL based expression parser. Also asserts that OGNL is in the classpath the first time the parser
 * is used.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public final class DefaultExpressionParserFactory {

	/**
	 * The singleton instance of the default expression parser.
	 */
	private static ExpressionParser INSTANCE;

	// static factory - not instantiable
	private DefaultExpressionParserFactory() {
	}

	/**
	 * Returns the default expression parser. The returned expression parser is a thread-safe object.
	 * @return the expression parser
	 */
	public static synchronized ExpressionParser getExpressionParser() {
		// return a wrapper that will lazily load the default expression parser
		// this prevents the default OGNL-based parser from being initialized until it is actually used
		// which allows OGNL to be an optional dependency if the expression parser wrapper is replaced and never used
		return new ExpressionParser() {
			public boolean isEvalExpressionString(String string) {
				return getDefaultExpressionParser().isEvalExpressionString(string);
			}

			public Expression parseExpression(String expressionString, Class expressionTargetType,
					Class expectedEvaluationResultType, ExpressionVariable[] expressionVariables)
					throws ParserException {
				return getDefaultExpressionParser().parseExpression(expressionString, expressionTargetType,
						expectedEvaluationResultType, expressionVariables);
			}
		};
	}

	/**
	 * Returns the default expression parser, creating it if necessary.
	 * @return the default expression parser
	 */
	private static synchronized ExpressionParser getDefaultExpressionParser() {
		if (INSTANCE == null) {
			INSTANCE = createDefaultExpressionParser();
		}
		return INSTANCE;
	}

	/**
	 * Create the default expression parser.
	 * @return the default expression parser
	 */
	private static ExpressionParser createDefaultExpressionParser() throws IllegalStateException {
		try {
			Class.forName("ognl.Ognl");
			return new WebFlowOgnlExpressionParser();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(
					"Unable to load the default expression parser: OGNL could not be found in the classpath.  "
							+ "Please add OGNL 2.x to your classpath or set the default ExpressionParser instance to something that is in the classpath.  "
							+ "Details: " + e.getMessage());
		} catch (NoClassDefFoundError e) {
			throw new IllegalStateException(
					"Unable to construct the default expression parser: ognl.Ognl could not be instantiated.  "
							+ "Please add OGNL 2.x to your classpath or set the default ExpressionParser instance to something that is in the classpath.  "
							+ "Details: " + e);
		}
	}
}