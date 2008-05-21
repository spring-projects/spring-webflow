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
package org.springframework.faces.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} implementation that tracks the currently selected rows, allowing any number of rows to be
 * selected at one time.
 * 
 * @author Jeremy Grelle
 */
public class ManySelectionTrackingListDataModel extends SerializableListDataModel implements SelectionAware {

	private List selections = new ArrayList();

	public ManySelectionTrackingListDataModel() {
		super();
	}

	public ManySelectionTrackingListDataModel(List list) {
		super(list);
	}

	public List getSelections() {
		return selections;
	}

	public boolean isCurrentRowSelected() {
		return selections.contains(getRowData());
	}

	public void selectAll() {
		selections.clear();
		selections.addAll((List) getWrappedData());
	}

	public void setSelected(boolean rowSelected) {
		if (!isRowAvailable()) {
			return;
		}
		if (rowSelected && !selections.contains(getRowData())) {
			selections.add(getRowData());
		} else {
			selections.remove(getRowData());
		}
	}

	public void setSelections(List selections) {
		this.selections = selections;
	}

	public void select(Object rowData) {
		Assert.isTrue(((List) getWrappedData()).contains(rowData),
				"The object to select is not contained in this DataModel.");
		if (!selections.contains(rowData)) {
			selections.add(rowData);
		}
	}

}
