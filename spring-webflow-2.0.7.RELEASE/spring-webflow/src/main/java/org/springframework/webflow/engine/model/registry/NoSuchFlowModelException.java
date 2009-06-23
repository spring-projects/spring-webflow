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
package org.springframework.webflow.engine.model.registry;

import org.springframework.webflow.core.FlowException;

/**
 * Thrown when no flow model was found during a lookup operation by a flow locator.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public class NoSuchFlowModelException extends FlowException {

	/**
	 * The id of the flow model that could not be located.
	 */
	private String flowModelId;

	/**
	 * Creates an exception indicating a flow model could not be found.
	 * @param flowModelId the flow model id
	 */
	public NoSuchFlowModelException(String flowModelId) {
		super("No flow model '" + flowModelId + "' found");
		this.flowModelId = flowModelId;
	}

	/**
	 * Returns the id of the flow model that could not be found.
	 */
	public String getFlowModelId() {
		return flowModelId;
	}
}