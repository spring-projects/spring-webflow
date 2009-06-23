/*
 * Copyright 2004-2008 the original author or authors.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

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

public class MockApplication extends Application {
	private ViewHandler viewHandler;

	public ActionListener getActionListener() {
		return null;
	}

	public void setActionListener(ActionListener listener) {
	}

	public Locale getDefaultLocale() {
		return null;
	}

	public void setDefaultLocale(Locale locale) {
	}

	public String getDefaultRenderKitId() {
		return null;
	}

	public void setDefaultRenderKitId(String renderKitId) {
	}

	public String getMessageBundle() {
		return null;
	}

	public void setMessageBundle(String bundle) {
	}

	public NavigationHandler getNavigationHandler() {
		return null;
	}

	public void setNavigationHandler(NavigationHandler handler) {
	}

	public PropertyResolver getPropertyResolver() {
		return null;
	}

	public void setPropertyResolver(PropertyResolver resolver) {
	}

	public VariableResolver getVariableResolver() {
		return null;
	}

	public void setVariableResolver(VariableResolver resolver) {
	}

	public ViewHandler getViewHandler() {
		return viewHandler;
	}

	public void setViewHandler(ViewHandler handler) {
		viewHandler = handler;
	}

	public StateManager getStateManager() {
		return null;
	}

	public void setStateManager(StateManager manager) {
	}

	public void addComponent(String componentType, String componentClass) {
	}

	public UIComponent createComponent(String componentType) throws FacesException {
		return null;
	}

	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
			throws FacesException {
		return null;
	}

	public Iterator getComponentTypes() {
		return null;
	}

	public void addConverter(String converterId, String converterClass) {
	}

	public void addConverter(Class targetClass, String converterClass) {
	}

	public Converter createConverter(String converterId) {
		return null;
	}

	public Converter createConverter(Class targetClass) {
		return null;
	}

	public Iterator getConverterIds() {
		return null;
	}

	public Iterator getConverterTypes() {
		return null;
	}

	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return null;
	}

	public Iterator getSupportedLocales() {
		return null;
	}

	public void setSupportedLocales(Collection locales) {
	}

	public void addValidator(String validatorId, String validatorClass) {
	}

	public Validator createValidator(String validatorId) throws FacesException {
		return null;
	}

	public Iterator getValidatorIds() {
		return null;
	}

	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return null;
	}
}