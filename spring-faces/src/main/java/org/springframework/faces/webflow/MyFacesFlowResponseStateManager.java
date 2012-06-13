/*
 * Copyright 2004-2012 the original author or authors.
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

import javax.faces.FacesWrapper;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import org.apache.myfaces.renderkit.MyfacesResponseStateManager;
import org.apache.myfaces.renderkit.StateCacheUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * A wrapper for {@link FlowResponseStateManager} used to support MyFaces partial state saving. MyFaces supports an
 * extension to the {@link ResponseStateManager} that reduces the amount of buffering required when writing a response.
 * Empty state is provided at the time that the {@link #writeState(FacesContext, Object) writeState} method is invoked
 * with an additional {@link #saveState(FacesContext, Object) saveState} method called later containing the real state
 * to save.
 * <p>
 * Since JSF 2.0, the strategy used by MyFaces to determine if a {@link MyfacesResponseStateManager} is available will
 * always succeed since it follows {@link FacesWrapper}s to find the root <tt>HtmlResponseStateManager</tt>
 * implementation. Since state management for web flow requests is handled by the {@link FlowResponseStateManager} this
 * assumption causes problems and results in empty state data being saved. This wrapper provides the additional hook
 * required to ensure that the {@link #saveState(FacesContext, Object) saveState} method also triggers web flow state
 * management.
 *
 * @see FlowResponseStateManager
 * @see FlowRenderKit
 *
 * @author Phillip Webb
 *
 * @since 2.4
 */
@SuppressWarnings("deprecation")
public class MyFacesFlowResponseStateManager extends MyfacesResponseStateManager implements
		FacesWrapper<ResponseStateManager> {

	private final ResponseStateManager wrapped;

	public MyFacesFlowResponseStateManager(FlowResponseStateManager wrapped) {
		this.wrapped = wrapped;
	}

	public ResponseStateManager getWrapped() {
		return this.wrapped;
	}

	private MyfacesResponseStateManager getWrappedMyfacesResponseStateManager() {
		return StateCacheUtils.getMyFacesResponseStateManager(this.wrapped);
	}

	public boolean isWriteStateAfterRenderViewRequired(FacesContext facesContext) {
		MyfacesResponseStateManager wrapped = getWrappedMyfacesResponseStateManager();
		if (wrapped != null) {
			return wrapped.isWriteStateAfterRenderViewRequired(facesContext);
		}
		return super.isWriteStateAfterRenderViewRequired(facesContext);
	}

	public void saveState(FacesContext facesContext, Object state) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		requestContext.getViewScope().put(FlowResponseStateManager.FACES_VIEW_STATE, state);
	}

	public void writeStateAsUrlParams(FacesContext facesContext, SerializedView serializedview) {
		MyfacesResponseStateManager wrapped = getWrappedMyfacesResponseStateManager();
		if (wrapped != null) {
			wrapped.writeStateAsUrlParams(facesContext, serializedview);
		}
		super.writeStateAsUrlParams(facesContext, serializedview);
	}

	public Object getComponentStateToRestore(FacesContext context) {
		return getWrapped().getComponentStateToRestore(context);
	}

	public Object getState(FacesContext context, String viewId) {
		return getWrapped().getState(context, viewId);
	}

	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		return getWrapped().getTreeStructureToRestore(context, viewId);
	}

	public String getViewState(FacesContext context, Object state) {
		return getWrapped().getViewState(context, state);
	}

	public boolean isPostback(FacesContext context) {
		return getWrapped().isPostback(context);
	}

	public void writeState(FacesContext context, Object state) throws IOException {
		getWrapped().writeState(context, state);
	}

	@Deprecated
	public void writeState(FacesContext context, SerializedView state) throws IOException {
		getWrapped().writeState(context, state);
	}
}
