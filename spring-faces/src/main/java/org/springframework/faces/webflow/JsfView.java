/*
 * Copyright 2004-2013 the original author or authors.
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
import java.io.Serializable;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * JSF-specific {@link View} implementation.
 *
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JsfView implements View {

	private static final Log logger = LogFactory.getLog(JsfView.class);

	public static final String EVENT_KEY = "org.springframework.webflow.FacesEvent";

	private UIViewRoot viewRoot;

	private final Lifecycle facesLifecycle;

	private final RequestContext requestContext;

	private String viewId;

	/**
	 * Creates a new JSF view.
	 * @param viewRoot the view root
	 * @param facesLifecycle the flow faces lifecycle
	 * @param context the current flow request
	 */
	public JsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle, RequestContext context) {
		this.viewRoot = viewRoot;
		this.viewId = viewRoot.getViewId();
		this.facesLifecycle = facesLifecycle;
		this.requestContext = context;
	}

	/**
	 * Returns the underlying view root.
	 * @return the view root
	 */
	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	public void setViewRoot(UIViewRoot viewRoot) {
		this.viewRoot = viewRoot;
	}

	/**
	 * Performs the standard duties of the JSF RENDER_RESPONSE phase.
	 */
	public void render() throws IOException {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		if (facesContext.getResponseComplete()) {
			return;
		}
		facesContext.setViewRoot(this.viewRoot);
		try {
			logger.debug("Asking faces lifecycle to render");
			this.facesLifecycle.render(facesContext);
		} finally {
			logger.debug("View rendering complete");
			facesContext.responseComplete();
		}
	}

	public boolean userEventQueued() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		if (facesContext != null) {
			// Use ExternalContext for multipart request parsing by component libraries
			return facesContext.getExternalContext().getRequestParameterMap().containsKey("javax.faces.ViewState");
		}
		return false;
	}

	/**
	 * Executes postback-processing portions of the standard JSF lifecycle including APPLY_REQUEST_VALUES through
	 * INVOKE_APPLICATION.
	 */
	public void processUserEvent() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();

		// Ensure serialized view state is always updated even if JSF didn't call StateManager.writeState(). SWF-1577
		saveState();

		// Must respect these flags in case user set them during RESTORE_VIEW phase
		if (!facesContext.getRenderResponse() && !facesContext.getResponseComplete()) {
			this.facesLifecycle.execute(facesContext);
		}
	}

	/**
	 * Updates the component state stored in View scope so that it remains in sync with the updated flow execution
	 * snapshot
	 */
	public void saveState() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		facesContext.setViewRoot(this.viewRoot);
		facesContext.getApplication().getStateManager().saveView(facesContext);
	}

	public Serializable getUserEventState() {
		// Set the temporary UIViewRoot state so that it will be available across the redirect (see comments in render()
		// method)
		return new ViewRootHolder(getViewRoot());
	}

	public boolean hasFlowEvent() {
		return this.requestContext.getExternalContext().getRequestMap().contains(EVENT_KEY);
	}

	public Event getFlowEvent() {
		return new Event(this, getEventId());
	}

	private String getEventId() {
		return (String) this.requestContext.getExternalContext().getRequestMap().get(EVENT_KEY);
	}

	public String toString() {
		return "[JSFView = '" + this.viewId + "']";
	}
}
