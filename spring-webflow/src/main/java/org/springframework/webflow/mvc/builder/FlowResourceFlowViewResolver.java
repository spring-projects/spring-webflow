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
package org.springframework.webflow.mvc.builder;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * Creates Spring-MVC Internal Resource view to render a flow-relative view resource such as a JSP template.
 * 
 * @see JstlView
 * @see InternalResourceView
 * 
 * @author Keith Donald
 */
public class FlowResourceFlowViewResolver implements FlowViewResolver {

	private static final boolean JSTL_PRESENT = ClassUtils.isPresent("javax.servlet.jsp.jstl.fmt.LocalizationContext");

	private String defaultViewSuffix = ".jsp";

	/**
	 * Returns the default view suffix when selecting views by convention. Default is .jsp.
	 * @return the default view suffix
	 */
	public String getDefaultViewSuffix() {
		return defaultViewSuffix;
	}

	/**
	 * Sets the default suffix for view templates when selecting views by convention. Default is .jsp. Respected when a
	 * {@link FlowResourceFlowViewResolver} is configured.
	 * @param defaultViewSuffix the default view suffix
	 */
	public void setDefaultViewSuffix(String defaultViewSuffix) {
		this.defaultViewSuffix = defaultViewSuffix;
	}

	public View resolveView(String viewId, RequestContext context) {
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

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId + defaultViewSuffix;
	}

	// internal helpers

	private View getViewInternal(String viewPath, RequestContext context, ApplicationContext flowContext) {
		if (viewPath.endsWith(".jsp") || viewPath.endsWith(".jspx")) {
			if (JSTL_PRESENT) {
				JstlView view = new JstlView(viewPath);
				view.setApplicationContext(flowContext);
				return view;
			} else {
				InternalResourceView view = new InternalResourceView(viewPath);
				view.setApplicationContext(flowContext);
				return view;
			}
		} else {
			throw new IllegalArgumentException("Unsupported view type " + viewPath
					+ " only types supported by this FlowViewResolver implementation are [.jsp] and [.jspx]");
		}
	}
}