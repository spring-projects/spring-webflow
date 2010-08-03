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

import static org.springframework.faces.webflow.JsfRuntimeInformation.isAtLeastJsf20;

import java.io.IOException;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom {@link StateManager} that manages the JSF component state in web flow's view scope.
 * 
 * @author Jeremy Grelle
 */
public class FlowViewStateManager extends StateManager {

	private static final Log logger = LogFactory.getLog(FlowViewStateManager.class);

	protected static final String SERIALIZED_VIEW_STATE = "flowSerializedViewState";

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
		FlowSerializedView view = (FlowSerializedView) requestContext.getViewScope().get(SERIALIZED_VIEW_STATE);
		viewRoot.processRestoreState(context, view.getComponentState());
		logger.debug("UIViewRoot component state restored");
	}

	protected UIViewRoot restoreTreeStructure(FacesContext context, String viewId, String renderKitId) {
		if (!JsfUtils.isFlowRequest()) {
			return super.restoreTreeStructure(context, viewId, renderKitId);
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		FlowSerializedView view = (FlowSerializedView) requestContext.getViewScope().get(SERIALIZED_VIEW_STATE);
		if (view == null || !view.getViewId().equals(viewId)) {
			logger.debug("No matching view in view scope");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Restoring view root with id '" + viewId + "' from view scope");
		}
		if (view.getTreeStructure() == null) {
			logger.debug("Tree structure is null indicating transient UIViewRoot; returning null");
			return null;
		}
		UIViewRoot viewRoot = new TreeStructureManager().restoreTreeStructure(view.getTreeStructure());
		logger.debug("UIViewRoot structure restored");
		return viewRoot;
	}

	public void writeState(FacesContext context, javax.faces.application.StateManager.SerializedView state)
			throws IOException {
		// Ensures that javax.faces.ViewState hidden field always gets written - needed for third-party component
		// compatibility
		delegate.writeState(context, state);
	}

	public void writeState(FacesContext context, Object state) throws IOException {
		if (state instanceof Object[]) {
			delegate.writeState(context, state); // MyFaces
		} else if (state instanceof FlowSerializedView) { // Mojarra
			FlowSerializedView view = (FlowSerializedView) state;
			delegate.writeState(context, view.asTreeStructAndCompStateArray());
		} else {
			super.writeState(context, state);
		}
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
		Object state = saveView(context);
		if (state instanceof FlowSerializedView) {
			FlowSerializedView serializedState = (FlowSerializedView) state;
			return new javax.faces.application.StateManager.SerializedView(serializedState.getTreeStructure(),
					serializedState.getComponentState());
		} else {
			Object[] serializedState = (Object[]) state;
			return new javax.faces.application.StateManager.SerializedView(serializedState[0], serializedState[1]);
		}
	}

	/**
	 * <p>
	 * JSF 1.2 (or higher) version of state saving.
	 * </p>
	 * 
	 * <p>
	 * In JSF 2 where a partial state saving algorithm is used, this method merely delegates to the next
	 * ViewStateManager. Thus partial state saving is handled by the JSF 2 runtime. However, a
	 * {@link FlowViewResponseStateManager} plugged in via {@link FlowRenderKit} will ensure the state is saved in a Web
	 * Flow view-scoped variable.
	 * </p>
	 */
	public Object saveView(FacesContext context) {
		if (context.getViewRoot().isTransient()) {
			return null;
		}
		if ((!JsfUtils.isFlowRequest()) || isAtLeastJsf20()) {
			return delegate.saveView(context);
		} else {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (logger.isDebugEnabled()) {
				logger.debug("Saving view root '" + context.getViewRoot().getViewId() + "' in view scope");
			}
			FlowSerializedView view = new FlowSerializedView(context.getViewRoot().getViewId(),
					getTreeStructureToSave(context), getComponentStateToSave(context));
			requestContext.getViewScope().put(SERIALIZED_VIEW_STATE, view);
			return view;
		}
	}

	/**
	 * <p>
	 * In JSF 2 where a partial state saving algorithm is used, this method merely delegates to the next
	 * ViewStateManager. Thus partial state saving is handled by the JSF 2 runtime. However, a
	 * {@link FlowViewResponseStateManager} plugged in via {@link FlowRenderKit} will ensure the state is saved in a Web
	 * Flow view-scoped variable.
	 * </p>
	 */
	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
		if ((!JsfUtils.isFlowRequest()) || isAtLeastJsf20()) {
			return delegate.restoreView(context, viewId, renderKitId);
		} else {
			UIViewRoot viewRoot = restoreTreeStructure(context, viewId, renderKitId);
			if (viewRoot != null) {
				context.setViewRoot(viewRoot);
				restoreComponentState(context, viewRoot, renderKitId);
			}
			return viewRoot;
		}
	}

	@Override
	public String getViewState(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return delegate.getViewState(context);
		}
		/*
		 * Mojarra 2: PartialRequestContextImpl.renderState() invokes this method during Ajax request rendering. We
		 * overridde it to convert FlowSerializedView state to an array before calling the
		 * ResponseStateManager.getViewState(), which in turn calls the ServerSideStateHelper and expects state to be an
		 * array.
		 */
		Object state = saveView(context);
		if (state != null) {
			if (state instanceof FlowSerializedView) {
				FlowSerializedView view = (FlowSerializedView) state;
				state = view.asTreeStructAndCompStateArray();
			}
			return context.getRenderKit().getResponseStateManager().getViewState(context, state);
		}
		return null;
	}

}
