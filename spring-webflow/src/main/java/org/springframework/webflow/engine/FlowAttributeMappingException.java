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
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * Base class for attribute mapping failures.
 * 
 * @author Keith Donald
 */
public class FlowAttributeMappingException extends FlowExecutionException {

	private MappingResults results;

	/**
	 * Creates a new attrbute mapping exception
	 * @param flowId the flow id
	 * @param stateId the state id
	 * @param results the mapping results with errors
	 * @param message the messge
	 */
	public FlowAttributeMappingException(String flowId, String stateId, MappingResults results, String message) {
		super(flowId, stateId, message);
		this.results = results;
	}

	/**
	 * Returns the maping results containing errors that triggred this exception.
	 * @return the results
	 */
	public MappingResults getMappingResults() {
		return results;
	}
}
