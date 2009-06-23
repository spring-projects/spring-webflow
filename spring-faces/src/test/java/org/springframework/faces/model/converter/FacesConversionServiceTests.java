package org.springframework.faces.model.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionExecutor;

public class FacesConversionServiceTests extends TestCase {
	private FacesConversionService service;

	protected void setUp() throws Exception {
		service = new FacesConversionService();
	}

	public void testGetAbstractType() {
		ConversionExecutor executor = service.getConversionExecutor(List.class, DataModel.class);
		ArrayList list = new ArrayList();
		list.add("foo");
		executor.execute(list);
	}

}
