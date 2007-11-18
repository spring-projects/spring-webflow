package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowViewStateManager extends StateManager {

	private static final Log logger = LogFactory.getLog(FlowViewStateManager.class);

	private static final String VIEW_MAP = "org.springframework.webflow.viewMap";

	public boolean isSavingStateInClient(FacesContext context) {
		return false;
	}

	// required to implement for 1.1 view handlers - unfortunately, facelets is one of these...
	public javax.faces.application.StateManager.SerializedView saveSerializedView(FacesContext context) {
		SerializedView view = (SerializedView) saveView(context);
		return new javax.faces.application.StateManager.SerializedView(view.treeStructure, view.componentState);
	}

	public Object saveView(FacesContext context) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedViewMap viewMap = (SerializedViewMap) requestContext.getFlowScope().get(VIEW_MAP);
		if (viewMap == null) {
			viewMap = new SerializedViewMap();
			requestContext.getFlowScope().put(VIEW_MAP, viewMap);
		}
		logger.debug("Saving view root in flow scope");
		return viewMap.putSerializedView(context);
	}

	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		SerializedViewMap viewMap = (SerializedViewMap) requestContext.getFlowScope().get(VIEW_MAP);
		if (viewMap == null) {
			logger.debug("No view map in flow scope; no views have been saved yet...");
			return null;
		}
		logger.debug("Restoring view root from flow scope");
		return viewMap.getDeserializedView(viewId, context);
	}

	public void writeState(FacesContext context, Object state) throws IOException {
		// nothing to do, as saving state to client always returns false
	}

	private static class SerializedViewMap implements Serializable {
		private static final Log logger = LogFactory.getLog(SerializedViewMap.class);

		private Map<String, SerializedView> views;

		public SerializedViewMap() {
			views = new HashMap<String, SerializedView>();
		}

		public void put(String viewId, SerializedView view) {
			views.put(viewId, view);
		}

		public SerializedView get(String viewId) {
			return views.get(viewId);
		}

		public Object putSerializedView(FacesContext context) {
			SerializedView view = SerializedView.create(context);
			if (logger.isDebugEnabled()) {
				logger.debug("Indexing serialized view under key '" + context.getViewRoot().getViewId() + "'");
			}
			put(context.getViewRoot().getViewId(), view);
			return view;
		}

		public UIViewRoot getDeserializedView(String viewId, FacesContext context) {
			SerializedView view = get(viewId);
			if (view == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No serialized view found under key '" + viewId + "; returning null");
				}
				return null;
			}
			return view.deserialize(context);
		}
	}

	private static class SerializedView implements Serializable {
		private static final Log logger = LogFactory.getLog(SerializedView.class);

		private Serializable treeStructure;

		private Object componentState;

		private SerializedView(Serializable treeStructure, Object componentState) {
			this.treeStructure = treeStructure;
			this.componentState = componentState;
		}

		public UIViewRoot deserialize(FacesContext context) {
			if (treeStructure == null) {
				logger.debug("Tree structure is null indicating transient UIViewRoot; returning null");
				return null;
			}
			UIViewRoot viewRoot = new TreeStructureManager().restoreTreeStructure(treeStructure);
			logger.debug("UIViewRoot structure restored");
			viewRoot.processRestoreState(context, componentState);
			logger.debug("UIViewRoot component state restored");
			logger.debug("Returning restored UIViewRoot");
			return viewRoot;
		}

		public static SerializedView create(FacesContext context) {
			return new SerializedView(getTreeStructure(context), getComponentState(context));
		}

		private static Serializable getTreeStructure(FacesContext context) {
			UIViewRoot viewRoot = context.getViewRoot();
			if (viewRoot.isTransient()) {
				return null;
			} else {
				return new TreeStructureManager().buildTreeStructureToSave(viewRoot);
			}
		}

		private static Object getComponentState(FacesContext context) {
			UIViewRoot viewRoot = context.getViewRoot();
			if (viewRoot.isTransient()) {
				return null;
			} else {
				return viewRoot.processSaveState(context);
			}
		}
	}

}
