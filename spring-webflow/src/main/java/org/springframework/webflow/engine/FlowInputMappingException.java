package org.springframework.webflow.engine;

import org.springframework.binding.mapping.MappingResults;

public class FlowInputMappingException extends FlowAttributeMappingException {
	public FlowInputMappingException(String flowId, MappingResults results) {
		super(flowId, null, results, "Errors occurred during input mapping on startup of the '" + flowId
				+ "' flow; errors = " + results.getErrorResults());
	}

	public FlowInputMappingException(String flowId, String stateId, MappingResults results) {
		super(flowId, stateId, results, "Errors occurred during input mapping in state '" + stateId + "' of flow '"
				+ flowId + "'; errors = " + results.getErrorResults());
	}
}
