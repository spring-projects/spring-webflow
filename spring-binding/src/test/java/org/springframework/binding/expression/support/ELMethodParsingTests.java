package org.springframework.binding.expression.support;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;

public class ELMethodParsingTests extends TestCase {

	ELExpressionParser parser;

	protected void setUp() throws Exception {
		parser = new ELExpressionParser();
	}

	public void testEmptyMethod() {

		String expStr1 = "#{foo.bar()}";
		Expression result1 = parser.parseExpression(expStr1);
		assertNotNull(result1);
		assertEquals(expStr1, result1.toString());

		String expStr2 = "foo.bar()";
		Expression result2 = parser.parseExpression(expStr2);
		assertNotNull(result2);
		assertEquals(expStr1, result2.toString());
	}

	public void testMethodWithParams() {

		String expStr1 = "#{foo.bar(moe.curly, groucho.harpo)}";
		Expression result1 = parser.parseExpression(expStr1);
		assertNotNull(result1);
		assertEquals(expStr1, result1.toString());
		
		String expStr2 = "foo.bar(moe.curly, groucho.harpo)";
		Expression result2 = parser.parseExpression(expStr2);
		assertNotNull(result2);
		assertEquals(expStr1, result2.toString());
	}

}
