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

import static org.springframework.faces.webflow.JsfRuntimeInformation.isAtLeastJsf20;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import org.springframework.util.Assert;

/**
 * Wraps an {@link Application} instance in order to ensure Web Flow specific implementations of {@link ViewHandler} and
 * {@link StateManager} are inserted at the front of the processing chain in JSF 1.2 and JSF 2.0 environments. This is
 * done by intercepting the corresponding setters. All other methods are simple delegation methods.
 * 
 * @author Rossen Stoyanchev
 * 
 * @see Jsf2FlowApplication
 */
public class FlowApplication extends Application {

	private Application delegate;

	/**
	 * Class constructor that accepts a delegate Application instance. If the delegate has default instantiation logic
	 * for its StateManager and ViewHandler instances, those will be wrapped with {@link FlowViewStateManager} and a
	 * {@link FlowViewHandler} instance.
	 * 
	 * @param delegate the Application instance to delegate to.
	 */
	public FlowApplication(Application delegate) {
		Assert.notNull(delegate, "The delegate Application instance must not be null!");
		this.delegate = delegate;

		ViewHandler handler = this.delegate.getViewHandler();
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		}

		StateManager manager = this.delegate.getStateManager();
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		}
	}

	/**
	 * @return the wrapped Application instance
	 */
	public Application getDelegate() {
		return delegate;
	}

	public ELContextListener[] getELContextListeners() {
		return getDelegate().getELContextListeners();
	}

	/**
	 * Inserts {@link FlowViewStateManager} in front of the given StateManager (if not already done).
	 */
	public void setStateManager(StateManager manager) {
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		} else {
			delegate.setStateManager(manager);
		}
	}

	/**
	 * Inserts a {@link FlowViewHandler} in front of the given ViewHandler (if not already done).
	 */
	public void setViewHandler(ViewHandler handler) {
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		} else {
			delegate.setViewHandler(handler);
		}
	}

	// ------------------- JSF 1.2 pass-through delegate methods ------------------//

	public void addComponent(String componentType, String componentClass) {
		delegate.addComponent(componentType, componentClass);
	}

	public void addConverter(String converterId, String converterClass) {
		delegate.addConverter(converterId, converterClass);
	}

	public void addConverter(Class<?> targetClass, String converterClass) {
		delegate.addConverter(targetClass, converterClass);
	}

	public void addELContextListener(ELContextListener listener) {
		getDelegate().addELContextListener(listener);
	}

	public void addELResolver(ELResolver resolver) {
		getDelegate().addELResolver(resolver);
	}

	public void addValidator(String validatorId, String validatorClass) {
		delegate.addValidator(validatorId, validatorClass);
	}

	public UIComponent createComponent(String componentType) throws FacesException {
		return delegate.createComponent(componentType);
	}

	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
			throws FacesException {
		return delegate.createComponent(componentBinding, context, componentType);
	}

	public UIComponent createComponent(ValueExpression componentExpression, FacesContext context, String componentType)
			throws FacesException {
		return getDelegate().createComponent(componentExpression, context, componentType);
	}

	public Converter createConverter(String converterId) {
		return delegate.createConverter(converterId);
	}

	public Converter createConverter(Class<?> targetClass) {
		return delegate.createConverter(targetClass);
	}

	public MethodBinding createMethodBinding(String ref, Class<?>... params) throws ReferenceSyntaxException {
		return delegate.createMethodBinding(ref, params);
	}

	public Validator createValidator(String validatorId) throws FacesException {
		return delegate.createValidator(validatorId);
	}

	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return delegate.createValueBinding(ref);
	}

	public <T> T evaluateExpressionGet(FacesContext context, String expression, Class<? extends T> expectedType)
			throws ELException {
		return getDelegate().evaluateExpressionGet(context, expression, expectedType);
	}

	public ActionListener getActionListener() {
		return delegate.getActionListener();
	}

	public Iterator<String> getComponentTypes() {
		return delegate.getComponentTypes();
	}

	public Iterator<String> getConverterIds() {
		return delegate.getConverterIds();
	}

	public Iterator<Class<?>> getConverterTypes() {
		return delegate.getConverterTypes();
	}

	public Locale getDefaultLocale() {
		return delegate.getDefaultLocale();
	}

	public String getDefaultRenderKitId() {
		return delegate.getDefaultRenderKitId();
	}

	public ELResolver getELResolver() {
		return getDelegate().getELResolver();
	}

	public ExpressionFactory getExpressionFactory() {
		return getDelegate().getExpressionFactory();
	}

	public String getMessageBundle() {
		return delegate.getMessageBundle();
	}

	public NavigationHandler getNavigationHandler() {
		return delegate.getNavigationHandler();
	}

	public PropertyResolver getPropertyResolver() {
		return delegate.getPropertyResolver();
	}

	public ResourceBundle getResourceBundle(FacesContext ctx, String name) {
		return getDelegate().getResourceBundle(ctx, name);
	}

	public StateManager getStateManager() {
		return delegate.getStateManager();
	}

	public Iterator<Locale> getSupportedLocales() {
		return delegate.getSupportedLocales();
	}

	public Iterator<String> getValidatorIds() {
		return delegate.getValidatorIds();
	}

	public VariableResolver getVariableResolver() {
		return delegate.getVariableResolver();
	}

	public ViewHandler getViewHandler() {
		return delegate.getViewHandler();
	}

	public void removeELContextListener(ELContextListener listener) {
		getDelegate().removeELContextListener(listener);
	}

	public void setActionListener(ActionListener listener) {
		delegate.setActionListener(listener);
	}

	public void setDefaultLocale(Locale locale) {
		delegate.setDefaultLocale(locale);
	}

	public void setDefaultRenderKitId(String renderKitId) {
		delegate.setDefaultRenderKitId(renderKitId);
	}

	public void setMessageBundle(String bundle) {
		delegate.setMessageBundle(bundle);
	}

	public void setNavigationHandler(NavigationHandler handler) {
		delegate.setNavigationHandler(handler);
	}

	public void setPropertyResolver(PropertyResolver resolver) {
		delegate.setPropertyResolver(resolver);
	}

	public void setSupportedLocales(Collection<Locale> locales) {
		delegate.setSupportedLocales(locales);
	}

	public void setVariableResolver(VariableResolver resolver) {
		delegate.setVariableResolver(resolver);
	}

	// ------------------- Private helper methods ------------------//

	private boolean shouldWrap(ViewHandler delegateViewHandler) {
		return (delegateViewHandler != null) && (!(delegateViewHandler instanceof FlowViewHandler));
	}

	private boolean wrapAndSetViewHandler(ViewHandler target) {
		if ((target != null) && (!(target instanceof FlowViewHandler))) {
			ViewHandler handler = (isAtLeastJsf20()) ? new Jsf2FlowViewHandler(target) : new FlowViewHandler(target);
			delegate.setViewHandler(handler);
			return true;
		}
		return false;
	}

	private boolean shouldWrap(StateManager manager) {
		return (manager != null) && (!(manager instanceof FlowViewStateManager));
	}

	private void wrapAndSetStateManager(StateManager target) {
		delegate.setStateManager(new FlowViewStateManager(target));
	}

}
