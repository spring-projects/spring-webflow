/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.core.collection;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.web.multipart.MultipartFile;

/**
 * An interface for accessing parameters in a backing map. Parameters are immutable and have string keys and string
 * values.
 * <p>
 * The accessor methods offered by this class taking a target type argument only need to support conversions to well
 * know types like String, Number subclasses, Boolean and so on.
 * 
 * @author Keith Donald
 */
public interface ParameterMap extends MapAdaptable<String, Object> {

	/**
	 * Is this parameter map empty, with a size of 0?
	 * @return true if empty, false if not
	 */
	boolean isEmpty();

	/**
	 * Returns the number of parameters in this map.
	 * @return the parameter count
	 */
	int size();

	/**
	 * Does the parameter with the provided name exist in this map?
	 * @param parameterName the parameter name
	 * @return true if so, false otherwise
	 */
	boolean contains(String parameterName);

	/**
	 * Get a parameter value, returning <code>null</code> if no value is found.
	 * @param parameterName the parameter name
	 * @return the parameter value
	 */
	String get(String parameterName);

	/**
	 * Get a parameter value, returning the defaultValue if no value is found.
	 * @param parameterName the parameter name
	 * @param defaultValue the default
	 * @return the parameter value
	 */
	String get(String parameterName, String defaultValue);

	/**
	 * Get a multi-valued parameter value, returning <code>null</code> if no value is found. If the parameter is single
	 * valued an array with a single element is returned.
	 * @param parameterName the parameter name
	 * @return the parameter value array
	 */
	String[] getArray(String parameterName);

	/**
	 * Get a multi-valued parameter value, converting each value to the target type or returning <code>null</code> if no
	 * value is found.
	 * @param parameterName the parameter name
	 * @param targetElementType the target type of the array's elements
	 * @return the converterd parameter value array
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T> T[] getArray(String parameterName, Class<T> targetElementType) throws ConversionExecutionException;

	/**
	 * Get a parameter value, converting it from <code>String</code> to the target type.
	 * @param parameterName the name of the parameter
	 * @param targetType the target type of the parameter value
	 * @return the converted parameter value, or null if not found
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T> T get(String parameterName, Class<T> targetType) throws ConversionExecutionException;

	/**
	 * Get a parameter value, converting it from <code>String</code> to the target type or returning the defaultValue if
	 * not found.
	 * @param parameterName name of the parameter to get
	 * @param targetType the target type of the parameter value
	 * @param defaultValue the default value
	 * @return the converted parameter value, or the default if not found
	 * @throws ConversionExecutionException when a value could not be converted
	 */
	<T> T get(String parameterName, Class<T> targetType, T defaultValue) throws ConversionExecutionException;

	/**
	 * Get the value of a required parameter.
	 * @param parameterName the name of the parameter
	 * @return the parameter value
	 * @throws IllegalArgumentException when the parameter is not found
	 */
	String getRequired(String parameterName) throws IllegalArgumentException;

	/**
	 * Get a required multi-valued parameter value.
	 * @param parameterName the name of the parameter
	 * @return the parameter value
	 * @throws IllegalArgumentException when the parameter is not found
	 */
	String[] getRequiredArray(String parameterName) throws IllegalArgumentException;

	/**
	 * Get a required multi-valued parameter value, converting each value to the target type.
	 * @param parameterName the name of the parameter
	 * @return the parameter value
	 * @throws IllegalArgumentException when the parameter is not found
	 * @throws ConversionExecutionException when a value could not be converted
	 */
	<T> T[] getRequiredArray(String parameterName, Class<T> targetElementType) throws IllegalArgumentException,
			ConversionExecutionException;

	/**
	 * Get the value of a required parameter and convert it to the target type.
	 * @param parameterName the name of the parameter
	 * @param targetType the target type of the parameter value
	 * @return the converted parameter value
	 * @throws IllegalArgumentException when the parameter is not found
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T> T getRequired(String parameterName, Class<T> targetType) throws IllegalArgumentException,
			ConversionExecutionException;

	/**
	 * Returns a number parameter value in the map that is of the specified type, returning <code>null</code> if no
	 * value was found.
	 * @param parameterName the parameter name
	 * @param targetType the target number type
	 * @return the number parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T extends Number> T getNumber(String parameterName, Class<T> targetType)
			throws ConversionExecutionException;

	/**
	 * Returns a number parameter value in the map of the specified type, returning the defaultValue if no value was
	 * found.
	 * @param parameterName the parameter name
	 * @param defaultValue the default
	 * @return the number parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T extends Number> T getNumber(String parameterName, Class<T> targetType, T defaultValue)
			throws ConversionExecutionException;

	/**
	 * Returns a number parameter value in the map, throwing an exception if the parameter is not present or could not
	 * be converted.
	 * @param parameterName the parameter name
	 * @return the number parameter value
	 * @throws IllegalArgumentException if the parameter is not present
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	<T extends Number> T getRequiredNumber(String parameterName, Class<T> targetType)
			throws IllegalArgumentException, ConversionExecutionException;

	/**
	 * Returns an integer parameter value in the map, returning <code>null</code> if no value was found.
	 * @param parameterName the parameter name
	 * @return the integer parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Integer getInteger(String parameterName) throws ConversionExecutionException;

	/**
	 * Returns an integer parameter value in the map, returning the defaultValue if no value was found.
	 * @param parameterName the parameter name
	 * @param defaultValue the default
	 * @return the integer parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Integer getInteger(String parameterName, Integer defaultValue) throws ConversionExecutionException;

	/**
	 * Returns an integer parameter value in the map, throwing an exception if the parameter is not present or could not
	 * be converted.
	 * @param parameterName the parameter name
	 * @return the integer parameter value
	 * @throws IllegalArgumentException if the parameter is not present
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Integer getRequiredInteger(String parameterName) throws IllegalArgumentException,
			ConversionExecutionException;

	/**
	 * Returns a long parameter value in the map, returning <code>null</code> if no value was found.
	 * @param parameterName the parameter name
	 * @return the long parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Long getLong(String parameterName) throws ConversionExecutionException;

	/**
	 * Returns a long parameter value in the map, returning the defaultValue if no value was found.
	 * @param parameterName the parameter name
	 * @param defaultValue the default
	 * @return the long parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Long getLong(String parameterName, Long defaultValue) throws ConversionExecutionException;

	/**
	 * Returns a long parameter value in the map, throwing an exception if the parameter is not present or could not be
	 * converted.
	 * @param parameterName the parameter name
	 * @return the long parameter value
	 * @throws IllegalArgumentException if the parameter is not present
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Long getRequiredLong(String parameterName) throws IllegalArgumentException, ConversionExecutionException;

	/**
	 * Returns a boolean parameter value in the map, returning <code>null</code> if no value was found.
	 * @param parameterName the parameter name
	 * @return the long parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Boolean getBoolean(String parameterName) throws ConversionExecutionException;

	/**
	 * Returns a boolean parameter value in the map, returning the defaultValue if no value was found.
	 * @param parameterName the parameter name
	 * @param defaultValue the default
	 * @return the boolean parameter value
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Boolean getBoolean(String parameterName, Boolean defaultValue) throws ConversionExecutionException;

	/**
	 * Returns a boolean parameter value in the map, throwing an exception if the parameter is not present or could not
	 * be converted.
	 * @param parameterName the parameter name
	 * @return the boolean parameter value
	 * @throws IllegalArgumentException if the parameter is not present
	 * @throws ConversionExecutionException when the value could not be converted
	 */
	Boolean getRequiredBoolean(String parameterName) throws IllegalArgumentException,
			ConversionExecutionException;

	/**
	 * Get a multi-part file parameter value, returning <code>null</code> if no value is found.
	 * @param parameterName the parameter name
	 * @return the multipart file
	 */
	MultipartFile getMultipartFile(String parameterName);

	/**
	 * Get the value of a required multipart file parameter.
	 * @param parameterName the name of the parameter
	 * @return the parameter value
	 * @throws IllegalArgumentException when the parameter is not found
	 */
	MultipartFile getRequiredMultipartFile(String parameterName);

	/**
	 * Adapts this parameter map to an {@link AttributeMap}.
	 * @return the underlying map as a unmodifiable attribute map
	 */
	AttributeMap<Object> asAttributeMap();

}
