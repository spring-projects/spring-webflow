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

import java.util.Iterator;
import java.util.List;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * View factory implementation that delegates to the Spring-configured view resolver chain to resolve the Spring MVC
 * view implementation to render.
 * 
 * @author Keith Donald
 */
public class DelegatingFlowViewResolver implements FlowViewResolver {

	private List viewResolvers;

	/**
	 * Creates a new flow view resolver that delegates to
	 * @param viewResolvers
	 */
	public DelegatingFlowViewResolver(List viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	public View resolveView(String viewName, RequestContext context) {
		for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
			ViewResolver viewResolver = (ViewResolver) it.next();
			try {
				View view = viewResolver.resolveViewName(viewName, context.getExternalContext().getLocale());
				if (view != null) {
					return view;
				}
			} catch (Exception e) {
				throw new IllegalStateException("Exception resolving view with name '" + viewName + "'", e);
			}
		}
		return null;
	}

}