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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.AnnotatedObject;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A point in a flow where something happens. What happens is determined by a state's type. Standard types of states
 * include action states, view states, subflow states, and end states.
 * <p>
 * Each state is associated with exactly one owning flow definition. Specializations of this class capture all the
 * configuration information needed for a specific kind of state.
 * <p>
 * Subclasses should implement the <code>doEnter</code> method to execute the processing that should occur when this
 * state is entered, acting on its configuration information. The ability to plug-in custom state types that execute
 * different behaviors is the classic GoF state pattern.
 * <p>
 * Equality: Two states are equal if they have the same id and are part of the same flow.
 * 
 * @see org.springframework.webflow.engine.TransitionableState
 * @see org.springframework.webflow.engine.ActionState
 * @see org.springframework.webflow.engine.ViewState
 * @see org.springframework.webflow.engine.SubflowState
 * @see org.springframework.webflow.engine.EndState
 * @see org.springframework.webflow.engine.DecisionState
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class State extends AnnotatedObject implements StateDefinition {

	/**
	 * Logger, for use in subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * The state's owning flow.
	 */
	private Flow flow;

	/**
	 * The state identifier, unique to the owning flow.
	 */
	private String id;

	/**
	 * The list of actions to invoke when this state is entered.
	 */
	private ActionList entryActionList = new ActionList();

	/**
	 * The set of exception handlers for this state.
	 */
	private FlowExecutionExceptionHandlerSet exceptionHandlerSet = new FlowExecutionExceptionHandlerSet();

	/**
	 * Creates a state for the provided <code>flow</code> identified by the provided <code>id</code>. The id must
	 * be locally unique to the owning flow. The state will be automatically added to the flow.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException if this state cannot be added to the flow, for instance when the provided id is
	 * not unique in the owning flow
	 * @see #getEntryActionList()
	 * @see #getExceptionHandlerSet()
	 */
	protected State(Flow flow, String id) throws IllegalArgumentException {
		setId(id);
		setFlow(flow);
	}

	// implementing StateDefinition

	public FlowDefinition getOwner() {
		return flow;
	}

	public String getId() {
		return id;
	}

	public boolean isViewState() {
		return false;
	}

	// implementation specific

	/**
	 * Returns the owning flow.
	 */
	public Flow getFlow() {
		return flow;
	}

	/**
	 * Set the owning flow.
	 * @throws IllegalArgumentException if this state cannot be added to the flow
	 */
	private void setFlow(Flow flow) throws IllegalArgumentException {
		Assert.hasText(getId(), "The id of the state should be set before adding the state to a flow");
		Assert.notNull(flow, "The owning flow is required");
		this.flow = flow;
		flow.add(this);
	}

	/**
	 * Set the state identifier, unique to the owning flow.
	 * @param id the state identifier
	 */
	private void setId(String id) {
		Assert.hasText(id, "This state must have a valid identifier");
		this.id = id;
	}

	/**
	 * Returns the list of actions executed by this state when it is entered. The returned list is mutable.
	 * @return the state entry action list
	 */
	public ActionList getEntryActionList() {
		return entryActionList;
	}

	/**
	 * Returns a mutable set of exception handlers, allowing manipulation of how exceptions are handled when thrown
	 * within this state.
	 * <p>
	 * Exception handlers are invoked when an exception occurs when this state is entered, and can execute custom
	 * exception handling logic as well as select an error view to display.
	 * @return the state exception handler set
	 */
	public FlowExecutionExceptionHandlerSet getExceptionHandlerSet() {
		return exceptionHandlerSet;
	}

	/**
	 * Returns a flag indicating if this state is the start state of its owning flow.
	 * @return true if the flow is the start state, false otherwise
	 */
	public boolean isStartState() {
		return flow.getStartState() == this;
	}

	// id and flow based equality

	public boolean equals(Object o) {
		if (!(o instanceof State)) {
			return false;
		}
		State other = (State) o;
		return id.equals(other.id) && flow.equals(other.flow);
	}

	public int hashCode() {
		return id.hashCode() + flow.hashCode();
	}

	// behavioral methods

	/**
	 * Enter this state in the provided flow control context. This implementation just calls the
	 * {@link #doEnter(RequestControlContext)} hook method, which should be implemented by subclasses, after executing
	 * the entry actions.
	 * @param context the control context for the currently executing flow, used by this state to manipulate the flow
	 * execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	public final void enter(RequestControlContext context) throws FlowExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering state '" + getId() + "' of flow '" + getFlow().getId() + "'");
		}
		context.setCurrentState(this);
		doPreEntryActions(context);
		entryActionList.execute(context);
		doEnter(context);
	}

	/**
	 * Hook method to execute before running state entry actions upon state entry. Does nothing by default. Subclasses
	 * may override.
	 * @param context the request control context
	 * @throws FlowExecutionException if an exception occurs
	 */
	protected void doPreEntryActions(RequestControlContext context) throws FlowExecutionException {

	}

	/**
	 * Hook method to execute custom behavior as a result of entering this state. By implementing this method subclasses
	 * specialize the behavior of the state.
	 * @param context the control context for the currently executing flow, used by this state to manipulate the flow
	 * execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected abstract void doEnter(RequestControlContext context) throws FlowExecutionException;

	/**
	 * Handle an exception that occurred in this state during the context of the current flow execution request.
	 * @param exception the exception that occurred
	 * @param context the flow execution control context
	 */
	public boolean handleException(FlowExecutionException exception, RequestControlContext context) {
		return getExceptionHandlerSet().handleException(exception, context);
	}

	public String toString() {
		ToStringCreator creator = new ToStringCreator(this).append("id", getId()).append("flow", flow.getId()).append(
				"entryActionList", entryActionList).append("exceptionHandlerSet", exceptionHandlerSet);
		appendToString(creator);
		return creator.toString();
	}

	/**
	 * Subclasses may override this hook method to print their internal state to a string. This default implementation
	 * does nothing.
	 * @param creator the toString creator, to print properties to string
	 * @see #toString()
	 */
	protected void appendToString(ToStringCreator creator) {
	}
}