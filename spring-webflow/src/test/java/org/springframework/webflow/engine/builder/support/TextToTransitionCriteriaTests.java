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
package org.springframework.webflow.engine.builder.support;

import junit.framework.TestCase;

import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockRequestContext;

public class TextToTransitionCriteriaTests extends TestCase {

	private MockFlowBuilderContext serviceLocator = new MockFlowBuilderContext("flowId");
	private TextToTransitionCriteria converter = new TextToTransitionCriteria(serviceLocator);

	public void setUp() {
	}

	public void testAny() {
		String expression = "*";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
		assertSame(WildcardTransitionCriteria.INSTANCE, converter.convert("*"));
		assertSame(WildcardTransitionCriteria.INSTANCE, converter.convert(""));
		assertSame(WildcardTransitionCriteria.INSTANCE, converter.convert(null));
	}

	public void testStaticEventId() {
		String expression = "sample";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	public void testTrueEvaluation() throws Exception {
		String expression = "${flowScope.foo == 'bar'}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		RequestContext ctx = getRequestContext();
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	public void testFalseEvaluation() throws Exception {
		String expression = "${flowScope.foo != 'bar'}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		RequestContext ctx = getRequestContext();
		assertFalse("Criterion should evaluate to false", criterion.test(ctx));
	}

	public void testNonStringEvaluation() throws Exception {
		String expression = "${3 + 4}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		MockRequestContext ctx = getRequestContext();
		ctx.setLastEvent(new Event(this, "7"));
		assertTrue("Criterion should evaluate to true", criterion.test(ctx));
	}

	public void testNullExpressionEvaluation() throws Exception {
		String expression = "${null}";
		TransitionCriteria criterion = (TransitionCriteria) converter.convert(expression);
		RequestContext ctx = getRequestContext();
		assertFalse("Criterion should evaluate to false", criterion.test(ctx));
	}

	private MockRequestContext getRequestContext() {
		Flow flow = new Flow("id");
		MockRequestContext ctx = new MockRequestContext(flow);
		RequestContextHolder.setRequestContext(ctx);
		ctx.getFlowScope().put("foo", "bar");
		ctx.setLastEvent(new Event(this, "sample"));
		return ctx;
	}
}