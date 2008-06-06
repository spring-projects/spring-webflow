package org.springframework.binding.format.formatters;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.binding.format.InvalidFormatException;

public class DateFormatterTests extends TestCase {

	public void testFormatDefaultPattern() {
		DateFormatter dateFormatter = new DateFormatter();
		dateFormatter.setLocale(new Locale("nl"));
		Calendar calendar = new GregorianCalendar(2008, 3, 1);
		assertEquals("2008-04-01", dateFormatter.format(calendar.getTime()));
	}

	public void testFormatCustomPattern() {
		DateFormatter dateFormatter = new DateFormatter();
		dateFormatter.setPattern("MM-dd-yyyy");
		Calendar calendar = new GregorianCalendar(2008, 3, 1);
		assertEquals("04-01-2008", dateFormatter.format(calendar.getTime()));
	}

	public void testFormatNull() {
		DateFormatter dateFormatter = new DateFormatter();
		assertEquals("", dateFormatter.format(null));
	}

	public void testParseDefaultPattern() {
		DateFormatter dateFormatter = new DateFormatter();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime((Date) dateFormatter.parse("2008-04-01"));
		assertEquals(2008, calendar.get(Calendar.YEAR));
		assertEquals(3, calendar.get(Calendar.MONTH));
		assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
	}

	public void testParseInvalidFormat() {
		DateFormatter dateFormatter = new DateFormatter();
		try {
			dateFormatter.parse("01/04/08");
			fail("Should have failed");
		} catch (InvalidFormatException e) {

		}
	}

	public void testParseNull() {
		DateFormatter dateFormatter = new DateFormatter();
		assertNull(dateFormatter.parse(null));
	}

	public void testParseEmptyString() {
		DateFormatter dateFormatter = new DateFormatter();
		assertNull(dateFormatter.parse(""));
	}

}
