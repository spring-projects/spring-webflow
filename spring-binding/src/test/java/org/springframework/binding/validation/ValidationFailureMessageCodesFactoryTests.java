package org.springframework.binding.validation;

import junit.framework.TestCase;

public class ValidationFailureMessageCodesFactoryTests extends TestCase {
	private ValidationFailureMessageCodesFactory factory = new ValidationFailureMessageCodesFactory();

	public void testCreateGeneralModelFailureMessageCodes() {
		ValidationFailure failure = new ValidationFailureBuilder().constraint("invalid").build();
		String[] codes = factory.createMessageCodes(failure, new ValidationFailureModelContext("testBean", null, null,
				null));
		assertEquals("validation.testBean.invalid", codes[0]);
		assertEquals("validation.invalid", codes[1]);
	}

	public void testCreatePropertyFailureMessageCodes() {
		ValidationFailure failure = new ValidationFailureBuilder().forProperty("foo").constraint("required").build();
		String[] codes = factory.createMessageCodes(failure, new ValidationFailureModelContext("testBean", null,
				String.class, null));
		assertEquals("validation.testBean.foo.required", codes[0]);
		assertEquals("validation.java.lang.String.required", codes[1]);
		assertEquals("validation.required", codes[2]);
	}
}
