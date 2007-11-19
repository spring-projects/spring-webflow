package org.springframework.faces.webflow;

import javax.faces.component.UIViewRoot;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.faces.ui.AjaxViewRoot;
import org.springframework.webflow.execution.RequestContext;

public class AjaxJsfView extends JsfView {

	public AjaxJsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle, RequestContext context) {
		super(new AjaxViewRoot(viewRoot), facesLifecycle, context);
	}

}
