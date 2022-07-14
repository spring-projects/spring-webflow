/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.mvc.servlet;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.convert.ConversionService;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.AbstractMvcView;

/**
 * The Spring Web Servlet MVC view implementation.
 *
 * @author Keith Donald
 */
public class ServletMvcView extends AbstractMvcView {

	/**
	 * Creates a new Servlet MVC view.
	 * @param view the view to render
	 * @param context the current flow request context.
	 */
	public ServletMvcView(org.springframework.web.servlet.View view, RequestContext context) {
		super(view, context);
	}

	protected void doRender(Map<String, ?> model) throws Exception {
		RequestContext context = getRequestContext();
		ExternalContext externalContext = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getNativeRequest();
		HttpServletResponse response = (HttpServletResponse) externalContext.getNativeResponse();
		request.setAttribute(org.springframework.web.servlet.support.RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				context.getActiveFlow().getApplicationContext());
		// spring:eval tag
		if (getConversionService() != null) {
			request.setAttribute(ConversionService.class.getName(), getConversionService().getDelegateConversionService());
		}
		getView().render(model, request, response);
	}

}
