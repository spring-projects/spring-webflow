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

import org.springframework.webflow.core.Annotated;

/**
 * A transition takes a flow from one state to another.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface TransitionDefinition extends Annotated {

	/**
	 * The identifier of this transition. This id value should be unique among all other transitions in a set.
	 * @return the transition identifier
	 */
	public String getId();

	/**
	 * Returns an identification of the target state of this transition. This could be an actual static state id or
	 * something more dynamic, like a string representation of an expression evaluating the target state id at flow
	 * execution time.
	 * @return the target state identifier
	 */
	public String getTargetStateId();
}