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
 */
public class SerializableListDataModel extends DataModel implements Serializable {

	private int rowIndex = 0;

	private List data;

	public SerializableListDataModel() {
		this(new ArrayList());
	}

	/**
	 * Adapt the list to a data model;
	 * @param list the list
	 */
	public SerializableListDataModel(List list) {
		if (list == null) {
			list = new ArrayList();
		}
		setWrappedData(list);
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getRowData() {
		Assert.isTrue(isRowAvailable(), getClass()
				+ " is in an illegal state - no row is available at the current index.");
		return data.get(rowIndex);
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public Object getWrappedData() {
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
			for (int i = 0; i < listeners.length; i++) {
				listeners[i].rowSelected(event);
			}
		}
	}

	public void setWrappedData(Object data) {
		if (data == null) {
			data = new ArrayList();
		}
		Assert.isInstanceOf(List.class, data, "The data object for " + getClass() + " must be a List");
		this.data = (List) data;
		int newRowIndex = 0;
		setRowIndex(newRowIndex);
	}

}
