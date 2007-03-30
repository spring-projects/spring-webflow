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
package org.springframework.binding.expression.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests simple expressions. Any expression language capable enough for
 * real life usage should be able to pass these tests.
 *  
 * @author Erwin Vervaet
 */
public class SimpleExpressionTests extends TestCase {
	
	private ExpressionParser expressionParser;
	private TestBean bean;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new SimpleExpressionTests("testGetValue", new OgnlExpressionParser()));
		suite.addTest(new SimpleExpressionTests("testSetValue", new OgnlExpressionParser()));
		suite.addTest(new SimpleExpressionTests("testSyntaxError", new OgnlExpressionParser()));
		suite.addTest(new SimpleExpressionTests("testGetValue", new BeanWrapperExpressionParser()));
		suite.addTest(new SimpleExpressionTests("testSetValue", new BeanWrapperExpressionParser()));
		suite.addTest(new SimpleExpressionTests("testSyntaxError", new BeanWrapperExpressionParser()));
		return suite;
	}
	
	public SimpleExpressionTests(String name, ExpressionParser expressionParser) {
		super(name);
		this.expressionParser = expressionParser;
	}
	
	protected void setUp() throws Exception {
		bean = new TestBean();
		bean.setFlag(true);
		List list = new ArrayList();
		list.add("foo");
		list.add("bar");
		bean.setList(list);
	}
	
	public void testGetValue() {
		assertEquals(Boolean.TRUE, expressionParser.parseExpression("${flag}").evaluate(bean, null));
		assertEquals(Boolean.TRUE, expressionParser.parseExpression("flag").evaluate(bean, null));
		assertSame(bean.getList(), expressionParser.parseExpression("${list}").evaluate(bean, null));
		assertEquals("foo", expressionParser.parseExpression("${list[0]}").evaluate(bean, null));
	}

	public void testSetValue() {
		expressionParser.parseSettableExpression("${flag}").evaluateToSet(bean, Boolean.FALSE, null);
		assertFalse(bean.isFlag());
		expressionParser.parseSettableExpression("flag").evaluateToSet(bean, Boolean.TRUE, null);
		assertTrue(bean.isFlag());
		List newList = new ArrayList();
		newList.add("boo");
		expressionParser.parseSettableExpression("${list}").evaluateToSet(bean, newList, null);
		assertSame(newList, bean.getList());
		expressionParser.parseSettableExpression("${list[0]}").evaluateToSet(bean, "baa", null);
		assertEquals("baa", bean.getList().get(0));
	}
	
	public void testSyntaxError() {
		try {
			expressionParser.parseExpression("foo(").evaluate(bean, null);
			fail("should have failed");
		}
		catch (ParserException e) {
		}
		catch (EvaluationException e) {
		}
	}
}
