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

import org.springframework.core.style.StylerUtils;
import org.springframework.webflow.core.FlowException;

/**
 * Thrown when no flow definition was found during a lookup operation by a flow
 * locator.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class NoSuchFlowDefinitionException extends FlowException {

	/**
	 * The id of the flow definition that could not be located.
	 */
	private String flowId;

	/**
	 * Creates an exception indicating a flow definition could not be found.
	 * @param flowId the flow id
	 * @param availableFlowIds all flow ids available to the locator generating
	 * this exception
	 */
	public NoSuchFlowDefinitionException(String flowId, String[] availableFlowIds) {
		super("No such flow definition with id '" + flowId + "' found; the flows available are: "
				+ StylerUtils.style(availableFlowIds));
		this.flowId = flowId;
	}

	/**
	 * Returns the id of the flow definition that could not be found.
	 */
	public String getFlowId() {
		return flowId;
	}
}