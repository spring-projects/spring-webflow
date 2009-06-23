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

import java.util.Iterator;

import javax.portlet.PortletRequest;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Map backed by the Portlet request attribute map for accessing request local attributes.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class PortletRequestMap extends StringKeyedMapAdapter {

	/**
	 * The wrapped portlet request.
	 */
	private PortletRequest request;

	/**
	 * Create a new map wrapping the attributes of given request.
	 */
	public PortletRequestMap(PortletRequest request) {
		this.request = request;
	}

	protected Object getAttribute(String key) {
		return request.getAttribute(key);
	}

	protected void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	protected void removeAttribute(String key) {
		request.removeAttribute(key);
	}

	protected Iterator getAttributeNames() {
		return CollectionUtils.toIterator(request.getAttributeNames());
	}
}