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

import java.util.Iterator;

import org.springframework.core.style.StylerUtils;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.definition.TransitionableStateDefinition;
import org.springframework.webflow.execution.RequestContext;

/**
 * Abstract superclass for states that can execute a transition in response to an event.
 * 
 * @see org.springframework.webflow.engine.Transition
 * @see org.springframework.webflow.engine.TransitionCriteria
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class TransitionableState extends State implements TransitionableStateDefinition {

	/**
	 * The set of possible transitions out of this state.
	 */
	private TransitionSet transitions = new TransitionSet();

	/**
	 * An actions to execute when exiting this state.
	 */
	private ActionList exitActionList = new ActionList();

	/**
	 * Create a new transitionable state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException when this state cannot be added to given flow, for instance when the id is not
	 * unique
	 * @see State#State(Flow, String)
	 * @see #getTransitionSet()
	 */
	protected TransitionableState(Flow flow, String id) throws IllegalArgumentException {
		super(flow, id);
	}

	// implementing TranstionableStateDefinition

	public TransitionDefinition[] getTransitions() {
		return getTransitionSet().toArray();
	}

	public TransitionDefinition getTransition(String eventId) {
		for (Iterator it = transitions.iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			if (transition.getId().equals(eventId)) {
				return transition;
			}
		}
		return null;
	}

	// impl

	/**
	 * Returns the set of transitions. The returned set is mutable.
	 */
	public TransitionSet getTransitionSet() {
		return transitions;
	}

	/**
	 * Get a transition in this state for given flow execution request context. Throws and exception when there is no
	 * corresponding transition.
	 * @throws NoMatchingTransitionException when a matching transition cannot be found
	 */
	public Transition getRequiredTransition(RequestContext context) throws NoMatchingTransitionException {
		Transition transition = getTransitionSet().getTransition(context);
		if (transition == null) {
			throw new NoMatchingTransitionException(getFlow().getId(), getId(), context.getCurrentEvent(),
					"No transition found on occurence of event '" + context.getCurrentEvent() + "' in state '"
							+ getId() + "' of flow '" + getFlow().getId() + "' -- valid transitional criteria are "
							+ StylerUtils.style(getTransitionSet().getTransitionCriterias())
							+ " -- likely programmer error, check the set of TransitionCriteria for this state");
		}
		return transition;
	}

	/**
	 * Returns the list of actions executed by this state when it is exited. The returned list is mutable.
	 * @return the state exit action list
	 */
	public ActionList getExitActionList() {
		return exitActionList;
	}

	// behavioral methods

	/**
	 * Inform this state definition that an event was signaled in it. The signaled event is the last event available in
	 * given request context ({@link RequestContext#getCurrentEvent()}).
	 * @param context the flow execution control context
	 * @throws NoMatchingTransitionException when a matching transition cannot be found
	 */
	public boolean handleEvent(RequestControlContext context) throws NoMatchingTransitionException {
		return context.execute(getRequiredTransition(context));
	}

	/**
	 * Exit this state. This is typically called when a transition takes the flow out of this state into another state.
	 * By default just executes any registered exit actions.
	 * @param context the flow control context
	 */
	public void exit(RequestControlContext context) {
		exitActionList.execute(context);
	}

	protected void appendToString(ToStringCreator creator) {
		creator.append("transitions", transitions).append("exitActionList", exitActionList);
	}
}