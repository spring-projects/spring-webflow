/*
 * Copyright 2004-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.webflow.context.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.webflow.core.collection.AttributeMap;

/**
 * A interface for parsing and generating flow URLs. Encapsulates a specific flow URL format.
 * 
 * @author Keith Donald
 */
public interface FlowUrlHandler {

	/**
	 * Extract the flow execution from the request.
	 * @param request the request
	 * @return the flow execution key, or null if no flow execution key is present
	 */
	public String getFlowExecutionKey(HttpServletRequest request);

	/**
	 * Extract the flow id from the request.
	 * @param request the request
	 * @return the flow id, or null if no flow id is present
	 */
	public String getFlowId(HttpServletRequest request);

	/**
	 * Create a URL that when addressed will launch a new execution of a flow.
	 * @param flowId the id of the flow definition
	 * @param input the input to pass the new flow execution
	 * @param request the current request
	 * @return the flow definition url
	 */
	public String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request);

	/**
	 * Create a URL that when addressed will resume an existing execution of a flow.
	 * @param flowExecutionKey the flow execution key that is paused and waiting to be resumed
	 * @param request the current request
	 * @return the flow execution url
	 */
	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, HttpServletRequest request);
}
