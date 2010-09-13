/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.faces.webflow.context.portlet;

import javax.portlet.PortletRequest;

import org.springframework.webflow.context.portlet.PortletRequestParameterMap;

/**
 * Map backed by a PortletContext for accessing Portlet request parameters. Request parameters can have multiple values.
 * The {@link RequestParameterMap#setUseArrayForMultiValueAttributes(Boolean)} property allows choosing whether the map
 * will return:
 * <ul>
 * <li>String - selects the first value in case of multiple value parameters</li>
 * <li>String[] - wraps single-values parameters as array</li>
 * <li>String or String[] - depends on the values of the parameter</li>
 * </ul>
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 * 
 * @see PortletRequest#getParameter(String)
 * @see PortletRequest#getParameterValues(String)
 */
public class RequestParameterMap extends PortletRequestParameterMap {

	private Boolean useArrayForMultiValueAttributes;

	private PortletRequest portletRequest;

	public RequestParameterMap(PortletRequest portletRequest) {
		super(portletRequest);
		this.portletRequest = portletRequest;
	}

	public void setUseArrayForMultiValueAttributes(Boolean useArrayForMultiValueAttributes) {
		this.useArrayForMultiValueAttributes = useArrayForMultiValueAttributes;
	}

	/**
	 * This property allows choosing what kind of attributes the map will return:
	 * <ol>
	 * <li>String - selects the first value in case of multiple value parameters</li>
	 * <li>String[] - wraps single-values parameters as array</li>
	 * <li>String or String[] - depends on the values of the parameter</li>
	 * </ol>
	 * The above choices correspond to the following values for useArrayForMultiValueAttributes:
	 * <ol>
	 * <li>False</li>
	 * <li>True</li>
	 * <li>null</li>
	 * </ol>
	 * 
	 * @param useArrayForMultiValueAttributes
	 */
	public Boolean useArrayForMultiValueAttributes() {
		return useArrayForMultiValueAttributes;
	}

	@Override
	protected Object getAttribute(String key) {
		if (null == useArrayForMultiValueAttributes) {
			return super.getAttribute(key);
		} else {
			if (useArrayForMultiValueAttributes) {
				return portletRequest.getParameterValues(key);
			} else {
				return portletRequest.getParameter(key);
			}
		}
	}

}