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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A strategy for handling an exception that occurs at runtime during an active flow execution.
 * 
 * Note: special care should be taken when implementing custom flow execution exception handlers. Exception handlers are
 * like Transitions in that they direct flow control when exceptions occur. They are more complex than Actions, which
 * can only execute behaviors and return results that drive flow control. For this reason, if implemented incorrectly, a
 * FlowExecutionHandler can leave a flow execution in an invalid state, which can render the flow execution unusable or
 * its future use undefined. For example, if an exception thrown at flow session startup gets routed to an exception
 * handler, the handler must take responsibility for ensuring the flow execution returns control to the caller in a
 * consistent state. Concretely, this means the exception handler must transition the flow to its start state. The
 * handler should not simply return leaving the flow with no current state set.
 * 
 * Note: Because flow execution handlers are more difficult to implement correctly, consider catching exceptions in your
 * web flow action code and returning result events that drive standard transitions. Alternatively, consider use of the
 * existing {@code TransitionExecutingFlowExecutionExceptionHandler} which illustrates the proper way to implement an
 * exception handler.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionExceptionHandler {

	/**
	 * Can this handler handle the given exception?
	 * @param exception the exception that occurred
	 * @return true if yes, false if no
	 */
	boolean canHandle(FlowExecutionException exception);

	/**
	 * Handle the exception in the context of the current request. An implementation is expected to transition the flow
	 * to a state using {@link RequestControlContext#execute(Transition)}.
	 * @param exception the exception that occurred
	 * @param context the execution control context for this request
	 */
	void handle(FlowExecutionException exception, RequestControlContext context);
}
