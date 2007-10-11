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
