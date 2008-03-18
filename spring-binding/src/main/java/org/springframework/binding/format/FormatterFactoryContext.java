package org.springframework.binding.format;

import java.util.Locale;

/**
 * A context for creating a formatter instance.
 * 
 * @author Keith Donald
 */
public interface FormatterFactoryContext {

	/**
	 * The current locale.
	 */
	public Locale getLocale();

	/**
	 * The type of object of which formatting is desired.
	 */
	public Class getFormattedClass();
}
