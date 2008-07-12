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
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom {@link StateManager} that manages the JSF component state in web flow's view scope.
 * 
 * @author Jeremy Grelle
 */
public class FlowViewStateManager extends StateManager {

	private static final Log logger = LogFactory.getLog(FlowViewStateManager.class);

	private static final String SERIALIZED_VIEW_STATE = "flowSerializedViewState";

	private StateManager delegate;

	public FlowViewStateManager(StateManager delegate) {
		this.delegate = delegate;
	}

	protected Object getComponentStateToSave(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return super.getComponentStateToSave(context);
		}
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot.isTransient()) {
			return null;
		} else {
			return viewRoot.processSaveState(context);
		}
	}

	protected Object getTreeStructureToSave(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return super.getTreeStructureToSave(context);
		}
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot.isTransient()) {
			return null;
		} else {
			return new TreeStructureManager().buildTreeStructureToSave(viewRoot);
		}
	}

	protected void restoreComponentState(FacesContext context, UIViewRoot viewRoot, String renderKitId) {
		if (!JsfUtils.isFlowRequest()) {
			super.restoreComponentState(context, viewRoot, renderKitId);
			return;
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedView view = (SerializedView) requestContext.getViewScope().get(SERIALIZED_VIEW_STATE);
		viewRoot.processRestoreState(context, view.componentState);
		logger.debug("UIViewRoot component state restored");
	}

	protected UIViewRoot restoreTreeStructure(FacesContext context, String viewId, String renderKitId) {
		if (!JsfUtils.isFlowRequest()) {
			return super.restoreTreeStructure(context, viewId, renderKitId);
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedView view = (SerializedView) requestContext.getViewScope().get(SERIALIZED_VIEW_STATE);
		if (view == null || !view.viewId.equals(viewId)) {
			logger.debug("No matching view in view scope");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Restoring view root with id '" + viewId + "' from view scope");
		}
		if (view.treeStructure == null) {
			logger.debug("Tree structure is null indicating transient UIViewRoot; returning null");
			return null;
		}
		UIViewRoot viewRoot = new TreeStructureManager().restoreTreeStructure(view.treeStructure);
		logger.debug("UIViewRoot structure restored");
		return viewRoot;
	}

	public void writeState(FacesContext context, javax.faces.application.StateManager.SerializedView state)
			throws IOException {
		if (!JsfUtils.isFlowRequest()) {
			delegate.writeState(context, state);
		}
		// nothing to do, as saving state to client always returns false
	}

	public boolean isSavingStateInClient(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return delegate.isSavingStateInClient(context);
		} else {
			return false;
		}
	}

	/**
	 * JSF 1.1 version of state saving
	 */
	public javax.faces.application.StateManager.SerializedView saveSerializedView(FacesContext context) {
		if (context.getViewRoot().isTransient()) {
			return null;
		}
		if (!JsfUtils.isFlowRequest()) {
			return delegate.saveSerializedView(context);
		}
		SerializedView view = (SerializedView) saveView(context);
		return new javax.faces.application.StateManager.SerializedView(view.treeStructure, view.componentState);
	}

	/**
	 * JSF 1.2 version of state saving
	 */
	public Object saveView(FacesContext context) {
		if (context.getViewRoot().isTransient()) {
			return null;
		}
		if (!JsfUtils.isFlowRequest()) {
			return delegate.saveView(context);
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		if (logger.isDebugEnabled()) {
			logger.debug("Saving view root '" + context.getViewRoot().getViewId() + "' in view scope");
		}
		SerializedView view = new SerializedView(context.getViewRoot().getViewId(), getTreeStructureToSave(context),
				getComponentStateToSave(context));
		requestContext.getViewScope().put(SERIALIZED_VIEW_STATE, view);
		return view;
	}

	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
		if (!JsfUtils.isFlowRequest()) {
			return delegate.restoreView(context, viewId, renderKitId);
		}
		UIViewRoot viewRoot = restoreTreeStructure(context, viewId, renderKitId);
		if (viewRoot != null) {
			restoreComponentState(context, viewRoot, renderKitId);
		}
		return viewRoot;
	}

	private static class SerializedView implements Serializable {
		private Object treeStructure;

		private Object componentState;

		private String viewId;

		public SerializedView(String viewId, Object treeStructure, Object componentState) {
			this.viewId = viewId;
			this.treeStructure = treeStructure;
			this.componentState = componentState;
		}

		public String toString() {
			return new ToStringCreator(this).append("viewId", viewId).toString();
		}
	}

}
