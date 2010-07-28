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

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Provides helper methods for getting a FacesContext that is suitable for use outside of Web Flow. Inside a running
 * Flow session {@link FlowFacesContext} is typically used instead.
 * <p>
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class FacesContextHelper {

	private boolean release = false;

	public FacesContext getFacesContext(ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response) {
		FacesContext facesContext = null;
		if (FacesContext.getCurrentInstance() != null) {
			facesContext = FacesContext.getCurrentInstance();
		} else {
			FacesContextFactory factory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = factory.getFacesContext(servletContext, request, response, FlowLifecycle.newInstance());
			release = true;
		}
		return facesContext;
	}

	public void releaseIfNecessary() {
		if (release) {
			FacesContext.getCurrentInstance().release();
		}
	}

}
