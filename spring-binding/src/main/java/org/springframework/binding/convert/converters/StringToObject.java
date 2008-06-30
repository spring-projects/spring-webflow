package org.springframework.binding.convert.converters;

public abstract class StringToObject implements TwoWayConverter {

	private Class objectClass;

	public StringToObject(Class objectClass) {
		this.objectClass = objectClass;
	}

	public final Class getSourceClass() {
		return String.class;
	}

	public final Class getTargetClass() {
		return objectClass;
	}

	public final Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		String string = (String) source;
		if (string != null && string.length() > 0) {
			return toObject(string, targetClass);
		} else {
			return null;
		}
	}

	public final Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception {
		if (target != null) {
			return toString(target);
		} else {
			return "";
		}
	}

	protected abstract Object toObject(String string, Class targetClass) throws Exception;

	protected abstract String toString(Object object) throws Exception;

}