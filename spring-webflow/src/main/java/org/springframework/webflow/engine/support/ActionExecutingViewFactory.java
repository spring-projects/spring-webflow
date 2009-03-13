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
package org.springframework.webflow.engine.support;

import org.springframework.web.util.WebUtils;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ActionExecutor;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A view factory implementation that creates views that execute an action when rendered. Used mainly to encapsulate an
 * action that renders a response. Examples include flow redirect and external redirect actions.
 */
public class ActionExecutingViewFactory implements ViewFactory {

	/**
	 * The action to execute.
	 */
	private Action action;

	/**
	 * Create a new action invoking view factory
	 * @param action the action to execute
	 */
	public ActionExecutingViewFactory(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public View getView(RequestContext context) {
		return new ActionExecutingView(action, context);
	}

	private static class ActionExecutingView implements View {

		private Action action;

		private RequestContext requestContext;

		private String eventId;

		private ActionExecutingView(Action action, RequestContext requestContext) {
			this.action = action;
			this.requestContext = requestContext;
		}

		public void render() {
			if (action != null) {
				ActionExecutor.execute(action, requestContext);
			}
		}

		public void processUserEvent() {
			determineEventId(requestContext);
		}

		public boolean hasFlowEvent() {
			return eventId != null;
		}

		public Event getFlowEvent() {
			if (!hasFlowEvent()) {
				return null;
			}
			return new Event(this, eventId);
		}

		private void determineEventId(RequestContext context) {
			eventId = WebUtils.findParameterValue(context.getRequestParameters().asMap(), "_eventId");
		}
	}
}