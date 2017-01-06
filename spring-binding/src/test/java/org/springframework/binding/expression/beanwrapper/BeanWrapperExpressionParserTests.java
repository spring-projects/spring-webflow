/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.binding.expression.beanwrapper;

import junit.framework.TestCase;

import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.convert.converters.StringToDate;
import org.springframework.binding.convert.service.GenericConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.binding.expression.support.FluentParserContext;

public class BeanWrapperExpressionParserTests extends TestCase {

	private BeanWrapperExpressionParser parser = new BeanWrapperExpressionParser();

	private TestBean bean = new TestBean();

	public void testParseSimple() {
		String exp = "flag";
		Expression e = parser.parseExpression(exp, null);
		assertNotNull(e);
		Boolean b = (Boolean) e.getValue(bean);
		assertFalse(b);
	}

	public void testParseSimpleAllowDelimited() {
		parser.setAllowDelimitedEvalExpressions(true);
		String exp = "${flag}";
		Expression e = parser.parseExpression(exp, null);
		assertNotNull(e);
		Boolean b = (Boolean) e.getValue(bean);
		assertFalse(b);
	}

	public void testParseSimpleDelimitedNotAllowed() {
		String exp = "${flag}";
		try {
			parser.parseExpression(exp, null);
			fail("should have failed");
		} catch (ParserException e) {
		}
	}

	public void testParseTemplateSimpleLiteral() {
		String exp = "flag";
		Expression e = parser.parseExpression(exp, new FluentParserContext().template());
		assertNotNull(e);
		assertEquals("flag", e.getValue(bean));
	}

	public void testParseTemplateEmpty() {
		Expression e = parser.parseExpression("", new FluentParserContext().template());
		assertNotNull(e);
		assertEquals("", e.getValue(bean));
	}

	public void testParseTemplateComposite() {
		String exp = "hello ${flag} ${flag} ${flag}";
		Expression e = parser.parseExpression(exp, new FluentParserContext().template());
		assertNotNull(e);
		String str = (String) e.getValue(bean);
		assertEquals("hello false false false", str);
	}

	public void testTemplateEnclosedCompositeNotSupported() {
		String exp = "${hello ${flag} ${flag} ${flag}}";
		try {
			parser.parseExpression(exp, new FluentParserContext().template());
			fail("Should've failed - not intended use");
		} catch (ParserException e) {
		}
	}

	public void testGetValueType() {
		String exp = "flag";
		Expression e = parser.parseExpression(exp, null);
		assertEquals(boolean.class, e.getValueType(bean));
	}

	public void testGetValueTypeNullCollectionValue() {
		String exp = "list[0]";
		Expression e = parser.parseExpression(exp, null);
		assertEquals(null, e.getValueType(bean));
	}

	public void testSetValueWithCoersion() {
		GenericConversionService cs = (GenericConversionService) parser.getConversionService();
		StringToDate converter = new StringToDate();
		converter.setPattern("yyyy-MM-dd");
		cs.addConverter(converter);
		Expression e = parser.parseExpression("date", null);
		e.setValue(bean, "2008-9-15");
	}

	public void testSetBogusValueWithCoersion() {
		Expression e = parser.parseExpression("date", null);
		try {
			e.setValue(bean, "bogus");
			fail("Should have failed tme");
		} catch (ValueCoercionException ex) {
			assertTrue(ex.getCause() instanceof TypeMismatchException);
		}
	}

}
