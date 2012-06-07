/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.webflow.context.portlet;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.core.Ordered;
import org.springframework.faces.webflow.FacesContextHelper;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.portlet.HandlerAdapter;
import org.springframework.web.portlet.HandlerExecutionChain;
import org.springframework.web.portlet.HandlerMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.handler.PortletContentGenerator;

/**
 * Handles a request by delegating to the JSF ResourceHandler, which serves web application and classpath resources such
 * as images, CSS and JavaScript files from well-known locations.
 *
 * @since 2.4.0
 * @author Phillip Webb
 * @see ResourceHandler
 */
public class JsfResourceRequestHandler extends PortletContentGenerator implements HandlerAdapter, HandlerMapping, Ordered {

	private static final String FACES_RESOURCE = "javax.faces.resource";

	private static final String RESOURCE_EXCLUDES_DEFAULT = ".class .jsp .jspx .properties .xhtml .groovy";

	private static final String RESOURCE_EXCLUDES_PARAM_NAME = "javax.faces.RESOURCE_EXCLUDES";

	private int order = Ordered.HIGHEST_PRECEDENCE;

	public HandlerExecutionChain getHandler(PortletRequest request) throws Exception {
		if (request instanceof ResourceRequest && request.getParameter(FACES_RESOURCE) != null) {
			return new HandlerExecutionChain(new JsfResourceRequest());
		}
		return null;
	}

	public boolean supports(Object handler) {
		return handler instanceof JsfResourceRequest;
	}

	public ModelAndView handleResource(ResourceRequest request, ResourceResponse response, Object handler) throws IOException {
		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getPortletContext(), request, response);
			handleResourceRequest(facesContext, request, response);
		} finally {
			helper.releaseIfNecessary();
		}
		return null;
	}

	private void handleResourceRequest(FacesContext facesContext, ResourceRequest request, ResourceResponse response) throws IOException {
		ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
		String resourceName = request.getParameter(FACES_RESOURCE);
		String libraryName = request.getParameter("ln");
		int statusCodeNotFound = HttpStatus.NOT_FOUND.value();
		if (isResourceExcluded(facesContext, resourceName)) {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(statusCodeNotFound));
			PortletResponseUtils.setStatusCodeForPluto(response, statusCodeNotFound);
			return;
		}
		Resource resource = createResource(resourceHandler, resourceName, libraryName);
		if (resource == null) {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(statusCodeNotFound));
			PortletResponseUtils.setStatusCodeForPluto(response, statusCodeNotFound);
			return;
		}
		for (Map.Entry<String, String> entry : resource.getResponseHeaders().entrySet()) {
			response.setProperty(entry.getKey(), entry.getValue());
		}
		response.setContentType(resource.getContentType());
		FileCopyUtils.copy(resource.getInputStream(), response.getPortletOutputStream());
	}

	private boolean isResourceExcluded(FacesContext context, String resourceName) {
		for (String resourceExclude : getResourceExcludes(context)) {
			if (StringUtils.endsWithIgnoreCase(resourceName, resourceExclude)) {
				return true;
			}
		}
		return false;
	}

	private String[] getResourceExcludes(FacesContext context) {
		String resourceExcludes = context.getExternalContext().getInitParameter(RESOURCE_EXCLUDES_PARAM_NAME);
		if (resourceExcludes == null) {
			resourceExcludes = RESOURCE_EXCLUDES_DEFAULT;
		}
		return StringUtils.tokenizeToStringArray(resourceExcludes, " ");
	}

	private Resource createResource(ResourceHandler resourceHandler, String resourceName, String libraryName) {
		if (libraryName != null) {
			return resourceHandler.createResource(resourceName, libraryName);
		}
		return resourceHandler.createResource(resourceName);
	}

	public void handleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {
	}

	public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {
		return null;
	}

	public void handleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	private static class JsfResourceRequest {
	}
}