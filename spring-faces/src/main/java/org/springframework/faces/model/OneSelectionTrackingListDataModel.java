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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} implementation that tracks the currently selected row, allowing only one selection at a time.
 *
 * @author Jeremy Grelle
 */
@SuppressWarnings("serial")
public class OneSelectionTrackingListDataModel<T> extends SerializableListDataModel<T> implements SelectionAware<T> {

	/**
	 * The list of currently selected row data objects.
	 */
	private List<T> selections = new ArrayList<>();

	public OneSelectionTrackingListDataModel() {
		super();
	}

	public OneSelectionTrackingListDataModel(List<T> list) {
		super(list);
	}

	public List<T> getSelections() {
		return this.selections;
	}

	public boolean isCurrentRowSelected() {
		return this.selections.contains(getRowData());
	}

	public void select(T rowData) {
		Assert.isTrue((getWrappedData()).contains(rowData), "The object to select is not contained in this DataModel.");
		this.selections.clear();
		this.selections.add(rowData);
	}

	public void selectAll() {
		if ((getWrappedData()).size() > 1) {
			throw new UnsupportedOperationException("This DataModel only allows one selection.");
		}
	}

	public void setCurrentRowSelected(boolean rowSelected) {
		if (!isRowAvailable()) {
			return;
		}

		if (!rowSelected) {
			this.selections.remove(getRowData());
		} else if (rowSelected && !this.selections.contains(getRowData())) {
			this.selections.clear();
			this.selections.add(getRowData());
		}
	}

	public void setSelections(List<T> selections) {
		Assert.isTrue(selections.size() <= 1, "This DataModel only allows one selection.");
		this.selections = selections;
	}

	public Object getSelectedRow() {
		if (this.selections.size() == 1) {
			return this.selections.get(0);
		} else {
			return null;
		}
	}

}
