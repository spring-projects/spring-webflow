/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.format;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Base class suitable for subclassing by most {@link NumberFormatFactory} implementations.
 * 
 * @author Keith Donald
 */
public abstract class AbstractNumberFormatFactory implements NumberFormatFactory {

	private Locale locale;

	/**
	 * The locale to use in formatting number values. If null, the locale associated with the current thread is used.
	 * @see LocaleContextHolder#getLocale()
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale to use in formatting number values.
	 * @param locale the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public final NumberFormat getNumberFormat() {
		Locale locale = determineLocale(this.locale);
		return getNumberFormat(locale);
	}

	/**
	 * Subclasses should override to create the new NumberFormat instance.
	 * @param locale the locale to use
	 * @return the number format
	 */
	protected abstract NumberFormat getNumberFormat(Locale locale);

	// internal helpers

	private Locale determineLocale(Locale locale) {
		return locale != null ? locale : LocaleContextHolder.getLocale();
	}
}