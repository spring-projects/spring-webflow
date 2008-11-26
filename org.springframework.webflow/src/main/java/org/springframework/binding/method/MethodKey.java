/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.binding.method;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * A helper for resolving and caching a Java method by reflection.
 * 
 * @author Keith Donald
 */
public class MethodKey implements Serializable {

	/**
	 * The class the method is a member of.
	 */
	private Class declaredType;

	/**
	 * The method name.
	 */
	private String methodName;

	/**
	 * The method's actual parameter types. Could contain null values if the user did not specify a parameter type for
	 * the corresponding parameter
	 */
	private Class[] parameterTypes;

	/**
	 * A cached handle to the resolved method (may be null).
	 */
	private transient Method method;

	/**
	 * Create a new method key.
	 * @param declaredType the class the method is a member of
	 * @param methodName the method name
	 * @param parameterTypes the method's parameter types, or <code>null</code> if the method has no parameters
	 */
	public MethodKey(Class declaredType, String methodName, Class[] parameterTypes) {
		Assert.notNull(declaredType, "The method's declared type is required");
		Assert.notNull(methodName, "The method name is required");
		this.declaredType = declaredType;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Return the class the method is a member of.
	 */
	public Class getDeclaredType() {
		return declaredType;
	}

	/**
	 * Returns the method name.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Returns the method parameter types. Could contain null values if no type was specified for the corresponding
	 * parameter.
	 */
	public Class[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * Returns the keyed method, resolving it if necessary via reflection.
	 */
	public Method getMethod() throws InvalidMethodKeyException {
		if (method == null) {
			method = resolveMethod();
		}
		return method;
	}

	// internal helpers

	/**
	 * Resolve the keyed method.
	 */
	protected Method resolveMethod() throws InvalidMethodKeyException {
		try {
			return declaredType.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			Method method = findMethodConsiderAssignableParameterTypes();
			if (method != null) {
				return method;
			} else {
				throw new InvalidMethodKeyException(this, e);
			}
		}
	}

	/**
	 * Find the keyed method using 'relaxed' typing.
	 */
	protected Method findMethodConsiderAssignableParameterTypes() {
		Method[] candidateMethods = getDeclaredType().getMethods();
		for (int i = 0; i < candidateMethods.length; i++) {
			if (candidateMethods[i].getName().equals(methodName)) {
				// Check if the method has the correct number of parameters.
				Class[] candidateParameterTypes = candidateMethods[i].getParameterTypes();
				if (candidateParameterTypes.length == parameterTypes.length) {
					int numberOfCorrectArguments = 0;
					for (int j = 0; j < candidateParameterTypes.length; j++) {
						// Check if the candidate type is assignable to the sig
						// parameter type.
						Class candidateType = candidateParameterTypes[j];
						Class parameterType = parameterTypes[j];
						if (parameterType != null) {
							if (isAssignable(candidateType, parameterType)) {
								numberOfCorrectArguments++;
							}
						} else {
							// just match on a null param type (effectively 'any')
							numberOfCorrectArguments++;
						}
					}
					if (numberOfCorrectArguments == parameterTypes.length) {
						return candidateMethods[i];
					}
				}
			}
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof MethodKey)) {
			return false;
		}
		MethodKey other = (MethodKey) obj;
		return declaredType.equals(other.declaredType) && methodName.equals(other.methodName)
				&& parameterTypesEqual(other.parameterTypes);
	}

	private boolean parameterTypesEqual(Class[] other) {
		if (parameterTypes == other) {
			return true;
		}
		if (parameterTypes.length != other.length) {
			return false;
		}
		for (int i = 0; i < this.parameterTypes.length; i++) {
			if (!ObjectUtils.nullSafeEquals(parameterTypes[i], other[i])) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		return declaredType.hashCode() + methodName.hashCode() + parameterTypesHash();
	}

	private int parameterTypesHash() {
		if (parameterTypes == null) {
			return 0;
		}
		int hash = 0;
		for (int i = 0; i < parameterTypes.length; i++) {
			Class parameterType = parameterTypes[i];
			if (parameterType != null) {
				hash += parameterTypes[i].hashCode();
			}
		}
		return hash;
	}

	public String toString() {
		return methodName + "(" + parameterTypesString() + ")";
	}

	/**
	 * Convenience method that returns the parameter types describing the signature of the method as a string.
	 */
	private String parameterTypesString() {
		StringBuffer parameterTypesString = new StringBuffer();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i] == null) {
				parameterTypesString.append("<any>");
			} else {
				parameterTypesString.append(ClassUtils.getShortName(parameterTypes[i]));
			}
			if (i < parameterTypes.length - 1) {
				parameterTypesString.append(',');
			}
		}
		return parameterTypesString.toString();
	}

	// internal helpers

	/**
	 * Determine if the given target type is assignable from the given value type, assuming setting by reflection.
	 * Considers primitive wrapper classes as assignable to the corresponding primitive types.
	 * <p>
	 * NOTE: Pulled from ClassUtils in Spring 2.0 for 1.2.8 compatability.
	 * @param targetType the target type
	 * @param valueType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 */
	private static boolean isAssignable(Class targetType, Class valueType) {
		return (targetType.isAssignableFrom(valueType) || targetType.equals(primitiveWrapperTypeMap.get(valueType)));
	}

	/**
	 * Map with primitive wrapper type as key and corresponding primitive type as value, for example: Integer.class ->
	 * int.class.
	 */
	private static final Map primitiveWrapperTypeMap = new HashMap(8);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
	}
}