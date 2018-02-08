package org.springframework.binding.convert.converters;

import org.springframework.util.NumberUtils;

/**
 * A one-way converter that can convert from any JDK-standard Number implementation to any other JDK-standard Number
 * implementation.
 * 
 * Support Number classes include byte, short, integer, float, double, long, big integer, big decimal. This class
 * delegates to {@link NumberUtils#convertNumberToTargetClass(Number, Class)} to perform the conversion.
 * 
 * @see java.lang.Byte
 * @see java.lang.Short
 * @see java.lang.Integer
 * @see java.lang.Long
 * @see java.math.BigInteger
 * @see java.lang.Float
 * @see java.lang.Double
 * @see java.math.BigDecimal
 * 
 * @author Keith Donald
 */
public class NumberToNumber implements Converter {

	public Class<?> getSourceClass() {
		return Number.class;
	}

	public Class<?> getTargetClass() {
		return Number.class;
	}

	@SuppressWarnings("unchecked")
	public Object convertSourceToTargetClass(Object source, Class<?> targetClass) {
		return NumberUtils.convertNumberToTargetClass((Number) source, (Class<? extends Number>) targetClass);
	}
}
