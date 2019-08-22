/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.expression.spel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.binding.expression.el.ELExpressionParserTests;
import org.springframework.binding.expression.el.TestBean;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * <p>
 * Test cases to verify that {@link SpringELExpressionParser} matches to the functionality of {@link ELExpressionParser}
 * as demonstrated in {@link ELExpressionParserTests}.
 * </p>
 * 
 * @author Rossen Stoyanchev
 */
public class ELExpressionParserCompatibilityTests {

	private SpringELExpressionParser parser = new SpringELExpressionParser(new SpelExpressionParser());

	@Before
	public void setUp() {
		parser.addPropertyAccessor(new SpecialPropertyAccessor());
	}

	@Test
	public void testParseSimpleEvalExpressionNoParserContext() {
		String expressionString = "3 + 4";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals(7, exp.getValue(null)); // Unified EL returns Long
	}

	@Test
	public void testParseNullExpressionString() {
		String expressionString = null;
		try {
			parser.parseExpression(expressionString, null);
			fail("should have thrown iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testParseNull() {
		Expression exp = parser.parseExpression("null", null);
		assertEquals(null, exp.getValue(null));
	}

	@Test
	public void testParseEmptyExpressionString() {
		String expressionString = "";
		try {
			parser.parseExpression(expressionString, null);
			fail("should have thrown iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testParseSimpleEvalExpressionNoEvalContextWithTypeCoersion() {
		String expressionString = "3 + 4";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().expectResult(Long.class));
		assertEquals(7L, exp.getValue(null));
	}

	@Test
	public void testParseBeanEvalExpressionNoParserContext() {
		String expressionString = "value";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("foo", exp.getValue(new TestBean()));
	}

	@Test
	public void testParseEvalExpressionWithContextTypeCoersion() {
		String expressionString = "maximum";
		Expression exp = parser
				.parseExpression(expressionString, new FluentParserContext().expectResult(Integer.class));
		assertEquals(2, exp.getValue(new TestBean()));
	}

	@Test
	public void testParseEvalExpressionWithContextCustomELVariableResolver() {
		String expressionString = "specialProperty";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().evaluate(TestBean.class));
		assertEquals("Custom resolver resolved this special property!", exp.getValue(null));
	}

	@Test
	public void testParseBeanEvalExpressionInvalidELVariable() {
		try {
			String expressionString = "bogus";
			Expression exp = parser.parseExpression(expressionString,
					new FluentParserContext().evaluate(TestBean.class));
			exp.getValue(new TestBean());
			fail("Should have failed");
		} catch (EvaluationException e) {

		}
	}

	@Test
	public void testParseLiteralExpression() {
		String expressionString = "'value'";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("value", exp.getValue(null));
	}

	@Test
	public void testParseTemplateExpression() {
		String expressionString = "text text text #{value} text text text#{value}";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().template());
		TestBean target = new TestBean();
		assertEquals("text text text foo text text textfoo", exp.getValue(target));
	}

	@Test
	public void testParseTemplateExpressionWithVariables() {
		String expressionString = "#{value}#{#max}";
		Expression exp = parser.parseExpression(expressionString,
				new FluentParserContext().template().variable(new ExpressionVariable("max", "maximum")));
		TestBean target = new TestBean();
		assertEquals("foo2", exp.getValue(target)); // TODO:
	}

	@Test
	public void testGetExpressionString() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, null);
		assertEquals("maximum", exp.getExpressionString());
	}

	@Test
	public void testGetExpressionType() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, null);
		TestBean context = new TestBean();
		Class<?> clazz = exp.getValueType(context);
		assertTrue(int.class.equals(clazz) || Integer.class.equals(clazz));
	}

	@Test
	public void testGetValueWithCoersion() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, new FluentParserContext().expectResult(String.class));
		TestBean context = new TestBean();
		assertEquals("2", exp.getValue(context));
	}

	@Test
	public void testGetValueCoersionError() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString,
				new FluentParserContext().expectResult(TestBean.class));
		TestBean context = new TestBean();
		try {
			exp.getValue(context);
			fail("Should have failed with coersion");
		} catch (ValueCoercionException e) {
		}
	}

	@Test
	public void testSetValue() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, null);
		TestBean context = new TestBean();
		exp.setValue(context, 5);
		assertEquals(5, context.getMaximum());
	}

	@Test
	public void testSetValueWithTypeCoersion() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, null);
		TestBean context = new TestBean();
		exp.setValue(context, "5");
		assertEquals(5, context.getMaximum());
	}

	@Test
	public void testSetValueCoersionError() {
		String expressionString = "maximum";
		Expression exp = parser.parseExpression(expressionString, null);
		TestBean context = new TestBean();
		try {
			exp.setValue(context, "bogus");
			fail("Should have failed with coersion");
		} catch (ValueCoercionException e) {
		}
	}

	private final class SpecialPropertyAccessor implements PropertyAccessor {
		public void write(EvaluationContext context, Object target, String name, Object newValue) {
		}

		public TypedValue read(EvaluationContext context, Object target, String name) {
			return new TypedValue("Custom resolver resolved this special property!",
					TypeDescriptor.valueOf(String.class));
		}

		public Class<?>[] getSpecificTargetClasses() {
			return null;
		}

		public boolean canWrite(EvaluationContext context, Object target, String name) {
			return false;
		}

		public boolean canRead(EvaluationContext context, Object target, String name) {
			return "specialProperty".equals(name);
		}
	}

}
