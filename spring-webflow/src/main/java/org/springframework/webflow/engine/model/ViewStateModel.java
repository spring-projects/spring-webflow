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
 * Model support for view states.
 * <p>
 * A state where the user participates. When a view state is entered, this flow pauses and control goes to the user.
 * After some think time, the user resumes this flow at the view-state by signaling an event.
 * <p>
 * Once paused, a view-state may be 'refreshed' by the user. A refresh causes the response to be reissued and then
 * returns control back to the user.
 * <p>
 * A view state may be configured with one or more render-actions using the 'on-render' element. Render actions are
 * executed immediately before the view is rendered.
 * <p>
 * A view state is a transitionable state. A view state transition is triggered by a user event.
 * 
 * @author Scott Andrews
 */
public class ViewStateModel extends AbstractTransitionableStateModel {
	private String view;
	private String redirect;
	private String popup;
	private String model;
	private LinkedList vars;
	private LinkedList onRenderActions;

	/**
	 * Create a view state model
	 * @param id the identifier of the state
	 */
	public ViewStateModel(String id) {
		setId(id);
	}

	/**
	 * Create a view state model
	 * @param id the identifier of the state
	 * @param view the view to render
	 */
	public ViewStateModel(String id, String view) {
		setId(id);
		setView(view);
	}

	/**
	 * Create a view state model
	 * @param id the identifier of the state
	 * @param view the view to render
	 * @param redirect request a flow execution redirect before render
	 * @param popup view should render in a popup dialog
	 * @param model the model object to bind for this view
	 * @param vars variables for this state
	 * @param onRenderActions actions to be executed before rendering
	 * @param attributes meta attributes for this state
	 * @param secured the security settings for this state
	 * @param onEntryActions actions to be executed on entry
	 * @param exceptionHandlers exception handlers for this state
	 * @param transitions transitions for this state
	 * @param onExitActions actions to be executed before exiting
	 */
	public ViewStateModel(String id, String view, String redirect, String popup, String model, LinkedList vars,
			LinkedList onRenderActions, LinkedList attributes, SecuredModel secured, LinkedList onEntryActions,
			LinkedList exceptionHandlers, LinkedList transitions, LinkedList onExitActions) {
		setId(id);
		setView(view);
		setRedirect(redirect);
		setPopup(popup);
		setModel(model);
		setVars(vars);
		setOnRenderActions(onRenderActions);
		setAttributes(attributes);
		setSecured(secured);
		setOnEntryActions(onEntryActions);
		setExceptionHandlers(exceptionHandlers);
		setTransitions(transitions);
		setOnExitActions(onExitActions);
	}

	/**
	 * Merge properties
	 * @param model the view state to merge into this state
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			ViewStateModel state = (ViewStateModel) model;
			setAttributes(merge(getAttributes(), state.getAttributes()));
			setSecured((SecuredModel) merge(getSecured(), state.getSecured()));
			setOnEntryActions(merge(getOnEntryActions(), state.getOnEntryActions(), false));
			setExceptionHandlers(merge(getExceptionHandlers(), state.getExceptionHandlers()));
			setTransitions(merge(getTransitions(), state.getTransitions()));
			setOnExitActions(merge(getOnExitActions(), state.getOnExitActions(), false));
			setView(merge(getView(), state.getView()));
			setRedirect(merge(getRedirect(), state.getRedirect()));
			setPopup(merge(getPopup(), state.getPopup()));
			setModel(merge(getModel(), state.getModel()));
			setVars(merge(getVars(), state.getVars(), false));
			setOnRenderActions(merge(getOnRenderActions(), state.getOnRenderActions(), false));
		}
	}

	/**
	 * Tests if the model is able to be merged with this view state
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof ViewStateModel)) {
			return false;
		}
		ViewStateModel state = (ViewStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ViewStateModel)) {
			return false;
		}
		ViewStateModel state = (ViewStateModel) obj;
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
		} else if (!ObjectUtils.nullSafeEquals(getView(), state.getView())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getRedirect(), state.getRedirect())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getPopup(), state.getPopup())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getModel(), state.getModel())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getVars(), state.getVars())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getOnRenderActions(), state.getOnRenderActions())) {
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
				+ ObjectUtils.nullSafeHashCode(getOnExitActions()) * 27 + ObjectUtils.nullSafeHashCode(getView()) * 27
				+ ObjectUtils.nullSafeHashCode(getRedirect()) * 27 + ObjectUtils.nullSafeHashCode(getPopup()) * 27
				+ ObjectUtils.nullSafeHashCode(getModel()) * 27 + ObjectUtils.nullSafeHashCode(getVars()) * 27
				+ ObjectUtils.nullSafeHashCode(getOnRenderActions()) * 27;
	}

	/**
	 * @return the view
	 */
	public String getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(String view) {
		if (StringUtils.hasText(view)) {
			this.view = view;
		} else {
			this.view = null;
		}
	}

	/**
	 * @return the redirect
	 */
	public String getRedirect() {
		return redirect;
	}

	/**
	 * @param redirect the redirect to set
	 */
	public void setRedirect(String redirect) {
		if (StringUtils.hasText(redirect)) {
			this.redirect = redirect;
		} else {
			this.redirect = null;
		}
	}

	/**
	 * @return the popup
	 */
	public String getPopup() {
		return popup;
	}

	/**
	 * @param popup the popup to set
	 */
	public void setPopup(String popup) {
		if (StringUtils.hasText(popup)) {
			this.popup = popup;
		} else {
			this.popup = null;
		}
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		if (StringUtils.hasText(model)) {
			this.model = model;
		} else {
			this.model = null;
		}
	}

	/**
	 * @return the vars
	 */
	public LinkedList getVars() {
		return vars;
	}

	/**
	 * @param vars the vars to set
	 */
	public void setVars(LinkedList vars) {
		this.vars = vars;
	}

	/**
	 * @param var the var to add
	 */
	public void addVar(VarModel var) {
		if (var == null) {
			return;
		}
		if (vars == null) {
			vars = new LinkedList();
		}
		vars.add(var);
	}

	/**
	 * @param vars the vars to add
	 */
	public void addVars(LinkedList vars) {
		if (vars == null || vars.isEmpty()) {
			return;
		}
		if (this.vars == null) {
			this.vars = new LinkedList();
		}
		this.vars.addAll(vars);
	}

	/**
	 * @return the on render actions
	 */
	public LinkedList getOnRenderActions() {
		return onRenderActions;
	}

	/**
	 * @param onRenderActions the on render actions to set
	 */
	public void setOnRenderActions(LinkedList onRenderActions) {
		this.onRenderActions = onRenderActions;
	}

	/**
	 * @param onRenderAction the on render action to add
	 */
	public void addOnRenderAction(AbstractActionModel onRenderAction) {
		if (onRenderAction == null) {
			return;
		}
		if (this.onRenderActions == null) {
			this.onRenderActions = new LinkedList();
		}
		this.onRenderActions.add(onRenderAction);
	}

	/**
	 * @param onRenderActions the on render actions to add
	 */
	public void addOnRenderActions(LinkedList onRenderActions) {
		if (onRenderActions == null || onRenderActions.isEmpty()) {
			return;
		}
		if (this.onRenderActions == null) {
			this.onRenderActions = new LinkedList();
		}
		this.onRenderActions.addAll(onRenderActions);
	}

}
