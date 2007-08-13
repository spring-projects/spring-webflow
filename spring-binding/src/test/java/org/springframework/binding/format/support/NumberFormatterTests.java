/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.binding.format.support;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.binding.format.Formatter;
import org.springframework.context.i18n.SimpleLocaleContext;

import junit.framework.TestCase;

/**
 * Unit tests for {@link NumberFormatterTests}.
 * 
 * @author Erwin Vervaet
 */
public class NumberFormatterTests extends TestCase {

	private Locale systemDefaultLocale;

	protected void setUp() throws Exception {
		systemDefaultLocale = Locale.getDefault();
	}

	protected void tearDown() throws Exception {
		// restore default
		Locale.setDefault(systemDefaultLocale);
	}

	public void testParseUsBigDecimalInUs() {
		Locale.setDefault(Locale.US);
		SimpleFormatterFactory formatterFactory = new SimpleFormatterFactory();
		Formatter formatter = formatterFactory.getNumberFormatter(BigDecimal.class);
		assertEquals(new BigDecimal("123.45"), formatter.parseValue("123.45", BigDecimal.class));
	}

	public void testParseUsBigDecimalInGermany() {
		Locale.setDefault(Locale.GERMANY);
		SimpleFormatterFactory formatterFactory = new SimpleFormatterFactory();
		// we're expressing our numbers in US notation!
		formatterFactory.setLocaleContext(new SimpleLocaleContext(Locale.US));
		Formatter formatter = formatterFactory.getNumberFormatter(BigDecimal.class);
		assertEquals(new BigDecimal("123.45"), formatter.parseValue("123.45", BigDecimal.class));
	}

	public void testParseGermanBigDecimalInGermany() {
		Locale.setDefault(Locale.GERMANY);
		SimpleFormatterFactory formatterFactory = new SimpleFormatterFactory();
		Formatter formatter = formatterFactory.getNumberFormatter(BigDecimal.class);
		assertEquals(new BigDecimal("123.45"), formatter.parseValue("123,45", BigDecimal.class));
	}
}
