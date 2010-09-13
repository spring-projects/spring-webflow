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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletRequest;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Map backed by a PortletContext for accessing Portlet request properties. Request properties can have multiple values.
 * The {@link RequestPropertyMap#setUseArrayForMultiValueAttributes(Boolean)} property allows choosing whether the map
 * will return:
 * <ul>
 * <li>String - selects the first element in case of multiple values</li>
 * <li>String[] - wraps single-values attributes as array</li>
 * <li>String or String[] - depends on the values of the property</li>
 * </ul>
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 * 
 * @see PortletRequest#getProperty(String)
 * @see PortletRequest#getProperties(String)
 */
public class RequestPropertyMap extends StringKeyedMapAdapter {

	private Boolean useArrayForMultiValueAttributes;

	private final PortletRequest portletRequest;

	public RequestPropertyMap(PortletRequest portletRequest) {
		this.portletRequest = portletRequest;
	}

	/**
	 * This property allows choosing what kind of attributes the map will return:
	 * <ol>
	 * <li>String - selects the first element in case of multiple values</li>
	 * <li>String[] - wraps single-values attributes as array</li>
	 * <li>String or String[] - depends on the values of the property</li>
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
	public void setUseArrayForMultiValueAttributes(Boolean useArrayForMultiValueAttributes) {
		this.useArrayForMultiValueAttributes = useArrayForMultiValueAttributes;
	}

	public Boolean useArrayForMultiValueAttributes() {
		return useArrayForMultiValueAttributes;
	}

	@Override
	protected Object getAttribute(String key) {
		if (null == useArrayForMultiValueAttributes) {
			List<String> list = Collections.list(portletRequest.getProperties(key));
			if (1 == list.size()) {
				return list.get(0);
			} else {
				return list.toArray(new String[list.size()]);
			}
		} else {
			if (useArrayForMultiValueAttributes) {
				List<String> list = Collections.list(portletRequest.getProperties(key));
				return list.toArray(new String[list.size()]);
			} else {
				return portletRequest.getProperty(key);
			}
		}
	}

	@Override
	protected void setAttribute(String key, Object value) {
		throw new UnsupportedOperationException("Cannot set PortletRequest property");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Cannot remove PortletRequest property");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterator<String> getAttributeNames() {
		return CollectionUtils.toIterator(portletRequest.getPropertyNames());
	}

}
