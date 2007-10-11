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

import org.springframework.webflow.action.MultiAction.MethodResolver;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.util.DispatchMethodInvoker.MethodLookupException;

/**
 * Unit tests for {@link MultiAction}.
 */
public class MultiActionTests extends TestCase {

	private TestMultiAction action = new TestMultiAction();

	private MockRequestContext context = new MockRequestContext();

	public void testDispatchWithMethodSignature() throws Exception {
		context.getAttributeMap().put(AnnotatedAction.METHOD_ATTRIBUTE, "increment");
		action.execute(context);
		assertEquals(1, action.counter);
	}

	public void testDispatchWithBogusMethodSignature() throws Exception {
		context.getAttributeMap().put(AnnotatedAction.METHOD_ATTRIBUTE, "bogus");
		try {
			action.execute(context);
			fail("Should've failed with no such method");
		} catch (MethodLookupException e) {

		}
	}

	public void testDispatchWithCurrentStateId() throws Exception {
		MockFlowSession session = context.getMockFlowExecutionContext().getMockActiveSession();
		session.setState(new ViewState(session.getDefinitionInternal(), "increment", new StubViewFactory()));
		action.execute(context);
		assertEquals(1, action.counter);
	}

	public void testNoSuchMethodWithCurrentStateId() throws Exception {
		try {
			action.execute(context);
			fail("Should've failed with no such method");
		} catch (MethodLookupException e) {

		}
	}

	public void testCannotResolveMethod() throws Exception {
		try {
			context.getMockFlowExecutionContext().getMockActiveSession().setState(null);
			action.execute(context);
			fail("Should've failed with illegal state");
		} catch (IllegalStateException e) {

		}
	}

	public void testCustomMethodResolver() throws Exception {
		MethodResolver methodResolver = new MethodResolver() {
			public String resolveMethod(RequestContext context) {
				return "increment";
			}
		};
		action.setMethodResolver(methodResolver);
		action.execute(context);
		assertEquals(1, action.counter);
	}
}