package org.springframework.binding.format.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;

import junit.framework.TestCase;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterFactoryContext;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.format.factories.NumberFormatterFactory;

public class FormatterRegistryImplTests extends TestCase {

	FormatterRegistryImpl registry = new FormatterRegistryImpl();

	public void testRegisterAndGetFormatter() {
		registry.registerFormatter(new NumberFormatterFactory());
		Formatter formatter = registry.getFormatter(Integer.class);
		Integer value = (Integer) formatter.parseValue("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterAbstractClass() {
		registry.registerFormatter(new NumberFormatterFactory());
		Formatter formatter = registry.getFormatter(Number.class);
		Long value = (Long) formatter.parseValue("3");
		assertEquals(new Long(3), value);
	}

	public void testRegisterAndGetFormatterPrimitive() {
		registry.registerFormatter(new NumberFormatterFactory());
		Formatter formatter = registry.getFormatter(int.class);
		Integer value = (Integer) formatter.parseValue("3");
		assertEquals(new Integer(3), value);
	}

	public void testRegisterAndGetFormatterInterface() {
		registry.registerFormatter(new CustomTypeFormatterFactory());
		Formatter formatter = registry.getFormatter(DefaultCustomType.class);
		assertEquals("12345", formatter.formatValue(new DefaultCustomType("12345")));
		assertEquals(new DefaultCustomType("12345"), formatter.parseValue("12345"));
	}

	public void testRegisterCustomFormatter() {
		registry.registerFormatter(new NumberFormatterFactory());
		registry.registerFormatter("percentNumberFormat", new PercentNumberFormatterFactory());
		Formatter formatter = registry.getFormatter("percentNumberFormat", BigDecimal.class);
		assertEquals("35%", formatter.formatValue(new BigDecimal(".35")));
		BigDecimal value = (BigDecimal) formatter.parseValue("35%");
		assertEquals(new BigDecimal(".35"), value);
	}

	public void testRegisterCustomFormatterBogusLookupId() {
		registry.registerFormatter(new NumberFormatterFactory());
		registry.registerFormatter("percentNumberFormat", new PercentNumberFormatterFactory());
		Formatter formatter = registry.getFormatter("bogusFormat", BigDecimal.class);
		assertNull(formatter);
		formatter = registry.getFormatter(BigDecimal.class);
		assertNotNull(formatter);
		assertEquals("0.35", formatter.formatValue(new BigDecimal(".35")));
		BigDecimal value = (BigDecimal) formatter.parseValue("0.35");
		assertEquals(new BigDecimal(".35"), value);

		formatter = registry.getFormatter("percentNumberFormat", BigDecimal.class);
		assertEquals("35%", formatter.formatValue(new BigDecimal(".35")));
		value = (BigDecimal) formatter.parseValue("35%");
		assertEquals(new BigDecimal(".35"), value);
	}

	public class PercentNumberFormatterFactory extends NumberFormatterFactory implements FormatterFactory {
		protected NumberFormat getNumberFormat(FormatterFactoryContext context) {
			return NumberFormat.getPercentInstance();
		}
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

	private class CustomTypeFormatterFactory implements FormatterFactory {

		public Formatter createFormatter(FormatterFactoryContext context) {
			return new CustomTypeFormatter();
		}

		public Class getFormattedClass() {
			return CustomType.class;
		}

		private class CustomTypeFormatter implements Formatter {

			public String formatValue(Object value) throws IllegalArgumentException {
				CustomType type = (CustomType) value;
				return type.getText();
			}

			public Object parseValue(String formattedString) throws InvalidFormatException {
				return new DefaultCustomType(formattedString);
			}

		}

	}

}
