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

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.RequestContext;

/**
 * A Web Flow flavor of the familiar Spring MVC View Resolver interface. Allows resolving a Spring MVC {@link View view}
 * from the state of an executing flow.
 * 
 * @author Keith Donald
 * @see ViewResolver
 */
public interface FlowViewResolver {

	/**
	 * Resolve the Spring MVC view with the provided id.
	 * @param viewId the view id, typically treated as a Spring MVC view name
	 * @param context the current flow request
	 * @return the resolved Spring MVC view
	 */
	public View resolveView(String viewId, RequestContext context);

	/**
	 * Get the default id of the view to render in the provided view state by convention.
	 * @param viewStateId the view state id
	 * @return the default view id
	 */
	public String getViewIdByConvention(String viewStateId);

}