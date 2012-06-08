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
package org.springframework.faces.webflow.context.portlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.faces.webflow.JsfUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * The default FacesContext implementation in Mojarra and in Apache MyFaces depends on the Servlet API. This
 * implementation provides an alternative that accepts Portlet request and response structures and creates a
 * {@link PortletExternalContextImpl} in its constructor. The rest of the method implementations mimic the equivalent
 * methods in the default FacesContext implementation.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 */
public class PortletFacesContextImpl extends FacesContext {

	private static final List<FacesMessage> NO_MESSAGES = Collections.unmodifiableList(Collections
			.<FacesMessage> emptyList());

	private static final Iterator<String> NO_CLIENT_IDS_WITH_MESSAGES = Collections.unmodifiableSet(
			Collections.<String> emptySet()).iterator();

	private Application application;

	private ELContext elContext;

	private ExternalContext externalContext;

	private List<Message> messages;

	private FacesMessage.Severity maximumSeverity;

	private boolean released = false;

	private RenderKitFactory renderKitFactory;

	private boolean renderResponse = false;

	private boolean responseComplete = false;

	private ResponseStream responseStream;

	private ResponseWriter responseWriter;

	private UIViewRoot viewRoot;

	private Map<Object, Object> attributes;

	private PhaseId currentPhaseId;

	private ExceptionHandler exceptionHandler;

	private boolean validationFailed;

	private PartialViewContext partialViewContext;

	private boolean processingEvents;

	public PortletFacesContextImpl(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public PortletFacesContextImpl(PortletContext portletContext, PortletRequest portletRequest,
			PortletResponse portletResponse) {
		application = JsfUtils.findFactory(ApplicationFactory.class).getApplication();
		renderKitFactory = JsfUtils.findFactory(RenderKitFactory.class);
		this.externalContext = new PortletExternalContextImpl(portletContext, portletRequest, portletResponse);
		this.exceptionHandler = JsfUtils.findFactory(ExceptionHandlerFactory.class).getExceptionHandler();
		FacesContext.setCurrentInstance(this);
	}

	public void release() {
		assertNotReleased();
		if (externalContext != null) {
			Method delegateMethod = ClassUtils.getMethodIfAvailable(externalContext.getClass(), "release");
			if (delegateMethod != null) {
				try {
					delegateMethod.invoke(externalContext);
				} catch (Exception e) {
					externalContext.log("Failed to release external context", e);
				}
				externalContext = null;
			}
		}
		messages = null;
		maximumSeverity = null;
		application = null;
		responseStream = null;
		responseWriter = null;
		viewRoot = null;
		exceptionHandler = null;
		attributes = null;

		released = true;
		FacesContext.setCurrentInstance(null);
	}

	public ExternalContext getExternalContext() {
		assertNotReleased();
		return externalContext;
	}

	public Application getApplication() {
		assertNotReleased();
		return application;
	}

	public RenderKit getRenderKit() {
		if (getViewRoot() == null) {
			return null;
		}

		String renderKitId = getViewRoot().getRenderKitId();

		if (renderKitId == null) {
			return null;
		}

		return renderKitFactory.getRenderKit(this, renderKitId);
	}

	public boolean getRenderResponse() {
		assertNotReleased();
		return renderResponse;
	}

	public boolean getResponseComplete() {
		assertNotReleased();
		return responseComplete;
	}

	public ResponseStream getResponseStream() {
		assertNotReleased();
		return responseStream;
	}

	public void setResponseStream(ResponseStream responseStream) {
		assertNotReleased();
		if (responseStream == null) {
			throw new NullPointerException("responseStream");
		}
		this.responseStream = responseStream;
	}

	public ResponseWriter getResponseWriter() {
		assertNotReleased();
		return responseWriter;
	}

	public void setResponseWriter(ResponseWriter responseWriter) {
		assertNotReleased();
		if (responseWriter == null) {
			throw new NullPointerException("responseWriter");
		}
		this.responseWriter = responseWriter;
	}

	public UIViewRoot getViewRoot() {
		assertNotReleased();
		return viewRoot;
	}

	public void setViewRoot(UIViewRoot viewRoot) {
		assertNotReleased();
		if (viewRoot == null) {
			throw new NullPointerException("viewRoot");
		}
		this.viewRoot = viewRoot;
	}

	public void renderResponse() {
		assertNotReleased();
		renderResponse = true;
	}

	public void responseComplete() {
		assertNotReleased();
		responseComplete = true;
	}

	public ELContext getELContext() {
		if (elContext == null) {
			createELContext();
		}
		return elContext;
	}

	private void createELContext() {
		elContext = new PortletELContextImpl(getApplication().getELResolver());
		elContext.putContext(FacesContext.class, FacesContext.getCurrentInstance());
		if (getViewRoot() != null) {
			elContext.setLocale(getViewRoot().getLocale());
		}
		fireContextCreated(getApplication().getELContextListeners());
	}

	private void fireContextCreated(ELContextListener[] listeners) {
		if (listeners.length > 0) {
			ELContextEvent event = new ELContextEvent(elContext);
			for (ELContextListener listener : listeners) {
				listener.contextCreated(event);
			}
		}
	}

	public void addMessage(String clientId, FacesMessage message) {
		assertNotReleased();
		if (message == null) {
			throw new NullPointerException("message");
		}

		if (messages == null) {
			messages = new ArrayList<Message>();
		}
		messages.add(new Message(clientId, message));
		recalculateMaximumSeverity(message.getSeverity());
	}

	private void recalculateMaximumSeverity(FacesMessage.Severity severity) {
		if (severity != null) {
			if (maximumSeverity == null || severity.compareTo(maximumSeverity) > 0) {
				maximumSeverity = severity;
			}
		}
	}

	public FacesMessage.Severity getMaximumSeverity() {
		assertNotReleased();
		return maximumSeverity;
	}

	public Iterator<FacesMessage> getMessages() {
		return getMessageList().iterator();
	}

	public List<FacesMessage> getMessageList() {
		assertNotReleased();
		if (messages == null || messages.isEmpty()) {
			return NO_MESSAGES;
		}
		List<FacesMessage> messageList = new ArrayList<FacesMessage>();
		for (Message message : messages) {
			messageList.add(message.getFacesMessage());
		}
		return Collections.unmodifiableList(messageList);
	}

	public Iterator<FacesMessage> getMessages(String clientId) {
		return getMessageList(clientId).iterator();
	}

	public List<FacesMessage> getMessageList(String clientId) {
		assertNotReleased();
		if (messages == null || messages.isEmpty()) {
			return NO_MESSAGES;
		}
		List<FacesMessage> messageList = new ArrayList<FacesMessage>();
		for (Message message : messages) {
			if (message.isForClientId(clientId)) {
				messageList.add(message.getFacesMessage());
			}
		}
		return Collections.unmodifiableList(messageList);
	}

	public Iterator<String> getClientIdsWithMessages() {
		assertNotReleased();
		if (messages == null || messages.isEmpty()) {
			return NO_CLIENT_IDS_WITH_MESSAGES;
		}
		Set<String> clientIdsWithMessags = new LinkedHashSet<String>();
		for (Message message : messages) {
			clientIdsWithMessags.add(message.getClientId());
		}
		return Collections.unmodifiableSet(clientIdsWithMessags).iterator();
	}

	public Map<Object, Object> getAttributes() {
		assertNotReleased();
		if (attributes == null) {
			attributes = new HashMap<Object, Object>();
		}
		return attributes;
	}

	public PhaseId getCurrentPhaseId() {
		assertNotReleased();
		return currentPhaseId;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public boolean isPostback() {
		RenderKit renderKit = getRenderKit();
		if (renderKit == null) {
			String renderKitId = getApplication().getViewHandler().calculateRenderKitId(this);
			renderKit = JsfUtils.findFactory(RenderKitFactory.class).getRenderKit(this, renderKitId);
		}
		return renderKit.getResponseStateManager().isPostback(this);
	}

	public boolean isReleased() {
		return released;
	}

	public boolean isValidationFailed() {
		assertNotReleased();
		return validationFailed;
	}

	public void setCurrentPhaseId(PhaseId currentPhaseId) {
		assertNotReleased();
		this.currentPhaseId = currentPhaseId;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		assertNotReleased();
		this.exceptionHandler = exceptionHandler;
	}

	public void validationFailed() {
		assertNotReleased();
		validationFailed = true;
	}

	public PartialViewContext getPartialViewContext() {
		assertNotReleased();
		if (partialViewContext == null) {
			partialViewContext = JsfUtils.findFactory(PartialViewContextFactory.class).getPartialViewContext(this);
		}
		return partialViewContext;
	}

	public boolean isProcessingEvents() {
		assertNotReleased();
		return processingEvents;
	}

	public void setProcessingEvents(boolean processingEvents) {
		assertNotReleased();
		this.processingEvents = processingEvents;
	}

	public boolean isProjectStage(ProjectStage stage) {
		Assert.notNull(stage, "Stage must not be null");
		return (stage.equals(getApplication().getProjectStage()));
	}

	private void assertNotReleased() {
		Assert.isTrue(!released, "FacesContext already released");
	}

	private class PortletELContextImpl extends ELContext {

		private FunctionMapper functionMapper;
		private VariableMapper variableMapper;
		private ELResolver resolver;

		public PortletELContextImpl(ELResolver resolver) {
			this.resolver = resolver;
		}

		@Override
		public FunctionMapper getFunctionMapper() {
			return functionMapper;
		}

		@Override
		public VariableMapper getVariableMapper() {
			return variableMapper;
		}

		@Override
		public ELResolver getELResolver() {
			return resolver;
		}

	}

	private static class Message {

		private String clientId;
		private FacesMessage facesMessage;

		public Message(String clientId, FacesMessage facesMessage) {
			this.clientId = clientId;
			this.facesMessage = facesMessage;
		}

		public String getClientId() {
			return clientId;
		}

		public boolean isForClientId(String clientId) {
			return ObjectUtils.nullSafeEquals(this.clientId, clientId);
		}

		public FacesMessage getFacesMessage() {
			return facesMessage;
		}
	}

}
