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
package org.springframework.webflow.core.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.binding.collection.MapAccessor;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * An immutable parameter map storing String-keyed, String-valued parameters in a backing {@link Map} implementation.
 * This base provides convenient operations for accessing parameters in a typed-manner.
 * 
 * @author Keith Donald
 */
public class LocalParameterMap implements ParameterMap, Serializable {

	private static final DefaultConversionService DEFAULT_CONVERSION_SERVICE = new DefaultConversionService();

	/**
	 * The backing map storing the parameters.
	 */
	private Map parameters;

	/**
	 * A helper for accessing parameters. Marked transient and restored on deserialization.
	 */
	private transient MapAccessor parameterAccessor;

	/**
	 * A helper for converting string parameter values. Marked transient and restored on deserialization.
	 */
	private transient ConversionService conversionService;

	/**
	 * Creates a new parameter map from the provided map.
	 * <p>
	 * It is expected that the contents of the backing map adhere to the parameter map contract; that is, map entries
	 * have string keys, string values, and remain unmodifiable.
	 * @param parameters the contents of this parameter map
	 */
	public LocalParameterMap(Map parameters) {
		this(parameters, DEFAULT_CONVERSION_SERVICE);
	}

	/**
	 * Creates a new parameter map from the provided map.
	 * <p>
	 * It is expected that the contents of the backing map adhere to the parameter map contract; that is, map entries
	 * have string keys, string values, and remain unmodifiable.
	 * @param parameters the contents of this parameter map
	 * @param conversionService a helper for performing type conversion of map entry values
	 */
	public LocalParameterMap(Map parameters, ConversionService conversionService) {
		initParameters(parameters);
		this.conversionService = conversionService;
	}

	public boolean equals(Object o) {
		if (!(o instanceof LocalParameterMap)) {
			return false;
		}
		LocalParameterMap other = (LocalParameterMap) o;
		return parameters.equals(other.parameters);
	}

	public int hashCode() {
		return parameters.hashCode();
	}

	public Map asMap() {
		return Collections.unmodifiableMap(parameterAccessor.asMap());
	}

	public boolean isEmpty() {
		return parameters.isEmpty();
	}

	public int size() {
		return parameters.size();
	}

	public boolean contains(String parameterName) {
		return parameters.containsKey(parameterName);
	}

	public String get(String parameterName) {
		return get(parameterName, (String) null);
	}

	public String get(String parameterName, String defaultValue) {
		if (!parameters.containsKey(parameterName)) {
			return defaultValue;
		}
		Object value = parameters.get(parameterName);
		if (value.getClass().isArray()) {
			parameterAccessor.assertKeyValueInstanceOf(parameterName, value, String[].class);
			String[] array = (String[]) value;
			if (array.length == 0) {
				return null;
			} else {
				Object first = ((String[]) value)[0];
				parameterAccessor.assertKeyValueInstanceOf(parameterName, first, String.class);
				return (String) first;
			}

		} else {
			parameterAccessor.assertKeyValueInstanceOf(parameterName, value, String.class);
			return (String) value;
		}
	}

	public String[] getArray(String parameterName) {
		if (!parameters.containsKey(parameterName)) {
			return null;
		}
		Object value = parameters.get(parameterName);
		if (value.getClass().isArray()) {
			parameterAccessor.assertKeyValueInstanceOf(parameterName, value, String[].class);
			return (String[]) value;
		} else {
			parameterAccessor.assertKeyValueInstanceOf(parameterName, value, String.class);
			return new String[] { (String) value };
		}
	}

	public Object[] getArray(String parameterName, Class targetElementType) throws ConversionExecutionException {
		String[] parameters = getArray(parameterName);
		return parameters != null ? convert(parameters, targetElementType) : null;
	}

	public Object get(String parameterName, Class targetType) throws ConversionExecutionException {
		return get(parameterName, targetType, null);
	}

	public Object get(String parameterName, Class targetType, Object defaultValue) throws ConversionExecutionException {
		if (defaultValue != null) {
			assertAssignableTo(targetType, defaultValue.getClass());
		}
		String parameter = get(parameterName);
		return parameter != null ? convert(parameter, targetType) : defaultValue;
	}

	public String getRequired(String parameterName) throws IllegalArgumentException {
		parameterAccessor.assertContainsKey(parameterName);
		return get(parameterName);
	}

	public String[] getRequiredArray(String parameterName) throws IllegalArgumentException {
		parameterAccessor.assertContainsKey(parameterName);
		return getArray(parameterName);
	}

	public Object[] getRequiredArray(String parameterName, Class targetElementType) throws IllegalArgumentException,
			ConversionExecutionException {
		String[] parameters = getRequiredArray(parameterName);
		return convert(parameters, targetElementType);
	}

	public Object getRequired(String parameterName, Class targetType) throws IllegalArgumentException,
			ConversionExecutionException {
		return convert(getRequired(parameterName), targetType);
	}

	public Number getNumber(String parameterName, Class targetType) throws ConversionExecutionException {
		assertAssignableTo(Number.class, targetType);
		return (Number) get(parameterName, targetType);
	}

	public Number getNumber(String parameterName, Class targetType, Number defaultValue)
			throws ConversionExecutionException {
		assertAssignableTo(Number.class, targetType);
		return (Number) get(parameterName, targetType, defaultValue);
	}

	public Number getRequiredNumber(String parameterName, Class targetType) throws IllegalArgumentException,
			ConversionExecutionException {
		assertAssignableTo(Number.class, targetType);
		return (Number) getRequired(parameterName, targetType);
	}

	public Integer getInteger(String parameterName) throws ConversionExecutionException {
		return (Integer) get(parameterName, Integer.class);
	}

	public Integer getInteger(String parameterName, Integer defaultValue) throws ConversionExecutionException {
		return (Integer) get(parameterName, Integer.class, defaultValue);
	}

	public Integer getRequiredInteger(String parameterName) throws IllegalArgumentException,
			ConversionExecutionException {
		return (Integer) getRequired(parameterName, Integer.class);
	}

	public Long getLong(String parameterName) throws ConversionExecutionException {
		return (Long) get(parameterName, Long.class);
	}

	public Long getLong(String parameterName, Long defaultValue) throws ConversionExecutionException {
		return (Long) get(parameterName, Long.class, defaultValue);
	}

	public Long getRequiredLong(String parameterName) throws IllegalArgumentException, ConversionExecutionException {
		return (Long) getRequired(parameterName, Long.class);
	}

	public Boolean getBoolean(String parameterName) throws ConversionExecutionException {
		return (Boolean) get(parameterName, Boolean.class);
	}

	public Boolean getBoolean(String parameterName, Boolean defaultValue) throws ConversionExecutionException {
		return (Boolean) get(parameterName, Boolean.class, defaultValue);
	}

	public Boolean getRequiredBoolean(String parameterName) throws IllegalArgumentException,
			ConversionExecutionException {
		return (Boolean) getRequired(parameterName, Boolean.class);
	}

	public MultipartFile getMultipartFile(String parameterName) {
		return (MultipartFile) parameterAccessor.get(parameterName, MultipartFile.class);
	}

	public MultipartFile getRequiredMultipartFile(String parameterName) throws IllegalArgumentException {
		return (MultipartFile) parameterAccessor.getRequired(parameterName, MultipartFile.class);
	}

	public AttributeMap asAttributeMap() {
		return new LocalAttributeMap(getMapInternal());
	}

	/**
	 * Initializes this parameter map.
	 * @param parameters the parameters
	 */
	protected void initParameters(Map parameters) {
		this.parameters = parameters;
		parameterAccessor = new MapAccessor(this.parameters);
	}

	/**
	 * Returns the wrapped, modifiable map implementation.
	 */
	protected Map getMapInternal() {
		return parameters;
	}

	// internal helpers

	/**
	 * Convert given String parameter to specified target type.
	 */
	private Object convert(String parameter, Class targetType) throws ConversionExecutionException {
		return conversionService.getConversionExecutor(String.class, targetType).execute(parameter);
	}

	/**
	 * Convert given array of String parameters to specified target type and return the resulting array.
	 */
	private Object[] convert(String[] parameters, Class targetElementType) throws ConversionExecutionException {
		List list = new ArrayList(parameters.length);
		ConversionExecutor converter = conversionService.getConversionExecutor(String.class, targetElementType);
		for (int i = 0; i < parameters.length; i++) {
			list.add(converter.execute(parameters[i]));
		}
		return list.toArray((Object[]) Array.newInstance(targetElementType, parameters.length));
	}

	/**
	 * Make sure clazz is assignable from requiredType.
	 */
	private void assertAssignableTo(Class clazz, Class requiredType) {
		Assert.isTrue(clazz.isAssignableFrom(requiredType), "The provided required type must be assignable to ["
				+ clazz + "]");
	}

	// custom serialization

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		parameterAccessor = new MapAccessor(parameters);
		conversionService = DEFAULT_CONVERSION_SERVICE;
	}

	public String toString() {
		return StylerUtils.style(parameters);
	}
}