package org.springframework.binding.format.factories;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterFactoryContext;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.util.StringUtils;

public class BooleanFormatterFactory implements FormatterFactory {

	public Class getFormattedClass() {
		return Boolean.class;
	}

	public Formatter createFormatter(FormatterFactoryContext context) {
		return new BooleanFormatter();
	}

	public static class BooleanFormatter implements Formatter {

		public String formatValue(Object value) throws IllegalArgumentException {
			if (value == null) {
				return "";
			}
			if (Boolean.TRUE.equals(value)) {
				return "true";
			} else if (Boolean.FALSE.equals(value)) {
				return "false";
			} else {
				throw new IllegalArgumentException("Must be a Boolean " + value);
			}
		}

		public Object parseValue(String formattedString) throws InvalidFormatException {
			formattedString = (formattedString != null ? formattedString.trim() : null);
			if (!StringUtils.hasText(formattedString)) {
				return null;
			}
			if (formattedString.equals("true")) {
				return Boolean.TRUE;
			} else if (formattedString.equals("false")) {
				return Boolean.FALSE;
			} else {
				throw new InvalidFormatException(formattedString, "true | false");
			}
		}
	}

}
