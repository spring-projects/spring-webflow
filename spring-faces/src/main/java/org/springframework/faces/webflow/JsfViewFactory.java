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

import java.util.Iterator;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.faces.ui.AjaxViewRoot;
import org.springframework.js.mvc.servlet.AjaxHandler;
import org.springframework.js.mvc.servlet.SpringJavascriptAjaxHandler;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * JSF-specific {@link ViewFactory} implementation.
 * <p>
 * This factory is responsible for performing the duties of the RESTORE_VIEW phase of the JSF lifecycle. If the current
 * request is a post-back, then the rest of the standard JSF lifecyle through INVOKE_APPLICATION will be executed as
 * well when an existing {@link JsfView} is found and restored.
 * 
 * @author Jeremy Grelle
 */
public class JsfViewFactory implements ViewFactory {

	private static final Log logger = LogFactory.getLog(JsfViewFactory.class);

	private final Expression viewIdExpression;

	private final Lifecycle lifecycle;

	public JsfViewFactory(Expression viewIdExpression, Lifecycle lifecycle) {
		this.viewIdExpression = viewIdExpression;
		this.lifecycle = lifecycle;
	}

	public View getView(RequestContext context) {
		FacesContext facesContext = FlowFacesContext.newInstance(context, lifecycle);
		try {
			if (!facesContext.getRenderResponse()) {
				// only publish a RESTORE_VIEW event if this is the first phase of the lifecycle
				// this won't be true when this method is called after a transition from one view-state to another
				JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
			}
			ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
			if (JsfUtils.isAtLeastJsf12() && !JsfUtils.isPortlet(facesContext)) {
				viewHandler.initView(facesContext);
			}
			JsfView view;
			String viewName = resolveViewName(context);
			if (viewAlreadySet(facesContext)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Existing view root found with id '" + facesContext.getViewRoot().getId() + "'");
				}
				UIViewRoot viewRoot = facesContext.getViewRoot();
				viewRoot.setLocale(context.getExternalContext().getLocale());
				processComponentBinding(facesContext, viewRoot);
				view = createJsfView(facesContext.getViewRoot(), lifecycle, context);
				view.setRestored(true);
			} else {
				UIViewRoot viewRoot = viewHandler.restoreView(facesContext, viewName);
				if (viewRoot != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("View root restored for '" + viewName + "'");
					}
					view = createJsfView(viewRoot, lifecycle, context);
					facesContext.setViewRoot(view.getViewRoot());
					processComponentBinding(facesContext, view.getViewRoot());
					view.setRestored(true);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Creating view root for '" + viewName + "'");
					}
					view = createJsfView(viewHandler.createView(facesContext, viewName), lifecycle, context);
					facesContext.setViewRoot(view.getViewRoot());
					view.setRestored(false);
				}
			}
			if (!facesContext.getRenderResponse()) {
				JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
			}
			return view;
		} finally {
			facesContext.release();
		}
	}

	private boolean viewAlreadySet(FacesContext facesContext) {
		if (facesContext.getViewRoot() != null && facesContext.getViewRoot() != null) {
			// the corner case where a before RESTORE_VIEW PhaseListener has handled setting the UIViewRoot
			return true;
		} else {
			return false;
		}
	}

	private JsfView createJsfView(UIViewRoot root, Lifecycle lifecycle, RequestContext context) {
		if (isSpringJavascriptAjaxRequest(context.getExternalContext())) {
			return new JsfView(new AjaxViewRoot(root), lifecycle, context);
		} else {
			return new JsfView(root, lifecycle, context);
		}
	}

	private boolean isSpringJavascriptAjaxRequest(ExternalContext context) {
		// this is not very clean
		if (context.getNativeContext() instanceof ServletContext) {
			AjaxHandler handler = new SpringJavascriptAjaxHandler();
			return handler.isAjaxRequest((ServletContext) context.getNativeContext(), (HttpServletRequest) context
					.getNativeRequest(), (HttpServletResponse) context.getNativeResponse());
		} else {
			return false;
		}
	}

	private String resolveViewName(RequestContext context) {
		String viewId = (String) viewIdExpression.getValue(context);
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
						"A ContextResource is required to get relative view paths within this context");
			}
			return ((ContextResource) viewResource).getPathWithinContext();
		}
	}

	private void processComponentBinding(FacesContext context, UIComponent component) {
		ValueBinding binding = component.getValueBinding("binding");
		if (binding != null) {
			binding.setValue(context, component);
		}
		Iterator it = component.getChildren().iterator();
		while (it.hasNext()) {
			UIComponent child = (UIComponent) it.next();
			processComponentBinding(context, child);
		}
	}
}
