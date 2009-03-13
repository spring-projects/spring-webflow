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
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Simple delegating {@link ViewHandler} implementation that provides JSF Form's with the correct FlowExecution URL,
 * including the current FlowExecutionKey, so that postbacks may be properly intercepted and handled by Web Flow.
 * 
 * @author Jeremy Grelle
 */
public class FlowViewHandler extends ViewHandler {

	private ViewHandler delegate;

	public FlowViewHandler(ViewHandler delegate) {
		this.delegate = delegate;
	}

	public String getActionURL(FacesContext context, String viewId) {
		if (JsfUtils.isFlowRequest()) {
			return RequestContextHolder.getRequestContext().getFlowExecutionUrl();
		} else {
			return delegate.getActionURL(context, viewId);
		}
	}

	public Locale calculateLocale(FacesContext context) {
		if (JsfUtils.isFlowRequest()) {
			return RequestContextHolder.getRequestContext().getExternalContext().getLocale();
		} else {
			return delegate.calculateLocale(context);
		}
	}

	public UIViewRoot createView(FacesContext context, String viewId) {
		String resourcePath = viewId;
		if (JsfUtils.isFlowRequest()) {
			resourcePath = resolveResourcePath(RequestContextHolder.getRequestContext(), viewId);
		}
		return delegate.createView(context, resourcePath);
	}

	public UIViewRoot restoreView(FacesContext context, String viewId) {
		String resourcePath = viewId;
		if (JsfUtils.isFlowRequest()) {
			resourcePath = resolveResourcePath(RequestContextHolder.getRequestContext(), viewId);
			return restoreFlowView(context, resourcePath);
		}
		return delegate.restoreView(context, resourcePath);
	}

	// ------------------- Pass-through delegate methods ------------------//

	public String calculateRenderKitId(FacesContext context) {
		return delegate.calculateRenderKitId(context);
	}

	public String getResourceURL(FacesContext context, String path) {
		return delegate.getResourceURL(context, path);
	}

	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		delegate.renderView(context, viewToRender);
	}

	public void writeState(FacesContext context) throws IOException {
		delegate.writeState(context);
	}

	// --------------------- Private Helpers ------------------------------//

	private String resolveResourcePath(RequestContext context, String viewId) {
		if (viewId.startsWith("/")) {
			return viewId;
		} else {
			ApplicationContext flowContext = context.getActiveFlow().getApplicationContext();
			if (flowContext == null) {
				throw new IllegalStateException("A Flow ApplicationContext is required to resolve Flow View Resources");
			}
			Resource viewResource = flowContext.getResource(viewId);
			if (!(viewResource instanceof ContextResource)) {
				throw new IllegalStateException(
						"A ContextResource is required to get relative view paths within this context; the resource was "
								+ viewResource);
			}
			String viewPath = ((ContextResource) viewResource).getPathWithinContext();
			if (!viewPath.startsWith("/")) {
				viewPath = "/" + viewPath;
			}
			return viewPath;
		}
	}

	private UIViewRoot restoreFlowView(FacesContext facesContext, String resourcePath) {
		RequestContext context = RequestContextHolder.getRequestContext();
		ViewRootHolder holder = (ViewRootHolder) context.getFlashScope().get(ViewRootHolder.VIEW_ROOT_HOLDER_KEY);
		if (holder != null && holder.getViewRoot().getViewId().equals(resourcePath)) {
			return holder.getViewRoot();
		} else {
			return delegate.restoreView(facesContext, resourcePath);
		}
	}

}
