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

import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy for calculating the target state of a transition. This facilitates dynamic transition target state
 * resolution that takes into account runtime contextual information.
 * 
 * @author Keith Donald
 */
public interface TargetStateResolver {

	/**
	 * Resolve the target state of the transition from the source state in the current request context. Should never
	 * return null.
	 * @param transition the transition
	 * @param sourceState the source state of the transition, could be null
	 * @param context the current request context
	 * @return the transition's target state - may be null if no state change should occur
	 */
	public State resolveTargetState(Transition transition, State sourceState, RequestContext context);
}