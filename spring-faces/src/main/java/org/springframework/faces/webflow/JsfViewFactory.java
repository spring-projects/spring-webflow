/*
 * Copyright 2004-2007 the original author or authors.
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

import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.faces.ui.AjaxViewRoot;
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

	private final ResourceLoader resourceLoader;

	private final Lifecycle lifecycle;

	public JsfViewFactory(Expression viewIdExpression, ResourceLoader resourceLoader, Lifecycle lifecycle) {
		this.viewIdExpression = viewIdExpression;
		this.resourceLoader = resourceLoader;
		this.lifecycle = lifecycle;
	}

	public View getView(RequestContext context) {

		FacesContext facesContext = createFlowFacesContext(context, lifecycle);
		try {
			boolean restored = false;

			if (!facesContext.getRenderResponse()) {
				JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
			}

			JsfView view;
			String viewName = resolveViewName(context);
			ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

			if (viewExists(facesContext, viewName)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Existing view root found for '" + viewName + "'");
				}
				view = createJsfView(facesContext.getViewRoot(), lifecycle, context);
				restored = true;
			} else {
				UIViewRoot viewRoot = viewHandler.restoreView(facesContext, viewName);
				if (viewRoot != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("View root restored for '" + viewName + "'");
					}
					view = createJsfView(viewRoot, lifecycle, context);
					facesContext.setViewRoot(view.getViewRoot());
					processComponentBinding(facesContext, view.getViewRoot());
					restored = true;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Creating view root for '" + viewName + "'");
					}
					view = createJsfView(viewHandler.createView(facesContext, viewName), lifecycle, context);
					facesContext.setViewRoot(view.getViewRoot());
					restored = false;
				}
			}

			if (!facesContext.getRenderResponse()) {
				JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
			}

			if (restored && !facesContext.getResponseComplete() && !facesContext.getRenderResponse()) {
				lifecycle.execute(facesContext);
				facesContext.renderResponse();
			}

			return view;
		} finally {
			facesContext.release();
		}
	}

	private JsfView createJsfView(UIViewRoot root, Lifecycle lifecycle, RequestContext context) {
		if (context.getExternalContext().isAjaxRequest()) {
			return new JsfView(new AjaxViewRoot(root), lifecycle, context);
		} else {
			return new JsfView(root, lifecycle, context);
		}
	}

	private FacesContext createFlowFacesContext(RequestContext context, Lifecycle lifecycle) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context.getExternalContext()
				.getNativeContext(), context.getExternalContext().getNativeRequest(), context.getExternalContext()
				.getNativeResponse(), lifecycle);
		return new FlowFacesContext(context, defaultFacesContext);
	}

	private String resolveViewName(RequestContext context) {
		String viewId = (String) viewIdExpression.getValue(context);
		if (viewId.startsWith("/")) {
			return viewId;
		} else {
			ContextResource viewResource = (ContextResource) resourceLoader.getResource(viewId);
			return viewResource.getPathWithinContext();
		}
	}

	private boolean viewExists(FacesContext facesContext, String viewId) {
		if (facesContext.getViewRoot() != null && facesContext.getViewRoot().getViewId().equals(viewId)) {
			return true;
		}
		return false;
	}

	private void processComponentBinding(FacesContext context, UIComponent component) {
		ValueBinding binding = component.getValueBinding("binding");
		if (binding != null) {
			binding.setValue(context, component);
		}

		Iterator i = component.getChildren().iterator();
		while (i.hasNext()) {
			UIComponent child = (UIComponent) i.next();
			processComponentBinding(context, child);
		}
	}
}
