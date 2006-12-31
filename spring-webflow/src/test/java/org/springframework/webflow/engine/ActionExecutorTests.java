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
package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowSessionStatus;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public class ActionExecutorTests extends TestCase {

	public void testBasicExecute() {
		TestAction action = new TestAction();
		Event result = ActionExecutor.execute(action, new MockRequestContext());
		assertEquals("success", result.getId());
	}

	public void testExceptionWhileStarted() {
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				throw new IllegalStateException("Oops");
			}
		};
		try {
			ActionExecutor.execute(action, new MockRequestContext());
			fail("Should've failed");
		}
		catch (ActionExecutionException e) {
			assertTrue(e.getCause() instanceof IllegalStateException);
		}
	}

	public void testExceptionWhileStarting() {
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				throw new IllegalStateException("Oops");
			}
		};
		MockRequestContext context = new MockRequestContext();
		MockFlowSession starting = new MockFlowSession(new Flow("flow"));
		starting.setStatus(FlowSessionStatus.STARTING);
		context.getMockFlowExecutionContext().setActiveSession(starting);
		try {
			ActionExecutor.execute(action, context);
			fail("Should've failed");
		}
		catch (ActionExecutionException e) {
			assertTrue(e.getCause() instanceof IllegalStateException);
		}
	}
}