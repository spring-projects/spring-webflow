/*
 * Copyright 2004-2012 the original author or authors.
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

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Base class for {@link Map}s allowing access to {@link PortletContext} request properties.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 * 
 * @see SingleValueRequestPropertyMap
 * @see MultiValueRequestPropertyMap
 */
public abstract class RequestPropertyMap<V> extends StringKeyedMapAdapter<V> {

	private final PortletRequest portletRequest;

	public RequestPropertyMap(PortletRequest portletRequest) {
		this.portletRequest = portletRequest;
	}

	protected final PortletRequest getPortletRequest() {
		return portletRequest;
	}

	@Override
	protected void setAttribute(String key, V value) {
		throw new UnsupportedOperationException("Cannot set PortletRequest property");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Cannot remove PortletRequest property");
	}

	@Override
	protected Iterator<String> getAttributeNames() {
		return CollectionUtils.toIterator(portletRequest.getPropertyNames());
	}
}
