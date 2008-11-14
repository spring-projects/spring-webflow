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
package org.springframework.webflow.engine;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A view state is a state that issues a response to the user, for example, for soliciting form input. To accomplish
 * this, a <code>ViewState</code> delegates to a {@link ViewFactory}.
 * 
 * @see ViewFactory
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ViewState extends TransitionableState {

	/**
	 * The list of actions to be executed before the view is rendered.
	 */
	private ActionList renderActionList = new ActionList();

	/**
	 * A factory for creating and restoring the view rendered by this view state.
	 */
	private ViewFactory viewFactory;

	/**
	 * The set of view variables created by this view state.
	 */
	private Map variables = new LinkedHashMap();

	/**
	 * Whether or not a redirect should occur before the view is rendered.
	 */
	private Boolean redirect;

	/**
	 * Whether or not the view should render as a popup.
	 */
	private boolean popup;

	/**
	 * Create a new view state.
	 * @param flow the owning flow
	 * @param id the state identifier (must be unique to the flow)
	 * @param viewFactory the view factory
	 * @throws IllegalArgumentException when this state cannot be added to given flow, e.g. because the id is not unique
	 */
	public ViewState(Flow flow, String id, ViewFactory viewFactory) throws IllegalArgumentException {
		super(flow, id);
		Assert.notNull(viewFactory, "The view factory is required");
		this.viewFactory = viewFactory;
	}

	// implementing StateDefinition

	public boolean isViewState() {
		return true;
	}

	/**
	 * Adds a view variable.
	 * @param variable the variable
	 */
	public void addVariable(ViewVariable variable) {
		variables.put(variable.getName(), variable);
	}

	/**
	 * Adds a set of view variables.
	 * @param variables the variables
	 */
	public void addVariables(ViewVariable[] variables) {
		for (int i = 0; i < variables.length; i++) {
			addVariable(variables[i]);
		}
	}

	/**
	 * Returns the view variable with the given name.
	 * @param name the name of the variable
	 */
	public ViewVariable getVariable(String name) {
		return (ViewVariable) variables.get(name);
	}

	/**
	 * Returns the configured view variables.
	 */
	public ViewVariable[] getVariables() {
		return (ViewVariable[]) variables.values().toArray(new ViewVariable[variables.size()]);
	}

	/**
	 * Returns whether this view state should request a flow execution redirect when entered.
	 */
	public boolean getRedirect() {
		return redirect.booleanValue();
	}

	/**
	 * Sets whether this view state should requests a flow execution redirect when entered.
	 * @param redirect the redirect flag
	 */
	public void setRedirect(Boolean redirect) {
		this.redirect = redirect;
	}

	/**
	 * Returns whether this view state should render as a popup.
	 */
	public boolean getPopup() {
		return popup;
	}

	/**
	 * Sets whether this view state should render as a popup.
	 * @param popup the popup flag
	 */
	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	/**
	 * Returns the view factory.
	 */
	public ViewFactory getViewFactory() {
		return viewFactory;
	}

	/**
	 * Returns the list of actions executable by this view state on entry and on refresh. The returned list is mutable.
	 * @return the state action list
	 */
	public ActionList getRenderActionList() {
		return renderActionList;
	}

	protected void doPreEntryActions(RequestControlContext context) throws FlowExecutionException {
		createVariables(context);
	}

	protected void doEnter(RequestControlContext context) throws FlowExecutionException {
		context.assignFlowExecutionKey();
		if (context.getExternalContext().isResponseAllowed()) {
			if (shouldRedirect(context)) {
				context.getExternalContext().requestFlowExecutionRedirect();
				if (popup) {
					context.getExternalContext().requestRedirectInPopup();
				}
			} else {
				View view = viewFactory.getView(context);
				render(context, view);
			}
		}
	}

	public void resume(RequestControlContext context) {
		restoreVariables(context);
		View view = viewFactory.getView(context);
		view.processUserEvent();
		if (view.hasFlowEvent()) {
			Event event = view.getFlowEvent();
			if (logger.isDebugEnabled()) {
				logger.debug("Event '" + event.getId() + "' returned from view " + view);
			}
			context.getRequestScope().put("webflow.originatingViewState", this);
			boolean stateExited = context.handleEvent(event);
			if (!stateExited && context.getExternalContext().isResponseAllowed()) {
				if (context.getExternalContext().isAjaxRequest()) {
					render(context, view);
				} else {
					context.getExternalContext().requestFlowExecutionRedirect();
				}
			}
		} else {
			if (context.getExternalContext().isResponseAllowed()) {
				render(context, view);
			}
		}
	}

	public void exit(RequestControlContext context) {
		super.exit(context);
		updateHistory(context);
		destroyVariables(context);
	}

	// internal helpers

	private void createVariables(RequestContext context) {
		Iterator it = variables.values().iterator();
		while (it.hasNext()) {
			ViewVariable variable = (ViewVariable) it.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Creating " + variable);
			}
			variable.create(context);
		}
	}

	private boolean shouldRedirect(RequestControlContext context) {
		if (redirect != null) {
			return redirect.booleanValue();
		} else {
			return context.getRedirectOnPause();
		}
	}

	private void render(RequestControlContext context, View view) throws ViewRenderingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering + " + view);
			logger.debug("  Flash scope = " + context.getFlashScope());
			logger.debug("  Messages = " + context.getMessageContext());
		}
		context.viewRendering(view);
		renderActionList.execute(context);
		try {
			view.render();
		} catch (IOException e) {
			throw new ViewRenderingException(getOwner().getId(), getId(), view, e);
		}
		context.getFlashScope().clear();
		context.getMessageContext().clearMessages();
		context.getExternalContext().recordResponseComplete();
		context.viewRendered(view);
	}

	private void restoreVariables(RequestContext context) {
		Iterator it = variables.values().iterator();
		while (it.hasNext()) {
			ViewVariable variable = (ViewVariable) it.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Restoring " + variable);

			}
			variable.restore(context);
		}
	}

	private void updateHistory(RequestControlContext context) {
		TransitionDefinition transition = context.getCurrentTransition();
		History history = (History) transition.getAttributes().get("history");
		if (history == null || history == History.PRESERVE) {
			context.updateCurrentFlowExecutionSnapshot();
		} else if (history == History.DISCARD) {
			context.removeCurrentFlowExecutionSnapshot();
		} else if (history == History.INVALIDATE) {
			context.removeAllFlowExecutionSnapshots();
		}
	}

	private void destroyVariables(RequestContext context) {
		Iterator it = variables.values().iterator();
		while (it.hasNext()) {
			ViewVariable variable = (ViewVariable) it.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Destroying " + variable);

			}
			variable.destroy(context);
		}
	}

	protected void appendToString(ToStringCreator creator) {
		super.appendToString(creator);
		creator.append("viewFactory", viewFactory).append("variables", variables).append("redirect", redirect).append(
				"popup", popup);
	}

}