package org.springframework.binding.convert.converters;

import java.lang.reflect.Array;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;

public class ObjectToArray implements TwoWayConverter {

	private ConversionService conversionService;

	public ObjectToArray(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return Object.class;
	}

	public Class getTargetClass() {
		return Object[].class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (source == null) {
			return null;
		}
		if (source instanceof String) {
			String string = (String) source;
			String[] elements = string.split(",");
			Class componentType = targetClass.getComponentType();
			Object array = Array.newInstance(componentType, elements.length);
			ConversionExecutor converter = conversionService.getConversionExecutor(String.class, componentType);
			for (int i = 0; i < elements.length; i++) {
				String element = elements[i].trim();
				Array.set(array, i, converter.execute(element));
			}
			return array;
		} else {
			Class componentType = targetClass.getComponentType();
			Object array = Array.newInstance(componentType, 1);
			ConversionExecutor converter = conversionService.getConversionExecutor(source.getClass(), componentType);
			Array.set(array, 0, converter.execute(source));
			return array;
		}
	}

	public Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception {
		if (target == null) {
			return null;
		}
		if (String.class.equals(sourceClass)) {
			int length = Array.getLength(target);
			Class componentType = target.getClass().getComponentType();
			ConversionExecutor converter = conversionService.getConversionExecutor(componentType, String.class);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < length; i++) {
				Object value = Array.get(target, i);
				buffer.append(converter.execute(value));
				if (i < length) {
					buffer.append(",");
				}
			}
		} else {
			Object value = Array.get(target, 0);
			Class componentType = target.getClass().getComponentType();
			ConversionExecutor converter = conversionService.getConversionExecutor(componentType, sourceClass);
			return converter.execute(value);
		}
		return null;
	}
}
