package org.springframework.binding.format.impl;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.format.formatters.NumberFormatter;
import org.springframework.binding.format.registry.FormatterRegistryImpl;

public class FormatterRegistryImplTests extends TestCase {

	FormatterRegistryImpl registry = new FormatterRegistryImpl();

	public void testRegisterAndGetFormatter() {
		registry.registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		Formatter formatter = registry.getFormatter(Integer.class);
		Integer value = (Integer) formatter.parse("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterAbstractClass() {
		registry.registerFormatter(Number.class, new NumberFormatter(Long.class));
		Formatter formatter = registry.getFormatter(Long.class);
		Number value = (Number) formatter.parse("3");
		assertEquals(3L, value.longValue());
	}

	public void testRegisterAndGetFormatterPrimitive() {
		registry.registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		Formatter formatter = registry.getFormatter(int.class);
		Integer value = (Integer) formatter.parse("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterInterface() {
		registry.registerFormatter(CustomType.class, new CustomTypeFormatter());
		Formatter formatter = registry.getFormatter(DefaultCustomType.class);
		assertEquals("12345", formatter.format(new DefaultCustomType("12345")));
		assertEquals(new DefaultCustomType("12345"), formatter.parse("12345"));
	}

	public void testRegisterCustomFormatter() {
		registry.registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		NumberFormatter percentFormatter = new NumberFormatter(BigDecimal.class);
		percentFormatter.setPattern("00%");
		registry.registerFormatter("percent", percentFormatter);
		Formatter formatter = registry.getFormatter("percent");
		assertEquals("35%", formatter.format(new BigDecimal(".35")));
		BigDecimal value = (BigDecimal) formatter.parse("35%");
		assertEquals(new BigDecimal(".35"), value);
	}

	public void testRegisterCustomFormatterBogusLookupId() {
		registry.registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		registry.registerFormatter("double", new NumberFormatter(Double.class));
		Formatter formatter = registry.getFormatter("bogusFormat");
		assertNull(formatter);
		formatter = registry.getFormatter("double");
		assertNotNull(formatter);
	}

	public interface CustomType {
		public String getText();
	}

	public class DefaultCustomType implements CustomType {

		private String text;

		public DefaultCustomType(String text) {
			this.text = text;
		}

		public boolean equals(Object o) {
			DefaultCustomType other = (DefaultCustomType) o;
			return text.equals(other.text);
		}

		public String getText() {
			return text;
		}
	}

	private class CustomTypeFormatter implements Formatter {

		public String format(Object value) throws IllegalArgumentException {
			CustomType type = (CustomType) value;
			return type.getText();
		}

		public Object parse(String formattedString) throws InvalidFormatException {
			return new DefaultCustomType(formattedString);
		}

	}

}
