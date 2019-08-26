package org.springframework.binding.message;

import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.MessageCodesResolver;

public class MessageContextErrorsMessageCodesTests {

	private DefaultMessageContext context;

	private String errorCode = "bar";

	private String objectName = "object";

	private MessageCodesResolver resolver;

	@BeforeEach
	public void setUp() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage(errorCode, Locale.getDefault(), "doesntmatter");
		context = new DefaultMessageContext(messageSource);

		resolver = EasyMock.createMock(MessageCodesResolver.class);
	}

	@Test
	public void testRejectUsesObjectName() {
		EasyMock.expect(resolver.resolveMessageCodes(errorCode, objectName)).andReturn(new String[] {});
		EasyMock.replay(resolver);

		Object object = new Object();
		MessageContextErrors errors = new MessageContextErrors(context, objectName, object, null, resolver, null);
		errors.reject(errorCode, "doesntmatter");

		EasyMock.verify(resolver);
	}

	@Test
	public void testRejectValueUsesObjectName() {
		EasyMock.expect(resolver.resolveMessageCodes(errorCode, objectName, "field", null)).andReturn(new String[] {});
		EasyMock.replay(resolver);

		MessageContextErrors errors = new MessageContextErrors(context, objectName, new Object(), null, resolver, null);
		errors.rejectValue("field", errorCode, new Object[] {}, "doesntmatter");

		EasyMock.verify(resolver);
	}

	@Test
	public void testRejectValueEmptyField() {
		EasyMock.expect(resolver.resolveMessageCodes(errorCode, objectName)).andReturn(new String[] {});
		EasyMock.replay(resolver);

		MessageContextErrors errors = new MessageContextErrors(context, objectName, new Object(), null, resolver, null);
		errors.rejectValue(null, errorCode, new Object[] {}, "doesntmatter");

		EasyMock.verify(resolver);
	}

}
