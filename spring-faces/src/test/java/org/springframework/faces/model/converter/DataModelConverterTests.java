package org.springframework.faces.model.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import junit.framework.TestCase;

import org.springframework.binding.convert.converters.Converter;
import org.springframework.faces.model.SerializableListDataModel;

public class DataModelConverterTests extends TestCase {

	Converter converter = new DataModelConverter();

	public void testConvertListToDataModel() throws Exception {
		List sourceList = new ArrayList();

		DataModel resultModel = (DataModel) converter.convertSourceToTargetClass(sourceList, DataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	public void testConvertListToListDataModel() throws Exception {
		List sourceList = new ArrayList();

		DataModel resultModel = (DataModel) converter.convertSourceToTargetClass(sourceList, ListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	public void testConvertListToSerializableListDataModel() throws Exception {
		List sourceList = new ArrayList();

		DataModel resultModel = (DataModel) converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
		assertTrue(resultModel instanceof Serializable);
	}

	public void testConvertListToSerializableListDataModelNullSource() throws Exception {
		List sourceList = null;

		DataModel resultModel = (DataModel) converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertTrue(resultModel instanceof Serializable);
		assertEquals(0, resultModel.getRowCount());
	}
}
