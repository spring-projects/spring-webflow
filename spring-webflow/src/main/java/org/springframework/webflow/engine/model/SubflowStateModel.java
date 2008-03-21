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
import org.springframework.util.StringUtils;

/**
 * Model support for subflow states.
 * <p>
 * Starts another flow as a subflow when entered. When the subflow ends, this state is expected to respond to its result
 * by executing a transition.
 * <p>
 * A subflow state is a transitionable state. A transition is triggered by the subflow outcome that was reached.
 * 
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

	/**
	 * Create a subflow state model
	 * @param id the identifier of the state
	 * @param subflow the identifier of the flow to launch as a subflow
	 * @param subflowAttributeMapper bean name of the attribute mapping
	 * @param inputs input mappings
	 * @param outputs output mappings
	 * @param attributes meta attributes for the state
	 * @param secured security settings for the state
	 * @param onEntryActions actions to be executed when entering the state
	 * @param exceptionHandlers exception handlers for the state
	 * @param transitions transitions for the state
	 * @param onExitActions actions to be executed before leaving the state.
	 */
	public SubflowStateModel(String id, String subflow, String subflowAttributeMapper, LinkedList inputs,
			LinkedList outputs, LinkedList attributes, SecuredModel secured, LinkedList onEntryActions,
			LinkedList exceptionHandlers, LinkedList transitions, LinkedList onExitActions) {
		setId(id);
		setSubflow(subflow);
		setSubflowAttributeMapper(subflowAttributeMapper);
		setInputs(inputs);
		setOutputs(outputs);
		setAttributes(attributes);
		setSecured(secured);
		setOnEntryActions(onEntryActions);
		setExceptionHandlers(exceptionHandlers);
		setTransitions(transitions);
		setOnExitActions(onExitActions);
	}

	/**
	 * Merge properties
	 * @param model the subflow state to merge into this state
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			SubflowStateModel state = (SubflowStateModel) model;
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
	}

	/**
	 * Tests if the model is able to be merged with this subflow state
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof SubflowStateModel)) {
			return false;
		}
		SubflowStateModel state = (SubflowStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof SubflowStateModel)) {
			return false;
		}
		SubflowStateModel state = (SubflowStateModel) obj;
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
		} else if (!ObjectUtils.nullSafeEquals(getSubflow(), state.getSubflow())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getSubflowAttributeMapper(), state.getSubflowAttributeMapper())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getInputs(), state.getInputs())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getOutputs(), state.getOutputs())) {
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
				+ ObjectUtils.nullSafeHashCode(getOnExitActions()) * 27 + ObjectUtils.nullSafeHashCode(getSubflow())
				* 27 + ObjectUtils.nullSafeHashCode(getSubflowAttributeMapper()) * 27
				+ ObjectUtils.nullSafeHashCode(getInputs()) * 27 + ObjectUtils.nullSafeHashCode(getOutputs()) * 27;
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
	 * @param input the input mapping to add
	 */
	public void addInput(InputModel input) {
		if (input == null) {
			return;
		}
		if (inputs == null) {
			inputs = new LinkedList();
		}
		inputs.add(input);
	}

	/**
	 * @param inputs the input mappings to add
	 */
	public void addInputs(LinkedList inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return;
		}
		if (this.inputs == null) {
			this.inputs = new LinkedList();
		}
		this.inputs.addAll(inputs);
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

	/**
	 * @param output the output mapping to add
	 */
	public void addOutput(OutputModel output) {
		if (output == null) {
			return;
		}
		if (outputs == null) {
			outputs = new LinkedList();
		}
		outputs.add(output);
	}

	/**
	 * @param outputs the output mappings to add
	 */
	public void addOutputs(LinkedList outputs) {
		if (outputs == null || outputs.isEmpty()) {
			return;
		}
		if (this.outputs == null) {
			this.outputs = new LinkedList();
		}
		this.outputs.addAll(outputs);
	}
}
