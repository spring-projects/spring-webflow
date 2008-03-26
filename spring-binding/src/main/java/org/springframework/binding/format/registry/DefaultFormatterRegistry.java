package org.springframework.binding.format.registry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.binding.format.formatters.BooleanFormatter;
import org.springframework.binding.format.formatters.DateFormatter;
import org.springframework.binding.format.formatters.NumberFormatter;

public class DefaultFormatterRegistry extends GenericFormatterRegistry {

	/**
	 * A singleton shared instance. Should never be modified.
	 */
	private static DefaultFormatterRegistry SHARED_INSTANCE;

	public DefaultFormatterRegistry() {
		registerDefaultFormatters();
	}

	protected void registerDefaultFormatters() {
		registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		registerFormatter(Long.class, new NumberFormatter(Long.class));
		registerFormatter(Short.class, new NumberFormatter(Short.class));
		registerFormatter(Float.class, new NumberFormatter(Float.class));
		registerFormatter(Double.class, new NumberFormatter(Double.class));
		registerFormatter(Byte.class, new NumberFormatter(Byte.class));
		registerFormatter(BigInteger.class, new NumberFormatter(BigInteger.class));
		registerFormatter(BigDecimal.class, new NumberFormatter(BigDecimal.class));
		registerFormatter(Boolean.class, new BooleanFormatter());
		registerFormatter(Date.class, new DateFormatter());
	}

	/**
	 * Returns the shared {@link DefaultConversionService} instance.
	 */
	public synchronized static FormatterRegistry getSharedInstance() {
		if (SHARED_INSTANCE == null) {
			SHARED_INSTANCE = new DefaultFormatterRegistry();
		}
		return SHARED_INSTANCE;
	}
}
