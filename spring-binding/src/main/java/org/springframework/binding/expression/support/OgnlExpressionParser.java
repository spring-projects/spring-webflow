/*
 * Copyright 2002-2006 the original author or authors.
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

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.SettableExpression;

/**
 * An expression parser that parses Ognl expressions.
 * 
 * @author Keith Donald
 */
public class OgnlExpressionParser extends AbstractExpressionParser {

	protected Expression doParseExpression(String expressionString) throws ParserException {
		return parseSettableExpression(expressionString);
	}

	public SettableExpression parseSettableExpression(String expressionString) throws ParserException {
		try {
			return new OgnlExpression(Ognl.parseExpression(expressionString));
		}
		catch (OgnlException e) {
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