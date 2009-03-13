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
 * Default implementation of the resultObject-to-event mapping interface. Always returns the "success" event.
 * 
 * @author Keith Donald
 */
public class SuccessEventFactory extends EventFactorySupport implements ResultEventFactory {

	public Event createResultEvent(Object source, Object resultObject, RequestContext context) {
		return success(source, resultObject);
	}
}