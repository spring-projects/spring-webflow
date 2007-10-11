package org.springframework.webflow.context;

import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.ParameterMap;

/**
 * A request to resume an existing flow execution that is currently paused.
 * 
 * @author Keith Donald
 */
public class FlowExecutionRequestInfo extends AbstractFlowRequestInfo {

	/**
	 * The key of the flow execution to resume.
	 */
	private String flowExecutionKey;

	/**
	 * Creates a new request to resume a flow execution.
	 * @param flowDefinitionId the definition identifier of the execution (required)
	 * @param flowExecutionKey the key of the flow execution (required)
	 */
	public FlowExecutionRequestInfo(String flowDefinitionId, String flowExecutionKey) {
		this(flowDefinitionId, flowExecutionKey, null, null, null);
	}

	/**
	 * Creates a new request to resume a flow execution.
	 * @param flowDefinitionId the definition identifier of the execution (required)
	 * @param flowExecutionKey the key of the flow execution (required)
	 * @param requestPath the request path (optional)
	 * @param requestParameters the request parameters (optional)
	 * @param fragment the fragment (optional)
	 */
	public FlowExecutionRequestInfo(String flowDefinitionId, String flowExecutionKey, RequestPath requestPath,
			ParameterMap requestParameters, String fragment) {
		super(flowDefinitionId, requestPath, requestParameters, fragment);
		Assert.hasText(flowDefinitionId,
				"The definition identifier of the flow execution to redirect to cannot be null");
		Assert.hasText(flowExecutionKey, "The flow execution key to redirect to cannot be null");
		this.flowExecutionKey = flowExecutionKey;
	}

	/**
	 * Returns the unique key of the flow execution to resume.
	 * @return the flow execution key
	 */
	public String getFlowExecutionKey() {
		return flowExecutionKey;
	}
}
