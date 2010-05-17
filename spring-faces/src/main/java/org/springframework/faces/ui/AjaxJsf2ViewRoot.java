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
package org.springframework.faces.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PhaseId;
import javax.faces.event.SystemEventListener;

import org.springframework.faces.webflow.JsfVersion;

/**
 * <p>
 * A subclass of AjaxViewRoot for use with JSF 2.0.
 * </p>
 * 
 * <p>
 * Note that these methods should ideally be in DelegatingViewRoot.java. However, due to some introspection done by the
 * JSF runtime that would make it impossible to use in JSF 1.2 because some of the delegate methods contain JSF 2.0
 * specific types and cause ClassNotFoundExceptions.
 * </p>
 * 
 * @author Phil Webb
 */
public class AjaxJsf2ViewRoot extends AjaxViewRoot {

	public AjaxJsf2ViewRoot(UIViewRoot original) {
		super(original);
		if (JsfVersion.isAtLeastJsf20()) {
			setId(createUniqueId());
		}
	}

	public void addClientBehavior(String eventName, ClientBehavior behavior) {
		getOriginalViewRoot().addClientBehavior(eventName, behavior);
	}

	public void addComponentResource(FacesContext context, UIComponent componentResource) {
		getOriginalViewRoot().addComponentResource(context, componentResource);
	}

	public void addComponentResource(FacesContext context, UIComponent componentResource, String target) {
		getOriginalViewRoot().addComponentResource(context, componentResource, target);
	}

	public void broadcastEvents(FacesContext context, PhaseId phaseId) {
		getOriginalViewRoot().broadcastEvents(context, phaseId);
	}

	public void clearInitialState() {
		getOriginalViewRoot().clearInitialState();
	}

	public String createUniqueId(FacesContext context, String seed) {
		return getOriginalViewRoot().createUniqueId(context, seed);
	}

	public Map getClientBehaviors() {
		return getOriginalViewRoot().getClientBehaviors();
	}

	public String getClientId() {
		return getOriginalViewRoot().getClientId();
	}

	public List getComponentResources(FacesContext context, String target) {
		return getOriginalViewRoot().getComponentResources(context, target);
	}

	public String getDefaultEventName() {
		return getOriginalViewRoot().getDefaultEventName();
	}

	public Collection getEventNames() {
		return getOriginalViewRoot().getEventNames();
	}

	public List getListenersForEventClass(Class eventClass) {
		return getOriginalViewRoot().getListenersForEventClass(eventClass);
	}

	public UIComponent getNamingContainer() {
		return getOriginalViewRoot().getNamingContainer();
	}

	public List getPhaseListeners() {
		return getOriginalViewRoot().getPhaseListeners();
	}

	public Map getResourceBundleMap() {
		return getOriginalViewRoot().getResourceBundleMap();
	}

	public List getViewListenersForEventClass(Class systemEvent) {
		return getOriginalViewRoot().getViewListenersForEventClass(systemEvent);
	}

	public Map getViewMap() {
		return getOriginalViewRoot().getViewMap();
	}

	public Map getViewMap(boolean create) {
		return getOriginalViewRoot().getViewMap(create);
	}

	public boolean initialStateMarked() {
		return getOriginalViewRoot().initialStateMarked();
	}

	public void markInitialState() {
		getOriginalViewRoot().markInitialState();
	}

	public boolean isInView() {
		return getOriginalViewRoot().isInView();
	}

	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
		getOriginalViewRoot().processEvent(event);
	}

	public void removeComponentResource(FacesContext context, UIComponent componentResource) {
		getOriginalViewRoot().removeComponentResource(context, componentResource);
	}

	public void removeComponentResource(FacesContext context, UIComponent componentResource, String target) {
		getOriginalViewRoot().removeComponentResource(context, componentResource, target);
	}

	public void setInView(boolean isInView) {
		getOriginalViewRoot().setInView(isInView);
	}

	public void subscribeToEvent(Class eventClass, ComponentSystemEventListener componentListener) {
		getOriginalViewRoot().subscribeToEvent(eventClass, componentListener);
	}

	public void subscribeToViewEvent(Class systemEvent, SystemEventListener listener) {
		getOriginalViewRoot().subscribeToViewEvent(systemEvent, listener);
	}

	public void unsubscribeFromEvent(Class eventClass, ComponentSystemEventListener componentListener) {
		getOriginalViewRoot().unsubscribeFromEvent(eventClass, componentListener);
	}

	public void unsubscribeFromViewEvent(Class systemEvent, SystemEventListener listener) {
		getOriginalViewRoot().unsubscribeFromViewEvent(systemEvent, listener);
	}

	public boolean visitTree(VisitContext context, VisitCallback callback) {
		return getOriginalViewRoot().visitTree(context, callback);
	}

}
