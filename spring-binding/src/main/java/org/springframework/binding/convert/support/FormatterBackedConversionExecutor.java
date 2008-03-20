package org.springframework.binding.convert.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.format.Formatter;

public class FormatterBackedConversionExecutor implements ConversionExecutor {

	private Formatter formatter;

	private Class targetClass;

	public FormatterBackedConversionExecutor(Formatter formatter, Class targetClass) {
		this.formatter = formatter;
		this.targetClass = targetClass;
	}

	public Object execute(Object source) throws ConversionException {
		return execute(source, null);
	}

	public Object execute(Object source, ConversionContext context) throws ConversionException {
		return formatter.parseValue((String) source);
	}

	public Class getSourceClass() {
		return String.class;
	}

	public Class getTargetClass() {
		return targetClass;
	}

}
