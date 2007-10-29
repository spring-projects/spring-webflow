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

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.binding.expression.Expression;
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

	private final Lifecycle facesLifecycle;

	private final Expression viewExpr;

	public JsfViewFactory(Lifecycle facesLifecycle, Expression viewExpr) {

		this.facesLifecycle = facesLifecycle;
		this.viewExpr = viewExpr;
	}

	public View getView(RequestContext context) {

		FacesContext facesContext = JsfFlowUtils.getFacesContext(facesLifecycle);
		try {
			boolean restored = false;

			if (!facesContext.getRenderResponse()) {
				JsfFlowUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, facesLifecycle);
			}

			JsfView view;

			ViewHandler handler = facesContext.getApplication().getViewHandler();

			if (viewExists(facesContext, viewExpr.getValue(context).toString())) {
				view = new JsfView(facesContext.getViewRoot(), facesLifecycle);
				restored = true;
			} else {
				String viewName = (String) viewExpr.getValue(context);
				UIViewRoot root = handler.restoreView(facesContext, viewName);
				if (root != null) {
					view = new JsfView(root, facesLifecycle);
					restored = true;
				} else {
					view = new JsfView(handler.createView(facesContext, viewName), facesLifecycle);
					restored = false;
				}
			}

			facesContext.setViewRoot(view.getViewRoot());

			processBindings(facesContext.getELContext(), view.getViewRoot());

			if (!facesContext.getRenderResponse()) {
				JsfFlowUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, facesLifecycle);
			}

			if (restored && !facesContext.getResponseComplete() && !facesContext.getRenderResponse()) {
				facesLifecycle.execute(facesContext);
				facesContext.renderResponse();
			}

			return view;
		} finally {
			facesContext.release();
		}
	}

	private boolean viewExists(FacesContext facesContext, String viewId) {
		if (facesContext.getViewRoot() != null && facesContext.getViewRoot().getViewId().equals(viewId)) {
			return true;
		}
		return false;
	}

	private void processBindings(ELContext elContext, UIComponent component) {

		ValueExpression expr = component.getValueExpression("binding");
		if (expr != null) {
			expr.setValue(elContext, component);
		}

		Iterator i = component.getChildren().iterator();
		while (i.hasNext()) {
			UIComponent child = (UIComponent) i.next();
			processBindings(elContext, child);
		}
	}
}
