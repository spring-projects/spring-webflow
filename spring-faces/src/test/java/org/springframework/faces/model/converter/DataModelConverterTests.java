package org.springframework.faces.model.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.binding.convert.Converter;
import org.springframework.faces.model.SerializableListDataModel;

import junit.framework.TestCase;

public class DataModelConverterTests extends TestCase {

	Converter converter = new DataModelConverter();

	public void testConvertListToListDataModel() {
		List sourceList = new ArrayList();

		DataModel resultModel = (DataModel) converter.convert(sourceList, ListDataModel.class, null);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	public void testConvertListToSerializableListDataModel() {
		List sourceList = new ArrayList();

		DataModel resultModel = (DataModel) converter.convert(sourceList, SerializableListDataModel.class, null);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
		assertTrue(resultModel instanceof Serializable);
	}

	public void testConvertListToSerializableListDataModelNullSource() {
		List sourceList = null;

		DataModel resultModel = (DataModel) converter.convert(sourceList, SerializableListDataModel.class, null);

		assertNotNull(resultModel);
		assertTrue(resultModel instanceof Serializable);
		assertEquals(0, resultModel.getRowCount());
	}
}
