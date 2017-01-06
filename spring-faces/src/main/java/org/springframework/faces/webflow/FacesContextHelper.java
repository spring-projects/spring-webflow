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

package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * Provides helper methods for getting a FacesContext that is suitable for use outside of Web Flow. Inside a running
 * Flow session {@link FlowFacesContext} is typically used instead.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 *
 * @since 2.2.0
 */
public class FacesContextHelper {

	private boolean release = false;

	/**
	 * Returns a faces context that can be used outside of Web Flow. The context must be {@link #releaseIfNecessary()
	 * released} after use.
	 *
	 * @param context the native context
	 * @param request the native request
	 * @param response the native response
	 * @return a {@link FacesContext} instance.
	 * @see #release
	 */
	public FacesContext getFacesContext(Object context, Object request, Object response) {
		FacesContext facesContext = null;
		if (FacesContext.getCurrentInstance() != null) {
			facesContext = FacesContext.getCurrentInstance();
		} else {
			facesContext = newDefaultInstance(context, request, response, FlowLifecycle.newInstance());
			this.release = true;
		}
		return facesContext;
	}

	/**
	 * Release any previously {@link #getFacesContext obtained} {@link FacesContext} if necessary.
	 *
	 * @see #getFacesContext(Object, Object, Object)
	 */
	public void releaseIfNecessary() {
		if (this.release) {
			FacesContext.getCurrentInstance().release();
		}
	}

	/**
	 * Factory method that can be used to create a new default {@link FacesContext} instance.
	 *
	 * @param context the native context
	 * @param request the native request
	 * @param response the native response
	 * @param lifecycle the JSF lifecycle
	 * @return a new {@link FacesContext} instance
	 */
	public static FacesContext newDefaultInstance(Object context, Object request, Object response, Lifecycle lifecycle) {
		FacesContextFactory facesContextFactory = JsfUtils.findFactory(FacesContextFactory.class);
		return facesContextFactory.getFacesContext(context, request, response, lifecycle);
	}

}
