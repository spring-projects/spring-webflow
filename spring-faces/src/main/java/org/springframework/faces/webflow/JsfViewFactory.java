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

import javax.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
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

	private final Expression viewExpr;

	private final ResourceLoader resourceLoader;

	public JsfViewFactory(Expression viewExpr, ResourceLoader resourceLoader) {
		this.viewExpr = viewExpr;
		this.resourceLoader = resourceLoader;
	}

	public View getView(RequestContext context) {
		Lifecycle lifecycle = createFlowFacesLifecycle();
		FacesContext facesContext = createFlowFacesContext(context, lifecycle);
		try {
			if (!facesContext.getRenderResponse()) {
				JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
			}
			String viewName = resolveViewName(context);
			ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
			viewHandler.initView(facesContext);
			UIViewRoot viewRoot = viewHandler.restoreView(facesContext, viewName);
			if (viewRoot != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("View root restored for '" + viewName + "'");
				}
				facesContext.setViewRoot(viewRoot);
				processComponentBinding(facesContext, viewRoot);
				JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
				lifecycle.execute(facesContext);
				return new JsfView(viewRoot, lifecycle, context);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Creating view root for '" + viewName + "'");
				}
				viewRoot = viewHandler.createView(facesContext, viewName);
				facesContext.setViewRoot(viewRoot);
				JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, lifecycle, facesContext);
				return new JsfView(viewRoot, lifecycle, context);
			}
		} finally {
			facesContext.release();
		}
	}

	private Lifecycle createFlowFacesLifecycle() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle defaultLifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		return new FlowLifecycle(defaultLifecycle);
	}

	private FacesContext createFlowFacesContext(RequestContext context, Lifecycle lifecycle) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context.getExternalContext()
				.getContext(), context.getExternalContext().getRequest(), context.getExternalContext().getResponse(),
				lifecycle);
		return new FlowFacesContext(context, defaultFacesContext);
	}

	private String resolveViewName(RequestContext context) {
		String viewId = (String) viewExpr.getValue(context);
		if (viewId.startsWith("/")) {
			return viewId;
		} else {
			ContextResource viewResource = (ContextResource) resourceLoader.getResource(viewId);
			return viewResource.getPathWithinContext();
		}
	}

	private void processComponentBinding(FacesContext context, UIComponent component) {
		ValueExpression binding = component.getValueExpression("binding");
		if (binding != null) {
			binding.setValue(context.getELContext(), component);
		}
		for (Iterator<UIComponent> iter = component.getFacetsAndChildren(); iter.hasNext();) {
			processComponentBinding(context, iter.next());
		}
	}
}