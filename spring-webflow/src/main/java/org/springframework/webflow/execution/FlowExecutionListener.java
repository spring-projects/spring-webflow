/*
 * Copyright 2002-2007 the original author or authors.
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
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;

/**
 * Interface to be implemented by objects that wish to listen and respond to the
 * lifecycle of {@link FlowExecution flow executions}.
 * <p>
 * An 'observer' that is very aspect like, allowing you to insert 'cross
 * cutting' behavior at well-defined points within one or more well-defined flow
 * execution lifecycles.
 * <p>
 * For example, one custom listener may apply security checks at the flow
 * execution level, preventing a flow from starting or a state from entering if
 * the curent user does not have the necessary permissions. Another listener may
 * track flow execution navigation history to support bread crumbs. Another may
 * perform auditing, or setup and tear down connections to a transactional
 * resource.
 * <p>
 * Note that flow execution listeners are registered with a flow execution when
 * that execution is created by a {@link FlowExecutionFactory factory} or
 * restored by a {@link org.springframework.webflow.execution.repository.FlowExecutionRepository}.
 * Typically a listener will not be registered with a flow execution <i>at
 * runtime</i>, when the flow execution is already active.
 * 
 * @see FlowDefinition
 * @see StateDefinition
 * @see FlowExecution
 * @see RequestContext
 * @see Event
 * @see ViewSelection
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowExecutionListener {

	/**
	 * Called when any client request is submitted to manipulate this flow
	 * execution. This call happens before request processing.
	 * @param context the source of the event
	 */
	public void requestSubmitted(RequestContext context);

	/**
	 * Called when a client request has completed processing.
	 * @param context the source of the event
	 */
	public void requestProcessed(RequestContext context);

	/**
	 * Called immediately after a start event is signaled, indicating a new
	 * session of the flow is starting but has not yet entered its start state.
	 * An exception may be thrown from this method to veto the start operation.
	 * Any type of runtime exception can be used for this purpose.
	 * @param context the source of the event
	 * @param definition the flow for which a new session is starting
	 * @param input a mutable input map to the starting flow session
	 */
	public void sessionStarting(RequestContext context, FlowDefinition definition, MutableAttributeMap input);

	/**
	 * Called when a new flow session has started. At this point the start state
	 * has been entered.
	 * @param context the source of the event
	 * @param session the session that was started
	 */
	public void sessionStarted(RequestContext context, FlowSession session);

	/**
	 * Called when an event is signaled in the current state, but prior to any
	 * state transition.
	 * @param context the source of the event
	 * @param event the event that occured
	 */
	public void eventSignaled(RequestContext context, Event event);

	/**
	 * Called when a state transitions, after the transition is matched but
	 * before the transition occurs.
	 * @param context the source of the event
	 * @param state the proposed state to transition to
	 * @throws EnterStateVetoException when entering the state is not allowed
	 */
	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException;

	/**
	 * Called when a state transitions, after the transition occured.
	 * @param context the source of the event
	 * @param previousState <i>from</i> state of the transition
	 * @param state <i>to</i> state of the transition
	 */
	public void stateEntered(RequestContext context, StateDefinition previousState, StateDefinition state);
	
	/**
	 * Called when a flow execution is paused, for instance when it is waiting
	 * for user input (after event processing).
	 * @param context the source of the event
	 * @param selectedView the view that will display
	 */
	public void paused(RequestContext context, ViewSelection selectedView);

	/**
	 * Called after a flow execution is successfully reactivated after pause
	 * (but before event processing).
	 * @param context the source of the event
	 */
	public void resumed(RequestContext context);

	/**
	 * Called when the active flow execution session has been asked to end but
	 * before it has ended.
	 * @param context the source of the event
	 * @param session the current active session that is ending
	 * @param output the flow output produced by the ending session, this map may
	 * be modified by this listener to affect the output returned
	 */
	public void sessionEnding(RequestContext context, FlowSession session, MutableAttributeMap output);

	/**
	 * Called when a flow execution session ends. If the ended session was the
	 * root session of the flow execution, the entire flow execution also ends.
	 * @param context the source of the event
	 * @param session ending flow session
	 * @param output final, unmodifiable output returned by the ended session
	 */
	public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output);
	
	/**
	 * Called when an exception is thrown during a flow execution, before the
	 * exception is handled by any registered {@link FlowExecutionExceptionHandler handler}.
	 * @param context the source of the exception
	 * @param exception the exception that occurred
	 */
	public void exceptionThrown(RequestContext context, FlowExecutionException exception);
}