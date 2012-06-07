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

import java.util.Locale;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * Simple {@link ViewHandler} implementation that provides JSF Form's with the correct FlowExecution URL, including the
 * current FlowExecutionKey, so that postbacks may be properly intercepted and handled by Web Flow.
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class FlowViewHandler extends ViewHandlerWrapper {

	private final ViewHandler wrapped;

	public FlowViewHandler(ViewHandler wrapped) {
		Assert.notNull(wrapped, "The wrapped ViewHandler instance must not be null!");
		this.wrapped = wrapped;
	}

	public ViewHandler getWrapped() {
		return this.wrapped;
	}

	public String getActionURL(FacesContext context, String viewId) {
		if (JsfUtils.isFlowRequest()) {
			return RequestContextHolder.getRequestContext().getFlowExecutionUrl();
		} else {
			return super.getActionURL(context, viewId);
		}
	}

	public Locale calculateLocale(FacesContext context) {
		if (JsfUtils.isFlowRequest()) {
			return RequestContextHolder.getRequestContext().getExternalContext().getLocale();
		} else {
			return super.calculateLocale(context);
		}
	}

	public UIViewRoot createView(FacesContext context, String viewId) {
		String resourcePath = viewId;
		if (JsfUtils.isFlowRequest()) {
			resourcePath = resolveResourcePath(RequestContextHolder.getRequestContext(), viewId);
		}
		return super.createView(context, resourcePath);
	}

	public UIViewRoot restoreView(FacesContext context, String viewId) {
		String resourcePath = viewId;
		if (JsfUtils.isFlowRequest()) {
			resourcePath = resolveResourcePath(RequestContextHolder.getRequestContext(), viewId);
			return restoreFlowView(context, resourcePath);
		}
		return super.restoreView(context, resourcePath);
	}

	public String deriveViewId(FacesContext context, String rawViewId) {
		if (JsfUtils.isFlowRequest()) {
			return resolveResourcePath(RequestContextHolder.getRequestContext(), rawViewId);
		} else {
			return super.deriveViewId(context, rawViewId);
		}
	}

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
		ViewRootHolder holder = (ViewRootHolder) context.getFlashScope().get(View.USER_EVENT_STATE_ATTRIBUTE);
		if (holder != null && holder.getViewRoot() != null && holder.getViewRoot().getViewId().equals(resourcePath)) {
			return holder.getViewRoot();
		} else {
			return super.restoreView(facesContext, resourcePath);
		}
	}
}
