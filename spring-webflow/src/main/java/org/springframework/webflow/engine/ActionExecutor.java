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

import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * A simple static helper that performs action execution that encapsulates common logging and exception handling logic.
 * This is an internal helper class that is not normally used by application code.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ActionExecutor {

	/**
	 * Private constructor to avoid instantiation.
	 */
	private ActionExecutor() {
	}

	/**
	 * Execute the given action.
	 * @param action the action to execute
	 * @param context the flow execution request context
	 * @return result of action execution
	 * @throws ActionExecutionException if the action threw an exception while executing, the orginal exception is
	 * available as the cause if this exception
	 */
	public static Event execute(Action action, RequestContext context) throws ActionExecutionException {
		try {
			return action.execute(context);
		} catch (ActionExecutionException e) {
			throw e;
		} catch (Exception e) {
			// wrap the exception as an ActionExecutionException
			throw new ActionExecutionException(context.getActiveFlow().getId(),
					context.getCurrentState() != null ? context.getCurrentState().getId() : null, action, context
							.getAttributes(), e);
		}
	}
}