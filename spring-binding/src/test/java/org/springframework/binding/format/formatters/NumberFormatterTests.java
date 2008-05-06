package org.springframework.binding.format.formatters;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.binding.format.InvalidFormatException;

public class NumberFormatterTests extends TestCase {

	private NumberFormatter formatter;

	public void testFormatIntegerDefaultPattern() {
		formatter = new NumberFormatter(Integer.class);
		String value = formatter.format(new Integer(12345));
		assertEquals("12345", value);
	}

	public void testFormatBigDecimalCustomPattern() {
		formatter = new NumberFormatter(BigDecimal.class);
		formatter.setPattern("000.00");
		BigDecimal dec = new BigDecimal("123.45");
		String value = formatter.format(dec);
		assertEquals("123.45", value);
	}

	public void testFormatNull() {
		formatter = new NumberFormatter(Integer.class);
		assertEquals("", formatter.format(null));
	}

	public void testParseIntegerDefaultPattern() {
		formatter = new NumberFormatter(Integer.class);
		Integer integer = (Integer) formatter.parse("12345");
		assertEquals(Integer.valueOf(12345), integer);
	}

	public void testParseBigDecimalCustomPattern() {
		formatter = new NumberFormatter(BigDecimal.class);
		formatter.setPattern("000.00");
		BigDecimal dec = (BigDecimal) formatter.parse("123.45");
		assertEquals(new BigDecimal("123.45"), dec);
	}

	public void testParseInvalidFormatNoPattern() {
		try {
			formatter = new NumberFormatter(Integer.class);
			formatter.parse("12345b");
			fail("Should have failed");
		} catch (InvalidFormatException e) {
		}
	}

	public void testParseInvalidFormatPattern() {
		try {
			formatter = new NumberFormatter(BigDecimal.class);
			formatter.setPattern("000.00");
			formatter.parse("bogus");
		} catch (InvalidFormatException e) {
		}
	}

	public void testParseNull() {
		formatter = new NumberFormatter(Integer.class);
		assertNull(formatter.parse(null));
	}

	public void testParseEmptyString() {
		formatter = new NumberFormatter(Integer.class);
		assertNull(formatter.parse(""));
	}

}
