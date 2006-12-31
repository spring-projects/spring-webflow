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
package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.TestBeanWithMap;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link SetAction}.
 */
public class SetActionTests extends TestCase {
	
	private ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser();

	private MockRequestContext context = new MockRequestContext();

	public void testSetActionWithBooleanValue() throws Exception {
		context.getFlowScope().put("bean", new TestBean());
		
		SettableExpression attr = parser.parseSettableExpression("bean.executed");
		Expression value = parser.parseExpression("true");
		SetAction action = new SetAction(attr, ScopeType.FLOW, value);
		Event outcome = action.execute(context);
		assertEquals("success", outcome.getId());
		assertEquals(true, ((TestBean)context.getFlowScope().get("bean")).executed);
	}
	
	public void testSetActionWithStringValue() throws Exception {
		SettableExpression attr = parser.parseSettableExpression("backState");
		Expression value = parser.parseExpression("'otherState'"); // ${'otherState'} also works
		SetAction action = new SetAction(attr, ScopeType.FLOW, value);
		assertEquals("success", action.execute(context).getId());
		assertEquals("otherState", context.getFlowScope().get("backState"));
	}
	
	public void testSetActionWithValueFromMap() throws Exception {
		TestBeanWithMap beanWithMap = new TestBeanWithMap();
		beanWithMap.getMap().put("key1", "value1");
		beanWithMap.getMap().put("key2", "value2");
		context.getFlowScope().put("beanWithMap", beanWithMap);
		
		SettableExpression attr = parser.parseSettableExpression("key");
		Expression value = parser.parseExpression("${flowScope.beanWithMap.map['key1']}");
		SetAction action = new SetAction(attr, ScopeType.FLASH, value);
		assertEquals("success", action.execute(context).getId());
		assertEquals("value1", context.getFlashScope().get("key"));
	}
}
