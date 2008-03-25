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
package org.springframework.webflow.context.portlet;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

/**
 * Default flow URL handler for SWF 2.
 * 
 * @author Scott Andrews
 */
public class DefaultFlowUrlHandler implements FlowUrlHandler {

	public String getFlowExecutionKey(PortletRequest request) {
		return request.getParameter("execution");
	}

	public void setFlowExecutionRenderParameter(String flowExecutionKey, ActionResponse response) {
		response.setRenderParameter("execution", flowExecutionKey);
	}

	public String createFlowExecutionUrl(String flowId, String flowExecutionKey, RenderResponse response) {
		PortletURL url = response.createActionURL();
		url.setParameter("execution", flowExecutionKey);
		return url.toString();
	}
}
