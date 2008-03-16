package org.springframework.webflow.engine;

import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.style.StylerUtils;

public class FlowOutputMappingException extends FlowAttributeMappingException {
	public FlowOutputMappingException(String flowId, MappingResults results) {
		this(flowId, null, results);
	}

	public FlowOutputMappingException(String flowId, String stateId, MappingResults results) {
		super(flowId, stateId, results, "Errors occured during flow output mapping; errors = "
				+ StylerUtils.style(results.getErrorResults()));
	}
}
