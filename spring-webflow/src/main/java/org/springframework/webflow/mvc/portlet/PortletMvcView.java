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
package org.springframework.webflow.mvc.portlet;

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.DispatcherPortlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.MvcView;

public class PortletMvcView extends MvcView {

	private ApplicationContext applicationContext;

	public PortletMvcView(org.springframework.web.servlet.View view, RequestContext context,
			ApplicationContext applicationContext) {
		super(view, context);
		this.applicationContext = applicationContext;
	}

	public void render(Map model, ExternalContext context) throws Exception {
		PortletContext portletContext = (PortletContext) context.getNativeContext();
		RenderRequest request = (RenderRequest) context.getNativeRequest();
		RenderResponse response = (RenderResponse) context.getNativeResponse();
		View view = getView();

		// Set the content type on the response if needed and if possible.
		// The Portlet spec requires the content type to be set on the RenderResponse;
		// it's not sufficient to let the View set it on the ServletResponse.
		if (response.getContentType() == null) {
			// No Portlet content type specified yet -> use the view-determined type.
			String contentType = view.getContentType();
			if (contentType != null) {
				response.setContentType(contentType);
			}
		}

		// Expose Portlet ApplicationContext to view objects.
		request.setAttribute(ViewRendererServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		// These attributes are required by the ViewRendererServlet.
		request.setAttribute(ViewRendererServlet.VIEW_ATTRIBUTE, view);
		request.setAttribute(ViewRendererServlet.MODEL_ATTRIBUTE, model);

		// Include the content of the view in the render response.
		portletContext.getRequestDispatcher(DispatcherPortlet.DEFAULT_VIEW_RENDERER_URL).include(request, response);
	}

}