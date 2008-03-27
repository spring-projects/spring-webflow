package org.springframework.webflow.engine;

import org.springframework.binding.mapping.MappingResults;

public class FlowOutputMappingException extends FlowAttributeMappingException {
	public FlowOutputMappingException(String flowId, MappingResults results) {
		super(flowId, null, results, "Errors occurred during output mapping on ending of the '" + flowId
				+ "' flow; errors = " + results.getErrorResults());
	}

	public FlowOutputMappingException(String flowId, String stateId, MappingResults results) {
		super(flowId, stateId, results, "Errors occurred during output mapping in state '" + stateId + "' of flow '"
				+ flowId + "'; errors = " + results.getErrorResults());
	}
}
