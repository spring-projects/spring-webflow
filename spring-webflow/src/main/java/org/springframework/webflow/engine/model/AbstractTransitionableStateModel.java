/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.model;

import java.util.LinkedList;

/**
 * Model support for transitionable states.
 * 
 * @author Scott Andrews
 */
public abstract class AbstractTransitionableStateModel extends AbstractStateModel {

	private LinkedList<TransitionModel> transitions;

	private LinkedList<AbstractActionModel> onExitActions;

	public AbstractTransitionableStateModel(String id) {
		super(id);
	}

	protected void fillCopy(final AbstractTransitionableStateModel copy) {
		super.fillCopy(copy);
		copy.setTransitions(copyList(transitions));
		copy.setOnExitActions(copyList(onExitActions));
	}

	/**
	 * @return the transitions
	 */
	public LinkedList<TransitionModel> getTransitions() {
		return transitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setTransitions(LinkedList<TransitionModel> transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the on exit actions
	 */
	public LinkedList<AbstractActionModel> getOnExitActions() {
		return onExitActions;
	}

	/**
	 * @param onExitActions the on exit actions to set
	 */
	public void setOnExitActions(LinkedList<AbstractActionModel> onExitActions) {
		this.onExitActions = onExitActions;
	}

}
