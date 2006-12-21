/*
 * Copyright 2002-2006 the original author or authors.
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

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Provides contextual information about a JSR-168 Portlet environment that has
 * called into Spring Web Flow.
 * 
 * @author Keith Donald
 */
public class PortletExternalContext implements ExternalContext {

	/**
	 * The context.
	 */
	private PortletContext context;

	/**
	 * The request.
	 */
	private PortletRequest request;

	/**
	 * The response.
	 */
	private PortletResponse response;

	/**
	 * Create an external context wrapping given Portlet context, request and response.
	 * @param context the Portlet context
	 * @param request the Portlet request
	 * @param response the Portlet response
	 */
	public PortletExternalContext(PortletContext context, PortletRequest request, PortletResponse response) {
		this.context = context;
		this.request = request;
		this.response = response;
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getDispatcherPath() {
		return null;
	}

	public String getRequestPathInfo() {
		return null;
	}

	public ParameterMap getRequestParameterMap() {
		return new LocalParameterMap(new PortletRequestParameterMap(request));
	}

	public MutableAttributeMap getRequestMap() {
		return new LocalAttributeMap(new PortletRequestMap(request));
	}
	
	public SharedAttributeMap getSessionMap() {
		return new LocalSharedAttributeMap(new PortletSessionMap(request, PortletSession.PORTLET_SCOPE));
	}

	public SharedAttributeMap getGlobalSessionMap() {
		return new LocalSharedAttributeMap(new PortletSessionMap(request, PortletSession.APPLICATION_SCOPE));
	}

	public SharedAttributeMap getApplicationMap() {
		return new LocalSharedAttributeMap(new PortletContextMap(context));
	}

	/**
	 * Returns the {@link PortletRequest#USER_INFO} map as a mutable attribute map.
	 * @return the Portlet user info
	 */
	public MutableAttributeMap getUserInfoMap() {
		Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
		if (userInfo != null) {
			return new LocalAttributeMap(userInfo);
		} else {
			return null;
		}
	}

	/**
	 * Returns the wrapped Portlet context.
	 */
	public PortletContext getContext() {
		return context;
	}

	/**
	 * Returns the wrapped Portlet request.
	 */
	public PortletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the wrapped Portlet response.
	 */
	public PortletResponse getResponse() {
		return response;
	}

	public String toString() {
		return new ToStringCreator(this).append("requestParameterMap", getRequestParameterMap()).toString();
	}
}