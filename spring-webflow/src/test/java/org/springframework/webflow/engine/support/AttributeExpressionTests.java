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

import java.util.HashMap;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link AttributeExpression}.
 */
public class AttributeExpressionTests extends TestCase {

	public void testFlowScopeExpression() {
		Expression exp = DefaultExpressionParserFactory.getExpressionParser().parseExpression("foo");
		AttributeExpression flowExp = new AttributeExpression(exp, ScopeType.FLOW);
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		assertEquals("bar", flowExp.evaluate(context, null));
	}
	
	public void testFlowScopeSettableExpression() {
		Expression exp = DefaultExpressionParserFactory.getExpressionParser().parseSettableExpression("foo");
		AttributeExpression flowExp = new AttributeExpression(exp, ScopeType.FLOW);
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("foo", "bar");
		flowExp.evaluateToSet(context, "newValue", null);
		assertEquals("newValue", context.getFlowScope().get("foo"));
	}
	
	public void testAttributeMapExpression() {
		Expression exp = DefaultExpressionParserFactory.getExpressionParser().parseExpression("foo");
		AttributeExpression attrExp = new AttributeExpression(exp);
		MutableAttributeMap attributeMap = new LocalAttributeMap();
		attributeMap.put("foo", "bar");
		assertEquals("bar", attrExp.evaluate(attributeMap, null));
	}
	
	public void testAttributeMapSettableExpression() {
		Expression exp = DefaultExpressionParserFactory.getExpressionParser().parseSettableExpression("foo");
		AttributeExpression attrExp = new AttributeExpression(exp);
		MutableAttributeMap attributeMap = new LocalAttributeMap();
		attributeMap.put("foo", "bar");
		attrExp.evaluateToSet(attributeMap, "newValue", null);
		assertEquals("newValue", attributeMap.get("foo"));
	}
	
	public void testInvalidExpressionType() {
		Expression exp = new StaticExpression("value");
		AttributeExpression attrExp = new AttributeExpression(exp);
		try {
			attrExp.evaluateToSet(new LocalAttributeMap(), "newValue", null);
			fail("we need a SettableExpression");
		}
		catch (IllegalArgumentException e) {
		}
	}
	
	public void testUnsupportedTarget() {
		try {
			new AttributeExpression(new StaticExpression("value")).evaluate(new HashMap(), null);
			fail("a Map is not supported");
		}
		catch(IllegalArgumentException e) {
		}
		try {
			Expression exp = DefaultExpressionParserFactory.getExpressionParser().parseSettableExpression("foo");
			new AttributeExpression(exp).evaluate(new HashMap(), null);
			fail("a Map is not supported");
		}
		catch(IllegalArgumentException e) {
		}
	}
}