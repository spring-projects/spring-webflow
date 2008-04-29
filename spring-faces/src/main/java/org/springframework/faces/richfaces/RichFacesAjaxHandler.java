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
package org.springframework.faces.richfaces;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajax4jsf.context.AjaxContext;
import org.springframework.faces.webflow.FlowLifecycle;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Ajax handler that works with Rich Faces, allowing support for Web Flow Ajax features with the Rich Faces toolkit.
 * 
 * @author Jeremy Grelle
 */
public class RichFacesAjaxHandler extends WebApplicationObjectSupport implements AjaxHandler {

	private AjaxHandler delegate = new SpringJavascriptAjaxHandler();

	public boolean isAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
		if (isRichFacesAjaxRequest(request, response)) {
			return true;
		} else {
			return delegate.isAjaxRequest(request, response);
		}
	}

	public void sendAjaxRedirect(String targetUrl, HttpServletRequest request, HttpServletResponse response,
			boolean popup) throws IOException {
		if (isRichFacesAjaxRequest(request, response)) {
			response.sendRedirect(response.encodeRedirectURL(targetUrl));
		} else {
			delegate.sendAjaxRedirect(targetUrl, request, response, popup);
		}
	}

	protected boolean isRichFacesAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getServletContext(), request, response);
			AjaxContext context = AjaxContext.getCurrentInstance(facesContext);
			if (context != null) {
				return context.isAjaxRequest(facesContext);
			} else {
				return false;
			}
		} finally {
			helper.cleanup();
		}
	}

	private static class FacesContextHelper {

		private boolean created = false;

		protected FacesContext getFacesContext(ServletContext context, HttpServletRequest request,
				HttpServletResponse response) {
			if (FacesContext.getCurrentInstance() != null) {
				return FacesContext.getCurrentInstance();
			} else {
				created = true;
				FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
						.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
				FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context, request, response,
						FlowLifecycle.newInstance());
				return defaultFacesContext;
			}
		}

		protected void cleanup() {
			if (created) {
				FacesContext.getCurrentInstance().release();
			}
		}
	}
}
