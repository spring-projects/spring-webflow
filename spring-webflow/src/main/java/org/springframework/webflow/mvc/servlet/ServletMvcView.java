/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.mvc.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.MvcView;

/**
 * Creates a new Spring Web Servlet MVC view.
 * @author Keith Donald
 */
public class ServletMvcView extends MvcView {

	/**
	 * Creates a new servlet view.
	 * @param view the view to render
	 * @param context the current flow request context.
	 */
	public ServletMvcView(org.springframework.web.servlet.View view, RequestContext context) {
		super(view, context);
	}

	public void doRender(org.springframework.web.servlet.View view, Map model, ExternalContext context) throws Exception {
		view.render(model, (HttpServletRequest) context.getNativeRequest(), (HttpServletResponse) context
				.getNativeResponse());
	}

}