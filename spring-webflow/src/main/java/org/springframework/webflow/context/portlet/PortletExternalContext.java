/*
 * Copyright 2004-2007 the original author or authors.
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
	 * An accessor for the portlet request parameter map.
	 */
	private ParameterMap requestParameterMap;

	/**
	 * An accessor for the portlet request attribute map.
	 */
	private MutableAttributeMap requestMap;

	/**
	 * An accessor for the local portlet session map.
	 */
	private SharedAttributeMap sessionMap;

	/**
	 * An accessor for the global portlet session map.
	 */
	private SharedAttributeMap globalSessionMap;

	/**
	 * An accessor for the portlet context application map.
	 */
	private SharedAttributeMap applicationMap;
	
	/**
	 * An accessor for the portlet user info map.
	 */
	private MutableAttributeMap userInfoMap;

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
		this.requestParameterMap = new LocalParameterMap(new PortletRequestParameterMap(request));
		this.requestMap = new LocalAttributeMap(new PortletRequestMap(request));
		this.sessionMap = new LocalSharedAttributeMap(new PortletSessionMap(request, PortletSession.PORTLET_SCOPE));
		this.globalSessionMap = new LocalSharedAttributeMap(new PortletSessionMap(request, PortletSession.APPLICATION_SCOPE));
		this.applicationMap = new LocalSharedAttributeMap(new PortletContextMap(context));
		Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
		this.userInfoMap = userInfo != null ? new LocalAttributeMap(userInfo) : null;
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getDispatcherPath() {
		// returns null in a portlet environment
		return null;
	}

	public String getRequestPathInfo() {
		// returns null in a portlet environment
		return null;
	}

	public ParameterMap getRequestParameterMap() {
		return requestParameterMap;
	}

	public MutableAttributeMap getRequestMap() {
		return requestMap;
	}
	
	public SharedAttributeMap getSessionMap() {
		return sessionMap;
	}

	public SharedAttributeMap getGlobalSessionMap() {
		return globalSessionMap;
	}

	public SharedAttributeMap getApplicationMap() {
		return applicationMap;
	}

	/**
	 * Returns the {@link PortletRequest#USER_INFO} map as a mutable attribute map.
	 * @return the Portlet user info
	 */
	public MutableAttributeMap getUserInfoMap() {
		return userInfoMap;
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