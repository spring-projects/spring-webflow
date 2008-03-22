package org.springframework.binding.format.factories;

import java.text.NumberFormat;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterFactoryContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.NumberUtils;

/**
 * Factory for number formatters.
 * 
 * @author Keith Donald
 */
public class NumberFormatterFactory implements FormatterFactory {

	public Class getFormattedClass() {
		return Number.class;
	}

	public Formatter createFormatter(FormatterFactoryContext context) {
		return new NumberFormatter(getNumberFormat(context), context.getFormattedClass());
	}

	/**
	 * Returns the number format to use with the formatter being created. Subclasses may override.
	 * @param context the factory context
	 * @return the number format
	 */
	protected NumberFormat getNumberFormat(FormatterFactoryContext context) {
		if (context.getFormattedClass().equals(Integer.class)) {
			return NumberFormat.getIntegerInstance(context.getLocale());
		} else {
			return NumberFormat.getNumberInstance(context.getLocale());
		}
	}

	private static class NumberFormatter extends AbstractFormatter {

		private NumberFormat numberFormat;

		private Class targetClass;

		/**
		 * Create a new number formatter.
		 * @param numberFormat the number format to use
		 * @param targetClass the number class to parse into
		 */
		public NumberFormatter(NumberFormat numberFormat, Class targetClass) {
			this.numberFormat = numberFormat;
			this.targetClass = targetClass;
		}

		protected String doFormatValue(Object number) {
			if (numberFormat != null) {
				// use NumberFormat for rendering value
				return numberFormat.format(number);
			} else {
				// use toString method for rendering value
				return number.toString();
			}
		}

		protected Object doParseValue(String text) throws IllegalArgumentException {
			if (numberFormat != null) {
				// use given NumberFormat for parsing text
				return NumberUtils.parseNumber(text, targetClass, numberFormat);
			} else {
				// use default valueOf methods for parsing text
				return NumberUtils.parseNumber(text, targetClass);
			}
		}

		public String toString() {
			return new ToStringCreator(this).append("format", numberFormat).append("targetClass", targetClass)
					.toString();
		}
	}
}
