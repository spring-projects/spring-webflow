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
package org.springframework.webflow.engine.model;

import java.util.LinkedList;

import org.springframework.util.ObjectUtils;

/**
 * Model support for action states.
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

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof ActionStateModel)) {
			return false;
		}
		ActionStateModel state = (ActionStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public void merge(Model model) {
		ActionStateModel state = (ActionStateModel) model;
		setParent(null);
		setAttributes(merge(getAttributes(), state.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
		setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
		setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
		setTransitions(merge(getTransitions(), state.getTransitions()));
		setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
		setActions(merge(getActions(), state.getActions(), false));
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

}