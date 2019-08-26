package org.springframework.webflow.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class WebFlowMessageResolverTests {
	private WebFlowMessageCodesResolver messageCodesResolver = new WebFlowMessageCodesResolver();

	@Test
	public void testResolveObjectMessageCodes() {
		String[] codes = messageCodesResolver.resolveMessageCodes("required", "testBean");
		assertEquals(2, codes.length);
		assertEquals("testBean.required", codes[0]);
		assertEquals("required", codes[1]);
	}

	@Test
	public void testResolveObjectMessageCodesWithPrefix() {
		messageCodesResolver.setPrefix("validation.");
		String[] codes = messageCodesResolver.resolveMessageCodes("required", "testBean");
		assertEquals(2, codes.length);
		assertEquals("validation.testBean.required", codes[0]);
		assertEquals("validation.required", codes[1]);
	}

	@Test
	public void testResolveFieldMessageCodes() {
		String[] codes = messageCodesResolver.resolveMessageCodes("required", "testBean", "foo", String.class);
		assertEquals(4, codes.length);
		assertEquals("testBean.foo.required", codes[0]);
		assertEquals("foo.required", codes[1]);
		assertEquals("java.lang.String.required", codes[2]);
		assertEquals("required", codes[3]);
	}

	@Test
	public void testResolveFieldMessageCodesKeyedField() {
		String[] codes = messageCodesResolver.resolveMessageCodes("required", "testBean", "foo[0]", String.class);
		assertEquals(6, codes.length);
		assertEquals("testBean.foo[0].required", codes[0]);
		assertEquals("testBean.foo.required", codes[1]);
		assertEquals("foo[0].required", codes[2]);
		assertEquals("foo.required", codes[3]);
		assertEquals("java.lang.String.required", codes[4]);
		assertEquals("required", codes[5]);
	}

	@Test
	public void testResolveFieldMessageCodesWithPrefix() {
		messageCodesResolver.setPrefix("validation.");
		String[] codes = messageCodesResolver.resolveMessageCodes("required", "testBean", "foo", String.class);
		assertEquals(4, codes.length);
		assertEquals("validation.testBean.foo.required", codes[0]);
		assertEquals("validation.foo.required", codes[1]);
		assertEquals("validation.java.lang.String.required", codes[2]);
		assertEquals("validation.required", codes[3]);
	}

}
