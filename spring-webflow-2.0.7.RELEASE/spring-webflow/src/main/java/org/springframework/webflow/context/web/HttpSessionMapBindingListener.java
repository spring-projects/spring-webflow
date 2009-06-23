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
package org.springframework.webflow.context.web;

import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.webflow.core.collection.AttributeMapBindingEvent;
import org.springframework.webflow.core.collection.AttributeMapBindingListener;
import org.springframework.webflow.core.collection.LocalAttributeMap;

/**
 * Helper class that adapts a generic {@link AttributeMapBindingListener} to a HTTP specific
 * {@link HttpSessionBindingListener}. Calls will be forwarded to the wrapped listener.
 * 
 * @author Keith Donald
 */
public class HttpSessionMapBindingListener implements HttpSessionBindingListener {

	private AttributeMapBindingListener listener;

	private Map sessionMap;

	/**
	 * Create a new wrapper for given listener.
	 * @param listener the listener to wrap
	 * @param sessionMap the session map containing the listener
	 */
	public HttpSessionMapBindingListener(AttributeMapBindingListener listener, Map sessionMap) {
		this.listener = listener;
		this.sessionMap = sessionMap;
	}

	/**
	 * Returns the wrapped listener.
	 */
	public AttributeMapBindingListener getListener() {
		return listener;
	}

	/**
	 * Returns the session map containing the listener.
	 */
	public Map getSessionMap() {
		return sessionMap;
	}

	public void valueBound(HttpSessionBindingEvent event) {
		listener.valueBound(getContextBindingEvent(event));
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		listener.valueUnbound(getContextBindingEvent(event));
	}

	/**
	 * Create a attribute map binding event for given HTTP session binding event.
	 */
	private AttributeMapBindingEvent getContextBindingEvent(HttpSessionBindingEvent event) {
		return new AttributeMapBindingEvent(new LocalAttributeMap(sessionMap), event.getName(), listener);
	}
}