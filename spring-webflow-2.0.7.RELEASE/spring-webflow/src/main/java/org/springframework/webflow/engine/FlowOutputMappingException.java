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

import org.springframework.binding.mapping.MappingResults;

/**
 * Thrown when flow output mapping fails.
 * 
 * @author Keith Donald
 */
public class FlowOutputMappingException extends FlowAttributeMappingException {

	/**
	 * Creates a new flow output mapping exception.
	 * @param flowId the id of the flow where output mapping failed
	 * @param results the mapping errors with errors
	 */
	public FlowOutputMappingException(String flowId, MappingResults results) {
		super(flowId, null, results, "Errors occurred during output mapping on ending of the '" + flowId
				+ "' flow; errors = " + results.getErrorResults());
	}

	/**
	 * Creates a new flow output mapping exception.
	 * @param flowId the id of the flow where output mapping failed
	 * @param stateId the id of the state where output mapping failed
	 * @param results the mapping errors with errors
	 */
	public FlowOutputMappingException(String flowId, String stateId, MappingResults results) {
		super(flowId, stateId, results, "Errors occurred during output mapping in state '" + stateId + "' of flow '"
				+ flowId + "'; errors = " + results.getErrorResults());
	}
}
