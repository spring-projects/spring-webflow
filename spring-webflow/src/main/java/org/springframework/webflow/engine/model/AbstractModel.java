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
package org.springframework.webflow.engine.model;

import java.util.Collections;
import java.util.LinkedList;

import org.springframework.util.StringUtils;

/**
 * Contains basic merge functions that can be utilized by other models.
 *
 * @author Scott Andrews
 */
public abstract class AbstractModel implements Model {

	/**
	 * Merge two objects. If the child is null, the parent will be returned. Else the child will be returned.
	 * @param child the child object to merge
	 * @param parent the parent object to merge
	 * @return the merged string
	 */
	protected Object merge(Object child, Object parent) {
		if (child == null) {
			return parent;
		} else {
			return child;
		}
	}

	/**
	 * Merge two strings. If the child is null, the parent will be returned. Else the child will be returned.
	 * @param child the child string to merge
	 * @param parent the parent string to merge
	 * @return the merged string
	 */
	protected String merge(String child, String parent) {
		return (String) merge((Object) child, (Object) parent);
	}

	/**
	 * Merge two model elements. If the child is null, the parent will be returned. Else the parent element will be
	 * merged into the child element with the result returned
	 * @param child the child model element to merge
	 * @param parent the parent model element to merge
	 * @return the merged element model
	 */
	protected Model merge(Model child, Model parent) {
		if (child == null) {
			if (parent == null) {
				return null;
			} else {
				return parent.createCopy();
			}
		} else if (parent == null) {
			return child;
		} else {
			child.merge(parent);
			return child;
		}
	}

	/**
	 * Merge two lists. All child element will be in the merged list. All parent elements not in the child list will be
	 * added. Mergeable elements in both lists will be merged according to that element merge rules. New items are added
	 * to the end of the list
	 * @param child the child list to merge
	 * @param parent the parent list to merge
	 * @return the merged list
	 */
	protected <T extends Model> LinkedList<T> merge(LinkedList<T> child, LinkedList<T> parent) {
		return merge(child, parent, true);
	}

	/**
	 * Merge two lists. All child element will be in the merged list. All parent elements not in the child list will be
	 * added. Mergeable elements in both lists will be merged according to that element merge rules.
	 * @param child the child list to merge
	 * @param parent the parent list to merge
	 * @param addAtEnd if true new items will be added at the end of the list, otherwise the beginning
	 * @return the merged list
	 */
	protected <T extends Model> LinkedList<T> merge(LinkedList<T> child, LinkedList<T> parent, boolean addAtEnd) {
		if (child == null) {
			return copyList(parent);
		}
		if (parent == null) {
			return child;
		}
		if (!addAtEnd) {
			parent = new LinkedList<T>(parent);
			Collections.reverse(parent);
		}
		for (T parentModel : parent) {
			addOrMerge(child, parentModel, addAtEnd);
		}
		return child;
	}

	private <T extends Model> void addOrMerge(LinkedList<T> list, T modelToMerge, boolean addAtEnd) {
		for (T model : list) {
			if (model.isMergeableWith(modelToMerge)) {
				model.merge(modelToMerge);
				return;
			}
		}
		@SuppressWarnings("unchecked")
		T copy = (T) modelToMerge.createCopy();
		if (addAtEnd) {
			list.addLast(copy);
		} else {
			list.addFirst(copy);
		}
	}

	protected Model copy(Model model) {
		if (model == null) {
			return null;
		}
		return model.createCopy();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Model> LinkedList<T> copyList(LinkedList<T> list) {
		if (list == null) {
			return null;
		}
		LinkedList<T> copy = new LinkedList<T>();
		for (T model : list) {
			copy.add((T) model.createCopy());
		}
		return copy;
	}

}
