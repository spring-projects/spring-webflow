/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.context.servlet;

import java.util.Iterator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.binding.collection.SharedMap;
import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.context.web.HttpSessionMapBindingListener;
import org.springframework.webflow.core.collection.AttributeMapBindingListener;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * A Shared Map backed by the Servlet HTTP session, for accessing session scoped attributes.
 *
 * @author Keith Donald
 */
public class HttpSessionMap extends StringKeyedMapAdapter<Object> implements SharedMap<String, Object> {

	/**
	 * The wrapped HTTP request, providing access to the session.
	 */
	private HttpServletRequest request;

	/**
	 * Create a map wrapping the session of given request.
	 */
	public HttpSessionMap(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Internal helper to get the HTTP session associated with the wrapped request, or null if there is no such session.
	 * <p>
	 * Note that this method will not force session creation.
	 */
	private HttpSession getSession() {
		return request.getSession(false);
	}

	protected Object getAttribute(String key) {
		HttpSession session = getSession();
		if (session == null) {
			return null;
		}
		Object value = session.getAttribute(key);
		if (value instanceof HttpSessionMapBindingListener) {
			// unwrap
			return ((HttpSessionMapBindingListener) value).getListener();
		} else {
			return value;
		}
	}

	protected void setAttribute(String key, Object value) {
		// force session creation
		HttpSession session = request.getSession(true);
		if (value instanceof AttributeMapBindingListener) {
			// wrap
			session.setAttribute(key, new HttpSessionMapBindingListener((AttributeMapBindingListener) value, this));
		} else {
			session.setAttribute(key, value);
		}
	}

	protected void removeAttribute(String key) {
		HttpSession session = getSession();
		if (session != null) {
			session.removeAttribute(key);
		}
	}

	protected Iterator<String> getAttributeNames() {
		HttpSession session = getSession();
		return (session == null) ?
				CollectionUtils.<String>emptyIterator() : CollectionUtils.toIterator(session.getAttributeNames());
	}

	public Object getMutex() {
		// force session creation
		HttpSession session = request.getSession(true);
		Object mutex = session.getAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
		return mutex != null ? mutex : session;
	}
}
