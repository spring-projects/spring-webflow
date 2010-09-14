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

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
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
public class JsfResourceRequestHandler extends WebApplicationObjectSupport implements HttpRequestHandler {

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getServletContext(), request, response);
			ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
			resourceHandler.handleResourceRequest(facesContext);
		} finally {
			helper.releaseIfNecessary();
		}
	}

}
