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
package org.springframework.webflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.webflow.core.collection.LocalAttributeMap;

/**
 * Unit tests for {@link WebFlowOgnlExpressionParser}.
 */
public class WebFlowOgnlExpressionParserTests extends TestCase {

	WebFlowOgnlExpressionParser parser = new WebFlowOgnlExpressionParser();

	public void testEvalSimpleExpression() {
		ArrayList list = new ArrayList();
		Expression exp = parser.parseExpression("size()");
		Integer size = (Integer)exp.evaluate(list, null);
		assertEquals(0, size.intValue());
	}

	public void testEvalMapAdaptable() {
		MapAdaptable adaptable = new MapAdaptable() {
			public Map asMap() {
				HashMap map = new HashMap();
				map.put("size", new Integer(0));
				return map;
			}
		};
		Expression exp = parser.parseExpression("size");
		Integer size = (Integer)exp.evaluate(adaptable, null);
		assertEquals(0, size.intValue());
	}

	public void testEvalAndSetMutableMap() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("size", new Integer(0));
		Expression exp = parser.parseExpression("size");
		Integer size = (Integer)exp.evaluate(map, null);
		assertEquals(0, size.intValue());
		assertTrue(exp instanceof SettableExpression);
		SettableExpression sexp = (SettableExpression)exp;
		sexp.evaluateToSet(map, new Integer(1), null);
		size = (Integer)exp.evaluate(map, null);
		assertEquals(1, size.intValue());
	}
}
