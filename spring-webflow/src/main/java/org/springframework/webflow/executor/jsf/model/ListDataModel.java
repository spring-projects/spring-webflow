/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.executor.jsf.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import javax.faces.model.DataModelListener;

/**
 * Serializable variant of JSF's {@link javax.faces.model.ListDataModel}.
 * 
 * @author Erwin Vervaet
 */
public class ListDataModel extends javax.faces.model.ListDataModel implements Externalizable {

	/**
	 * Construct a new {@link ListDataModel} with no specified wrapped data.
	 * 
	 * @see javax.faces.model.ListDataModel#ListDataModel()
	 */
	public ListDataModel() {
	}

	/**
	 * Construct a new {@link ListDataModel} wrapping the specified list.
	 * @param list the list to be wrapped, if any
	 * 
	 * @see javax.faces.model.ListDataModel#ListDataModel(List)
	 */
	public ListDataModel(List list) {
		super(list);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setWrappedData(in.readObject());
		setRowIndex(in.readInt());
		DataModelListener[] listeners = (DataModelListener[]) in.readObject();
		for (int i = 0; i < listeners.length; i++) {
			addDataModelListener(listeners[i]);
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getWrappedData());
		out.writeInt(getRowIndex());
		out.writeObject(getDataModelListeners());
	}
}
