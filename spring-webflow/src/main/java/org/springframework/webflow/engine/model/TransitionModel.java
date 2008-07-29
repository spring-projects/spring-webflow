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
 * Model support for transitions.
 * <p>
 * A path from this state to another state triggered by an event. Transitions may execute one or more actions. All
 * transition actions must execute successfully for the transition itself to complete. If no transition target is
 * specified, the transition acts as a simple event handler and does not change the state of the flow.
 * 
 * @author Scott Andrews
 */
public class TransitionModel extends AbstractModel {

	private String on;

	private String onException;

	private String to;

	private String bind;

	private String validate;

	private String history;

	private LinkedList attributes;

	private SecuredModel secured;

	private LinkedList actions;

	/**
	 * Create a transition model
	 */
	public TransitionModel() {
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof TransitionModel)) {
			return false;
		}
		TransitionModel transition = (TransitionModel) model;
		return ObjectUtils.nullSafeEquals(getOn(), transition.getOn());
	}

	public void merge(Model model) {
		TransitionModel transition = (TransitionModel) model;
		setOnException(merge(getOnException(), transition.getOnException()));
		setTo(merge(getTo(), transition.getTo()));
		setBind(merge(getBind(), transition.getBind()));
		setBind(merge(getValidate(), transition.getValidate()));
		setHistory(merge(getHistory(), transition.getHistory()));
		setAttributes(merge(getAttributes(), transition.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), transition.getSecured()));
		setActions(merge(getActions(), transition.getActions(), false));
	}

	/**
	 * @return the on
	 */
	public String getOn() {
		return on;
	}

	/**
	 * @param on the on to set
	 */
	public void setOn(String on) {
		if (StringUtils.hasText(on)) {
			this.on = on;
		} else {
			this.on = null;
		}
	}

	/**
	 * @return the on exception
	 */
	public String getOnException() {
		return onException;
	}

	/**
	 * @param onException the on exception to set
	 */
	public void setOnException(String onException) {
		if (StringUtils.hasText(onException)) {
			this.onException = onException;
		} else {
			this.onException = null;
		}
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		if (StringUtils.hasText(to)) {
			this.to = to;
		} else {
			this.to = null;
		}
	}

	/**
	 * @return the bind
	 */
	public String getBind() {
		return bind;
	}

	/**
	 * @param bind the bind to set
	 */
	public void setBind(String bind) {
		if (StringUtils.hasText(bind)) {
			this.bind = bind;
		} else {
			this.bind = null;
		}
	}

	/**
	 * @return the validate
	 */
	public String getValidate() {
		return validate;
	}

	/**
	 * @param validate the validate to set
	 */
	public void setValidate(String validate) {
		if (StringUtils.hasText(validate)) {
			this.validate = validate;
		} else {
			this.validate = null;
		}
	}

	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		if (StringUtils.hasText(history)) {
			this.history = history;
		} else {
			this.history = null;
		}
	}

	/**
	 * @return the attributes
	 */
	public LinkedList getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(LinkedList attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the secured
	 */
	public SecuredModel getSecured() {
		return secured;
	}

	/**
	 * @param secured the secured to set
	 */
	public void setSecured(SecuredModel secured) {
		this.secured = secured;
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