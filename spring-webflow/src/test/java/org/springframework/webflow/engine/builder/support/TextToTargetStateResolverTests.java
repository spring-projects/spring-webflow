/*
 * Copyright 2004-2008 the original author or authors.
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

import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockRequestContext;

public class TextToTargetStateResolverTests extends TestCase {

	private MockFlowBuilderContext serviceLocator = new MockFlowBuilderContext("flowId");
	private TextToTargetStateResolver converter = new TextToTargetStateResolver(serviceLocator);

	public void setUp() {
	}

	public void testStatic() throws Exception {
		String expression = "mockState";
		TargetStateResolver resolver = (TargetStateResolver) converter.convertSourceToTargetClass(expression,
				TargetStateResolver.class);
		MockRequestContext context = new MockRequestContext();
		Transition transition = new Transition();
		assertEquals("mockState", resolver.resolveTargetState(transition, null, context).getId());
	}

	public void testDynamic() throws Exception {
		String expression = "${flowScope.lastState}";
		TargetStateResolver resolver = (TargetStateResolver) converter.convertSourceToTargetClass(expression,
				TargetStateResolver.class);
		MockRequestContext context = new MockRequestContext();
		context.getFlowScope().put("lastState", "mockState");
		Transition transition = new Transition();
		assertEquals("mockState", resolver.resolveTargetState(transition, null, context).getId());
	}

	public void testNull() throws Exception {
		String expression = null;
		TargetStateResolver resolver = (TargetStateResolver) converter.convertSourceToTargetClass(expression,
				TargetStateResolver.class);
		assertNull(resolver);
	}

	public void testEmpty() throws Exception {
		String expression = "";
		TargetStateResolver resolver = (TargetStateResolver) converter.convertSourceToTargetClass(expression,
				TargetStateResolver.class);
		assertNull(resolver);
	}
}