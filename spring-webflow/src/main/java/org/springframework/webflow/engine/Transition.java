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
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A path from one {@link TransitionableState state} to another {@link State state}.
 * <p>
 * When executed a transition takes a flow execution from its current state, called the <i>source state</i>, to another
 * state, called the </i>target state</i>. A transition may become eligible for execution on the occurrence of an
 * {@link Event} from within a transitionable source state.
 * <p>
 * When an event occurs within this transition's source <code>TransitionableState</code> the determination of the
 * eligibility of this transition is made by a <code>TransitionCriteria</code> object called the <i>matching criteria</i>.
 * If the matching criteria returns <code>true</code> this transition is marked eligible for execution for that event.
 * <p>
 * Determination as to whether an eligible transition should be allowed to execute is made by a
 * <code>TransitionCriteria</code> object called the <i>execution criteria</i>. If the execution criteria test fails
 * this transition will <i>roll back</i> and reenter its source state. If the execution criteria test succeeds this
 * transition will execute and take the flow to the transition's target state.
 * <p>
 * The target state of this transition is typically specified at configuration time in a static manner. If the target
 * state of this transition needs to be calculated in a dynamic fashion at runtime configure a
 * {@link TargetStateResolver} that supports such calculations.
 * 
 * @see TransitionableState
 * @see TransitionCriteria
 * @see TargetStateResolver
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class Transition extends AnnotatedObject implements TransitionDefinition {

	/**
	 * Logger, for use in subclasses.
	 */
	protected final Log logger = LogFactory.getLog(Transition.class);

	/**
	 * The criteria that determine whether or not this transition matches as eligible for execution when an event occurs
	 * in the source state.
	 */
	private TransitionCriteria matchingCriteria;

	/**
	 * The criteria that determine whether or not this transition, once matched, should complete execution or should
	 * <i>roll back</i>.
	 */
	private TransitionCriteria executionCriteria = WildcardTransitionCriteria.INSTANCE;

	/**
	 * The resolver responsible for calculating the target state of this transition.
	 */
	private TargetStateResolver targetStateResolver;

	/**
	 * Create a new transition that always matches and always executes, but its execution does nothing by default.
	 * @see #setMatchingCriteria(TransitionCriteria)
	 * @see #setExecutionCriteria(TransitionCriteria)
	 * @see #setTargetStateResolver(TargetStateResolver)
	 */
	public Transition() {
		this(WildcardTransitionCriteria.INSTANCE, null);
	}

	/**
	 * Create a new transition that always matches and always executes, transitioning to the target state calculated by
	 * the provided targetStateResolver.
	 * @param targetStateResolver the resolver of the target state of this transition
	 * @see #setMatchingCriteria(TransitionCriteria)
	 * @see #setExecutionCriteria(TransitionCriteria)
	 */
	public Transition(TargetStateResolver targetStateResolver) {
		this(WildcardTransitionCriteria.INSTANCE, targetStateResolver);
	}

	/**
	 * Create a new transition that matches on the specified criteria, transitioning to the target state calculated by
	 * the provided targetStateResolver.
	 * @param matchingCriteria the criteria for matching this transition
	 * @param targetStateResolver the resolver of the target state of this transition
	 * @see #setExecutionCriteria(TransitionCriteria)
	 */
	public Transition(TransitionCriteria matchingCriteria, TargetStateResolver targetStateResolver) {
		setMatchingCriteria(matchingCriteria);
		setTargetStateResolver(targetStateResolver);
	}

	// implementing transition definition

	public String getId() {
		return matchingCriteria.toString();
	}

	public String getTargetStateId() {
		if (targetStateResolver != null) {
			return targetStateResolver.toString();
		} else {
			return null;
		}
	}

	/**
	 * Returns the criteria that determine whether or not this transition matches as eligible for execution.
	 * @return the transition matching criteria
	 */
	public TransitionCriteria getMatchingCriteria() {
		return matchingCriteria;
	}

	/**
	 * Set the criteria that determine whether or not this transition matches as eligible for execution.
	 * @param matchingCriteria the transition matching criteria
	 */
	public void setMatchingCriteria(TransitionCriteria matchingCriteria) {
		Assert.notNull(matchingCriteria, "The criteria for matching this transition is required");
		this.matchingCriteria = matchingCriteria;
	}

	/**
	 * Returns the criteria that determine whether or not this transition, once matched, should complete execution or
	 * should <i>roll back</i>.
	 * @return the transition execution criteria
	 */
	public TransitionCriteria getExecutionCriteria() {
		return executionCriteria;
	}

	/**
	 * Set the criteria that determine whether or not this transition, once matched, should complete execution or should
	 * <i>roll back</i>.
	 * @param executionCriteria the transition execution criteria
	 */
	public void setExecutionCriteria(TransitionCriteria executionCriteria) {
		this.executionCriteria = executionCriteria;
	}

	/**
	 * Returns this transition's target state resolver.
	 */
	public TargetStateResolver getTargetStateResolver() {
		return targetStateResolver;
	}

	/**
	 * Set this transition's target state resolver, to calculate what state to transition to when this transition is
	 * executed.
	 * @param targetStateResolver the target state resolver
	 */
	public void setTargetStateResolver(TargetStateResolver targetStateResolver) {
		this.targetStateResolver = targetStateResolver;
	}

	/**
	 * Checks if this transition is eligible for execution given the state of the provided flow execution request
	 * context.
	 * @param context the flow execution request context
	 * @return true if this transition should execute, false otherwise
	 */
	public boolean matches(RequestContext context) {
		return matchingCriteria.test(context);
	}

	/**
	 * Checks if this transition can complete its execution or should be rolled back, given the state of the flow
	 * execution request context.
	 * @param context the flow execution request context
	 * @return true if this transition can complete execution, false if it should roll back
	 */
	public boolean canExecute(RequestContext context) {
		if (executionCriteria != null) {
			return executionCriteria.test(context);
		} else {
			return false;
		}
	}

	/**
	 * Execute this state transition. Should only be called if the {@link #matches(RequestContext)} method returns true
	 * for the given context.
	 * @param sourceState the source state to transition from, may be null if the current state is null
	 * @param context the flow execution control context
	 * @return a boolean indicating if executing this transition caused the current state to exit and a new state to
	 * enter
	 * @throws FlowExecutionException when transition execution fails
	 */
	public boolean execute(State sourceState, RequestControlContext context) throws FlowExecutionException {
		if (canExecute(context)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Executing " + this);
			}
			context.setCurrentTransition(this);
			if (targetStateResolver != null) {
				State targetState = targetStateResolver.resolveTargetState(this, sourceState, context);
				if (targetState != null) {
					if (sourceState != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Exiting state '" + sourceState.getId() + "'");
						}
						if (sourceState instanceof TransitionableState) {
							((TransitionableState) sourceState).exit(context);
						}
					}
					targetState.enter(context);
					if (logger.isDebugEnabled()) {
						if (context.getFlowExecutionContext().isActive()) {
							logger.debug("Completed transition execution.  As a result, the new state is '"
									+ context.getCurrentState().getId() + "' in flow '"
									+ context.getActiveFlow().getId() + "'");
						} else {
							logger.debug("Completed transition execution.  As a result, the flow execution has ended");
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public String toString() {
		return new ToStringCreator(this).append("on", getMatchingCriteria()).append("to", getTargetStateResolver())
				.toString();
	}
}