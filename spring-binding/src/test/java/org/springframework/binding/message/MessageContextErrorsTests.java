package org.springframework.binding.message;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.context.support.StaticMessageSource;

public class MessageContextErrorsTests extends TestCase {
	public void testReject() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo", Locale.getDefault(), "bar");
		messageSource.addMessage("bar", Locale.getDefault(), "{0}");

		DefaultMessageContext context = new DefaultMessageContext(messageSource);
		MessageContextErrors errors = new MessageContextErrors(context);
		errors.reject("foo");
		errors.reject("bogus", "baz");
		errors.reject("bar", new Object[] { "boop" }, null);

		Message msg = context.getAllMessages()[0];
		assertEquals(null, msg.getSource());
		assertEquals("bar", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());

		msg = context.getAllMessages()[1];
		assertEquals(null, msg.getSource());
		assertEquals("baz", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());

		msg = context.getAllMessages()[2];
		assertEquals(null, msg.getSource());
		assertEquals("boop", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());
	}

	public void testRejectValue() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo", Locale.getDefault(), "bar");
		messageSource.addMessage("bar", Locale.getDefault(), "{0}");

		DefaultMessageContext context = new DefaultMessageContext(messageSource);
		MessageContextErrors errors = new MessageContextErrors(context);
		errors.rejectValue("field", "foo");
		errors.rejectValue("field", "bogus", "baz");
		errors.rejectValue("field", "bar", new Object[] { "boop" }, null);

		Message msg = context.getAllMessages()[0];
		assertEquals("field", msg.getSource());
		assertEquals("bar", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());

		msg = context.getAllMessages()[1];
		assertEquals("field", msg.getSource());
		assertEquals("baz", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());

		msg = context.getAllMessages()[2];
		assertEquals("field", msg.getSource());
		assertEquals("boop", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());
	}
}
