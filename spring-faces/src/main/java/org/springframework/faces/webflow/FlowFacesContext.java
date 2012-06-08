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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.message.Severity;
import org.springframework.context.MessageSource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Custom {@link FacesContext} implementation that delegates all standard FacesContext messaging functionality to a
 * Spring {@link MessageSource} made accessible as part of the current Web Flow request. Additionally, it manages the
 * {@code renderResponse} flag in flash scope so that the execution of the JSF {@link Lifecycle} may span multiple
 * requests in the case of the POST+REDIRECT+GET pattern being enabled.
 * 
 * @see FlowExternalContext
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 * @author Rossen Stoyanchev
 */
public class FlowFacesContext extends FacesContextWrapper {

	/**
	 * The key for storing the renderResponse flag
	 */
	static final String RENDER_RESPONSE_KEY = "flowRenderResponse";

	private static final Map<Severity, FacesMessage.Severity> SPRING_SEVERITY_TO_FACES;
	static {
		SPRING_SEVERITY_TO_FACES = new HashMap<Severity, FacesMessage.Severity>();
		SPRING_SEVERITY_TO_FACES.put(Severity.INFO, FacesMessage.SEVERITY_INFO);
		SPRING_SEVERITY_TO_FACES.put(Severity.WARNING, FacesMessage.SEVERITY_WARN);
		SPRING_SEVERITY_TO_FACES.put(Severity.ERROR, FacesMessage.SEVERITY_ERROR);
		SPRING_SEVERITY_TO_FACES.put(Severity.FATAL, FacesMessage.SEVERITY_FATAL);
	}

	private static final Map<FacesMessage.Severity, Severity> FACES_SEVERITY_TO_SPRING;
	static {
		FACES_SEVERITY_TO_SPRING = new HashMap<FacesMessage.Severity, Severity>();
		for (Map.Entry<Severity, FacesMessage.Severity> entry : SPRING_SEVERITY_TO_FACES.entrySet()) {
			FACES_SEVERITY_TO_SPRING.put(entry.getValue(), entry.getKey());
		}
	}

	private final FacesContext wrapped;

	private final RequestContext context;

	private final ExternalContext externalContext;

	private final PartialViewContext partialViewContext;

	public FlowFacesContext(RequestContext context, FacesContext wrapped) {
		this.context = context;
		this.wrapped = wrapped;
		this.externalContext = new FlowExternalContext(context, wrapped.getExternalContext());
		PartialViewContextFactory factory = JsfUtils.findFactory(PartialViewContextFactory.class);
		PartialViewContext partialViewContextDelegate = factory.getPartialViewContext(this);
		this.partialViewContext = new FlowPartialViewContext(partialViewContextDelegate);
		setCurrentInstance(this);
	}

	public FacesContext getWrapped() {
		return this.wrapped;
	}

	public void release() {
		super.release();
		setCurrentInstance(null);
	}

	public ExternalContext getExternalContext() {
		return this.externalContext;
	}

	public PartialViewContext getPartialViewContext() {
		return this.partialViewContext;
	}

	public ELContext getELContext() {
		ELContext elContext = super.getELContext();
		// Ensure that our wrapper is used over the stock FacesContextImpl
		elContext.putContext(FacesContext.class, this);
		return elContext;
	}

	public boolean getRenderResponse() {
		Boolean renderResponse = this.context.getFlashScope().getBoolean(RENDER_RESPONSE_KEY);
		return (renderResponse == null ? false : renderResponse);
	}

	public boolean getResponseComplete() {
		return this.context.getExternalContext().isResponseComplete();
	}

	public void renderResponse() {
		// stored in flash scope to survive a redirect when transitioning from one view to another
		this.context.getFlashScope().put(RENDER_RESPONSE_KEY, true);
	}

	public void responseComplete() {
		this.context.getExternalContext().recordResponseComplete();
	}

	public boolean isValidationFailed() {
		if (this.context.getMessageContext().hasErrorMessages()) {
			return true;
		} else {
			return super.isValidationFailed();
		}
	}

	/**
	 * Translates a FacesMessage to a Spring Web Flow message and adds it to the current MessageContext
	 */
	public void addMessage(String clientId, FacesMessage message) {
		FacesMessageSource source = new FacesMessageSource(clientId);
		FlowFacesMessage flowFacesMessage = new FlowFacesMessage(source, message);
		this.context.getMessageContext().addMessage(flowFacesMessage);
	}

	/**
	 * Returns an Iterator for all component clientId's for which messages have been added.
	 */
	public Iterator<String> getClientIdsWithMessages() {
		Set<String> clientIds = new LinkedHashSet<String>();
		for (Message message : this.context.getMessageContext().getAllMessages()) {
			Object source = message.getSource();
			if (source != null && source instanceof String) {
				clientIds.add((String) source);
			} else if (message.getSource() instanceof FacesMessageSource) {
				clientIds.add(((FacesMessageSource) source).getClientId());
			}
		}
		return Collections.unmodifiableSet(clientIds).iterator();
	}

	/**
	 * Return the maximum severity level recorded on any FacesMessages that has been queued, whether or not they are
	 * associated with any specific UIComponent. If no such messages have been queued, return null.
	 */
	public FacesMessage.Severity getMaximumSeverity() {
		if (this.context.getMessageContext().getAllMessages().length == 0) {
			return null;
		}
		FacesMessage.Severity max = FacesMessage.SEVERITY_INFO;
		Iterator<FacesMessage> messages = getMessages();
		while (messages.hasNext()) {
			FacesMessage message = messages.next();
			if (message.getSeverity().getOrdinal() > max.getOrdinal()) {
				max = message.getSeverity();
			}
			if (max.getOrdinal() == FacesMessage.SEVERITY_FATAL.getOrdinal()) {
				break;
			}
		}
		return max;
	}

	/**
	 * Returns an Iterator for all Messages in the current MessageContext that does translation to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages() {
		return getMessageList().iterator();
	}

	/**
	 * Returns a List for all Messages in the current MessageContext that does translation to FacesMessages.
	 */
	public List<FacesMessage> getMessageList() {
		Message[] messages = this.context.getMessageContext().getAllMessages();
		return asFacesMessages(messages);
	}

	/**
	 * Returns an Iterator for all Messages with the given clientId in the current MessageContext that does translation
	 * to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages(String clientId) {
		return getMessageList(clientId).iterator();
	}

	/**
	 * Returns a List for all Messages with the given clientId in the current MessageContext that does translation to
	 * FacesMessages.
	 */
	public List<FacesMessage> getMessageList(final String clientId) {
		final FacesMessageSource source = new FacesMessageSource(clientId);
		Message[] messages = this.context.getMessageContext().getMessagesByCriteria(new MessageCriteria() {
			public boolean test(Message message) {
				return ObjectUtils.nullSafeEquals(message.getSource(), source)
						|| ObjectUtils.nullSafeEquals(message.getSource(), clientId);
			}
		});
		return asFacesMessages(messages);
	}

	private List<FacesMessage> asFacesMessages(Message[] messages) {
		if (messages == null || messages.length == 0) {
			return Collections.emptyList();
		}
		List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();
		for (Message message : messages) {
			facesMessages.add(asFacesMessage(message));
		}
		return Collections.unmodifiableList(facesMessages);
	}

	private FacesMessage asFacesMessage(Message message) {
		if (message instanceof FlowFacesMessage) {
			return ((FlowFacesMessage) message).getFacesMessage();
		}
		FacesMessage.Severity severity = SPRING_SEVERITY_TO_FACES.get(message.getSeverity());
		if (severity == null) {
			severity = FacesMessage.SEVERITY_INFO;
		}
		return new FacesMessage(severity, message.getText(), null);
	}

	public static FlowFacesContext newInstance(RequestContext context, Lifecycle lifecycle) {
		FacesContext defaultFacesContext = newDefaultInstance(context, lifecycle);
		return new FlowFacesContext(context, defaultFacesContext);
	}

	private static FacesContext newDefaultInstance(RequestContext context, Lifecycle lifecycle) {
		Object nativeContext = context.getExternalContext().getNativeContext();
		Object nativeRequest = context.getExternalContext().getNativeRequest();
		Object nativeResponse = context.getExternalContext().getNativeResponse();
		return FacesContextHelper.newDefaultInstance(nativeContext, nativeRequest, nativeResponse, lifecycle);
	}

	/**
	 * Adapter class to convert a {@link FacesMessage} to a Spring {@link Message}. This adapter is required to allow
	 * <tt>FacesMessages</tt> to be registered with Spring whilst still retaining their mutable nature. It is not
	 * uncommon for <tt>FacesMessages</tt> to be changed after they have been added to a <tt>FacesContext</tt>, for
	 * example, from a <tt>PhaseListener</tt>.
	 * <p>
	 * NOTE: Only {@link javax.faces.application.FacesMessage} instances are directly adapted, any subclasses will be
	 * converted to the standard FacesMessage implementation. This is to protect against bugs such as SWF-1073.
	 * 
	 * For convenience this class also implements the {@link MessageResolver} interface.
	 */
	protected static class FlowFacesMessage extends Message implements MessageResolver {

		private transient FacesMessage facesMessage;

		public FlowFacesMessage(FacesMessageSource source, FacesMessage message) {
			super(source, null, null);
			this.facesMessage = asStandardFacesMessageInstance(message);
		}

		/**
		 * Use standard faces message as required to protect against bugs such as SWF-1073.
		 * 
		 * @param message {@link javax.faces.application.FacesMessage} or subclass.
		 * @return {@link javax.faces.application.FacesMessage} instance
		 */
		private FacesMessage asStandardFacesMessageInstance(FacesMessage message) {
			if (FacesMessage.class.equals(message.getClass())) {
				return message;
			}
			return new FacesMessage(message.getSeverity(), message.getSummary(), message.getDetail());
		}

		// Custom serialization to work around myfaces bug MYFACES-1347

		private void writeObject(ObjectOutputStream oos) throws IOException {
			oos.defaultWriteObject();
			oos.writeObject(this.facesMessage.getSummary());
			oos.writeObject(this.facesMessage.getDetail());
			oos.writeInt(this.facesMessage.getSeverity().getOrdinal());
		}

		private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			String summary = (String) ois.readObject();
			String detail = (String) ois.readObject();
			int severityOrdinal = ois.readInt();
			FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
			for (Iterator<?> iterator = FacesMessage.VALUES.iterator(); iterator.hasNext();) {
				FacesMessage.Severity value = (FacesMessage.Severity) iterator.next();
				if (value.getOrdinal() == severityOrdinal) {
					severity = value;
				}
			}
			this.facesMessage = new FacesMessage(severity, summary, detail);
		}

		public String getText() {
			StringBuilder text = new StringBuilder();
			if (StringUtils.hasLength(this.facesMessage.getSummary())) {
				text.append(this.facesMessage.getSummary());
			}
			if (StringUtils.hasLength(this.facesMessage.getDetail())) {
				text.append(text.length() == 0 ? "" : " : ");
				text.append(this.facesMessage.getDetail());
			}
			return text.toString();
		}

		public Severity getSeverity() {
			Severity severity = null;
			if (this.facesMessage.getSeverity() != null) {
				severity = FACES_SEVERITY_TO_SPRING.get(this.facesMessage.getSeverity());
			}
			return (severity == null ? Severity.INFO : severity);
		}

		public String toString() {
			ToStringCreator rtn = new ToStringCreator(this);
			rtn.append("severity", getSeverity());
			if (FacesContext.getCurrentInstance() != null) {
				// Only append text if running within a faces context
				rtn.append("text", getText());
			}
			return rtn.toString();
		}

		public Message resolveMessage(MessageSource messageSource, Locale locale) {
			return this;
		}

		/**
		 * @return The original {@link FacesMessage} adapted by this class.
		 */
		public FacesMessage getFacesMessage() {
			return this.facesMessage;
		}
	}

	/**
	 * A Spring Message {@link Message#getSource() Source} that originated from JSF.
	 */
	public static class FacesMessageSource implements Serializable {

		private String clientId;

		public FacesMessageSource(String clientId) {
			if (StringUtils.hasLength(clientId)) {
				this.clientId = clientId;
			}
		}

		public String getClientId() {
			return this.clientId;
		}

		public int hashCode() {
			return ObjectUtils.nullSafeHashCode(this.clientId);
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass().equals(FacesMessageSource.class)) {
				return ObjectUtils.nullSafeEquals(getClientId(), ((FacesMessageSource) obj).getClientId());
			}
			return false;
		}
	}
}
