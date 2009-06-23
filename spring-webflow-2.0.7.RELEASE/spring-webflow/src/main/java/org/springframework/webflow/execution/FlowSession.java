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
package org.springframework.webflow.execution;

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;

/**
 * A single, local instantiation of a {@link FlowDefinition flow definition} launched within an overall flow execution.
 * <p>
 * This object maintains all instance state including session status within exactly one governing FlowExecution, as well
 * as the current flow state. This object also acts as the local "flow scope" data model. Data in
 * {@link #getScope() flow scope} lives for the life of this object and is cleaned up automatically when this object is
 * destroyed. Destruction happens when this session enters an end state.
 * <p>
 * Note that a flow <i>session</i> is in no way linked to an HTTP session. It just uses the familiar "session" naming
 * convention to denote a stateful object.
 * 
 * @see FlowDefinition
 * @see FlowExecution
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowSession {

	/**
	 * Returns the flow definition backing this session.
	 */
	public FlowDefinition getDefinition();

	/**
	 * Returns the current state of this flow session. This value changes as the flow executes.
	 */
	public StateDefinition getState();

	/**
	 * Return this session's local attributes; the basis for "flow scope" (flow session scope).
	 * @return the flow scope attributes
	 */
	public MutableAttributeMap getScope();

	/**
	 * Returns a mutable map for data held in "view scope". Attributes in this map are cleared out when the current view
	 * state exits.
	 * @return view scope
	 * @throws IllegalStateException if this flow session is not currently in a view state
	 */
	public MutableAttributeMap getViewScope() throws IllegalStateException;

	/**
	 * Returns the parent flow session in the current flow execution, or <code>null</code> if there is no parent flow
	 * session.
	 */
	public FlowSession getParent();

	/**
	 * Returns whether this flow session is the root flow session in the ongoing flow execution. The root flow session
	 * does not have a parent flow session.
	 */
	public boolean isRoot();

}