package org.springframework.binding.convert.converters;

public class ReverseConverter implements Converter {

	private TwoWayConverter converter;

	public ReverseConverter(TwoWayConverter converter) {
		this.converter = converter;
	}

	public Class getSourceClass() {
		return converter.getTargetClass();
	}

	public Class getTargetClass() {
		return converter.getSourceClass();
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		return converter.convertTargetToSourceClass(source, targetClass);
	}

}
