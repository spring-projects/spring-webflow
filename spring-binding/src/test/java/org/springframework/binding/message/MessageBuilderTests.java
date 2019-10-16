package org.springframework.binding.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.StaticMessageSource;

public class MessageBuilderTests {
	private StaticMessageSource messageSource = new StaticMessageSource();
	private Locale locale = Locale.getDefault();
	private MessageBuilder builder = new MessageBuilder();

	@BeforeEach
	public void setUp() {
		messageSource.addMessage("foo", locale, "bar");
		messageSource.addMessage("bar", locale, "{0}");
		messageSource.addMessage("baz", locale, "boop");
	}

	@Test
	public void testBuildDefaultText() {
		MessageResolver resolver = builder.defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.INFO, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildFatal() {
		MessageResolver resolver = builder.fatal().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.FATAL, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildError() {
		MessageResolver resolver = builder.error().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildWarning() {
		MessageResolver resolver = builder.warning().defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
		assertEquals(Severity.WARNING, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildNothing() {
		MessageResolver resolver = builder.build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}

	@Test
	public void testBuildCode() {
		MessageResolver resolver = builder.error().code("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("bar", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildCodes() {
		MessageResolver resolver = builder.error().codes("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("bar", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildArg() {
		MessageResolver resolver = builder.error().code("bar").arg("baz").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("baz", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildArgs() {
		MessageResolver resolver = builder.error().codes("bar").args("baz").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("baz", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildCodesNull() {
		MessageResolver resolver = builder.codes().build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}

	@Test
	public void testBuildArgsNull() {
		MessageResolver resolver = builder.args().build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {

		}
	}

	@Test
	public void testBuildArgsWithNullCodes() {
		MessageResolver resolver = builder.error().args("baz").build();
		try {
			resolver.resolveMessage(messageSource, locale);
			fail("Should have failed");
		} catch (NoSuchMessageException e) {
		}
	}

	@Test
	public void testBuildArgsWithNullCodesDefaultText() {
		MessageResolver resolver = builder.error().args("baz").defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getText());
	}

	@Test
	public void testBuildWithSource() {
		MessageResolver resolver = builder.source("foo").defaultText("foo").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("foo", message.getSource());
		assertEquals("foo", message.getText());
		assertEquals(Severity.INFO, message.getSeverity());
	}

	@Test
	public void testBuildResolvableArg() {
		MessageResolver resolver = builder.error().code("bar").resolvableArg("baz").build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("boop", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}

	@Test
	public void testBuildResolvableArgs() {
		MessageResolver resolver = builder.error().codes("bar").resolvableArgs("baz")
				.build();
		Message message = resolver.resolveMessage(messageSource, locale);
		assertEquals("boop", message.getText());
		assertEquals(Severity.ERROR, message.getSeverity());
		assertNull(message.getSource());
	}
}
