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
package org.springframework.webflow.mvc.portlet;

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.DispatcherPortlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.AbstractMvcView;

/**
 * The Spring Web Portlet MVC view implementation.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class PortletMvcView extends AbstractMvcView {

	/**
	 * Creates a new Portlet MVC view.
	 * @param view the view to render
	 * @param context the current flow request context
	 */
	public PortletMvcView(org.springframework.web.servlet.View view, RequestContext context) {
		super(view, context);
	}

	protected void doRender(Map model) throws Exception {
		RequestContext context = getRequestContext();
		ExternalContext externalContext = context.getExternalContext();
		View view = getView();
		PortletContext portletContext = (PortletContext) externalContext.getNativeContext();
		RenderRequest request = (RenderRequest) externalContext.getNativeRequest();
		RenderResponse response = (RenderResponse) externalContext.getNativeResponse();
		if (response.getContentType() == null) {
			// No Portlet content type specified yet -> use the view-determined type.
			// (The Portlet spec requires the content type to be set on the RenderResponse)
			String contentType = view.getContentType();
			if (contentType != null) {
				response.setContentType(contentType);
			}
		}
		request.setAttribute(ViewRendererServlet.VIEW_ATTRIBUTE, view);
		request.setAttribute(ViewRendererServlet.MODEL_ATTRIBUTE, model);
		request.setAttribute(org.springframework.web.servlet.support.RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				context.getActiveFlow().getApplicationContext());
		portletContext.getRequestDispatcher(DispatcherPortlet.DEFAULT_VIEW_RENDERER_URL).include(request, response);
	}

}