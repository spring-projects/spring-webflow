package org.springframework.binding.mapping;

import org.springframework.binding.message.MessageContext;

public class MappingContextImpl implements MappingContext {

	private MessageContext messageContext;

	public MappingContextImpl(MessageContext messageContext) {
		this.messageContext = messageContext;
	}

	public MessageContext getMessageContext() {
		return messageContext;
	}

}
