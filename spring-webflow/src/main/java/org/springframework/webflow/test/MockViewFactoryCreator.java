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
package org.springframework.webflow.test;

import java.io.IOException;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A view factory creator that returns view factories that produce Mock View implementations that can be used to assert
 * that the correct view id was selected as part of a flow execution test.
 * 
 * @author Keith Donald
 */
class MockViewFactoryCreator implements ViewFactoryCreator {

	public ViewFactory createViewFactory(Expression viewId, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration) {
		return new MockViewFactory(viewId);
	}

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId;
	}

	/**
	 * Returns a Mock View implementation that simply holds the evaluated view identifier.
	 * @author Keith Donald
	 */
	static class MockViewFactory implements ViewFactory {
		private Expression viewId;

		/**
		 * Creates a new mock view factory
		 * @param viewId the view id expression
		 */
		public MockViewFactory(Expression viewId) {
			this.viewId = viewId;
		}

		public View getView(RequestContext context) {
			String viewId = (String) this.viewId.getValue(context);
			return new MockView(viewId, context);
		}
	}

	/**
	 * A Mock view implementation that simply holds a reference to a identifier for a view that should be rendered.
	 * Useful to assert that the right view was selected as part of a flow execution test, without actually exercising
	 * any real rendering logic.
	 * 
	 * @author Keith Donald
	 */
	static class MockView implements View {

		/**
		 * The id of the view that would have been rendered.
		 */
		private String viewId;

		private RequestContext context;

		public MockView(String viewId, RequestContext context) {
			this.viewId = viewId;
			this.context = context;
		}

		/**
		 * Returns the id of the view that would have been rendered.
		 * @return the view id
		 */
		public String getViewId() {
			return viewId;
		}

		public void processUserEvent() {
			// TODO - implement me as appropriate for a test environment
		}

		public boolean hasFlowEvent() {
			return context.getRequestParameters().contains("_eventId");
		}

		public Event getFlowEvent() {
			return new Event(this, context.getRequestParameters().get("_eventId"));
		}

		public void render() throws IOException {
			context.getExternalContext().getResponseWriter().write(viewId);
		}

		public String toString() {
			return new ToStringCreator(this).append("viewId", viewId).toString();
		}
	}
}