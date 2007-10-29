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
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * JSF-specific {@link View} implementation.
 * 
 * @author Jeremy Grelle
 */
public class JsfView implements View {

	public static final String EVENT_KEY = "org.springframework.webflow.FacesEvent";

	public static final String STATE_KEY = "org.springframework.webflow.FacesState";

	/**
	 * The root of the JSF component tree managed by this view
	 */
	private UIViewRoot viewRoot;

	private Event event;

	private Lifecycle facesLifecycle;

	public JsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle) {

		Assert.notNull(viewRoot);
		Assert.notNull(facesLifecycle);

		this.viewRoot = viewRoot;
		this.facesLifecycle = facesLifecycle;
	}

	public boolean eventSignaled() {
		return getEvent() != null;
	}

	public Event getEvent() {
		if (event == null) {

			String jsfEvent = (String) RequestContextHolder.getRequestContext().getExternalContext().getRequestMap()
					.get(EVENT_KEY);
			if (StringUtils.hasText(jsfEvent)) {
				event = new Event(this, jsfEvent);
			}
		}
		return event;
	}

	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	/**
	 * This implementation performs the standard duties of the JSF RENDER_RESPONSE phase.
	 */
	public void render() {
		FacesContext facesContext = JsfFlowUtils.getFacesContext(facesLifecycle);
		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
		try {
			JsfFlowUtils.notifyBeforeListeners(PhaseId.RENDER_RESPONSE, facesLifecycle);
			facesContext.getApplication().getViewHandler().renderView(facesContext, viewRoot);
			JsfFlowUtils.notifyAfterListeners(PhaseId.RENDER_RESPONSE, facesLifecycle);
		} catch (IOException e) {
			throw new FacesException("An I/O error occurred during view rendering", e);
		} finally {
			facesContext.responseComplete();
			facesContext.release();
		}
	}

}
