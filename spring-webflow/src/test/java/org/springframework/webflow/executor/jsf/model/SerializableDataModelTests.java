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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import junit.framework.TestCase;

/**
 * Tests for the serializable JSF data model variants provided by Spring Web Flow.
 * 
 * @author Erwin Vervaet
 */
public class SerializableDataModelTests extends TestCase {

	public void testListDataModelSerializability() throws Exception {
		// from scratch
		ListDataModel ldm = (ListDataModel) writeAndReadBack(new ListDataModel());
		assertNull(ldm.getWrappedData());
		assertEquals(-1, ldm.getRowIndex());
		assertEquals(0, ldm.getDataModelListeners().length);

		// with input list
		ldm = (ListDataModel) writeAndReadBack(new ListDataModel(sampleList()));
		assertEquals(sampleList(), ldm.getWrappedData());
		assertEquals(0, ldm.getRowIndex());
		assertEquals(0, ldm.getDataModelListeners().length);

		// manipulated
		ldm = new ListDataModel(sampleList());
		ldm.setRowIndex(1);
		ldm = (ListDataModel) writeAndReadBack(ldm);
		assertEquals(sampleList(), ldm.getWrappedData());
		assertEquals(1, ldm.getRowIndex());
		assertEquals(0, ldm.getDataModelListeners().length);

		// with listeners
		ldm = new ListDataModel(sampleList());
		ldm.addDataModelListener(new TestDataModelListener("swf"));
		ldm.setRowIndex(1);
		ldm = (ListDataModel) writeAndReadBack(ldm);
		assertEquals(sampleList(), ldm.getWrappedData());
		assertEquals(1, ldm.getRowIndex());
		assertEquals(1, ldm.getDataModelListeners().length);
		assertEquals("swf", ldm.getDataModelListeners()[0].toString());

		// serialization fails with non serializable objects
		try {
			ldm = new ListDataModel(Collections.singletonList(new EventListener() {
			}));
			writeAndReadBack(ldm);
			fail();
		} catch (NotSerializableException e) {
			// expected
		}
	}

	public void testArrayDataModelSerializability() throws Exception {
		// from scratch
		ArrayDataModel adm = (ArrayDataModel) writeAndReadBack(new ArrayDataModel());
		assertNull(adm.getWrappedData());
		assertEquals(-1, adm.getRowIndex());
		assertEquals(0, adm.getDataModelListeners().length);

		// with input array
		adm = (ArrayDataModel) writeAndReadBack(new ArrayDataModel(sampleList().toArray()));
		assertEquals(sampleList(), Arrays.asList((Object[]) adm.getWrappedData()));
		assertEquals(0, adm.getRowIndex());
		assertEquals(0, adm.getDataModelListeners().length);

		// manipulated
		adm = new ArrayDataModel(sampleList().toArray());
		adm.setRowIndex(1);
		adm = (ArrayDataModel) writeAndReadBack(adm);
		assertEquals(sampleList(), Arrays.asList((Object[]) adm.getWrappedData()));
		assertEquals(1, adm.getRowIndex());
		assertEquals(0, adm.getDataModelListeners().length);

		// with listeners
		adm = new ArrayDataModel(sampleList().toArray());
		adm.addDataModelListener(new TestDataModelListener("swf"));
		adm.setRowIndex(1);
		adm = (ArrayDataModel) writeAndReadBack(adm);
		assertEquals(sampleList(), Arrays.asList((Object[]) adm.getWrappedData()));
		assertEquals(1, adm.getRowIndex());
		assertEquals(1, adm.getDataModelListeners().length);
		assertEquals("swf", adm.getDataModelListeners()[0].toString());

		// serialization fails with non serializable objects
		try {
			adm = new ArrayDataModel(new Object[] { new EventListener() {
			} });
			writeAndReadBack(adm);
			fail();
		} catch (NotSerializableException e) {
			// expected
		}
	}

	public void testScalarDataModelSerializability() throws Exception {
		// from scratch
		ScalarDataModel sdm = (ScalarDataModel) writeAndReadBack(new ScalarDataModel());
		assertNull(sdm.getWrappedData());
		assertEquals(-1, sdm.getRowIndex());
		assertEquals(0, sdm.getDataModelListeners().length);

		// with input object
		sdm = (ScalarDataModel) writeAndReadBack(new ScalarDataModel("foobar"));
		assertEquals("foobar", sdm.getWrappedData());
		assertEquals(0, sdm.getRowIndex());
		assertEquals(0, sdm.getDataModelListeners().length);

		// manipulated
		sdm = new ScalarDataModel("foobar");
		sdm.setRowIndex(1);
		sdm = (ScalarDataModel) writeAndReadBack(sdm);
		assertEquals("foobar", sdm.getWrappedData());
		assertEquals(1, sdm.getRowIndex());
		assertEquals(0, sdm.getDataModelListeners().length);

		// with listeners
		sdm = new ScalarDataModel("foobar");
		sdm.addDataModelListener(new TestDataModelListener("swf"));
		sdm.setRowIndex(1);
		sdm = (ScalarDataModel) writeAndReadBack(sdm);
		assertEquals("foobar", sdm.getWrappedData());
		assertEquals(1, sdm.getRowIndex());
		assertEquals(1, sdm.getDataModelListeners().length);
		assertEquals("swf", sdm.getDataModelListeners()[0].toString());

		// serialization fails with non serializable objects
		try {
			sdm = new ScalarDataModel(new EventListener() {
			});
			writeAndReadBack(sdm);
			fail();
		} catch (NotSerializableException e) {
			// expected
		}
	}

	// internal helpers

	private Object writeAndReadBack(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(obj);
		oout.flush();
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectInputStream oin = new ObjectInputStream(bin);
		return oin.readObject();
	}

	private List sampleList() {
		List list = new ArrayList(2);
		list.add("foo");
		list.add("bar");
		return list;
	}

	private static class TestDataModelListener implements DataModelListener, Serializable {

		private String name;

		public TestDataModelListener(String name) {
			this.name = name;
		}

		public void rowSelected(DataModelEvent event) {
		}

		public String toString() {
			return name;
		}
	}
}
