package org.springframework.binding.format.impl;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.format.formatters.NumberFormatter;
import org.springframework.binding.format.registry.GenericFormatterRegistry;

public class GenericFormatterRegistryTests extends TestCase {

	GenericFormatterRegistry registry = new GenericFormatterRegistry();

	public void testRegisterAndGetFormatter() {
		registry.registerFormatter(new NumberFormatter(Integer.class));
		Formatter formatter = registry.getFormatter(Integer.class);
		Integer value = (Integer) formatter.parse("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterAbstractClass() {
		registry.registerFormatter(new NumberFormatter(Number.class));
		Formatter formatter = registry.getFormatter(Long.class);
		assertNull(formatter);
	}

	public void testRegisterAndGetFormatterPrimitive() {
		registry.registerFormatter(new NumberFormatter(Integer.class));
		Formatter formatter = registry.getFormatter(int.class);
		Integer value = (Integer) formatter.parse("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterInterface() {
		registry.registerFormatter(new CustomTypeFormatter());
		Formatter formatter = registry.getFormatter(CustomType.class);
		assertEquals("12345", formatter.format(new DefaultCustomType("12345")));
		assertEquals(new DefaultCustomType("12345"), formatter.parse("12345"));
	}

	public void testRegisterCustomFormatter() {
		registry.registerFormatter(new NumberFormatter(Integer.class));
		NumberFormatter percentFormatter = new NumberFormatter(BigDecimal.class);
		percentFormatter.setPattern("00%");
		registry.registerFormatter("percent", percentFormatter);
		Formatter formatter = registry.getFormatter(Integer.class, "percent");
		assertEquals("35%", formatter.format(new BigDecimal(".35")));
		BigDecimal value = (BigDecimal) formatter.parse("35%");
		assertEquals(new BigDecimal(".35"), value);
	}

	public void testRegisterCustomFormatterBogusLookupId() {
		registry.registerFormatter(new NumberFormatter(Integer.class));
		registry.registerFormatter("double", new NumberFormatter(Double.class));
		Formatter formatter = registry.getFormatter(Integer.class, "bogusFormat");
		assertNull(formatter);
		formatter = registry.getFormatter(Double.class, "double");
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

		public Class getObjectType() {
			return CustomType.class;
		}

		public String format(Object value) throws IllegalArgumentException {
			CustomType type = (CustomType) value;
			return type.getText();
		}

		public Object parse(String formattedString) throws InvalidFormatException {
			return new DefaultCustomType(formattedString);
		}

	}

}
