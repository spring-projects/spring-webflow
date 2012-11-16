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
package org.springframework.faces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.springframework.util.Assert;

/**
 * A simple {@link ListDataModel List-to-JSF-DataModel} adapter that is also {@link Serializable}.
 *
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class SerializableListDataModel<T> extends ListDataModel<T> implements Serializable {

	public SerializableListDataModel() {
		this(new ArrayList<T>());
	}

	/**
	 * Adapt the list to a data model;
	 * @param list the list
	 */
	public SerializableListDataModel(List<T> list) {
		if (list == null) {
			list = new ArrayList<T>();
		}
		setWrappedData(list);
	}

	@SuppressWarnings("unchecked")
	public List<T> getWrappedData() {
		return (List<T>) super.getWrappedData();
	}

	public void setWrappedData(Object data) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		Assert.isInstanceOf(List.class, data, "The data object for " + getClass() + " must be a List");
		super.setWrappedData(data);
	}

	public String toString() {
		return getWrappedData().toString();
	}
}
