package org.springframework.binding.convert.converters;

import java.lang.reflect.Array;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;

public class ArrayToArray implements Converter {

	private ConversionService conversionService;

	public ArrayToArray(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return Object[].class;
	}

	public Class getTargetClass() {
		return Object[].class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (source == null) {
			return null;
		}
		Class sourceComponentType = source.getClass().getComponentType();
		Class targetComponentType = targetClass.getComponentType();
		int length = Array.getLength(source);
		Object targetArray = Array.newInstance(targetComponentType, length);
		ConversionExecutor converter = conversionService
				.getConversionExecutor(sourceComponentType, targetComponentType);
		for (int i = 0; i < length; i++) {
			Object value = Array.get(source, i);
			Array.set(targetArray, i, converter.execute(value));
		}
		return targetArray;
	}
}
