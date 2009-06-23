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
		messageSource.addMessage("bar", locale, "{0}");
		messageSource.addMessage("baz", locale, "boop");
	}

	public void testBuildDefaultText() {
		MessageResolver resolver = builder.defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.INFO, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildFatal() {
		MessageResolver resolver = builder.fatal().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.FATAL, message.getSeverity());
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

	public void testBuildArg() {
		MessageResolver resolver = builder.error().code("bar").arg("baz").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("baz", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildArgs() {
		MessageResolver resolver = builder.error().codes(new String[] { "bar" }).args(new Object[] { "baz" }).build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("baz", message.getText());
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

	public void testBuildArgsNull() {
		MessageResolver resolver = builder.args(null).build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}

	public void testBuildArgsWithNullCodes() {
		MessageResolver resolver = builder.error().args(new Object[] { "baz" }).build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {
		}
	}

	public void testBuildArgsWithNullCodesDefaultText() {
		MessageResolver resolver = builder.error().args(new Object[] { "baz" }).defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
	}

	public void testBuildWithSource() {
		MessageResolver resolver = builder.source("foo").defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getSource());
		assertEquals("foo", message.getText());
		assertEquals(Severity.INFO, message.getSeverity());
	}

	public void testBuildResolvableArg() {
		MessageResolver resolver = builder.error().code("bar").resolvableArg("baz").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("boop", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	public void testBuildResolvableArgs() {
		MessageResolver resolver = builder.error().codes(new String[] { "bar" }).resolvableArgs(new Object[] { "baz" })
				.build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("boop", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}
}
