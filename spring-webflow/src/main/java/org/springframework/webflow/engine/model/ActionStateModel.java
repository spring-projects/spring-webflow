/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.engine.model;

import java.util.LinkedList;

import org.springframework.util.ObjectUtils;

/**
 * Model support for action states.
 * <p>
 * A state where one or more actions are executed. This state type is typically used to invoke application code. An
 * action state is a transitionable state. A transition out of this state is driven by the result of action execution.
 * 
 * @author Scott Andrews
 */
public class ActionStateModel extends AbstractTransitionableStateModel {
	private LinkedList actions;

	/**
	 * Create an action state model
	 * @param id the state identifier
	 */
	public ActionStateModel(String id) {
		setId(id);
	}

	/**
	 * Merge properties
	 * @param model the action state to merge into this state
	 */
	public void merge(Model model) {
		ActionStateModel state = (ActionStateModel) model;
		setAttributes(merge(getAttributes(), state.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
		setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
		setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
		setTransitions(merge(getTransitions(), state.getTransitions()));
		setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
		setActions(merge(getActions(), state.getActions(), false));
	}

	/**
	 * Tests if the model is able to be merged with this action state
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (!(model instanceof ActionStateModel)) {
			return false;
		}
		ActionStateModel state = (ActionStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	/**
	 * @return the actions
	 */
	public LinkedList getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(LinkedList actions) {
		this.actions = actions;
	}

	/**
	 * @param action the action to add
	 */
	public void addAction(AbstractActionModel action) {
		if (action == null) {
			return;
		}
		if (actions == null) {
			actions = new LinkedList();
		}
		actions.add(action);
	}

	/**
	 * @param actions the actions to add
	 */
	public void addAction(LinkedList actions) {
		if (actions == null || actions.isEmpty()) {
			return;
		}
		if (this.actions == null) {
			this.actions = new LinkedList();
		}
		this.actions.addAll(actions);
	}
}
