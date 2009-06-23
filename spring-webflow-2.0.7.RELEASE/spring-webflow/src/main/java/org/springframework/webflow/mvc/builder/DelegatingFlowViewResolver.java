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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * Delegates to a configured view resolver chain to resolve the Spring MVC view implementation to render.
 * 
 * @see ViewResolver
 * 
 * @author Keith Donald
 */
public class DelegatingFlowViewResolver implements FlowViewResolver {

	private List viewResolvers;

	/**
	 * Creates a new flow view resolver.
	 * @param viewResolvers the Spring MVC view resolver chain to delegate to
	 */
	public DelegatingFlowViewResolver(List viewResolvers) {
		this.viewResolvers = viewResolvers != null ? viewResolvers : Collections.EMPTY_LIST;
	}

	public View resolveView(String viewId, RequestContext context) {
		for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
			ViewResolver viewResolver = (ViewResolver) it.next();
			try {
				View view = viewResolver.resolveViewName(viewId, context.getExternalContext().getLocale());
				if (view != null) {
					return view;
				}
			} catch (Exception e) {
				IllegalStateException ise = new IllegalStateException("Exception resolving view with name '" + viewId
						+ "'");
				ise.initCause(e);
				throw ise;
			}
		}
		return null;
	}

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId;
	}
}