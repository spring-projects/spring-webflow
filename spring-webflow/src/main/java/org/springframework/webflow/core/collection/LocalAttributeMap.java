/*
 * Copyright 2004-2012 the original author or authors.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.collection.MapAccessor;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.Assert;

/**
 * A generic, mutable attribute map with string keys.
 * 
 * @author Keith Donald
 */
public class LocalAttributeMap<V> implements MutableAttributeMap<V>, Serializable {

	/**
	 * The backing map storing the attributes.
	 */
	private Map<String, V> attributes;

	/**
	 * A helper for accessing attributes. Marked transient and restored on deserialization.
	 */
	private transient MapAccessor<String, V> attributeAccessor;

	/**
	 * Creates a new attribute map, initially empty.
	 */
	public LocalAttributeMap() {
		initAttributes(createTargetMap());
	}

	/**
	 * Creates a new attribute map, initially empty.
	 * @param size the initial size
	 * @param loadFactor the load factor
	 */
	public LocalAttributeMap(int size, int loadFactor) {
		initAttributes(createTargetMap(size, loadFactor));
	}

	/**
	 * Creates a new attribute map with a single entry.
	 */
	public LocalAttributeMap(String attributeName, V attributeValue) {
		initAttributes(createTargetMap(1, 1));
		put(attributeName, attributeValue);
	}

	/**
	 * Creates a new attribute map wrapping the specified map.
	 */
	public LocalAttributeMap(Map<String, V> map) {
		Assert.notNull(map, "The target map is required");
		initAttributes(map);
	}

	// implementing attribute map

	public Map<String, V> asMap() {
		return attributeAccessor.asMap();
	}

	public int size() {
		return attributes.size();
	}

	public V get(String attributeName) {
		return attributes.get(attributeName);
	}

	public boolean isEmpty() {
		return attributes.isEmpty();
	}

	public boolean contains(String attributeName) {
		return attributes.containsKey(attributeName);
	}

	public boolean contains(String attributeName, Class<? extends V> requiredType) throws IllegalArgumentException {
		return attributeAccessor.containsKey(attributeName, requiredType);
	}

	public V get(String attributeName, V defaultValue) {
		return attributeAccessor.get(attributeName, defaultValue);
	}

	public <T extends V> T get(String attributeName, Class<T> requiredType) throws IllegalArgumentException {
		return attributeAccessor.get(attributeName, requiredType);
	}

	public <T extends V> T get(String attributeName, Class<T> requiredType, T defaultValue)
			throws IllegalStateException {
		return attributeAccessor.get(attributeName, requiredType, defaultValue);
	}

	public V getRequired(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequired(attributeName);
	}

	public <T extends V> T getRequired(String attributeName, Class<T> requiredType) throws IllegalArgumentException {
		return attributeAccessor.getRequired(attributeName, requiredType);
	}

	public String getString(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getString(attributeName);
	}

	public String getString(String attributeName, String defaultValue) throws IllegalArgumentException {
		return attributeAccessor.getString(attributeName, defaultValue);
	}

	public String getRequiredString(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequiredString(attributeName);
	}

	public Collection<V> getCollection(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getCollection(attributeName);
	}

	public <T extends Collection<V>> T getCollection(String attributeName, Class<T> requiredType)
			throws IllegalArgumentException {
		return attributeAccessor.getCollection(attributeName, requiredType);
	}

	public Collection<V> getRequiredCollection(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequiredCollection(attributeName);
	}

	public <T extends Collection<V>> T getRequiredCollection(String attributeName, Class<T> requiredType)
			throws IllegalArgumentException {
		return attributeAccessor.getRequiredCollection(attributeName, requiredType);
	}

	public <T extends V> T[] getArray(String attributeName, Class<? extends T[]> requiredType)
			throws IllegalArgumentException {
		return attributeAccessor.getArray(attributeName, requiredType);
	}

	public <T extends V> T[] getRequiredArray(String attributeName, Class<? extends T[]> requiredType)
			throws IllegalArgumentException {
		return attributeAccessor.getRequiredArray(attributeName, requiredType);
	}

	public <T extends Number> T getNumber(String attributeName, Class<T> requiredType) throws IllegalArgumentException {
		return attributeAccessor.getNumber(attributeName, requiredType);
	}

	public <T extends Number> T getNumber(String attributeName, Class<T> requiredType, T defaultValue)
			throws IllegalArgumentException {
		return attributeAccessor.getNumber(attributeName, requiredType, defaultValue);
	}

	public <T extends Number> T getRequiredNumber(String attributeName, Class<T> requiredType)
			throws IllegalArgumentException {
		return attributeAccessor.getRequiredNumber(attributeName, requiredType);
	}

	public Integer getInteger(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getInteger(attributeName);
	}

	public Integer getInteger(String attributeName, Integer defaultValue) throws IllegalArgumentException {
		return attributeAccessor.getInteger(attributeName, defaultValue);
	}

	public Integer getRequiredInteger(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequiredInteger(attributeName);
	}

	public Long getLong(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getLong(attributeName);
	}

	public Long getLong(String attributeName, Long defaultValue) throws IllegalArgumentException {
		return attributeAccessor.getLong(attributeName, defaultValue);
	}

	public Long getRequiredLong(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequiredLong(attributeName);
	}

	public Boolean getBoolean(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getBoolean(attributeName);
	}

	public Boolean getBoolean(String attributeName, Boolean defaultValue) throws IllegalArgumentException {
		return attributeAccessor.getBoolean(attributeName, defaultValue);
	}

	public Boolean getRequiredBoolean(String attributeName) throws IllegalArgumentException {
		return attributeAccessor.getRequiredBoolean(attributeName);
	}

	public AttributeMap<V> union(AttributeMap<? extends V> attributes) {
		if (attributes == null) {
			return new LocalAttributeMap<>(getMapInternal());
		} else {
			Map<String, V> map = createTargetMap();
			map.putAll(getMapInternal());
			map.putAll(attributes.asMap());
			return new LocalAttributeMap<>(map);
		}
	}

	// implementing MutableAttributeMap

	public V put(String attributeName, V attributeValue) {
		return getMapInternal().put(attributeName, attributeValue);
	}

	public MutableAttributeMap<V> putAll(AttributeMap<? extends V> attributes) {
		if (attributes == null) {
			return this;
		}
		getMapInternal().putAll(attributes.asMap());
		return this;
	}

	public MutableAttributeMap<V> removeAll(MutableAttributeMap<? extends V> attributes) {
		if (attributes == null) {
			return this;
		}
		Map<String, V> internal = getMapInternal();
		for (String attribute : attributes.asMap().keySet()) {
			internal.remove(attribute);
		}
		return this;
	}

	public Object remove(String attributeName) {
		return getMapInternal().remove(attributeName);
	}

	public Object extract(String attributeName) {
		Map<String, V> map = getMapInternal();
		if (map.containsKey(attributeName)) {
			Object value = map.get(attributeName);
			map.remove(attributeName);
			return value;
		} else {
			return null;
		}
	}

	public MutableAttributeMap<V> clear() throws UnsupportedOperationException {
		getMapInternal().clear();
		return this;
	}

	public MutableAttributeMap<V> replaceWith(AttributeMap<? extends V> attributes)
			throws UnsupportedOperationException {
		clear();
		putAll(attributes);
		return this;
	}

	// helpers for subclasses

	/**
	 * Initializes this attribute map.
	 * @param attributes the attributes
	 */
	protected void initAttributes(Map<String, V> attributes) {
		this.attributes = attributes;
		attributeAccessor = new MapAccessor<>(this.attributes);
	}

	/**
	 * Returns the wrapped, modifiable map implementation.
	 */
	protected Map<String, V> getMapInternal() {
		return attributes;
	}

	// helpers

	/**
	 * Factory method that returns the target map storing the data in this attribute map.
	 * @return the target map
	 */
	protected Map<String, V> createTargetMap() {
		return new HashMap<>();
	}

	/**
	 * Factory method that returns the target map storing the data in this attribute map.
	 * @param size the initial size of the map
	 * @param loadFactor the load factor
	 * @return the target map
	 */
	protected Map<String, V> createTargetMap(int size, int loadFactor) {
		return new HashMap<>(size, loadFactor);
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof LocalAttributeMap)) {
			return false;
		}
		LocalAttributeMap<V> other = (LocalAttributeMap<V>) o;
		return getMapInternal().equals(other.getMapInternal());
	}

	public int hashCode() {
		return getMapInternal().hashCode();
	}

	// custom serialization

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		attributeAccessor = new MapAccessor<>(attributes);
	}

	public String toString() {
		return StylerUtils.style(attributes);
	}
}
