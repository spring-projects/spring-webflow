package org.springframework.webflow.engine.builder.support;

import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

public class ActionInvokingViewFactory implements ViewFactory {

	private Action action;

	public ActionInvokingViewFactory(Action action) {
		this.action = action;
	}

	public View getView(RequestContext context) {
		return new ActionExecutingView(action, context);
	}

	private static class ActionExecutingView implements View {

		private Action action;

		private RequestContext context;

		private ActionExecutingView(Action action, RequestContext context) {
			this.action = action;
			this.context = context;
		}

		public boolean eventSignaled() {
			return context.getExternalContext().getRequestParameterMap().contains("_eventId");
		}

		public Event getEvent() {
			return new Event(this, context.getExternalContext().getRequestParameterMap().get("_eventId"));
		}

		public void render() {
			try {
				action.execute(context);
			} catch (Exception e) {
				// TODO
			}
		}

	}

}