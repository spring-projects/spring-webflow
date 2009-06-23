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

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;

/**
 * Interface to be implemented by objects that wish to listen and respond to the lifecycle of
 * {@link FlowExecution flow executions}.
 * <p>
 * An 'observer' that is very aspect like, allowing you to insert 'cross cutting' behavior at well-defined points within
 * one or more well-defined flow execution lifecycles.
 * <p>
 * For example, one custom listener may apply security checks at the flow execution level, preventing a flow from
 * starting or a state from entering if the current user does not have the necessary permissions. Another listener may
 * track flow execution navigation history to support bread crumbs. Another may perform auditing, or setup and tear down
 * connections to a transactional resource.
 * <p>
 * Note that flow execution listeners are registered with a flow execution when that execution is created by a
 * {@link FlowExecutionFactory factory} or restored by a
 * {@link org.springframework.webflow.execution.repository.FlowExecutionRepository}. Typically a listener will not be
 * registered with a flow execution <i>at runtime</i>, when the flow execution is already active.
 * 
 * @see FlowDefinition
 * @see StateDefinition
 * @see FlowExecution
 * @see RequestContext
 * @see Event
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public interface FlowExecutionListener {

	/**
	 * Called when any client request is submitted to manipulate this flow execution. This call happens before request
	 * processing.
	 * @param context the current flow request context
	 */
	public void requestSubmitted(RequestContext context);

	/**
	 * Called when a client request has completed processing.
	 * @param context the source of the event
	 */
	public void requestProcessed(RequestContext context);

	/**
	 * Called to indicate a new flow definition session is about to be created. Called before the session is created. An
	 * exception may be thrown from this method to veto the start operation. Any type of runtime exception can be used
	 * for this purpose.
	 * @param context the current flow request context
	 * @param definition the flow for which a new session is starting
	 */
	public void sessionCreating(RequestContext context, FlowDefinition definition);

	/**
	 * Called after a new flow session has been created but before it starts. Useful for setting arbitrary attributes in
	 * the session before the flow starts.
	 * @param context the current flow request context
	 * @param session the session that was created
	 * @param input a mutable input map - attributes placed in this map are eligible for input mapping by the flow
	 * definition at startup
	 */
	public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input);

	/**
	 * Called after a new flow session has started. At this point the flow's start state has been entered and any other
	 * startup behaviors have been executed.
	 * @param context the current flow request context
	 * @param session the session that was started
	 */
	public void sessionStarted(RequestContext context, FlowSession session);

	/**
	 * Called when an event is signaled in the current state, but prior to any state transition.
	 * @param context the current flow request context
	 * @param event the event that occurred
	 */
	public void eventSignaled(RequestContext context, Event event);

	/**
	 * Called when a transition is matched but before the transition occurs.
	 * @param context the current flow request context
	 * @param transition the proposed transition
	 */
	public void transitionExecuting(RequestContext context, TransitionDefinition transition);

	/**
	 * Called when a state transitions, after the transition is matched but before the transition occurs.
	 * @param context the current flow request context
	 * @param state the proposed state to transition to
	 * @throws EnterStateVetoException when entering the state is not allowed
	 */
	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException;

	/**
	 * Called when a state transitions, after the transition occurred.
	 * @param context the current flow request context
	 * @param previousState <i>from</i> state of the transition
	 * @param state <i>to</i> state of the transition
	 */
	public void stateEntered(RequestContext context, StateDefinition previousState, StateDefinition state);

	/**
	 * Called when a view is about to render in a view-state, before any render actions are executed.
	 * @param context the current flow request context
	 * @param view the view that is about to render
	 * @param viewState the current view state
	 */
	public void viewRendering(RequestContext context, View view, StateDefinition viewState);

	/**
	 * Called after a view has completed rendering.
	 * @param context the current flow request context
	 * @param view the view that rendered
	 * @param viewState the current view state
	 */
	public void viewRendered(RequestContext context, View view, StateDefinition viewState);

	/**
	 * Called when a flow execution is paused, for instance when it is waiting for user input (after event processing).
	 * @param context the current flow request context
	 */
	public void paused(RequestContext context);

	/**
	 * Called after a flow execution is successfully reactivated after pause (but before event processing).
	 * @param context the current flow request context
	 */
	public void resuming(RequestContext context);

	/**
	 * Called when the active flow execution session has been asked to end but before it has ended.
	 * @param context the current flow request context
	 * @param session the current active session that is ending
	 * @param outcome the outcome reached by the ending session, generally the id of the terminating end-state
	 * @param output the flow output produced by the ending session, this map may be modified by this listener to affect
	 * the output returned
	 */
	public void sessionEnding(RequestContext context, FlowSession session, String outcome, MutableAttributeMap output);

	/**
	 * Called when a flow execution session ends. If the ended session was the root session of the flow execution, the
	 * entire flow execution also ends.
	 * @param context the current flow request context
	 * @param session ending flow session
	 * @param outcome the outcome reached by the ended session, generally the id of the terminating end-state
	 * @param output the flow output returned by the ending session
	 */
	public void sessionEnded(RequestContext context, FlowSession session, String outcome, AttributeMap output);

	/**
	 * Called when an exception is thrown during a flow execution, before the exception is handled by any registered
	 * {@link FlowExecutionExceptionHandler handler}.
	 * @param context the current flow request context
	 * @param exception the exception that occurred
	 */
	public void exceptionThrown(RequestContext context, FlowExecutionException exception);

}