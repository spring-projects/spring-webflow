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
package org.springframework.webflow.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Unit tests for {@link org.springframework.webflow.engine.FlowExecutionExceptionHandler} related code.
 * 
 * @author Erwin Vervaet
 */
public class FlowExecutionHandlerSetTests {

	Flow flow = new Flow("myFlow");
	MockRequestControlContext context = new MockRequestControlContext(flow);
	boolean handled;

	@Test
	public void testHandleException() {
		FlowExecutionExceptionHandlerSet handlerSet = new FlowExecutionExceptionHandlerSet();
		handlerSet.add(new TestStateExceptionHandler(NullPointerException.class, "null"));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, "execution 1"));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, "execution 2"));
		assertEquals(3, handlerSet.size());
		FlowExecutionException e = new FlowExecutionException("flowId", "stateId", "Test");
		assertTrue(handlerSet.handleException(e, context));
		assertFalse(context.getFlowScope().contains("null"));
		assertTrue(context.getFlowScope().contains("execution 1"));
		assertFalse(context.getFlowScope().contains("execution 2"));
	}

	/**
	 * State exception handler used in tests.
	 */
	public static class TestStateExceptionHandler implements FlowExecutionExceptionHandler {

		private Class<?> typeToHandle;
		private String resultName;

		public TestStateExceptionHandler(Class<?> typeToHandle, String resultName) {
			this.typeToHandle = typeToHandle;
			this.resultName = resultName;
		}

		public boolean canHandle(FlowExecutionException exception) {
			return typeToHandle.isInstance(exception);
		}

		public void handle(FlowExecutionException exception, RequestControlContext context) {
			context.getFlowScope().put(resultName, true);
		}
	}

}
