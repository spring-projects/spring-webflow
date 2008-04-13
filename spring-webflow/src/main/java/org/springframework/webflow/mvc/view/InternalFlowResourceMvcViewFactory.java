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
package org.springframework.webflow.mvc.view;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.portlet.PortletMvcView;
import org.springframework.webflow.mvc.servlet.ServletMvcView;

/**
 * View factory implementation that creates a Spring-MVC Internal Resource view to render a flow-relative view resource
 * such as a JSP or Velocity template.
 * @author Keith Donald
 */
class InternalFlowResourceMvcViewFactory implements ViewFactory {

	private static final boolean JSTL_PRESENT = ClassUtils.isPresent("javax.servlet.jsp.jstl.fmt.LocalizationContext");

	private Expression viewIdExpression;

	private ExpressionParser expressionParser;

	private FormatterRegistry formatterRegistry;

	public InternalFlowResourceMvcViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry) {
		this.viewIdExpression = viewIdExpression;
		this.expressionParser = expressionParser;
		this.formatterRegistry = formatterRegistry;
	}

	public View getView(RequestContext context) {
		String viewId = (String) viewIdExpression.getValue(context);
		if (viewId.startsWith("/")) {
			return getViewInternal(viewId, context, context.getActiveFlow().getApplicationContext());
		} else {
			ApplicationContext flowContext = context.getActiveFlow().getApplicationContext();
			if (flowContext == null) {
				throw new IllegalStateException("A Flow ApplicationContext is required to resolve Flow View Resources");
			}
			Resource viewResource = flowContext.getResource(viewId);
			if (!(viewResource instanceof ContextResource)) {
				throw new IllegalStateException(
						"A ContextResource is required to get relative view paths within this context");
			}
			return getViewInternal(((ContextResource) viewResource).getPathWithinContext(), context, flowContext);
		}
	}

	private View getViewInternal(String viewPath, RequestContext context, ApplicationContext flowContext) {
		if (viewPath.endsWith(".jsp")) {
			if (JSTL_PRESENT) {
				JstlView view = new JstlView(viewPath);
				view.setApplicationContext(flowContext);
				return createMvcView(view, context);
			} else {
				InternalResourceView view = new InternalResourceView(viewPath);
				view.setApplicationContext(flowContext);
				return createMvcView(view, context);
			}
		} else {
			throw new IllegalArgumentException("Unsupported view type " + viewPath + " only types supported are [.jsp]");
		}
	}

	private MvcView createMvcView(org.springframework.web.servlet.View view, RequestContext context) {
		MvcView mvcView;
		if (context.getExternalContext() instanceof PortletExternalContext) {
			mvcView = new PortletMvcView(view, context);
		} else {
			mvcView = new ServletMvcView(view, context);
		}
		mvcView.setExpressionParser(expressionParser);
		mvcView.setFormatterRegistry(formatterRegistry);
		return mvcView;
	}

}