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

import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;

/**
 * A context for a single request to manipulate a flow execution. Allows Web Flow users to access contextual information
 * about the executing request, as well as the governing {@link #getFlowExecutionContext() active flow execution}.
 * <p>
 * The term <i>request</i> is used to describe a single call (thread) into the flow system by an external actor to
 * manipulate exactly one flow execution.
 * <p>
 * A new instance of this object is typically created when one of the core operations supported by a flow execution is
 * invoked, either <code>start</code> to launch the flow execution, <code>signalEvent</code> to resume the flow
 * execution, or <code>refresh</code> to reconstitute the flow execution's last view selection for purposes of reissuing
 * a user response.
 * <p>
 * Once created this context object is passed around throughout flow execution request processing where it may be
 * accessed and reasoned upon by SWF-internal artifacts such as states, user-implemented action code, and state
 * transition criteria.
 * <p>
 * When a call into a flow execution returns this object goes out of scope and is disposed of automatically. Thus a
 * request context is an internal artifact used within a FlowExecution: this object is not exposed to external client
 * code, e.g. a view implementation (JSP).
 * <p>
 * The {@link #getRequestScope() requestScope} property may be used as a store for arbitrary data that should exist for
 * the life of this object.
 * <p>
 * The web flow system will ensure that a RequestContext object is local to the current thread. It can be safely
 * manipulated without needing to worry about concurrent access.
 * <p>
 * Note: this request context is in no way linked to an HTTP or Portlet request. It uses the familiar "request" naming
 * convention to indicate a single call to manipulate a runtime execution of a flow definition.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface RequestContext {

	/**
	 * Returns the definition of the flow that is currently executing.
	 * @return the flow definition for the active session
	 * @throws IllegalStateException if the flow execution has not been started at all, or if the execution has ended
	 * and is no longer actively executing
	 */
	public FlowDefinition getActiveFlow() throws IllegalStateException;

	/**
	 * Returns the current state of the executing flow. May return <code>null</code> if this flow execution is in the
	 * process of starting and has not yet entered its start state.
	 * @return the current state, or <code>null</code> if in the process of starting
	 * @throws IllegalStateException if this flow execution has not been started at all, or if this execution has ended
	 * and is no longer actively executing
	 */
	public StateDefinition getCurrentState() throws IllegalStateException;

	/**
	 * Returns the transition that would execute on the occurrence of the given event.
	 * @param eventId the id of the user event
	 * @return the transition that would trigger, or <code>null</code> if no transition matches
	 * @throws IllegalStateException if this flow execution has not been started at all, or if this execution has ended
	 * and is no longer actively executing
	 */
	public TransitionDefinition getMatchingTransition(String eventId) throws IllegalStateException;

	/**
	 * Returns true if the flow is currently active and in a view state. When in a view state {@link #getViewScope()},
	 * can be safely called.
	 * @see #getViewScope()
	 * @return true if in a view state, false if not
	 */
	public boolean inViewState();

	/**
	 * Returns a mutable map for accessing and/or setting attributes in request scope. <b>Request scoped attributes
	 * exist for the duration of this request only.</b>
	 * @return the request scope
	 */
	public MutableAttributeMap getRequestScope();

	/**
	 * Returns a mutable map for accessing and/or setting attributes in flash scope. <b>Flash scoped attributes exist
	 * until the next event is signaled in the flow execution.</b>
	 * @return the flash scope
	 */
	public MutableAttributeMap getFlashScope();

	/**
	 * Returns a mutable map for accessing and/or setting attributes in view scope. <b>View scoped attributes exist for
	 * the life of the current view state.</b>
	 * @return the view scope
	 * @see #inViewState()
	 * @throws IllegalStateException this flow is not in a view-state
	 */
	public MutableAttributeMap getViewScope() throws IllegalStateException;

	/**
	 * Returns a mutable map for accessing and/or setting attributes in flow scope. <b>Flow scoped attributes exist for
	 * the life of the active flow session.</b>
	 * @return the flow scope
	 * @see FlowSession
	 */
	public MutableAttributeMap getFlowScope();

	/**
	 * Returns a mutable accessor for accessing and/or setting attributes in conversation scope. <b>Conversation scoped
	 * attributes exist for the life of the executing flow and are shared across all flow sessions.</b>
	 * @return the conversation scope
	 * @see FlowExecutionContext
	 */
	public MutableAttributeMap getConversationScope();

	/**
	 * Returns the immutable input parameters associated with this request into Spring Web Flow. The map returned is
	 * immutable and cannot be changed.
	 * <p>
	 * This is typically a convenient shortcut for accessing the {@link ExternalContext#getRequestParameterMap()}
	 * directly.
	 * @see #getExternalContext()
	 */
	public ParameterMap getRequestParameters();

	/**
	 * Returns the external client context that originated (or triggered) this request.
	 * <p>
	 * Acting as a facade, the returned context object provides a single point of access to the calling client's
	 * environment. It provides normalized access to attributes of the client environment without tying you to specific
	 * constructs within that environment.
	 * <p>
	 * In addition, this context may be downcastable to a specific context type for a specific client environment, such
	 * as Servlets or Portlets. Such downcasting will give you full access to a native HttpServletRequest, for example.
	 * With that said, for portability reasons you should avoid coupling your flow artifacts to a specific deployment
	 * environment when possible.
	 * @return the originating external context, the one that triggered the current execution request
	 */
	public ExternalContext getExternalContext();

	/**
	 * Returns the message context of this request. Useful for recording messages during the course of flow execution
	 * for display to the client.
	 * @return the message context
	 */
	public MessageContext getMessageContext();

	/**
	 * Returns contextual information about the flow execution itself. Information in this context typically spans more
	 * than one request.
	 * @return the flow execution context
	 */
	public FlowExecutionContext getFlowExecutionContext();

	/**
	 * Returns the current event being processed by this flow. The event may or may not have caused a state transition
	 * to happen.
	 * @return the current event, or null if no event has been signaled yet
	 */
	public Event getCurrentEvent();

	/**
	 * Returns the current transition executing in this request.
	 * @return the current transition, or <code>null</code> if no transition has occurred yet
	 */
	public TransitionDefinition getCurrentTransition();

	/**
	 * Returns a context map for accessing attributes about the state of the current request. These attributes may be
	 * used to influence flow execution behavior.
	 * @return the current attributes of this request, or empty if none are set
	 */
	public MutableAttributeMap getAttributes();

	/**
	 * Returns the URL of this flow execution. Needed by response writers that write out the URL of this flow execution
	 * to allow calling back this execution in a subsequent request.
	 * @throws IllegalStateException if the flow execution has not yet had its key assigned
	 * @return the flow execution URL
	 */
	public String getFlowExecutionUrl() throws IllegalStateException;

}