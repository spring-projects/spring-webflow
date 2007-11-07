package org.springframework.webflow.action;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewFactory;

/**
 * Simple adapter that adapts a view factory render cycle to the action interface.
 * @author Keith Donald
 */
public class ViewFactoryActionAdapter extends AbstractAction {
	private ViewFactory viewFactory;

	public ViewFactoryActionAdapter(ViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		viewFactory.getView(context).render();
		return new Event(this, "success");
	}
}