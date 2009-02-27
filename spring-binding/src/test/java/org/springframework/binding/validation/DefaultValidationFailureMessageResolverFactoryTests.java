package org.springframework.binding.validation;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.convert.converters.StringToObject;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.support.ResourceBundleMessageSource;

public class DefaultValidationFailureMessageResolverFactoryTests extends TestCase {

	private ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());

	private DefaultConversionService conversionService = new DefaultConversionService();

	private ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

	private DefaultValidationFailureMessageResolverFactory factory;

	ValidationFailureBuilder builder = new ValidationFailureBuilder();

	public void setUp() {
		factory = new DefaultValidationFailureMessageResolverFactory(parser, conversionService);
		messageSource.setBasename("org.springframework.binding.validation.messages");
	}

	public void testResolveMessage() {
		ValidationFailure failure = builder.forProperty("foo").constraint("required").build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"", String.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Foo is required", message.getText());
	}

	public void testResolveMessageNoPropertyLabel() {
		ValidationFailure failure = builder.forProperty("bogus").constraint("required").build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"", String.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("bogus is required", message.getText());
	}

	public void testResolveMessageWithCustomArg() {
		ValidationFailureBuilder builder = new ValidationFailureBuilder();
		ValidationFailure failure = builder.forProperty("checkinDate").constraint("invalidFormat").arg("format",
				"yyyy-MM-dd").build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"bogus", Date.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Check In Date must be in format yyyy-MM-dd", message.getText());
	}

	public void testResolveMessageWithCustomResolvableArg() {
		ValidationFailure failure = builder.forProperty("checkinDate").constraint("invalidFormat").resolvableArg(
				"format", "formats.dateFormat").build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"bogus", Date.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Check In Date must be in format yyyy-MM-dd", message.getText());
	}

	public void testResolveMessageWithValue() {
		ValidationFailure failure = builder.forProperty("checkinDate").constraint("invalidFormat2").resolvableArg(
				"format", "formats.dateFormat").build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"bogus", Date.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Check In Date must be in format yyyy-MM-dd but it was 'bogus'", message.getText());
	}

	public void testResolveMessageWithArgDefaultConversion() {
		ValidationFailure failure = builder.forProperty("amount").constraint("range").arg("min", new Integer(1)).arg(
				"max", new Integer(100)).build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"bogus", Integer.class, null));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Amount must be between 1 and 100", message.getText());
	}

	public void testResolveMessageWithArgCustomConversion() {
		conversionService.addConverter("stringToMoney", new StringToMoney());
		ValidationFailure failure = builder.forProperty("amount").constraint("range").arg("min", new Money(1)).arg(
				"max", new Money(100)).build();
		MessageResolver resolver = factory.createMessageResolver(failure, new ValidationFailureModelContext("testBean",
				"bogus", Money.class, "stringToMoney"));
		Message message = resolver.resolveMessage(messageSource, Locale.getDefault());
		assertEquals("Amount must be between $1 and $100", message.getText());
	}

	public static class Money {
		private int amount;

		public Money(int amount) {
			this.amount = amount;
		}

		public int getAmount() {
			return amount;
		}
	}

	public static class StringToMoney extends StringToObject {

		public StringToMoney() {
			super(Money.class);
		}

		protected Object toObject(String string, Class targetClass) throws Exception {
			throw new UnsupportedOperationException("Not supported");
		}

		protected String toString(Object object) throws Exception {
			return "$" + String.valueOf(((Money) object).amount);
		}

	}
}
