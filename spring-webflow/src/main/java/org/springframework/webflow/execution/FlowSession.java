/*
 * Copyright 2002-2006 the original author or authors.
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
 * A single, local instantiation of a {@link FlowDefinition flow definition}
 * launched within an overall flow execution.
 * <p>
 * This object maintains all instance state including session status within
 * exactly one governing FlowExecution, as well as the current flow state. This
 * object also acts as the local "flow scope" data model. Data in
 * {@link #getScope() flow scope} lives for the life of this object and is
 * cleaned up automatically when this object is destroyed. Destruction happens
 * when this session enters an end state.
 * <p>
 * A flow session will go through several status changes during its lifecycle.
 * Initially it will be {@link FlowSessionStatus#CREATED} when a new execution
 * is started.
 * <p>
 * After passing through the {@link FlowSessionStatus#STARTING} status, the flow
 * session is activated (about to be manipulated) and its status becomes
 * {@link FlowSessionStatus#ACTIVE}. In the case of a new execution session
 * activation happens immediately after creation to put the "root flow" at the
 * top of the stack and transition it to its start state.
 * <p>
 * When control returns to the client for user think time the status is updated
 * to {@link FlowSessionStatus#PAUSED}. The flow is no longer actively
 * processing then, as it is stored off to a repository waiting on the user to
 * resume.
 * <p>
 * If a flow session is pushed down in the stack because a subflow is spawned,
 * its status becomes {@link FlowSessionStatus#SUSPENDED} until the subflow
 * returns (ends) and is popped off the stack. The resuming flow session then
 * becomes active once again.
 * <p>
 * When a flow session is terminated because an EndState is reached its status
 * becomes {@link FlowSessionStatus#ENDED}, which ends its life. When this
 * happens the session is popped off the stack and discarded, and any allocated
 * resources in "flow scope" are automatically cleaned up.
 * <p>
 * Note that a flow <i>session</i> is in no way linked to an HTTP session. It
 * just uses the familiar "session" naming convention to denote a stateful
 * object.
 * 
 * @see FlowDefinition
 * @see FlowExecution
 * @see FlowSessionStatus
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
	 * Returns the current state of this flow session. This value changes as the
	 * flow executes.
	 */
	public StateDefinition getState();

	/**
	 * Returns the current status of this flow session. This value changes as
	 * the flow executes.
	 */
	public FlowSessionStatus getStatus();

	/**
	 * Return this session's local attributes; the basis for "flow scope" (flow
	 * session scope).
	 * @return the flow scope attributes
	 */
	public MutableAttributeMap getScope();

	/**
	 * Returns the local "flash map". Attributes in this map are cleared out
	 * on the next event signaled in the flow execution, so they survive a refresh.
	 * @return the flash map
	 */
	public MutableAttributeMap getFlashMap();

	/**
	 * Returns the parent flow session in the current flow execution, or
	 * <code>null</code> if there is no parent flow session.
	 */
	public FlowSession getParent();

	/**
	 * Returns whether this flow session is the root flow session in the ongoing
	 * flow execution. The root flow session does not have a parent flow
	 * session.
	 */
	public boolean isRoot();

}