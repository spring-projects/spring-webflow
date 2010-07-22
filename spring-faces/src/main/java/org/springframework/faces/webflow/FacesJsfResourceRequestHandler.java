/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.faces.webflow;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Handles a request by delegating to the JSF ResourceHandler, which serves web application and classpath resources such
 * as images, CSS and JavaScript files from well-known locations.
 * 
 * @since 2.2.0
 * @author Rossen Stoyanchev
 * @see ResourceHandler
 */
public class FacesJsfResourceRequestHandler extends WebApplicationObjectSupport implements HttpRequestHandler {

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		FacesContext context = getFacesContext(request, response);
		ResourceHandler resourceHandler = context.getApplication().getResourceHandler();
		resourceHandler.handleResourceRequest(context);
	}

	private FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		return facesContextFactory.getFacesContext(getServletContext(), request, response, lifecycle);
	}

}
