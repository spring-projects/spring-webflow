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
package org.springframework.webflow.core.collection;

/**
 * Causes an object to be notified when it is bound or unbound from an {@link AttributeMap}.
 * <p>
 * Note that this is an optional feature and not all {@link AttributeMap} implementations support it.
 * 
 * @see AttributeMap
 * 
 * @author Ben Hale
 */
public interface AttributeMapBindingListener {

	/**
	 * Called when the implementing instance is bound into an <code>AttributeMap</code>.
	 * @param event information about the binding event
	 */
	void valueBound(AttributeMapBindingEvent event);

	/**
	 * Called when the implementing instance is unbound from an <code>AttributeMap</code>.
	 * @param event information about the unbinding event
	 */
	void valueUnbound(AttributeMapBindingEvent event);
}