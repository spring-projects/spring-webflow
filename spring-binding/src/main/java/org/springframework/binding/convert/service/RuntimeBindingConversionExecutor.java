package org.springframework.binding.convert.service;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.util.Assert;

/**
 * A conversion executor that doesn't resolve its converter until its time to perform a conversion.
 * @author Keith Donald
 */
public class RuntimeBindingConversionExecutor implements ConversionExecutor {

	private Class targetClass;

	private ConversionService conversionService;

	/**
	 * Creates a new runtime binding conversion executor.
	 * @param targetClass the target type to convert to
	 * @param conversionService the conversion service to get converters from
	 */
	public RuntimeBindingConversionExecutor(Class targetClass, ConversionService conversionService) {
		Assert.notNull(targetClass, "The target class of the conversion is required");
		Assert.notNull(conversionService, "The conversion service is required");
		this.targetClass = targetClass;
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return null;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RuntimeBindingConversionExecutor)) {
			return false;
		}
		RuntimeBindingConversionExecutor o = (RuntimeBindingConversionExecutor) obj;
		return targetClass.equals(o.targetClass);
	}

	public int hashCode() {
		return targetClass.hashCode();
	}

	public Object execute(Object source) throws ConversionException {
		return execute(source, null);
	}

	public Object execute(Object source, Object context) throws ConversionException {
		return conversionService.getConversionExecutor(source.getClass(), targetClass).execute(source);
	}

}