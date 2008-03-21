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
	 * Create an action state model
	 * @param id the state identifier
	 * @param attributes meta attributes for the state
	 * @param secured security settings for the state
	 * @param onEntryActions actions to execute upon entry
	 * @param transitions transitions for the state
	 * @param onExitActions actions to execute before leaving the state
	 * @param actions actions to execute during the state
	 * @param exceptionHandlers exception handlers for the state
	 */
	public ActionStateModel(String id, LinkedList attributes, SecuredModel secured, LinkedList onEntryActions,
			LinkedList transitions, LinkedList onExitActions, LinkedList actions, LinkedList exceptionHandlers) {
		setId(id);
		setAttributes(attributes);
		setSecured(secured);
		setOnEntryActions(onEntryActions);
		setTransitions(transitions);
		setOnExitActions(onExitActions);
		setActions(actions);
		setExceptionHandlers(exceptionHandlers);
	}

	/**
	 * Merge properties
	 * @param model the action state to merge into this state
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			ActionStateModel state = (ActionStateModel) model;
			setAttributes(merge(getAttributes(), state.getAttributes()));
			setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
			setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
			setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
			setTransitions(merge(getTransitions(), state.getTransitions()));
			setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
			setActions(merge(getActions(), state.getActions(), false));
		}
	}

	/**
	 * Tests if the model is able to be merged with this action state
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof ActionStateModel)) {
			return false;
		}
		ActionStateModel state = (ActionStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ActionStateModel)) {
			return false;
		}
		ActionStateModel state = (ActionStateModel) obj;
		if (state == null) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getId(), state.getId())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getAttributes(), state.getAttributes())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getSecured(), state.getSecured())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getOnEntryActions(), state.getOnEntryActions())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getExceptionHandlers(), state.getExceptionHandlers())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getTransitions(), state.getTransitions())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getOnExitActions(), state.getOnExitActions())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getActions(), state.getActions())) {
			return false;
		} else {
			return true;
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getId()) * 27 + ObjectUtils.nullSafeHashCode(getAttributes()) * 27
				+ ObjectUtils.nullSafeHashCode(getSecured()) * 27 + ObjectUtils.nullSafeHashCode(getOnEntryActions())
				* 27 + ObjectUtils.nullSafeHashCode(getExceptionHandlers()) * 27
				+ ObjectUtils.nullSafeHashCode(getTransitions()) * 27
				+ ObjectUtils.nullSafeHashCode(getOnExitActions()) * 27 + ObjectUtils.nullSafeHashCode(getActions())
				* 27;
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
