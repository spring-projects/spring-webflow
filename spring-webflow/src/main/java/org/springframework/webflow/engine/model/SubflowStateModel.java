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
import org.springframework.util.StringUtils;

/**
 * Model support for subflow states.
 * @author Scott Andrews
 */
public class SubflowStateModel extends AbstractTransitionableStateModel {

	private String subflow;

	private String subflowAttributeMapper;

	private LinkedList inputs;

	private LinkedList outputs;

	/**
	 * Create a subflow state model
	 * @param id the identifier of the state
	 * @param subflow the identifier of the flow to launch as a subflow
	 */
	public SubflowStateModel(String id, String subflow) {
		setId(id);
		setSubflow(subflow);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof SubflowStateModel)) {
			return false;
		}
		SubflowStateModel state = (SubflowStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public void merge(Model model) {
		SubflowStateModel state = (SubflowStateModel) model;
		setParent(null);
		setAttributes(merge(getAttributes(), state.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
		setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
		setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
		setTransitions(merge(getTransitions(), state.getTransitions()));
		setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
		setSubflow(merge(getSubflow(), state.getSubflow()));
		setSubflowAttributeMapper(merge(getSubflowAttributeMapper(), state.getSubflowAttributeMapper()));
		setInputs(merge(getInputs(), state.getInputs()));
		setOutputs(merge(getOutputs(), state.getOutputs()));
	}

	/**
	 * @return the subflow
	 */
	public String getSubflow() {
		return subflow;
	}

	/**
	 * @param subflow the subflow to set
	 */
	public void setSubflow(String subflow) {
		if (StringUtils.hasText(subflow)) {
			this.subflow = subflow;
		} else {
			this.subflow = null;
		}
	}

	/**
	 * @return the subflow attribute mapper
	 */
	public String getSubflowAttributeMapper() {
		return subflowAttributeMapper;
	}

	/**
	 * @param subflowAttributeMapper the subflow attribute mapper to set
	 */
	public void setSubflowAttributeMapper(String subflowAttributeMapper) {
		if (StringUtils.hasText(subflowAttributeMapper)) {
			this.subflowAttributeMapper = subflowAttributeMapper;
		} else {
			this.subflowAttributeMapper = null;
		}
	}

	/**
	 * @return the input mappings
	 */
	public LinkedList getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the input mappings to set
	 */
	public void setInputs(LinkedList inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the output mappings
	 */
	public LinkedList getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the output mappings to set
	 */
	public void setOutputs(LinkedList outputs) {
		this.outputs = outputs;
	}

}