package org.springframework.binding.message;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.StaticMessageSource;

public class MessageBuilderTests extends TestCase {
	private StaticMessageSource messageSource = new StaticMessageSource();
	private Locale locale = Locale.getDefault();
	private MessageBuilder builder = new MessageBuilder();

	public void setUp() {
		messageSource.addMessage("foo", locale, "bar");
	}

	public void testBuildDefaultText() {
		MessageResolver resolver = builder.defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.INFO, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildError() {
		MessageResolver resolver = builder.error().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildWarning() {
		MessageResolver resolver = builder.warning().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.WARNING, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildNothing() {
		MessageResolver resolver = builder.build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}

	public void testBuildCode() {
		MessageResolver resolver = builder.error().code("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("bar", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildCodes() {
		MessageResolver resolver = builder.error().codes(new String[] { "foo" }).build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("bar", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildCodesNull() {
		MessageResolver resolver = builder.codes(null).build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}
}
