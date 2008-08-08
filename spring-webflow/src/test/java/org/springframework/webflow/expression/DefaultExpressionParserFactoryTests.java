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
package org.springframework.webflow.expression;

import java.util.Calendar;

import junit.framework.TestCase;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.webflow.TestBean;

/**
 * Unit tests for {@link DefaultExpressionParserFactory}.
 */
public class DefaultExpressionParserFactoryTests extends TestCase {

	public void testGetDefaultExpressionParser() {
		ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser();
		assertNotNull(parser);
	}

	public void testGetDefaultExpressionParserConversionService() {
		DefaultConversionService conversionService = new DefaultConversionService();
		ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser(conversionService);
		Expression exp = parser.parseExpression("datum3", new FluentParserContext().expectResult(String.class));
		TestBean context = new TestBean();
		Calendar cal = Calendar.getInstance();
		cal.set(2008, 1, 1);
		exp.setValue(context, cal.getTime());
		String string = (String) exp.getValue(context);
		assertEquals(null, string);
	}
}
