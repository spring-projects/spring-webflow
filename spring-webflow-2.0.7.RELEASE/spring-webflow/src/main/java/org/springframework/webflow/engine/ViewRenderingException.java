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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.View;

/**
 * Thrown if a IO exception was thrown during view rendering.
 * 
 * @author Keith Donald
 */
public class ViewRenderingException extends FlowExecutionException {

	/**
	 * Create a new action execution exception.
	 * @param flowId the current flow
	 * @param stateId the current state (may be null)
	 * @param view the view that generated an unrecoverable exception
	 * @param cause the underlying cause
	 */
	public ViewRenderingException(String flowId, String stateId, View view, Throwable cause) {
		super(flowId, stateId, "Exception thrown rendering " + view + " in state '" + stateId + "' of flow '" + flowId
				+ "'", cause);
	}
}