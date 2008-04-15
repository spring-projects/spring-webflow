package org.springframework.binding.format.formatters;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class DateFormatterTests extends TestCase {

	public void testFormatDefaultPattern() {
		DateFormatter dateFormatter = new DateFormatter();
		Calendar calendar = new GregorianCalendar(2008, 3, 1);
		assertEquals("2008-04-01", dateFormatter.format(calendar.getTime()));
	}

	public void testParseDefaultPattern() {
		DateFormatter dateFormatter = new DateFormatter();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime((Date) dateFormatter.parse("2008-04-01"));
		assertEquals(2008, calendar.get(Calendar.YEAR));
		assertEquals(3, calendar.get(Calendar.MONTH));
		assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
	}

}
