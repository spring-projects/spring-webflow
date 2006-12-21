/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.definition.registry;

import org.springframework.webflow.core.FlowException;

/**
 * Thrown when a flow definition was found during a lookup operation
 * but could not be constructed.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class FlowDefinitionConstructionException extends FlowException {
	
	/**
	 * The id of the flow that could not be constructed.
	 */
	private String flowId;
	
	/**
	 * Creates an exception indicating a flow definition could not be constructed.
	 * @param flowId the flow id
	 * @param cause underlying cause of the exception
	 */
	public FlowDefinitionConstructionException(String flowId, Throwable cause) {
		super("An exception occured constructing the flow with id '" + flowId + "'", cause);
	}
	
	/**
	 * Returns the id of the flow definition that could not be constructed.
	 * @return the flow id
	 */
	public String getFlowId() {
		return flowId;
	}
}