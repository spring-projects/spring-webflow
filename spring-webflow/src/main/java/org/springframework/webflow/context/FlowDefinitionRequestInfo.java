package org.springframework.webflow.context;

import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.ParameterMap;

/**
 * A request to launch a new execution of a flow definition.
 * 
 * @author Keith Donald
 */
public class FlowDefinitionRequestInfo extends AbstractFlowRequestInfo {

	/**
	 * Creates a new flow definition request.
	 * @param flowDefinitionId the flow definition id (required)
	 * @param requestPath the request path (optional)
	 * @param requestParameters the request parameters (optional)
	 * @param fragment the fragment (optional)
	 */
	public FlowDefinitionRequestInfo(String flowDefinitionId, RequestPath requestPath, ParameterMap requestParameters,
			String fragment) {
		super(flowDefinitionId, requestPath, requestParameters, fragment);
		Assert.hasText(flowDefinitionId, "The id of the flow definition to redirect to is required");
	}
}
