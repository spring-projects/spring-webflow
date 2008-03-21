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

import org.springframework.util.StringUtils;

/**
 * Model support for states.
 * 
 * @author Scott Andrews
 */
public abstract class AbstractStateModel extends AbstractModel {
	private String id;
	private LinkedList attributes;
	private SecuredModel secured;
	private LinkedList onEntryActions;
	private LinkedList exceptionHandlers;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		if (StringUtils.hasText(id)) {
			this.id = id;
		} else {
			this.id = null;
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
	 * @param attribute the attribute to add
	 */
	public void addAttribute(AttributeModel attribute) {
		if (attribute == null) {
			return;
		}
		if (attributes == null) {
			attributes = new LinkedList();
		}
		attributes.add(attribute);
	}

	/**
	 * @param attributes the attributes to add
	 */
	public void addAttributes(LinkedList attributes) {
		if (attributes == null || attributes.isEmpty()) {
			return;
		}
		if (this.attributes == null) {
			this.attributes = new LinkedList();
		}
		this.attributes.addAll(attributes);
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
	 * @return the on entry actions
	 */
	public LinkedList getOnEntryActions() {
		return onEntryActions;
	}

	/**
	 * @param onEntryActions the on entry actions to set
	 */
	public void setOnEntryActions(LinkedList onEntryActions) {
		this.onEntryActions = onEntryActions;
	}

	/**
	 * @param onEntryAction the on entry action to add
	 */
	public void addOnEntryAction(AbstractActionModel onEntryAction) {
		if (onEntryAction == null) {
			return;
		}
		if (onEntryActions == null) {
			onEntryActions = new LinkedList();
		}
		onEntryActions.add(onEntryAction);
	}

	/**
	 * @param onEntryActions the on entry actions to add
	 */
	public void addOnEntryActions(LinkedList onEntryActions) {
		if (onEntryActions == null || onEntryActions.isEmpty()) {
			return;
		}
		if (this.onEntryActions == null) {
			this.onEntryActions = new LinkedList();
		}
		this.onEntryActions.addAll(onEntryActions);
	}

	/**
	 * @return the exception handlers
	 */
	public LinkedList getExceptionHandlers() {
		return exceptionHandlers;
	}

	/**
	 * @param exceptionHandlers the exception handlers to set
	 */
	public void setExceptionHandlers(LinkedList exceptionHandlers) {
		this.exceptionHandlers = exceptionHandlers;
	}

	/**
	 * @param exceptionHandler the exception handler to add
	 */
	public void addExceptionHandler(ExceptionHandlerModel exceptionHandler) {
		if (exceptionHandler == null) {
			return;
		}
		if (exceptionHandlers == null) {
			exceptionHandlers = new LinkedList();
		}
		exceptionHandlers.add(exceptionHandler);
	}

	/**
	 * @param exceptionHandlers the exception handlers to add
	 */
	public void addExceptionHandlers(LinkedList exceptionHandlers) {
		if (exceptionHandlers == null || exceptionHandlers.isEmpty()) {
			return;
		}
		if (this.exceptionHandlers == null) {
			this.exceptionHandlers = new LinkedList();
		}
		this.exceptionHandlers.addAll(exceptionHandlers);
	}
}
