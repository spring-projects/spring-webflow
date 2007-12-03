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
public class OneSelectionTrackingListDataModel extends SerializableListDataModel implements SelectionAware {

	private List selections = new ArrayList();

	public OneSelectionTrackingListDataModel(List list) {
		super(list);
	}

	public List getSelections() {
		return selections;
	}

	public boolean isCurrentRowSelected() {
		return selections.contains(getRowData());
	}

	public void select(Object rowData) {
		Assert.isTrue(((List) getWrappedData()).contains(rowData),
				"The object to select is not contained in this DataModel.");
		selections.clear();
		selections.add(rowData);
	}

	public void selectAll() {
		if (((List) getWrappedData()).size() > 1) {
			throw new UnsupportedOperationException("This DataModel only allows one selection.");
		}
	}

	public void setSelected(boolean rowSelected) {
		if (rowSelected && !selections.contains(getRowData())) {
			selections.clear();
			selections.add(getRowData());
		} else if (!rowSelected) {
			selections.clear();
		}
	}

	public void setSelections(List selections) {
		Assert.isTrue(selections.size() <= 1, "This DataModel only allows one selection.");
		this.selections = selections;
	}

	public Object getSelectedRow() {
		if (selections.size() == 1) {
			return selections.get(0);
		} else {
			return null;
		}
	}

}
