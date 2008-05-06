package org.springframework.binding.format.formatters;

import junit.framework.TestCase;

import org.springframework.binding.format.InvalidFormatException;

public class BooleanFormatterTests extends TestCase {

	private BooleanFormatter formatter = new BooleanFormatter();

	public void testFormatTrue() {
		assertEquals("true", formatter.format(Boolean.TRUE));
	}

	public void testFormatFalse() {
		assertEquals("false", formatter.format(Boolean.FALSE));
	}

	public void testFormatNull() {
		assertEquals("", formatter.format(null));
	}

	public void testParseTrue() {
		assertEquals(Boolean.TRUE, formatter.parse("true"));
	}

	public void testParseFalse() {
		assertEquals(Boolean.FALSE, formatter.parse("false"));
	}

	public void testParseInvalid() {
		try {
			formatter.parse("bogus");
			fail("Should have failed");
		} catch (InvalidFormatException e) {
		}
	}

	public void testParseNull() {
		assertNull(formatter.parse(null));
	}

	public void testParseEmptyString() {
		assertNull(formatter.parse(""));
	}

}
