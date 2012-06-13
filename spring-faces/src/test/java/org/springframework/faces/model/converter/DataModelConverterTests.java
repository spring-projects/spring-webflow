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

	@SuppressWarnings("unchecked")
	public void testConvertListToDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<Object>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				DataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	@SuppressWarnings("unchecked")
	public void testConvertListToListDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<Object>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				ListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	@SuppressWarnings("unchecked")
	public void testConvertListToSerializableListDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<Object>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
		assertTrue(resultModel instanceof Serializable);
	}

	@SuppressWarnings("unchecked")
	public void testConvertListToSerializableListDataModelNullSource() throws Exception {
		List<Object> sourceList = null;

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertTrue(resultModel instanceof Serializable);
		assertEquals(0, resultModel.getRowCount());
	}
}
