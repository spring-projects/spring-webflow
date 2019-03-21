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
package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.servlet.AjaxHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;

/**
 * An extension of {@link FlowHandlerAdapter} that replaces the default {@link AjaxHandler} instance with a
 * {@link JsfAjaxHandler}.
 *
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class JsfFlowHandlerAdapter extends FlowHandlerAdapter {


	public void afterPropertiesSet() throws Exception {

		boolean isAjaxHandlerConfigured = (getAjaxHandler() != null);
		super.afterPropertiesSet();

		if (!isAjaxHandlerConfigured) {
			JsfAjaxHandler handler = new JsfAjaxHandler();
			handler.setApplicationContext(getApplicationContext());
			setAjaxHandler(handler);
		}
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getServletContext(), request, response);
			request.setAttribute(FlowFacesContextLifecycleListener.DEFAULT_FACES_CONTEXT, facesContext);
			return super.handle(request, response, handler);

		} finally {
			request.removeAttribute(FlowFacesContextLifecycleListener.DEFAULT_FACES_CONTEXT);
			helper.releaseIfNecessary();
		}
	}
}
