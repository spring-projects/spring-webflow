/*
 * Copyright 2004-2015 the original author or authors.
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
package org.springframework.faces.webflow;

import java.util.EnumSet;
import java.util.Iterator;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitHint;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import jakarta.faces.event.PhaseId;
import jakarta.faces.lifecycle.Lifecycle;

import com.sun.faces.component.CompositeComponentStackManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.binding.expression.Expression;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

import static org.springframework.faces.webflow.JsfRuntimeInformation.isMojarraPresent;

/**
 * JSF-specific {@link ViewFactory} implementation.
 *
 * <p>This factory is responsible for performing the duties of the RESTORE_VIEW
 * phase of the JSF lifecycle.
 *
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JsfViewFactory implements ViewFactory {

	private static final Log logger = LogFactory.getLog(JsfViewFactory.class);

	private final Expression viewIdExpression;

	private final Lifecycle lifecycle;

	public JsfViewFactory(Expression viewIdExpression, Lifecycle lifecycle) {
		this.viewIdExpression = viewIdExpression;
		this.lifecycle = lifecycle;
	}

	/**
	 * Executes the RESTORE_VIEW phase of the JSF lifecycle at the beginning of a request, and creates the next view to
	 * be rendered in the case of an executing transition.
	 */
	public View getView(RequestContext context) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			throw new IllegalStateException("FacesContext has not been initialized within " +
					"the current Web Flow request. Has FlowFacesContextLifecycleListener been added?");
		}
		facesContext.setCurrentPhaseId(PhaseId.RESTORE_VIEW);

		// only publish a RESTORE_VIEW event if this is the first phase of the lifecycle
		// this won't be true when this method is called after a transition from one view-state to another
		boolean notifyPhaseListeners = !facesContext.getRenderResponse();

		if (notifyPhaseListeners) {
			JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, this.lifecycle, facesContext);
		}
		UIViewRoot viewRoot = getViewRoot(context, facesContext);
		facesContext.setViewRoot(viewRoot);
		publishPostRestoreStateEvent(facesContext);
		if (notifyPhaseListeners) {
			JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, this.lifecycle, facesContext);
		}
		return new JsfView(viewRoot, this.lifecycle, context);
	}

	private UIViewRoot getViewRoot(RequestContext context, FacesContext facesContext) {
		ViewHandler viewHandler = getViewHandler(facesContext);
		String viewName = (String) this.viewIdExpression.getValue(context);
		if (viewAlreadySet(facesContext, viewName)) {
			return getViewRootForAlreadySetView(context, facesContext);
		}
		if (context.inViewState()) {
			return getViewStateViewRoot(context, facesContext, viewHandler, viewName);
		}
		return getTransientViewRoot(context, facesContext, viewHandler, viewName);
	}

	private ViewHandler getViewHandler(FacesContext facesContext) {
		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
		viewHandler.initView(facesContext);
		return viewHandler;
	}

	private boolean viewAlreadySet(FacesContext facesContext, String viewName) {
		// the corner case where a before RESTORE_VIEW PhaseListener has handled setting the UIViewRoot
		return facesContext.getViewRoot() != null && facesContext.getViewRoot().getViewId().equals(viewName);
	}

	private UIViewRoot getViewRootForAlreadySetView(RequestContext context, FacesContext facesContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Existing view root found with id '" + facesContext.getViewRoot().getId() + "'");
		}
		UIViewRoot viewRoot = facesContext.getViewRoot();
		viewRoot.setLocale(context.getExternalContext().getLocale());
		processTree(facesContext, viewRoot);
		return viewRoot;
	}

	private UIViewRoot getViewStateViewRoot(RequestContext context, FacesContext facesContext,
			ViewHandler handler, String viewName) {

		UIViewRoot viewRoot = handler.restoreView(facesContext, viewName);
		if (viewRoot != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("UIViewRoot restored for '" + viewName + "'");
			}
			processTree(facesContext, viewRoot);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating UIViewRoot from '" + viewName + "'");
			}
			viewRoot = handler.createView(facesContext, viewName);
		}
		return viewRoot;
	}

	private UIViewRoot getTransientViewRoot(RequestContext context, FacesContext facesContext, ViewHandler viewHandler,
			String viewName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating transient UIViewRoot from '" + viewName + "'");
		}
		UIViewRoot viewRoot = viewHandler.createView(facesContext, viewName);
		viewRoot.setTransient(true);
		return viewRoot;
	}

	/**
	 * Walk the component tree to perform any required per-component operations.
	 */
	private void processTree(FacesContext context, UIComponent component) {

		MojarraProcessTreeHandler mojarraTreeHandler = null;
		if (isMojarraPresent() && !JsfRuntimeInformation.isMyFacesInUse()) {
			mojarraTreeHandler = new MojarraProcessTreeHandler().handleBefore(context, component);
		}

		// Only resetting the valid flag in the RESTORE_VIEW phase,
		// not during RENDER_RESPONSE
		if (!context.getRenderResponse() && component instanceof EditableValueHolder) {
			((EditableValueHolder) component).setValid(true);
		}

		ValueExpression binding = component.getValueExpression("binding");
		if (binding != null) {
			binding.setValue(context.getELContext(), component);
		}

		Iterator<UIComponent> it = component.getFacetsAndChildren();
		while (it.hasNext()) {
			processTree(context, it.next());
		}

		if (mojarraTreeHandler != null) {
			mojarraTreeHandler.handleAfter();
		}
	}

	private void publishPostRestoreStateEvent(FacesContext facesContext) {
		VisitHint visitHint = null;
		try {
			visitHint = Enum.valueOf(VisitHint.class, "SKIP_ITERATION");
		}
		catch (IllegalArgumentException ex) {
			// JSF < 2.1
		}
		String name = "jakarta.faces.visit.SKIP_ITERATION";
		facesContext.getAttributes().put(name, true);
		try {
			VisitContext visitContext = (visitHint != null ?
					VisitContext.createVisitContext(facesContext, null, EnumSet.of(visitHint)) :
					VisitContext.createVisitContext(facesContext));
			facesContext.getViewRoot().visitTree(visitContext, new PostRestoreStateEventVisitCallback());
		}
		catch (AbortProcessingException e) {
			PhaseId phaseId = facesContext.getCurrentPhaseId();
			Object eventContext = new ExceptionQueuedEventContext(facesContext, e, null, phaseId);
			facesContext.getApplication().publishEvent(facesContext, ExceptionQueuedEvent.class, eventContext);
		}
		finally {
			facesContext.getAttributes().remove(name);
		}
	}

	private static class MojarraProcessTreeHandler {

		private CompositeComponentStackManager stackManager;

		private boolean pushed;


		public MojarraProcessTreeHandler handleBefore(FacesContext context, UIComponent component) {
			// For correct evaluation of #{cc.XXX} binding expressions
			this.stackManager = CompositeComponentStackManager.getManager(context);
			if (UIComponent.isCompositeComponent(component)) {
				this.pushed = stackManager.push(component, CompositeComponentStackManager.StackType.TreeCreation);
			}
			return this;
		}

		public void handleAfter() {
			if (this.pushed) {
				this.stackManager.pop(CompositeComponentStackManager.StackType.TreeCreation);
			}
		}
	}
}
