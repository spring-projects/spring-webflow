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
 * Model support for view states.
 * 
 * @author Scott Andrews
 */
public class ViewStateModel extends AbstractTransitionableStateModel {

	private String view;

	private String redirect;

	private String popup;

	private String model;

	private LinkedList vars;

	private BinderModel binder;

	private LinkedList onRenderActions;

	/**
	 * Create a view state model
	 * @param id the identifier of the state
	 */
	public ViewStateModel(String id) {
		setId(id);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof ViewStateModel)) {
			return false;
		}
		ViewStateModel state = (ViewStateModel) model;
		return ObjectUtils.nullSafeEquals(getId(), state.getId());
	}

	public void merge(Model model) {
		ViewStateModel state = (ViewStateModel) model;
		setParent(null);
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
		setBinder((BinderModel) merge(getBinder(), state.getBinder()));
		setOnRenderActions(merge(getOnRenderActions(), state.getOnRenderActions(), false));
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

	public BinderModel getBinder() {
		return binder;
	}

	public void setBinder(BinderModel binder) {
		this.binder = binder;
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

}
