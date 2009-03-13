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
package org.springframework.faces.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseListener;

/**
 * Base class for wrapping an existing UIViewRoot to customize its behavior.
 * 
 * @author Jeremy Grelle
 */
public abstract class DelegatingViewRoot extends UIViewRoot {

	private UIViewRoot original;

	public DelegatingViewRoot(UIViewRoot original) {
		this.original = original;
	}

	protected UIViewRoot getOriginalViewRoot() {
		return original;
	}

	/**
	 * @param phaseListener
	 * @see javax.faces.component.UIViewRoot#addPhaseListener(javax.faces.event.PhaseListener)
	 */
	public void addPhaseListener(PhaseListener phaseListener) {
		original.addPhaseListener(phaseListener);
	}

	/**
	 * @param event
	 * @throws AbortProcessingException
	 * @see javax.faces.component.UIComponentBase#broadcast(javax.faces.event.FacesEvent)
	 */
	public void broadcast(FacesEvent event) throws AbortProcessingException {
		original.broadcast(event);
	}

	/**
	 * @see javax.faces.component.UIViewRoot#createUniqueId()
	 */
	public String createUniqueId() {
		return original.createUniqueId();
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
	 */
	public void decode(FacesContext context) {
		original.decode(context);
	}

	/**
	 * @param context
	 * @throws IOException
	 * @see javax.faces.component.UIComponent#encodeAll(javax.faces.context.FacesContext)
	 */
	public void encodeAll(FacesContext context) throws IOException {
		original.encodeAll(context);
	}

	/**
	 * @param context
	 * @throws IOException
	 * @see javax.faces.component.UIViewRoot#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		original.encodeBegin(context);
	}

	/**
	 * @param context
	 * @throws IOException
	 * @see javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		original.encodeChildren(context);
	}

	/**
	 * @param context
	 * @throws IOException
	 * @see javax.faces.component.UIViewRoot#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		original.encodeEnd(context);
	}

	/**
	 * @param expr
	 * @see javax.faces.component.UIComponentBase#findComponent(java.lang.String)
	 */
	public UIComponent findComponent(String expr) {
		return original.findComponent(expr);
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getAfterPhaseListener()
	 */
	public MethodExpression getAfterPhaseListener() {
		return original.getAfterPhaseListener();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getAttributes()
	 */
	public Map getAttributes() {
		return original.getAttributes();
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getBeforePhaseListener()
	 */
	public MethodExpression getBeforePhaseListener() {
		return original.getBeforePhaseListener();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getChildCount()
	 */
	public int getChildCount() {
		return original.getChildCount();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getChildren()
	 */
	public List getChildren() {
		return original.getChildren();
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIComponentBase#getClientId(javax.faces.context.FacesContext)
	 */
	public String getClientId(FacesContext context) {
		return original.getClientId(context);
	}

	/**
	 * @param ctx
	 * @see javax.faces.component.UIComponent#getContainerClientId(javax.faces.context.FacesContext)
	 */
	public String getContainerClientId(FacesContext ctx) {
		return original.getContainerClientId(ctx);
	}

	/**
	 * @param name
	 * @see javax.faces.component.UIComponentBase#getFacet(java.lang.String)
	 */
	public UIComponent getFacet(String name) {
		return original.getFacet(name);
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getFacetCount()
	 */
	public int getFacetCount() {
		return original.getFacetCount();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getFacets()
	 */
	public Map getFacets() {
		return original.getFacets();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getFacetsAndChildren()
	 */
	public Iterator getFacetsAndChildren() {
		return original.getFacetsAndChildren();
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getFamily()
	 */
	public String getFamily() {
		return original.getFamily();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getId()
	 */
	public String getId() {
		return original.getId();
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getLocale()
	 */
	public Locale getLocale() {
		return original.getLocale();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getParent()
	 */
	public UIComponent getParent() {
		return original.getParent();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getRendererType()
	 */
	public String getRendererType() {
		return original.getRendererType();
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getRenderKitId()
	 */
	public String getRenderKitId() {
		return original.getRenderKitId();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#getRendersChildren()
	 */
	public boolean getRendersChildren() {
		return original.getRendersChildren();
	}

	/**
	 * @param name
	 * @deprecated
	 * @see javax.faces.component.UIComponentBase#getValueBinding(java.lang.String)
	 */
	public ValueBinding getValueBinding(String name) {
		return original.getValueBinding(name);
	}

	/**
	 * @param name
	 * @see javax.faces.component.UIComponent#getValueExpression(java.lang.String)
	 */
	public ValueExpression getValueExpression(String name) {
		return original.getValueExpression(name);
	}

	/**
	 * @see javax.faces.component.UIViewRoot#getViewId()
	 */
	public String getViewId() {
		return original.getViewId();
	}

	/**
	 * @param context
	 * @param clientId
	 * @param callback
	 * @throws FacesException
	 * @see javax.faces.component.UIComponentBase#invokeOnComponent(javax.faces.context.FacesContext, java.lang.String,
	 * javax.faces.component.ContextCallback)
	 */
	public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback)
			throws FacesException {
		return original.invokeOnComponent(context, clientId, callback);
	}

	/**
	 * @see javax.faces.component.UIComponentBase#isRendered()
	 */
	public boolean isRendered() {
		return original.isRendered();
	}

	/**
	 * @see javax.faces.component.UIComponentBase#isTransient()
	 */
	public boolean isTransient() {
		return original.isTransient();
	}

	/**
	 * @see javax.faces.component.UIViewRoot#processApplication(javax.faces.context.FacesContext)
	 */
	public void processApplication(FacesContext context) {
		original.processApplication(context);
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIViewRoot#processDecodes(javax.faces.context.FacesContext)
	 */
	public void processDecodes(FacesContext context) {
		original.processDecodes(context);
	}

	/**
	 * @param context
	 * @param state
	 * @see javax.faces.component.UIComponentBase#processRestoreState(javax.faces.context.FacesContext,
	 * java.lang.Object)
	 */
	public void processRestoreState(FacesContext context, Object state) {
		original.processRestoreState(context, state);
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIComponentBase#processSaveState(javax.faces.context.FacesContext)
	 */
	public Object processSaveState(FacesContext context) {
		return original.processSaveState(context);
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIViewRoot#processUpdates(javax.faces.context.FacesContext)
	 */
	public void processUpdates(FacesContext context) {
		original.processUpdates(context);
	}

	/**
	 * @param context
	 * @see javax.faces.component.UIViewRoot#processValidators(javax.faces.context.FacesContext)
	 */
	public void processValidators(FacesContext context) {
		original.processValidators(context);
	}

	/**
	 * @param event
	 * @see javax.faces.component.UIViewRoot#queueEvent(javax.faces.event.FacesEvent)
	 */
	public void queueEvent(FacesEvent event) {
		original.queueEvent(event);
	}

	/**
	 * @param phaseListener
	 * @see javax.faces.component.UIViewRoot#removePhaseListener(javax.faces.event.PhaseListener)
	 */
	public void removePhaseListener(PhaseListener phaseListener) {
		original.removePhaseListener(phaseListener);
	}

	/**
	 * @param facesContext
	 * @param state
	 * @see javax.faces.component.UIViewRoot#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext facesContext, Object state) {
		original.restoreState(facesContext, state);
	}

	/**
	 * @param facesContext
	 * @see javax.faces.component.UIViewRoot#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext facesContext) {
		return original.saveState(facesContext);
	}

	/**
	 * @param afterPhaseListener
	 * @see javax.faces.component.UIViewRoot#setAfterPhaseListener(javax.el.MethodExpression)
	 */
	public void setAfterPhaseListener(MethodExpression afterPhaseListener) {
		original.setAfterPhaseListener(afterPhaseListener);
	}

	/**
	 * @param beforePhaseListener
	 * @see javax.faces.component.UIViewRoot#setBeforePhaseListener(javax.el.MethodExpression)
	 */
	public void setBeforePhaseListener(MethodExpression beforePhaseListener) {
		original.setBeforePhaseListener(beforePhaseListener);
	}

	/**
	 * @param id
	 * @see javax.faces.component.UIComponentBase#setId(java.lang.String)
	 */
	public void setId(String id) {
		original.setId(id);
	}

	/**
	 * @param locale
	 * @see javax.faces.component.UIViewRoot#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		original.setLocale(locale);
	}

	/**
	 * @param parent
	 * @see javax.faces.component.UIComponentBase#setParent(javax.faces.component.UIComponent)
	 */
	public void setParent(UIComponent parent) {
		original.setParent(parent);
	}

	/**
	 * @param rendered
	 * @see javax.faces.component.UIComponentBase#setRendered(boolean)
	 */
	public void setRendered(boolean rendered) {
		original.setRendered(rendered);
	}

	/**
	 * @param rendererType
	 * @see javax.faces.component.UIComponentBase#setRendererType(java.lang.String)
	 */
	public void setRendererType(String rendererType) {
		if (original != null) {
			original.setRendererType(rendererType);
		}
	}

	/**
	 * @param renderKitId
	 * @see javax.faces.component.UIViewRoot#setRenderKitId(java.lang.String)
	 */
	public void setRenderKitId(String renderKitId) {
		original.setRenderKitId(renderKitId);
	}

	/**
	 * @param transientFlag
	 * @see javax.faces.component.UIComponentBase#setTransient(boolean)
	 */
	public void setTransient(boolean transientFlag) {
		original.setTransient(transientFlag);
	}

	/**
	 * @param name
	 * @param binding
	 * @deprecated
	 * @see javax.faces.component.UIComponentBase#setValueBinding(java.lang.String, javax.faces.el.ValueBinding)
	 */
	public void setValueBinding(String name, ValueBinding binding) {
		original.setValueBinding(name, binding);
	}

	/**
	 * @param name
	 * @param expression
	 * @see javax.faces.component.UIComponent#setValueExpression(java.lang.String, javax.el.ValueExpression)
	 */
	public void setValueExpression(String name, ValueExpression expression) {
		original.setValueExpression(name, expression);
	}

	/**
	 * @param viewId
	 * @see javax.faces.component.UIViewRoot#setViewId(java.lang.String)
	 */
	public void setViewId(String viewId) {
		original.setViewId(viewId);
	}

}
