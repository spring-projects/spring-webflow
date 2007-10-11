package org.springframework.faces.webflow;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

public class JsfRenderFinalResponseAction implements Action {

	ViewFactory viewFactory;

	public JsfRenderFinalResponseAction(ViewFactory viewFactory) {
		Assert.notNull(viewFactory);
		this.viewFactory = viewFactory;
	}

	public Event execute(RequestContext context) throws Exception {

		View view = viewFactory.getView(context);
		Assert.isInstanceOf(JsfView.class, view);

		((JsfView) view).getViewRoot().setTransient(true);

		view.render();

		return new Event(this, "success");

	}

}
