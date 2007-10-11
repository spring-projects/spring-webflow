package org.springframework.webflow.context;

import org.springframework.webflow.core.collection.ParameterMap;

/**
 * An abstract representation request to be sent into the Spring Web Flow system at a later date. Used to request
 * operations such as flow definition redirects, flow execution redirects, as well as to generate flow definition and
 * flow execution URLs.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFlowRequestInfo {

	/**
	 * The flow definition the request should be sent to.
	 */
	private String flowDefinitionId;

	/**
	 * Hierarchical data to be sent along in the request.
	 */
	private RequestPath requestPath;

	/**
	 * Query parameters to be sent along in the request.
	 */
	private ParameterMap requestParameters;

	/**
	 * An anchor fragment to be sent in the request, for interpretation by the agent that initiates the request.
	 */
	private String fragment;

	protected AbstractFlowRequestInfo(String flowDefinitionId, RequestPath requestPath, ParameterMap requestParameters,
			String fragment) {
		this.flowDefinitionId = flowDefinitionId;
		this.requestPath = requestPath;
		this.requestParameters = requestParameters;
		this.fragment = fragment;
	}

	/**
	 * Returns the flow definition this request should be sent to.
	 * @return the flow definition identifier
	 */
	public String getFlowDefinitionId() {
		return flowDefinitionId;
	}

	/**
	 * Returns hierarchical data to be sent along in the request.
	 * @return the request path
	 */
	public RequestPath getRequestPath() {
		return requestPath;
	}

	/**
	 * Returns query parameters to send along in the request.
	 * @return the request parameters
	 */
	public ParameterMap getRequestParameters() {
		return requestParameters;
	}

	/**
	 * Returns the fragment to be sent along in the request. A fragment is a free-form string value that may be
	 * interpreted by the client agent that initiates the request.
	 * @return the fragment
	 */
	public String getFragment() {
		return fragment;
	}
}
