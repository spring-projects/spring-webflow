package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowViewStateManager extends StateManager {

	private static final Log logger = LogFactory.getLog(FlowViewStateManager.class);

	private static final String SERIALIZED_VIEW_STATE = "org.springframework.webflow.viewState";

	private static final String ACTIVE_VIEW_ROOT = "org.springframework.webflow.viewRoot";

	protected Object getComponentStateToSave(FacesContext context) {
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot.isTransient()) {
			return null;
		} else {
			return viewRoot.processSaveState(context);
		}
	}

	protected Object getTreeStructureToSave(FacesContext context) {
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot.isTransient()) {
			return null;
		} else {
			return new TreeStructureManager().buildTreeStructureToSave(viewRoot);
		}
	}

	protected void restoreComponentState(FacesContext context, UIViewRoot viewRoot, String renderKitId) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedView view = (SerializedView) requestContext.getFlowScope().get(SERIALIZED_VIEW_STATE);
		viewRoot.processRestoreState(context, view.componentState);
		logger.debug("UIViewRoot component state restored");
	}

	protected UIViewRoot restoreTreeStructure(FacesContext context, String viewId, String renderKitId) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedView view = (SerializedView) requestContext.getFlowScope().get(SERIALIZED_VIEW_STATE);
		if (view == null || !view.viewId.equals(viewId)) {
			logger.debug("No matching view in flow scope;");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Restoring view root with id '" + viewId + "' from flow scope");
		}
		if (view.treeStructure == null) {
			logger.debug("Tree structure is null indicating transient UIViewRoot; returning null");
			return null;
		}
		UIViewRoot viewRoot = new TreeStructureManager().restoreTreeStructure(view.treeStructure);
		logger.debug("UIViewRoot structure restored.");
		return viewRoot;
	}

	public void writeState(FacesContext context, javax.faces.application.StateManager.SerializedView state)
			throws IOException {
		// nothing to do, as saving state to client always returns false
	}

	public boolean isSavingStateInClient(FacesContext context) {
		return false;
	}

	/**
	 * JSF 1.1 version of state saving
	 */
	public javax.faces.application.StateManager.SerializedView saveSerializedView(FacesContext context) {
		SerializedView view = (SerializedView) saveView(context);
		return new javax.faces.application.StateManager.SerializedView(view.treeStructure, view.componentState);
	}

	/**
	 * JSF 1.2 version of state saving
	 */
	public Object saveView(FacesContext context) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		if (logger.isDebugEnabled()) {
			logger.debug("Saving view root '" + context.getViewRoot().getViewId() + "' in flow scope");
		}
		SerializedView view = new SerializedView(context.getViewRoot().getViewId(), getTreeStructureToSave(context),
				getComponentStateToSave(context));
		requestContext.getFlowScope().put(SERIALIZED_VIEW_STATE, view);
		return view;
	}

	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {

		UIViewRoot viewRoot = restoreTreeStructure(context, viewId, renderKitId);
		if (viewRoot != null) {
			restoreComponentState(context, viewRoot, renderKitId);
		}
		return viewRoot;
	}

	private static class SerializedView implements Serializable {
		private static final Log logger = LogFactory.getLog(SerializedView.class);

		private Object treeStructure;

		private Object componentState;

		private String viewId;

		public SerializedView(String viewId, Object treeStructure, Object componentState) {
			this.viewId = viewId;
			this.treeStructure = treeStructure;
			this.componentState = componentState;
		}
	}

}
