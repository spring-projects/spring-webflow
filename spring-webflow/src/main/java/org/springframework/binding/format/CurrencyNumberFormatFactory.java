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

/**
 * Produces NumberFormat instances that format currency values.
 * 
 * @see NumberFormat
 * @author Keith Donald
 */
public class CurrencyNumberFormatFactory extends AbstractNumberFormatFactory {
	protected NumberFormat getNumberFormat(Locale locale) {
		return NumberFormat.getCurrencyInstance(locale);
	}
}