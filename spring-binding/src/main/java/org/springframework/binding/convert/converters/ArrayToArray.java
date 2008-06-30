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
		if (!sourceComponentType.isPrimitive()) {
			Object[] sourceArray = (Object[]) source;
			Class targetComponentType = targetClass.getComponentType();
			ConversionExecutor executor = conversionService.getConversionExecutor(sourceComponentType,
					targetComponentType);
			Object target = Array.newInstance(targetComponentType, sourceArray.length);
			if (!targetComponentType.isPrimitive()) {
				Object[] targetArray = (Object[]) target;
				for (int i = 0; i < sourceArray.length; i++) {
					targetArray[i] = executor.execute(sourceArray[i]);
				}
				return targetArray;
			} else {
				throw new UnsupportedOperationException("Primitive arrays not yet supported");
			}
		} else {
			throw new UnsupportedOperationException("Primitive arrays not yet supported");
		}
	}
}
