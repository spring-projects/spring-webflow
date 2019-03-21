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

import org.springframework.util.ObjectUtils;

/**
 * Model support for decision states.
 * @author Scott Andrews
 */
public class DecisionStateModel extends AbstractStateModel {

	private LinkedList<IfModel> ifs;

	private LinkedList<AbstractActionModel> onExitActions;

	/**
	 * Create a decision state model
	 * @param id the state identifier
	 */
	public DecisionStateModel(String id) {
		super(id);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof DecisionStateModel)) {
			return false;
		}
		DecisionStateModel state = (DecisionStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public void merge(Model model) {
		DecisionStateModel state = (DecisionStateModel) model;
		setParent(null);
		setAttributes(merge(getAttributes(), state.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
		setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
		setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
		setIfs(merge(getIfs(), state.getIfs()));
		setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
	}

	public Model createCopy() {
		DecisionStateModel copy = new DecisionStateModel(getId());
		super.fillCopy(copy);
		copy.setIfs(copyList(ifs));
		copy.setOnExitActions(copyList(onExitActions));
		return copy;
	}

	/**
	 * @return the ifs
	 */
	public LinkedList<IfModel> getIfs() {
		return ifs;
	}

	/**
	 * @param ifs the ifs to set
	 */
	public void setIfs(LinkedList<IfModel> ifs) {
		this.ifs = ifs;
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
