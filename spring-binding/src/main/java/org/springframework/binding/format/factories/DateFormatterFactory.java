package org.springframework.binding.format.factories;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterFactoryContext;

/**
 * Factory for date formatters.
 * 
 * @author Keith Donald
 */
public class DateFormatterFactory implements FormatterFactory {

	public Class getFormattedClass() {
		return Date.class;
	}

	public Formatter createFormatter(FormatterFactoryContext context) {
		return new DateFormatter(getDateFormat(context));
	}

	/**
	 * Returns the date format to use with the formatter being created. Subclasses may override.
	 * @param context the factory context
	 * @return the date format
	 */
	protected DateFormat getDateFormat(FormatterFactoryContext context) {
		return DateFormat.getDateInstance(DateFormat.SHORT, context.getLocale());
	}

	private class DateFormatter extends AbstractFormatter {

		private DateFormat dateFormat;

		/**
		 * Constructs a date formatter that will delegate to the specified date format.
		 * @param dateFormat the date format to use
		 */
		public DateFormatter(DateFormat dateFormat) {
			this.dateFormat = dateFormat;
		}

		// convert from date to string
		protected String doFormatValue(Object date) {
			return dateFormat.format((Date) date);
		}

		// convert back from string to date
		protected Object doParseValue(String formattedString) throws ParseException {
			return dateFormat.parse(formattedString);
		}

	}

}
