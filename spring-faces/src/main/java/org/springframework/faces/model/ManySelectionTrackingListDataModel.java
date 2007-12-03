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
