/*
 * Copyright 2004-2023 the original author or authors.
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
package org.springframework.faces.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.model.DataModel;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} implementation that tracks the currently selected rows, allowing any number of rows to be
 * selected at one time.
 * 
 * @author Jeremy Grelle
 */
public class ManySelectionTrackingListDataModel<T> extends SerializableListDataModel<T> implements SelectionAware<T> {

	private List<T> selections = new ArrayList<>();

	public ManySelectionTrackingListDataModel() {
		super();
	}

	public ManySelectionTrackingListDataModel(List<T> list) {
		super(list);
	}

	public List<T> getSelections() {
		return this.selections;
	}

	public boolean isCurrentRowSelected() {
		return this.selections.contains(getRowData());
	}

	public void selectAll() {
		this.selections.clear();
		this.selections.addAll(getWrappedData());
	}

	public void setCurrentRowSelected(boolean rowSelected) {
		if (!isRowAvailable()) {
			return;
		}
		if (rowSelected && !this.selections.contains(getRowData())) {
			this.selections.add(getRowData());
		} else if (!rowSelected) {
			this.selections.remove(getRowData());
		}
	}

	public void setSelections(List<T> selections) {
		this.selections = selections;
	}

	public void select(T rowData) {
		Assert.isTrue((getWrappedData()).contains(rowData), "The object to select is not contained in this DataModel.");
		if (!this.selections.contains(rowData)) {
			this.selections.add(rowData);
		}
	}

}
