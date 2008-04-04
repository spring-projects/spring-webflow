package org.springframework.webflow.mvc.view;

import java.util.List;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;

/**
 * Adapts a MessageContext object to the Spring Errors interface. Allows Spring Validators to record errors that are
 * managed by a backing MessageContext.
 * 
 * @author Keith Donald
 */
public class MessageContextErrors extends AbstractErrors {

	private MessageContext messageContext;

	/**
	 * Creates a new message context errors adapter.
	 * @param messageContext the backing message context
	 */
	public MessageContextErrors(MessageContext messageContext) {
		this.messageContext = messageContext;
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).defaultText(defaultMessage).build());
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).defaultText(defaultMessage).build());
	}

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public List getFieldErrors() {
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public Object getFieldValue(String field) {
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public List getGlobalErrors() {
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public String getObjectName() {
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

}
