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

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.context.MessageSource;
import org.springframework.faces.webflow.context.portlet.PortletFacesContextImpl;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Custom {@link FacesContext} implementation that delegates all standard FacesContext messaging functionality to a
 * Spring {@link MessageSource} made accessible as part of the current Web Flow request. Additionally, it manages the
 * {@code renderResponse} flag in flash scope so that the execution of the JSF {@link Lifecycle} may span multiple
 * requests in the case of the POST+REDIRECT+GET pattern being enabled.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class FlowFacesContext extends FacesContext {

	/**
	 * The key for storing the renderResponse flag
	 */
	static final String RENDER_RESPONSE_KEY = "flowRenderResponse";

	/**
	 * The key for storing the renderResponse flag
	 */
	private RequestContext context;

	private FlowFacesContextMessageDelegate messageDelegate;

	private ExternalContext externalContext;

	/**
	 * The base FacesContext delegate
	 */
	private FacesContext delegate;

	public static FlowFacesContext newInstance(RequestContext context, Lifecycle lifecycle) {
		FacesContext defaultFacesContext = null;
		if (JsfRuntimeInformation.isPortletRequest(context)) {
			defaultFacesContext = new PortletFacesContextImpl((PortletContext) context.getExternalContext()
					.getNativeContext(), (PortletRequest) context.getExternalContext().getNativeRequest(),
					(PortletResponse) context.getExternalContext().getNativeResponse());
		} else {
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			defaultFacesContext = facesContextFactory.getFacesContext(context.getExternalContext().getNativeContext(),
					context.getExternalContext().getNativeRequest(), context.getExternalContext().getNativeResponse(),
					lifecycle);
		}
		return (JsfRuntimeInformation.isAtLeastJsf20()) ? new Jsf2FlowFacesContext(context, defaultFacesContext)
				: new FlowFacesContext(context, defaultFacesContext);
	}

	public FlowFacesContext(RequestContext context, FacesContext delegate) {
		this.context = context;
		this.delegate = delegate;
		this.messageDelegate = new FlowFacesContextMessageDelegate(context);
		this.externalContext = new FlowExternalContext(delegate.getExternalContext());
		setCurrentInstance(this);
	}

	/**
	 * Translates a FacesMessage to a Spring Web Flow message and adds it to the current MessageContext
	 */
	public void addMessage(String clientId, FacesMessage message) {
		messageDelegate.addToFlowMessageContext(clientId, message);
	}

	/**
	 * Returns an Iterator for all component clientId's for which messages have been added.
	 */
	public Iterator<String> getClientIdsWithMessages() {
		return messageDelegate.getClientIdsWithMessages();
	}

	public ELContext getELContext() {
		Method delegateMethod = ClassUtils.getMethodIfAvailable(delegate.getClass(), "getELContext");
		if (delegateMethod != null) {
			try {
				ELContext context = (ELContext) delegateMethod.invoke(delegate);
				context.putContext(FacesContext.class, this);
				return context;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Return the maximum severity level recorded on any FacesMessages that has been queued, whether or not they are
	 * associated with any specific UIComponent. If no such messages have been queued, return null.
	 */
	public FacesMessage.Severity getMaximumSeverity() {
		return messageDelegate.getMaximumSeverity();
	}

	/**
	 * Returns an Iterator for all Messages in the current MessageContext that does translation to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages() {
		return messageDelegate.getMessages();
	}

	/**
	 * Returns an Iterator for all Messages with the given clientId in the current MessageContext that does translation
	 * to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages(String clientId) {
		return messageDelegate.getMessages(clientId);
	}

	public boolean getRenderResponse() {
		Boolean renderResponse = context.getFlashScope().getBoolean(RENDER_RESPONSE_KEY);
		if (renderResponse == null) {
			return false;
		}
		return renderResponse.booleanValue();
	}

	public boolean getResponseComplete() {
		return context.getExternalContext().isResponseComplete();
	}

	public void renderResponse() {
		// stored in flash scope to survive a redirect when transitioning from one view to another
		context.getFlashScope().put(RENDER_RESPONSE_KEY, Boolean.TRUE);
	}

	public void responseComplete() {
		context.getExternalContext().recordResponseComplete();
	}

	// ------------------ Pass-through delegate methods ----------------------//

	public Application getApplication() {
		return delegate.getApplication();
	}

	public ExternalContext getExternalContext() {
		return externalContext;
	}

	public RenderKit getRenderKit() {
		return delegate.getRenderKit();
	}

	public ResponseStream getResponseStream() {
		return delegate.getResponseStream();
	}

	public ResponseWriter getResponseWriter() {
		return delegate.getResponseWriter();
	}

	public UIViewRoot getViewRoot() {
		return delegate.getViewRoot();
	}

	public void release() {
		delegate.release();
		setCurrentInstance(null);
	}

	public void setResponseStream(ResponseStream responseStream) {
		delegate.setResponseStream(responseStream);
	}

	public void setResponseWriter(ResponseWriter responseWriter) {
		delegate.setResponseWriter(responseWriter);
	}

	public void setViewRoot(UIViewRoot root) {
		delegate.setViewRoot(root);
	}

	protected FacesContext getDelegate() {
		return delegate;
	}

	protected FlowFacesContextMessageDelegate getMessageDelegate() {
		return messageDelegate;
	}

	protected class FlowExternalContext extends ExternalContextWrapper {

		private static final String CUSTOM_RESPONSE = "customResponse";

		public FlowExternalContext(ExternalContext delegate) {
			super(delegate);
		}

		public Object getResponse() {
			if (context.getRequestScope().contains(CUSTOM_RESPONSE)) {
				return context.getRequestScope().get(CUSTOM_RESPONSE);
			}
			return delegate.getResponse();
		}

		/**
		 * Store the native response object to be used for the duration of the Faces Request
		 */
		public void setResponse(Object response) {
			context.getRequestScope().put(CUSTOM_RESPONSE, response);
			delegate.setResponse(response);
		}

	}
}
