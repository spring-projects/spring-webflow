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
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.context.FacesContext;
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

	public void addELContextListener(ELContextListener listener) {
		getDelegate().addELContextListener(listener);
	}

	public void addELResolver(ELResolver resolver) {
		getDelegate().addELResolver(resolver);
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

	public UIComponent createComponent(ValueExpression componentExpression, FacesContext context, String componentType)
			throws FacesException {
		return getDelegate().createComponent(componentExpression, context, componentType);
	}

	public <T> T evaluateExpressionGet(FacesContext context, String expression, Class<? extends T> expectedType)
			throws ELException {
		return getDelegate().evaluateExpressionGet(context, expression, expectedType);
	}

	public Iterator<String> getBehaviorIds() {
		return getDelegate().getBehaviorIds();
	}

	public Map<String, String> getDefaultValidatorInfo() {
		return getDelegate().getDefaultValidatorInfo();
	}

	public ELContextListener[] getELContextListeners() {
		return getDelegate().getELContextListeners();
	}

	public ELResolver getELResolver() {
		return getDelegate().getELResolver();
	}

	public ExpressionFactory getExpressionFactory() {
		return getDelegate().getExpressionFactory();
	}

	public ProjectStage getProjectStage() {
		return getDelegate().getProjectStage();
	}

	public ResourceBundle getResourceBundle(FacesContext ctx, String name) {
		return getDelegate().getResourceBundle(ctx, name);
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

	public void removeELContextListener(ELContextListener listener) {
		getDelegate().removeELContextListener(listener);
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

}
