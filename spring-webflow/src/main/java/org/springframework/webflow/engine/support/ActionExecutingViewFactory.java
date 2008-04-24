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

import java.util.Iterator;

import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.engine.ActionExecutor;
import org.springframework.webflow.execution.Action;
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
			eventId = findParameter("_eventId", context.getRequestParameters());
		}

		/**
		 * Obtain a named parameter from the request parameters. This method will try to obtain a parameter value using
		 * the following algorithm:
		 * <ol>
		 * <li>Try to get the parameter value using just the given <i>logical</i> name. This handles parameters of the
		 * form <tt>logicalName = value</tt>. For normal parameters, e.g. submitted using a hidden HTML form field,
		 * this will return the requested value.</li>
		 * <li>Try to obtain the parameter value from the parameter name, where the parameter name in the request is of
		 * the form <tt>logicalName_value = xyz</tt> with "_" being the configured delimiter. This deals with
		 * parameter values submitted using an HTML form submit button.</li>
		 * <li>If the value obtained in the previous step has a ".x" or ".y" suffix, remove that. This handles cases
		 * where the value was submitted using an HTML form image button. In this case the parameter in the request
		 * would actually be of the form <tt>logicalName_value.x = 123</tt>. </li>
		 * </ol>
		 * @param logicalParameterName the <i>logical</i> name of the request parameter
		 * @param parameters the available parameter map
		 * @return the value of the parameter, or <code>null</code> if the parameter does not exist in given request
		 */
		private String findParameter(String logicalParameterName, ParameterMap parameters) {
			// first try to get it as a normal name=value parameter
			String value = parameters.get(logicalParameterName);
			if (value != null) {
				return value;
			}
			// if no value yet, try to get it as a name_value=xyz parameter
			String prefix = logicalParameterName + "_";
			Iterator paramNames = parameters.asMap().keySet().iterator();
			while (paramNames.hasNext()) {
				String paramName = (String) paramNames.next();
				if (paramName.startsWith(prefix)) {
					String strValue = paramName.substring(prefix.length());
					// support images buttons, which would submit parameters as
					// name_value.x=123
					if (strValue.endsWith(".x") || strValue.endsWith(".y")) {
						strValue = strValue.substring(0, strValue.length() - 2);
					}
					return strValue;
				}
			}
			// we couldn't find the parameter value
			return null;
		}

	}
}