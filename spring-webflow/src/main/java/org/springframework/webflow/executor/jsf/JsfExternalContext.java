/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;

import org.springframework.binding.collection.SharedMapDecorator;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Provides contextual information about a JSF environment that has interacted
 * with SWF.
 * 
 * @author Keith Donald
 */
public class JsfExternalContext implements ExternalContext {

	/**
	 * The JSF Faces context.
	 */
	private FacesContext facesContext;

	/**
	 * The id of the action or "command button" that fired.
	 */
	private String actionId;

	/**
	 * The action outcome.
	 */
	private String outcome;

	/**
	 * Creates a JSF External Context.
	 * @param facesContext the JSF faces context
	 */
	public JsfExternalContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}

	/**
	 * Creates a JSF External Context.
	 * @param facesContext the JSF faces context.
	 * @param actionId the action that fired
	 * @param outcome the action outcome
	 */
	public JsfExternalContext(FacesContext facesContext, String actionId, String outcome) {
		this.facesContext = facesContext;
		this.actionId = actionId;
		this.outcome = outcome;
	}

	public String getContextPath() {
		return facesContext.getExternalContext().getRequestContextPath();
	}

	public String getDispatcherPath() {
		return facesContext.getExternalContext().getRequestServletPath();
	}

	public String getRequestPathInfo() {
		return facesContext.getExternalContext().getRequestPathInfo();
	}

	public ParameterMap getRequestParameterMap() {
		return new LocalParameterMap(facesContext.getExternalContext().getRequestParameterMap());
	}

	public MutableAttributeMap getRequestMap() {
		return new LocalAttributeMap(facesContext.getExternalContext().getRequestMap());
	}

	public SharedAttributeMap getSessionMap() {
		return new LocalSharedAttributeMap(new SessionSharedMap(facesContext));
	}

	public SharedAttributeMap getGlobalSessionMap() {
		return getSessionMap();
	}
	
	public SharedAttributeMap getApplicationMap() {
		return new LocalSharedAttributeMap(new ApplicationSharedMap(facesContext));
	}

	/**
	 * Returns the JSF FacesContext.
	 */
	public FacesContext getFacesContext() {
		return facesContext;
	}

	/**
	 * Returns the action identifier.
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * Returns the action outcome.
	 */
	public String getOutcome() {
		return outcome;
	}

	private static class SessionSharedMap extends SharedMapDecorator {

		private FacesContext facesContext;

		public SessionSharedMap(FacesContext facesContext) {
			super(facesContext.getExternalContext().getSessionMap());
			this.facesContext = facesContext;
		}

		public Object getMutex() {
			return facesContext.getExternalContext().getSession(false);
		}
	}

	private static class ApplicationSharedMap extends SharedMapDecorator {

		private FacesContext facesContext;

		public ApplicationSharedMap(FacesContext facesContext) {
			super(facesContext.getExternalContext().getApplicationMap());
			this.facesContext = facesContext;
		}

		public Object getMutex() {
			return facesContext.getExternalContext().getContext();
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("actionId", actionId).append("outcome", outcome).append("facesContext",
				facesContext).toString();
	}
}