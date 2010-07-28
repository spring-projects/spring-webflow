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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajax4jsf.context.AjaxContext;
import org.springframework.faces.webflow.FacesContextHelper;
import org.springframework.js.ajax.AbstractAjaxHandler;
import org.springframework.js.ajax.AjaxHandler;

/**
 * Ajax handler that works with Rich Faces, allowing support for Web Flow Ajax features with the Rich Faces toolkit.
 * 
 * @see AbstractAjaxHandler
 * 
 * @author Jeremy Grelle
 */
public class RichFacesAjaxHandler extends AbstractAjaxHandler implements AjaxHandler {

	/**
	 * Create a RichFacesAjaxHandler that is not part of a chain of AjaxHandler's.
	 */
	public RichFacesAjaxHandler() {
		this(null);
	}

	/**
	 * Create a RichFacesAjaxHandler as part of a chain of AjaxHandler's.
	 */
	public RichFacesAjaxHandler(AbstractAjaxHandler delegate) {
		super(delegate);
	}

	protected boolean isAjaxRequestInternal(HttpServletRequest request, HttpServletResponse response) {
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
			helper.releaseIfNecessary();
		}
	}

	protected void sendAjaxRedirectInternal(String targetUrl, HttpServletRequest request, HttpServletResponse response,
			boolean popup) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(targetUrl));
	}

}
