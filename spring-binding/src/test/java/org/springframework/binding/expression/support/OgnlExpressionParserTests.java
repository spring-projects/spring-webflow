/*
 * Copyright 2002-2007 the original author or authors.
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

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ParserException;

/**
 * Unit tests for {@link org.springframework.binding.expression.support.OgnlExpressionParser}.
 */
public class OgnlExpressionParserTests extends TestCase {

	private OgnlExpressionParser parser = new OgnlExpressionParser();

	private TestBean bean = new TestBean();
	
	public void testParseSimpleDelimited() {
		String exp = "${flag}";
		Expression e = parser.parseExpression(exp);
		assertNotNull(e);
		Boolean b = (Boolean)e.evaluate(bean, null);
		assertFalse(b.booleanValue());
	}
	
	public void testParseSimple() {
		String exp = "flag";
		Expression e = parser.parseExpression(exp);
		assertNotNull(e);
		Boolean b = (Boolean)e.evaluate(bean, null);
		assertFalse(b.booleanValue());
	}
	
	public void testParseNull() {
		Expression e = parser.parseExpression(null);
		assertNotNull(e);
		assertNull(e.evaluate(bean, null));
	}
	
	public void testParseEmpty() {
		Expression e = parser.parseExpression("");
		assertNotNull(e);
		assertEquals("", e.evaluate(bean, null));
	}

	public void testParseComposite() {
		String exp = "hello ${flag} ${flag} ${flag}";
		Expression e = parser.parseExpression(exp);
		assertNotNull(e);
		String str = (String)e.evaluate(bean, null);
		assertEquals("hello false false false", str);
	}

	public void testEnclosedCompositeNotSupported() {
		String exp = "${hello ${flag} ${flag} ${flag}}";
		try {
			parser.parseExpression(exp);
			fail("Should've failed - not intended use");
		}
		catch (ParserException e) {
		}
	}

	public void testSyntaxError1() {
		try {
			String exp = "hello ${flag} ${abcd defg";
			parser.parseExpression(exp);
			fail("Should've failed - not intended use");
		}
		catch (ParserException e) {
		}
	}
	
	public void testSyntaxError2() {
		try {
			String exp = "hello ${flag} ${}";
			parser.parseExpression(exp);
			fail("Should've failed - not intended use");
		}
		catch (ParserException e) {
		}
	}

	public void testIsDelimitedExpression() {
		assertTrue(parser.isDelimitedExpression("${foo}"));
		assertTrue(parser.isDelimitedExpression("${foo ${foo}}"));
		assertTrue(parser.isDelimitedExpression("foo ${bar}"));
	}
	
	public void testIsNotDelimitedExpression() {
		assertFalse(parser.isDelimitedExpression("foo"));
		assertFalse(parser.isDelimitedExpression("foo ${"));
		assertFalse(parser.isDelimitedExpression("$foo}"));
		assertFalse(parser.isDelimitedExpression("foo ${}"));
	}
}