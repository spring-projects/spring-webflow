/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link EvaluateAction}.
 * @author Jeremy Grelle
 */
public class EvaluateActionTests {

	@Test
	public void testEvaluateExpressionNoResultExposer() throws Exception {
		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"), null);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
	}

	@Test
	public void testEvaluateExpressionEmptyStringResult() throws Exception {
		EvaluateAction action = new EvaluateAction(new StaticExpression(""), null);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("null", result.getId());
	}

	@Test
	public void testEvaluateExpressionNullResult() throws Exception {
		EvaluateAction action = new EvaluateAction(new StaticExpression(null), null);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
	}

	@Test
	public void testEvaluateExpressionResultExposer() throws Exception {
		StaticExpression resultExpression = new StaticExpression("");
		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"), resultExpression);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
		assertEquals("bar", resultExpression.getValue(null));
	}
}