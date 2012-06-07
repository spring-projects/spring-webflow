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
import org.springframework.webflow.context.portlet.PortletRequestParameterMap;

/**
 * /** Base class for {@link Map}s allowing access to {@link PortletContext} request paramters.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 * 
 * @see SingleValueRequestParameterMap
 * @see MultiValueRequestParameterMap
 */
public abstract class RequestParameterMap<V> extends StringKeyedMapAdapter<V> {

	private final PortletRequest portletRequest;

	private final Delegate delegate;

	public RequestParameterMap(PortletRequest portletRequest) {
		this.portletRequest = portletRequest;
		this.delegate = new Delegate(portletRequest);
	}

	protected final PortletRequest getPortletRequest() {
		return this.portletRequest;
	}

	protected void setAttribute(String key, V value) {
		this.delegate.setAttribute(key, value);
	}

	protected void removeAttribute(String key) {
		this.delegate.removeAttribute(key);
	}

	protected Iterator<String> getAttributeNames() {
		return this.delegate.getAttributeNames();
	}

	private static class Delegate extends PortletRequestParameterMap {

		public Delegate(PortletRequest request) {
			super(request);
		}

		public Object getAttribute(String key) {
			return super.getAttribute(key);
		}

		public void setAttribute(String key, Object value) {
			super.setAttribute(key, value);
		}

		public void removeAttribute(String key) {
			super.removeAttribute(key);
		}

		public Iterator<String> getAttributeNames() {
			return super.getAttributeNames();
		}
	}

}
