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

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.util.Assert;

/**
 * A simple List-to-JSF-DataModel adapter that is also serializable.
 * 
 * @author Jeremy Grelle
 */
public class SerializableListDataModel<T> extends DataModel<T> implements Serializable {

	private int rowIndex = 0;

	private List<T> data;

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

	public int getRowCount() {
		return data.size();
	}

	public T getRowData() {
		Assert.isTrue(isRowAvailable(), getClass()
				+ " is in an illegal state - no row is available at the current index.");
		return data.get(rowIndex);
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public List<T> getWrappedData() {
		return data;
	}

	public boolean isRowAvailable() {
		return rowIndex >= 0 && rowIndex < data.size();
	}

	public void setRowIndex(int newRowIndex) {
		if (newRowIndex < -1) {
			throw new IllegalArgumentException("Illegal row index for " + getClass() + ": " + newRowIndex);
		}
		int oldRowIndex = rowIndex;
		rowIndex = newRowIndex;
		if (data != null && oldRowIndex != rowIndex) {
			Object row = isRowAvailable() ? getRowData() : null;
			DataModelEvent event = new DataModelEvent(this, rowIndex, row);
			DataModelListener[] listeners = getDataModelListeners();
			for (DataModelListener listener : listeners) {
				listener.rowSelected(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setWrappedData(Object data) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		Assert.isInstanceOf(List.class, data, "The data object for " + getClass() + " must be a List");
		this.data = (List<T>) data;
		int newRowIndex = 0;
		setRowIndex(newRowIndex);
	}

	public String toString() {
		return data.toString();
	}

}
