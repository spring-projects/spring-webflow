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

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class for working with attribute and parameter collections used by Spring Web FLow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class CollectionUtils {

	/**
	 * The shared, singleton empty iterator instance.
	 */
	public static final Iterator EMPTY_ITERATOR = new EmptyIterator();

	/**
	 * The shared, singleton empty attribute map instance.
	 */
	public static final AttributeMap EMPTY_ATTRIBUTE_MAP = new LocalAttributeMap(Collections.EMPTY_MAP);

	/**
	 * Private constructor to avoid instantiation.
	 */
	private CollectionUtils() {
	}

	/**
	 * Factory method that adapts an enumeration to an iterator.
	 * @param enumeration the enumeration
	 * @return the iterator
	 */
	public static Iterator toIterator(Enumeration enumeration) {
		return new EnumerationIterator(enumeration);
	}

	/**
	 * Factory method that returns a unmodifiable attribute map with a single entry.
	 * @param attributeName the attribute name
	 * @param attributeValue the attribute value
	 * @return the unmodifiable map with a single element
	 */
	public static AttributeMap singleEntryMap(String attributeName, Object attributeValue) {
		return new LocalAttributeMap(attributeName, attributeValue);
	}

	/**
	 * Add all given objects to given target list. No duplicates will be added. The contains() method of the given
	 * target list will be used to determine whether or not an object is already in the list.
	 * @param target the collection to which to objects will be added
	 * @param objects the objects to add
	 * @return whether or not the target collection changed
	 */
	public static boolean addAllNoDuplicates(List target, Object[] objects) {
		if (objects == null || objects.length == 0) {
			return false;
		} else {
			boolean changed = false;
			for (int i = 0; i < objects.length; i++) {
				if (!target.contains(objects[i])) {
					target.add(objects[i]);
					changed = true;
				}
			}
			return changed;
		}
	}

	/**
	 * Iterator iterating over no elements (hasNext() always returns false).
	 */
	private static class EmptyIterator implements Iterator, Serializable {

		private EmptyIterator() {
		}

		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new UnsupportedOperationException("There are no elements");
		}

		public void remove() {
			throw new UnsupportedOperationException("There are no elements");
		}
	}

	/**
	 * Iterator wrapping an Enumeration.
	 */
	private static class EnumerationIterator implements Iterator {

		private Enumeration enumeration;

		public EnumerationIterator(Enumeration enumeration) {
			this.enumeration = enumeration;
		}

		public boolean hasNext() {
			return enumeration.hasMoreElements();
		}

		public Object next() {
			return enumeration.nextElement();
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Not supported");
		}
	}
}