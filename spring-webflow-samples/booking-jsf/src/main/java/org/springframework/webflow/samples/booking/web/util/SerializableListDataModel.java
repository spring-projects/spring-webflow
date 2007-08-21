package org.springframework.webflow.samples.booking.web.util;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

public class SerializableListDataModel extends DataModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private int _rowIndex = -1;

    private List _data;

    public SerializableListDataModel() {
	super();
    }

    public SerializableListDataModel(List list) {
	if (list == null)
	    throw new NullPointerException("list");
	setWrappedData(list);
    }

    public int getRowCount() {
	if (_data == null) {
	    return -1;
	}
	return _data.size();
    }

    public Object getRowData() {
	if (_data == null) {
	    return null;
	}
	if (!isRowAvailable()) {
	    throw new IllegalArgumentException("row is unavailable");
	}
	return _data.get(_rowIndex);
    }

    public int getRowIndex() {
	return _rowIndex;
    }

    public Object getWrappedData() {
	return _data;
    }

    public boolean isRowAvailable() {
	if (_data == null) {
	    return false;
	}
	return _rowIndex >= 0 && _rowIndex < _data.size();
    }

    public void setRowIndex(int rowIndex) {
	if (rowIndex < -1) {
	    throw new IllegalArgumentException("illegal rowIndex " + rowIndex);
	}
	int oldRowIndex = _rowIndex;
	_rowIndex = rowIndex;
	if (_data != null && oldRowIndex != _rowIndex) {
	    Object data = isRowAvailable() ? getRowData() : null;
	    DataModelEvent event = new DataModelEvent(this, _rowIndex, data);
	    DataModelListener[] listeners = getDataModelListeners();
	    for (int i = 0; i < listeners.length; i++) {
		listeners[i].rowSelected(event);
	    }
	}
    }

    public void setWrappedData(Object data) {
	_data = (List) data;
	int rowIndex = _data != null ? 0 : -1;
	setRowIndex(rowIndex);
    }

}
