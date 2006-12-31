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
package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;

/**
 * Unit tests for {@link org.springframework.webflow.engine.FlowExecutionExceptionHandler} related code.
 * 
 * @author Erwin Vervaet
 */
public class StateExceptionHandlerTests extends TestCase {
	
	public void testHandleException() {
		FlowExecutionExceptionHandlerSet handlerSet = new FlowExecutionExceptionHandlerSet();
		
		handlerSet.add(new TestStateExceptionHandler(NullPointerException.class, new ApplicationView("NOK", null)));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, new ApplicationView("OK", null)));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, new ApplicationView("NOK", null)));
		
		FlowExecutionException testException = new FlowExecutionException("flowId", "stateId", "Test");
		assertNotNull(
				"First handler should have been ignored since it does not handle StateException",
				handlerSet.handleException(testException, null));
		assertEquals(
				"Third handler should not have been reached since second handler handles excpetion and returns not-null",
				"OK", ((ApplicationView)handlerSet.handleException(testException, null)).getViewName());
	}
	
	public void testHandleExceptionWithNulls() {
		FlowExecutionExceptionHandlerSet handlerSet = new FlowExecutionExceptionHandlerSet();
		
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, null));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, new ApplicationView("OK", null)));
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, new ApplicationView("NOK", null)));
		
		FlowExecutionException testException = new FlowExecutionException("flowId", "stateId", "Test");
		assertNotNull(
				"First handler should have been ignored since it return null",
				handlerSet.handleException(testException, null));
		assertEquals(
				"Third handler should not have been reached since second handler handles excpetion and returns not-null",
				"OK", ((ApplicationView)handlerSet.handleException(testException, null)).getViewName());
	}
	
	public void testHandleExceptionNoMatch() {
		FlowExecutionExceptionHandlerSet handlerSet = new FlowExecutionExceptionHandlerSet();
		
		handlerSet.add(new TestStateExceptionHandler(FlowExecutionException.class, null));
		handlerSet.add(new TestStateExceptionHandler(NullPointerException.class, new ApplicationView("NOK", null)));
		
		FlowExecutionException testException = new FlowExecutionException("flowId", "stateId", "Test");
		assertNull(
				"First handler should have been ignored since it return null, " +
				"second handler should have been ignored since it does not handle the exception",
				handlerSet.handleException(testException, null));
	}
	
	/**
	 * State exception handler used in tests.
	 */
	public static class TestStateExceptionHandler implements FlowExecutionExceptionHandler {
		
		private Class typeToHandle;
		private ViewSelection handleResult;
		
		public TestStateExceptionHandler(Class typeToHandle, ViewSelection handleResult) {
			this.typeToHandle = typeToHandle;
			this.handleResult = handleResult;
		}
		
		public boolean handles(FlowExecutionException exception) {
			return typeToHandle.isInstance(exception);
		}
		
		public ViewSelection handle(FlowExecutionException exception, RequestControlContext context) {
			return handleResult;
		}
	}

}
