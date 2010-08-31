package org.springframework.binding.message;

import java.util.HashMap;
import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MapBindingResult;

public class MessageContextErrorsTests extends TestCase {
	public void testReject() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo", Locale.getDefault(), "bar");
		messageSource.addMessage("bar", Locale.getDefault(), "{0}");

		DefaultMessageContext context = new DefaultMessageContext(messageSource);
		Object object = new Object();
		MessageContextErrors errors = new MessageContextErrors(context, "object", object, null,
				new DefaultMessageCodesResolver(), null);
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
		Object object = new Object();
		MessageContextErrors errors = new MessageContextErrors(context, "object", object, null,
				new DefaultMessageCodesResolver(), null);
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

	public void testGlobalError() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo", Locale.getDefault(), "bar");
		messageSource.addMessage("bar", Locale.getDefault(), "{0}");

		DefaultMessageContext context = new DefaultMessageContext(messageSource);
		Object object = new Object();
		SpringELExpressionParser expressionParser = new SpringELExpressionParser(new SpelExpressionParser());
		MessageContextErrors errors = new MessageContextErrors(context, "object", object, expressionParser,
				new DefaultMessageCodesResolver(), null);
		errors.rejectValue(null, "bar", new Object[] { "boop" }, null);
		Message msg = context.getAllMessages()[0];
		assertEquals("", msg.getSource());
		assertEquals("boop", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());
	}

	public void testAddAllErrors() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo", Locale.getDefault(), "bar");
		messageSource.addMessage("bar", Locale.getDefault(), "{0}");

		DefaultMessageContext context = new DefaultMessageContext(messageSource);
		Object object = new Object();
		MessageContextErrors errors = new MessageContextErrors(context, "object", object, null,
				new DefaultMessageCodesResolver(), null);
		MapBindingResult result = new MapBindingResult(new HashMap(), "object");
		result.reject("bar", new Object[] { "boop" }, null);
		result.rejectValue("field", "bar", new Object[] { "boop" }, null);
		errors.addAllErrors(result);

		Message msg = context.getAllMessages()[0];
		assertEquals(null, msg.getSource());
		assertEquals("boop", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());

		msg = context.getAllMessages()[1];
		assertEquals("field", msg.getSource());
		assertEquals("boop", msg.getText());
		assertEquals(Severity.ERROR, msg.getSeverity());
	}

	public void testGetGlobalErrors() {

	}

	public void testGetFieldErrors() {

	}

	public void testGetFieldValue() {

	}

}