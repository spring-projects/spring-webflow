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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link org.springframework.webflow.engine.support.BooleanExpressionTransitionCriteria}.
 */
public class BooleanExpressionTransitionCriteriaTests extends TestCase {
	
	private ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser();

	public void testMatchCriteria() {
		Expression exp = parser.parseExpression("${requestScope.flag}");
		BooleanExpressionTransitionCriteria c = new BooleanExpressionTransitionCriteria(exp);
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("flag", Boolean.TRUE);
		assertEquals(true, c.test(context));
	}	

	public void testNotABoolean() {
		Expression exp = parser.parseExpression("${requestScope.flag}");
		BooleanExpressionTransitionCriteria c = new BooleanExpressionTransitionCriteria(exp);
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("flag", "foo");
		try {
			c.test(context);
			fail("not a boolean");
		}
		catch (IllegalArgumentException e) {
		}
	}
	
	public void testResult() {
		Expression exp = parser.parseExpression("${#result == 'foo'}");
		BooleanExpressionTransitionCriteria c = new BooleanExpressionTransitionCriteria(exp);
		MockRequestContext context = new MockRequestContext();
		context.setLastEvent(new Event(this, "foo"));
		assertEquals(true, c.test(context));
	}
	
	public void testFunctionInvocation() {
		Expression exp = parser.parseExpression("${#result.endsWith('error')}");
		BooleanExpressionTransitionCriteria c = new BooleanExpressionTransitionCriteria(exp);
		MockRequestContext context = new MockRequestContext();
		context.setLastEvent(new Event(this, "error"));
		assertTrue(c.test(context));
	}
}