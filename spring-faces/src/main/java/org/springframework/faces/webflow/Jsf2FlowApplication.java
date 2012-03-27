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

import java.util.Iterator;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * Extends FlowApplication in order to provide JSF 2 delegation method. This is necessary because some of the methods
 * use JSF 2 specific types as input or output parameters.
 * 
 * @author Rossen Stoyanchev
 */
public class Jsf2FlowApplication extends FlowApplication {

	public Jsf2FlowApplication(Application delegate) {
		super(delegate);
	}

	// ------------------- JSF 2 pass-through delegate methods ------------------//

	public void addBehavior(String behaviorId, String behaviorClass) {
		getDelegate().addBehavior(behaviorId, behaviorClass);
	}

	public void addDefaultValidatorId(String validatorId) {
		getDelegate().addDefaultValidatorId(validatorId);
	}

	public Behavior createBehavior(String behaviorId) throws FacesException {
		return getDelegate().createBehavior(behaviorId);
	}

	public UIComponent createComponent(FacesContext context, Resource componentResource) {
		return getDelegate().createComponent(context, componentResource);
	}

	public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
		return getDelegate().createComponent(context, componentType, rendererType);
	}

	public UIComponent createComponent(ValueExpression componentExpression, FacesContext context, String componentType,
			String rendererType) {
		return getDelegate().createComponent(componentExpression, context, componentType, rendererType);
	}

	public Iterator<String> getBehaviorIds() {
		return getDelegate().getBehaviorIds();
	}

	public Map<String, String> getDefaultValidatorInfo() {
		return getDelegate().getDefaultValidatorInfo();
	}

	public ProjectStage getProjectStage() {
		return getDelegate().getProjectStage();
	}

	public ResourceHandler getResourceHandler() {
		return getDelegate().getResourceHandler();
	}

	public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass,
			Class<?> sourceBaseType, Object source) {
		getDelegate().publishEvent(context, systemEventClass, sourceBaseType, source);
	}

	public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass, Object source) {
		getDelegate().publishEvent(context, systemEventClass, source);
	}

	public void setResourceHandler(ResourceHandler resourceHandler) {
		getDelegate().setResourceHandler(resourceHandler);
	}

	public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass, Class<?> sourceClass,
			SystemEventListener listener) {
		getDelegate().subscribeToEvent(systemEventClass, sourceClass, listener);
	}

	public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass, SystemEventListener listener) {
		getDelegate().subscribeToEvent(systemEventClass, listener);
	}

	public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass, Class<?> sourceClass,
			SystemEventListener listener) {
		getDelegate().unsubscribeFromEvent(systemEventClass, sourceClass, listener);
	}

	public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass, SystemEventListener listener) {
		getDelegate().unsubscribeFromEvent(systemEventClass, listener);
	}

	// Ideally this method should be in JsfView
	// We keep it here to avoid ClassNotFoundExceptions for JSF 1.2 apps

	static void publishPostRestoreStateEvent() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		try {
			facesContext.getViewRoot().visitTree(VisitContext.createVisitContext(facesContext),
					new PostRestoreStateEventVisitCallback());
		} catch (AbortProcessingException e) {
			facesContext.getApplication().publishEvent(facesContext, ExceptionQueuedEvent.class,
					new ExceptionQueuedEventContext(facesContext, e, null, facesContext.getCurrentPhaseId()));
		}
	}
}
