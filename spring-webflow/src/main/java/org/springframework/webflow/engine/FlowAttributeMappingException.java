package org.springframework.webflow.engine;

import org.springframework.binding.mapping.MappingResults;
import org.springframework.webflow.execution.FlowExecutionException;

public class FlowAttributeMappingException extends FlowExecutionException {

	private MappingResults results;

	public FlowAttributeMappingException(String flowId, String stateId, MappingResults results, String message) {
		super(flowId, stateId, message);
		this.results = results;
	}

	public MappingResults getMappingResults() {
		return results;
	}
}
