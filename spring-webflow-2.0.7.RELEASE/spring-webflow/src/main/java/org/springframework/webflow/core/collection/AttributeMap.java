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

import java.util.Collection;

import org.springframework.binding.collection.MapAdaptable;

/**
 * An immutable interface for accessing attributes in a backing map with string keys.
 * <p>
 * Implementations can optionally support {@link AttributeMapBindingListener listeners} that will be notified when
 * they're bound in or unbound from the map.
 * 
 * @author Keith Donald
 */
public interface AttributeMap extends MapAdaptable {

	/**
	 * Get an attribute value out of this map, returning <code>null</code> if not found.
	 * @param attributeName the attribute name
	 * @return the attribute value
	 */
	public Object get(String attributeName);

	/**
	 * Returns the size of this map.
	 * @return the number of entries in the map
	 */
	public int size();

	/**
	 * Is this attribute map empty with a size of 0?
	 * @return true if empty, false if not
	 */
	public boolean isEmpty();

	/**
	 * Does the attribute with the provided name exist in this map?
	 * @param attributeName the attribute name
	 * @return true if so, false otherwise
	 */
	public boolean contains(String attributeName);

	/**
	 * Does the attribute with the provided name exist in this map and is its value of the specified required type?
	 * @param attributeName the attribute name
	 * @param requiredType the required class of the attribute value
	 * @return true if so, false otherwise
	 * @throws IllegalArgumentException when the value is not of the required type
	 */
	public boolean contains(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Get an attribute value, returning the default value if no value is found.
	 * @param attributeName the name of the attribute
	 * @param defaultValue the default value
	 * @return the attribute value, falling back to the default if no such attribute exists
	 */
	public Object get(String attributeName, Object defaultValue);

	/**
	 * Get an attribute value, asserting the value is of the required type.
	 * @param attributeName the name of the attribute
	 * @param requiredType the required type of the attribute value
	 * @return the attribute value, or null if not found
	 * @throws IllegalArgumentException when the value is not of the required type
	 */
	public Object get(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Get an attribute value, asserting the value is of the required type and returning the default value if not found.
	 * @param attributeName the name of the attribute
	 * @param requiredType the value required type
	 * @param defaultValue the default value
	 * @return the attribute value, or the default if not found
	 * @throws IllegalArgumentException when the value (if found) is not of the required type
	 */
	public Object get(String attributeName, Class requiredType, Object defaultValue) throws IllegalStateException;

	/**
	 * Get the value of a required attribute, throwing an exception of no attribute is found.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalArgumentException when the attribute is not found
	 */
	public Object getRequired(String attributeName) throws IllegalArgumentException;

	/**
	 * Get the value of a required attribute and make sure it is of the required type.
	 * @param attributeName name of the attribute to get
	 * @param requiredType the required type of the attribute value
	 * @return the attribute value
	 * @throws IllegalArgumentException when the attribute is not found or not of the required type
	 */
	public Object getRequired(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns a string attribute value in the map, returning <code>null</code> if no value was found.
	 * @param attributeName the attribute name
	 * @return the string attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a string
	 */
	public String getString(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a string attribute value in the map, returning the default value if no value was found.
	 * @param attributeName the attribute name
	 * @param defaultValue the default
	 * @return the string attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a string
	 */
	public String getString(String attributeName, String defaultValue) throws IllegalArgumentException;

	/**
	 * Returns a string attribute value in the map, throwing an exception if the attribute is not present and of the
	 * correct type.
	 * @param attributeName the attribute name
	 * @return the string attribute value
	 * @throws IllegalArgumentException if the attribute is not present or present but not a string
	 */
	public String getRequiredString(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a collection attribute value in the map.
	 * @param attributeName the attribute name
	 * @return the collection attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a collection
	 */
	public Collection getCollection(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a collection attribute value in the map and make sure it is of the required type.
	 * @param attributeName the attribute name
	 * @param requiredType the required type of the attribute value
	 * @return the collection attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a collection of the required type
	 */
	public Collection getCollection(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns a collection attribute value in the map, throwing an exception if the attribute is not present or not a
	 * collection.
	 * @param attributeName the attribute name
	 * @return the collection attribute value
	 * @throws IllegalArgumentException if the attribute is not present or is present but not a collection
	 */
	public Collection getRequiredCollection(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a collection attribute value in the map, throwing an exception if the attribute is not present or not a
	 * collection of the required type.
	 * @param attributeName the attribute name
	 * @param requiredType the required collection type
	 * @return the collection attribute value
	 * @throws IllegalArgumentException if the attribute is not present or is present but not a collection of the
	 * required type
	 */
	public Collection getRequiredCollection(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns an array attribute value in the map and makes sure it is of the required type.
	 * @param attributeName the attribute name
	 * @param requiredType the required type of the attribute value
	 * @return the array attribute value
	 * @throws IllegalArgumentException if the attribute is present but not an array of the required type
	 */
	public Object[] getArray(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns an array attribute value in the map, throwing an exception if the attribute is not present or not an
	 * array of the required type.
	 * @param attributeName the attribute name
	 * @param requiredType the required array type
	 * @return the collection attribute value
	 * @throws IllegalArgumentException if the attribute is not present or is present but not a array of the required
	 * type
	 */
	public Object[] getRequiredArray(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns a number attribute value in the map that is of the specified type, returning <code>null</code> if no
	 * value was found.
	 * @param attributeName the attribute name
	 * @param requiredType the required number type
	 * @return the number attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a number of the required type
	 */
	public Number getNumber(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns a number attribute value in the map of the specified type, returning the default value if no value was
	 * found.
	 * @param attributeName the attribute name
	 * @param defaultValue the default
	 * @return the number attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a number of the required type
	 */
	public Number getNumber(String attributeName, Class requiredType, Number defaultValue)
			throws IllegalArgumentException;

	/**
	 * Returns a number attribute value in the map, throwing an exception if the attribute is not present and of the
	 * correct type.
	 * @param attributeName the attribute name
	 * @return the number attribute value
	 * @throws IllegalArgumentException if the attribute is not present or present but not a number of the required type
	 */
	public Number getRequiredNumber(String attributeName, Class requiredType) throws IllegalArgumentException;

	/**
	 * Returns an integer attribute value in the map, returning <code>null</code> if no value was found.
	 * @param attributeName the attribute name
	 * @return the integer attribute value
	 * @throws IllegalArgumentException if the attribute is present but not an integer
	 */
	public Integer getInteger(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns an integer attribute value in the map, returning the default value if no value was found.
	 * @param attributeName the attribute name
	 * @param defaultValue the default
	 * @return the integer attribute value
	 * @throws IllegalArgumentException if the attribute is present but not an integer
	 */
	public Integer getInteger(String attributeName, Integer defaultValue) throws IllegalArgumentException;

	/**
	 * Returns an integer attribute value in the map, throwing an exception if the attribute is not present and of the
	 * correct type.
	 * @param attributeName the attribute name
	 * @return the integer attribute value
	 * @throws IllegalArgumentException if the attribute is not present or present but not an integer
	 */
	public Integer getRequiredInteger(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a long attribute value in the map, returning <code>null</code> if no value was found.
	 * @param attributeName the attribute name
	 * @return the long attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a long
	 */
	public Long getLong(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a long attribute value in the map, returning the default value if no value was found.
	 * @param attributeName the attribute name
	 * @param defaultValue the default
	 * @return the long attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a long
	 */
	public Long getLong(String attributeName, Long defaultValue) throws IllegalArgumentException;

	/**
	 * Returns a long attribute value in the map, throwing an exception if the attribute is not present and of the
	 * correct type.
	 * @param attributeName the attribute name
	 * @return the long attribute value
	 * @throws IllegalArgumentException if the attribute is not present or present but not a long
	 */
	public Long getRequiredLong(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a boolean attribute value in the map, returning <code>null</code> if no value was found.
	 * @param attributeName the attribute name
	 * @return the long attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a boolean
	 */
	public Boolean getBoolean(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a boolean attribute value in the map, returning the default value if no value was found.
	 * @param attributeName the attribute name
	 * @param defaultValue the default
	 * @return the boolean attribute value
	 * @throws IllegalArgumentException if the attribute is present but not a boolean
	 */
	public Boolean getBoolean(String attributeName, Boolean defaultValue) throws IllegalArgumentException;

	/**
	 * Returns a boolean attribute value in the map, throwing an exception if the attribute is not present and of the
	 * correct type.
	 * @param attributeName the attribute name
	 * @return the boolean attribute value
	 * @throws IllegalArgumentException if the attribute is not present or present but is not a boolean
	 */
	public Boolean getRequiredBoolean(String attributeName) throws IllegalArgumentException;

	/**
	 * Returns a new attribute map containing the union of this map with the provided map.
	 * @param attributes the map to combine with this map
	 * @return a new, combined map
	 */
	public AttributeMap union(AttributeMap attributes);

}