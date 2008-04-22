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

/**
 * An interface for accessing and modifying attributes in a backing map with string keys.
 * <p>
 * Implementations can optionally support {@link AttributeMapBindingListener listeners} that will be notified when
 * they're bound in or unbound from the map.
 * 
 * @author Keith Donald
 */
public interface MutableAttributeMap extends AttributeMap {

	/**
	 * Put the attribute into this map.
	 * <p>
	 * If the attribute value is an {@link AttributeMapBindingListener} this map will publish
	 * {@link AttributeMapBindingEvent binding events} such as on "bind" and "unbind" if supported.
	 * <p>
	 * <b>Note</b>: not all <code>MutableAttributeMap</code> implementations support this.
	 * @param attributeName the attribute name
	 * @param attributeValue the attribute value
	 * @return the previous value of the attribute, or <code>null</code> of there was no previous value
	 */
	public Object put(String attributeName, Object attributeValue);

	/**
	 * Put all the attributes into this map.
	 * @param attributes the attributes to put into this map
	 * @return this, to support call chaining
	 */
	public MutableAttributeMap putAll(AttributeMap attributes);

	/**
	 * Remove all attributes in the map provided from this map.
	 * @param attributes the attributes to remove from this map
	 * @return this, to support call chaining
	 */
	public MutableAttributeMap removeAll(MutableAttributeMap attributes);

	/**
	 * Remove an attribute from this map.
	 * @param attributeName the name of the attribute to remove
	 * @return previous value associated with specified attribute name, or <tt>null</tt> if there was no mapping for
	 * the name
	 */
	public Object remove(String attributeName);

	/**
	 * Extract an attribute from this map, getting it and removing it in a single operation.
	 * @param attributeName the attribute name
	 * @return the value of the attribute, or <code>null</code> of there was no value
	 */
	public Object extract(String attributeName);

	/**
	 * Remove all attributes in this map.
	 * @return this, to support call chaining
	 */
	public MutableAttributeMap clear();

	/**
	 * Replace the contents of this attribute map with the contents of the provided collection.
	 * @param attributes the attribute collection
	 * @return this, to support call chaining
	 */
	public MutableAttributeMap replaceWith(AttributeMap attributes) throws UnsupportedOperationException;

}