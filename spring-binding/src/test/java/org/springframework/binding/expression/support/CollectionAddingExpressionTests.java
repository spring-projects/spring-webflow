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

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;

/**
 * Unit tests for {@link org.springframework.binding.expression.support.CollectionAddingExpression}.
 */
public class CollectionAddingExpressionTests extends TestCase {

	ExpressionParser parser = new BeanWrapperExpressionParser();

	TestBean bean = new TestBean();

	Expression exp = parser.parseExpression("list");

	public void testEvaluation() {
		ArrayList list = new ArrayList();
		bean.setList(list);
		CollectionAddingExpression colExp = new CollectionAddingExpression(exp);
		assertSame(list, colExp.evaluate(bean, null));
	}

	public void testAddToCollection() {
		CollectionAddingExpression colExp = new CollectionAddingExpression(exp);
		colExp.evaluateToSet(bean, "1", null);
		colExp.evaluateToSet(bean, "2", null);
		assertEquals("1", bean.getList().get(0));
		assertEquals("2", bean.getList().get(1));
	}
	
	public void testNotACollection() {
		Expression exp = parser.parseExpression("flag");		
		CollectionAddingExpression colExp = new CollectionAddingExpression(exp);
		try {
			colExp.evaluateToSet(bean, "1", null);
			fail("not a collection");
		}
		catch (IllegalArgumentException e) {
		}
	}
	
	public void testNoAddOnNullValue() {
		CollectionAddingExpression colExp = new CollectionAddingExpression(exp);
		colExp.evaluateToSet(bean, null, null);
		colExp.evaluateToSet(bean, "2", null);
		assertEquals("2", bean.getList().get(0));
	}
}