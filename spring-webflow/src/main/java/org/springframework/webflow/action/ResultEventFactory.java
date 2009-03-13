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
package org.springframework.webflow.action;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy for creating an {@link Event} object from an arbitrary object such as an expression evaluation result or
 * bean method return value.
 * 
 * @author Keith Donald
 */
public interface ResultEventFactory {

	/**
	 * Create an event instance from the result object.
	 * @param source the source of the event
	 * @param resultObject the result object, typically the return value of a bean method
	 * @param context a flow execution request context
	 * @return the event
	 */
	public Event createResultEvent(Object source, Object resultObject, RequestContext context);
}