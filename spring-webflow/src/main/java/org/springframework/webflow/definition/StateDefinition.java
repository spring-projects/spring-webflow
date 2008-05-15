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
 * A step within a {@link FlowDefinition flow definition} where behavior is executed.
 * <p>
 * States have identifiers that are local to their containing flow definitions. They may also be annotated with
 * attributes.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface StateDefinition extends Annotated {

	/**
	 * Returns the flow definition this state belongs to.
	 * @return the owning flow definition
	 */
	public FlowDefinition getOwner();

	/**
	 * Returns this state's identifier, locally unique to is containing flow definition.
	 * @return the state identifier
	 */
	public String getId();

	/**
	 * Returns true if this state is a view state.
	 * @return true if a view state, false otherwise
	 */
	public boolean isViewState();
}