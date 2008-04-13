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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

public class StubViewFactory implements ViewFactory {

	public View getView(RequestContext context) {
		return new NullView(context);
	}

	private static class NullView implements View {
		private RequestContext context;

		public NullView(RequestContext context) {
			this.context = context;
		}

		public void render() {
			context.getFlowScope().put("renderCalled", Boolean.TRUE);
		}

		public void processUserEvent() {

		}

		public boolean hasFlowEvent() {
			return context.getExternalContext().getRequestParameterMap().contains("_eventId");
		}

		public Event getFlowEvent() {
			return new Event(this, context.getExternalContext().getRequestParameterMap().get("_eventId"));
		}
	}
}