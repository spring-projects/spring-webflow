package org.springframework.binding.validation;

import junit.framework.TestCase;

public class DefaultSpringMvcValidationFailureMessageCodesFactoryTests extends TestCase {
	private DefaultSpringMvcValidationFailureMessageCodesFactory factory = new DefaultSpringMvcValidationFailureMessageCodesFactory();

	public void testCreateGeneralModelFailureMessageCodes() {
		ValidationFailure failure = new ValidationFailureBuilder().constraint("invalid").build();
		String[] codes = factory.createMessageCodes(failure, new TestValidationFailureModelContext("testBean", null,
				null, null));
		assertEquals("validation.invalid.testBean", codes[0]);
		assertEquals("validation.invalid", codes[1]);
	}

	public void testCreatePropertyFailureMessageCodes() {
		ValidationFailure failure = new ValidationFailureBuilder().forProperty("foo").constraint("required").build();
		String[] codes = factory.createMessageCodes(failure, new TestValidationFailureModelContext("testBean", null,
				String.class, null));
		assertEquals("validation.required.testBean.foo", codes[0]);
		assertEquals("validation.required.java.lang.String", codes[1]);
		assertEquals("validation.required", codes[2]);
	}
}
