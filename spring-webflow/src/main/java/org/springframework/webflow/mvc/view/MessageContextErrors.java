package org.springframework.webflow.mvc.view;

import java.util.List;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Adapts a MessageContext object to the Spring Errors interface. Allows Spring Validators to record errors that are
 * managed by a backing MessageContext.
 * 
 * @author Keith Donald
 */
public class MessageContextErrors implements Errors {

	private MessageContext messageContext;

	/**
	 * Creates a new message context errors adapter.
	 * @param messageContext the backing message context
	 */
	public MessageContextErrors(MessageContext messageContext) {
		this.messageContext = messageContext;
	}

	public void reject(String errorCode) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).build());
	}

	public void reject(String errorCode, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).defaultText(defaultMessage).build());
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).defaultText(defaultMessage).build());
	}

	public void rejectValue(String field, String errorCode) {
		messageContext.addMessage(new MessageBuilder().error().source(field).code(errorCode).build());
	}

	public void rejectValue(String field, String errorCode, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().source(field).code(errorCode)
				.defaultText(defaultMessage).build());
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().source(field).code(errorCode).args(errorArgs)
				.defaultText(defaultMessage).build());
	}

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public List getAllErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public int getErrorCount() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public FieldError getFieldError() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public FieldError getFieldError(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public int getFieldErrorCount() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public int getFieldErrorCount(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public List getFieldErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public List getFieldErrors(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public Class getFieldType(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public Object getFieldValue(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public ObjectError getGlobalError() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public int getGlobalErrorCount() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public List getGlobalErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public String getNestedPath() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public String getObjectName() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public boolean hasErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public boolean hasFieldErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public boolean hasFieldErrors(String field) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public boolean hasGlobalErrors() {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public void popNestedPath() throws IllegalStateException {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public void pushNestedPath(String subPath) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

	public void setNestedPath(String nestedPath) {
		throw new UnsupportedOperationException("Should not be called by a validator");
	}

}
