package org.springframework.faces.model.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import org.junit.jupiter.api.Test;

import org.springframework.binding.convert.converters.Converter;
import org.springframework.faces.model.SerializableListDataModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataModelConverterTests {

	Converter converter = new DataModelConverter();

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertListToDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				DataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertListToListDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				ListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertListToSerializableListDataModel() throws Exception {
		List<Object> sourceList = new ArrayList<>();

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertSame(sourceList, resultModel.getWrappedData());
		assertTrue(resultModel instanceof Serializable);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertListToSerializableListDataModelNullSource() throws Exception {
		List<Object> sourceList = null;

		DataModel<Object> resultModel = (DataModel<Object>) this.converter.convertSourceToTargetClass(sourceList,
				SerializableListDataModel.class);

		assertNotNull(resultModel);
		assertTrue(resultModel instanceof Serializable);
		assertEquals(0, resultModel.getRowCount());
	}
}
