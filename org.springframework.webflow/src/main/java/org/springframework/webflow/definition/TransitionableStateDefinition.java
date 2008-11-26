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
package org.springframework.webflow.definition;

/**
 * A state that can transition to another state.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface TransitionableStateDefinition extends StateDefinition {

	/**
	 * Returns the available transitions out of this state.
	 * @return the available state transitions
	 */
	public TransitionDefinition[] getTransitions();

	/**
	 * Returns the transition that matches the event with the provided id.
	 * @param eventId the event id
	 * @return the transition that matches, or null if no match is found.
	 */
	public TransitionDefinition getTransition(String eventId);
}