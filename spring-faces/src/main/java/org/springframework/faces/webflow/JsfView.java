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
package org.springframework.faces.webflow;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseId;
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
 */
public class JsfView implements View {

	private static final Log logger = LogFactory.getLog(JsfView.class);

	public static final String EVENT_KEY = "org.springframework.webflow.FacesEvent";

	/**
	 * The root of the JSF component tree managed by this view
	 */
	private UIViewRoot viewRoot;

	private Lifecycle facesLifecycle;

	private RequestContext context;

	public JsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle, RequestContext context) {
		this.viewRoot = viewRoot;
		this.facesLifecycle = facesLifecycle;
		this.context = context;
	}

	public boolean eventSignaled() {
		return context.getExternalContext().getRequestMap().contains(EVENT_KEY);
	}

	public Event getEvent() {
		String eventId = (String) context.getExternalContext().getRequestMap().get(EVENT_KEY);
		return new Event(this, eventId);
	}

	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	/**
	 * This implementation performs the standard duties of the JSF RENDER_RESPONSE phase.
	 */
	public void render() {
		FacesContext facesContext = createFlowFacesContext();
		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
		try {
			JsfUtils.notifyBeforeListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
			logger.debug("Asking view handler to render view");
			facesContext.getApplication().getViewHandler().renderView(facesContext, viewRoot);
			JsfUtils.notifyAfterListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
		} catch (IOException e) {
			throw new FacesException("An I/O error occurred during view rendering", e);
		} finally {
			logger.debug("View rendering complete");
			facesContext.responseComplete();
			facesContext.release();
		}
	}

	private FacesContext createFlowFacesContext() {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context.getExternalContext()
				.getNativeContext(), context.getExternalContext().getNativeRequest(), context.getExternalContext()
				.getNativeResponse(), facesLifecycle);
		return new FlowFacesContext(context, defaultFacesContext);
	}

	public String toString() {
		return "[JSFView = '" + viewRoot.getViewId() + "']";
	}
}