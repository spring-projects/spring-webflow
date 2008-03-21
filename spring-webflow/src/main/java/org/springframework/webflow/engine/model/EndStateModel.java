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
 * Model support for end states.
 * <p>
 * A state that terminates this flow when entered. Defines a flow outcome.
 * <p>
 * An end state is not transitionable; there are never transitions out of an end state. When an end-state is entered, an
 * instance of this flow is terminated.
 * <p>
 * When this flow terminates, if it was the "root" flow the entire execution is terminated. If this flow was a subflow,
 * its parent flow resumes.
 * 
 * @author Scott Andrews
 */
public class EndStateModel extends AbstractStateModel {
	private String view;
	private String commit;
	private LinkedList outputs;

	/**
	 * Create an end state model
	 * @param id the state identifier
	 */
	public EndStateModel(String id) {
		setId(id);
	}

	/**
	 * Create an end state model
	 * @param id the state identifier
	 * @param view the view to render
	 * @param commit indicate if the persistence context should be committed
	 * @param outputs output mappings
	 * @param attributes meta attributes for the state
	 * @param secured security settings for the state
	 * @param onEntryActions actions to execute when entering the state
	 * @param exceptionHandlers exception handlers for the state
	 */
	public EndStateModel(String id, String view, String commit, LinkedList outputs, LinkedList attributes,
			SecuredModel secured, LinkedList onEntryActions, LinkedList exceptionHandlers) {
		setId(id);
		setView(view);
		setCommit(commit);
		setOutputs(outputs);
		setAttributes(attributes);
		setSecured(secured);
		setOnEntryActions(onEntryActions);
		setExceptionHandlers(exceptionHandlers);
	}

	/**
	 * Merge properties
	 * @param model the end state to merge into this state
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			EndStateModel state = (EndStateModel) model;
			setAttributes(merge(getAttributes(), state.getAttributes()));
			setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
			setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
			setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
			setView(merge(getView(), state.getView()));
			setCommit(merge(getCommit(), state.getCommit()));
			setOutputs(merge(getOutputs(), state.getOutputs(), false));
		}
	}

	/**
	 * Tests if the model is able to be merged with this end state
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof EndStateModel)) {
			return false;
		}
		EndStateModel state = (EndStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof EndStateModel)) {
			return false;
		}
		EndStateModel state = (EndStateModel) obj;
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
		} else if (!ObjectUtils.nullSafeEquals(getView(), state.getView())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getCommit(), state.getCommit())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getOutputs(), state.getOutputs())) {
			return false;
		} else {
			return true;
		}
	}

	public int hasCode() {
		return ObjectUtils.nullSafeHashCode(getId()) * 27 + ObjectUtils.nullSafeHashCode(getAttributes()) * 27
				+ ObjectUtils.nullSafeHashCode(getSecured()) * 27 + ObjectUtils.nullSafeHashCode(getOnEntryActions())
				* 27 + ObjectUtils.nullSafeHashCode(getExceptionHandlers()) * 27
				+ ObjectUtils.nullSafeHashCode(getView()) * 27 + ObjectUtils.nullSafeHashCode(getCommit()) * 27
				+ ObjectUtils.nullSafeHashCode(getOutputs()) * 27;
	}

	/**
	 * @return the view
	 */
	public String getView() {
		return view;
	}

	/**
	 * @param view the view factory to set
	 */
	public void setView(String view) {
		if (StringUtils.hasText(view)) {
			this.view = view;
		} else {
			this.view = null;
		}
	}

	/**
	 * @return the commit
	 */
	public String getCommit() {
		return commit;
	}

	/**
	 * @param commit the commit to set
	 */
	public void setCommit(String commit) {
		if (StringUtils.hasText(commit)) {
			this.commit = commit;
		} else {
			this.commit = null;
		}
	}

	/**
	 * @return the outputs
	 */
	public LinkedList getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
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
