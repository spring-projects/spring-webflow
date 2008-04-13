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
package org.springframework.webflow.context.portlet;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * A interface for parsing and generating flow URLs. Encapsulates a specific flow URL format.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface FlowUrlHandler {

	/**
	 * Extract the flow execution from the request.
	 * @param request the request
	 * @return the flow execution key, or null if no flow execution key is present
	 */
	public String getFlowExecutionKey(PortletRequest request);

	/**
	 * Set the flow execution key render parameter.
	 * @param flowExecutionKey the key
	 * @param response the action response
	 */
	public void setFlowExecutionRenderParameter(String flowExecutionKey, ActionResponse response);

	/**
	 * Set the flow execution key into the portlet session. This should only be used when the portlet is started before
	 * any action requests are made
	 * @param flowExecutionKey the key
	 * @param request the render request
	 */
	public void setFlowExecutionInSession(String flowExecutionKey, RenderRequest request);

	/**
	 * Creates a flow execution URL suitable for use as an action URL.
	 * @param flowId the flow id
	 * @param flowExecutionKey the flow execution key
	 * @param response the render response
	 * @return the execution url
	 */
	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, RenderResponse response);

}
