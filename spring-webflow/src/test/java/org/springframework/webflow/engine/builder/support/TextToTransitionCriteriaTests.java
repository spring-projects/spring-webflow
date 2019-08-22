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
package org.springframework.webflow.engine.builder.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockRequestContext;

public class TextToTransitionCriteriaTests {

	private MockFlowBuilderContext serviceLocator = new MockFlowBuilderContext("flowId");
	private TextToTransitionCriteria converter = new TextToTransitionCriteria(serviceLocator);

	@After
	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testAny() throws Exception {
		String expression = "*";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
		assertSame(WildcardTransitionCriteria.INSTANCE,
				converter.convertSourceToTargetClass("*", TransitionCriteria.class));
		assertSame(WildcardTransitionCriteria.INSTANCE,
				converter.convertSourceToTargetClass("", TransitionCriteria.class));
		assertSame(WildcardTransitionCriteria.INSTANCE,
				converter.convertSourceToTargetClass(null, TransitionCriteria.class));
	}

	@Test
	public void testStaticEventId() throws Exception {
		String expression = "sample";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	@Test
	public void testTrueEvaluation() throws Exception {
		String expression = "#{flowScope.foo == 'bar'}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	@Test
	public void testFalseEvaluation() throws Exception {
		String expression = "#{flowScope.foo != 'bar'}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		RequestContext ctx = getRequestContext();
		assertFalse("Criterion should evaluate to false", criterion.test(ctx));
	}

	@Test
	public void testNonStringEvaluation() throws Exception {
		String expression = "#{3 + 4}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		MockRequestContext ctx = getRequestContext();
		ctx.setCurrentEvent(new Event(this, "7"));
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	@Test
	public void testCurrenEventEval() throws Exception {
		String expression = "#{currentEvent.id == 'submit'}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass(expression,
				TransitionCriteria.class);
		MockRequestContext ctx = getRequestContext();
		ctx.setCurrentEvent(new Event(this, "submit"));
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	@Test
	public void testNullExpressionEvaluation() throws Exception {
		serviceLocator.getFlowBuilderServices()
				.setExpressionParser((expressionString, context) -> new StaticExpression(null));
		TransitionCriteria criterion = (TransitionCriteria) converter.convertSourceToTargetClass("doesnt matter",
				TransitionCriteria.class);
		RequestContext ctx = getRequestContext();
		assertFalse("Criterion should evaluate to false", criterion.test(ctx));
	}

	private MockRequestContext getRequestContext() {
		Flow flow = new Flow("id");
		MockRequestContext ctx = new MockRequestContext(flow);
		RequestContextHolder.setRequestContext(ctx);
		ctx.getFlowScope().put("foo", "bar");
		ctx.setCurrentEvent(new Event(this, "sample"));
		return ctx;
	}
}
