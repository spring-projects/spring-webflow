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

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.AbstractExpressionParser;
import org.springframework.binding.expression.support.NullParserContext;

/**
 * An expression parser that parses Ognl expressions.
 * 
 * @author Keith Donald
 */
public class OgnlExpressionParser extends AbstractExpressionParser {

	public Expression doParseExpression(String expressionString, ParserContext context) throws ParserException {
		if (context == null) {
			context = NullParserContext.INSTANCE;
		}
		try {
			return new OgnlExpression(Ognl.parseExpression(expressionString), context.getExpressionVariables());
		} catch (OgnlException e) {
			throw new ParserException(expressionString, e);
		}
	}

	/**
	 * Add a property access strategy for the given class.
	 * @param clazz the class that contains properties needing access
	 * @param propertyAccessor the property access strategy
	 */
	public void addPropertyAccessor(Class clazz, PropertyAccessor propertyAccessor) {
		OgnlRuntime.setPropertyAccessor(clazz, propertyAccessor);
	}
}