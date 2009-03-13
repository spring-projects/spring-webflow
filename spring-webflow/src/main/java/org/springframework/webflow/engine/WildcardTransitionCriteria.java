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
package org.springframework.webflow.engine;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.springframework.webflow.execution.RequestContext;

/**
 * Transition criteria that always returns true.
 * 
 * @author Keith Donald
 */
public class WildcardTransitionCriteria implements TransitionCriteria, Serializable {

	/*
	 * Implementation note: not located in webflow.execution.support package to avoid a cyclic dependency between
	 * webflow.execution and webflow.execution.support.
	 */

	/**
	 * Event id value ("*") that will cause the transition to match on any event.
	 */
	public static final String WILDCARD_EVENT_ID = "*";

	/**
	 * Shared instance of a TransitionCriteria that always returns true.
	 */
	public static final WildcardTransitionCriteria INSTANCE = new WildcardTransitionCriteria();

	/**
	 * Private constructor because this is a singleton.
	 */
	private WildcardTransitionCriteria() {
	}

	public boolean test(RequestContext context) {
		return true;
	}

	// resolve the singleton instance
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}

	public String toString() {
		return WILDCARD_EVENT_ID;
	}
}