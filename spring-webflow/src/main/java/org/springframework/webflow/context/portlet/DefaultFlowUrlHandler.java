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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Default flow URL handler for SWF 2.
 * 
 * @author Scott Andrews
 */
public class DefaultFlowUrlHandler implements FlowUrlHandler {

	private static final String EXECUTION_ATTRIBUTE = "execution";

	public String getFlowExecutionKey(PortletRequest request) {
		String flowExecutionKey = request.getParameter(EXECUTION_ATTRIBUTE);
		PortletSession session = request.getPortletSession(false);
		if (session != null) {
			if (flowExecutionKey == null && request instanceof RenderRequest) {
				flowExecutionKey = (String) session.getAttribute(EXECUTION_ATTRIBUTE);
			} else if (flowExecutionKey != null && request instanceof ActionRequest) {
				session.removeAttribute(EXECUTION_ATTRIBUTE);
			}
		}
		return flowExecutionKey;
	}

	public void setFlowExecutionRenderParameter(String flowExecutionKey, ActionResponse response) {
		response.setRenderParameter(EXECUTION_ATTRIBUTE, flowExecutionKey);
	}

	public void setFlowExecutionInSession(String flowExecutionKey, RenderRequest request) {
		PortletSession session = request.getPortletSession();
		session.setAttribute(EXECUTION_ATTRIBUTE, flowExecutionKey);
	}

	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, RenderResponse response) {
		PortletURL url = response.createActionURL();
		url.setParameter(EXECUTION_ATTRIBUTE, flowExecutionKey);
		return url.toString();
	}
}
