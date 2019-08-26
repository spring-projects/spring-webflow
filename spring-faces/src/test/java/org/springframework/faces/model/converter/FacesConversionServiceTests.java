package org.springframework.faces.model.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.ConversionExecutor;

public class FacesConversionServiceTests {
	private FacesConversionService service;

	@BeforeEach
	public void setUp() throws Exception {
		this.service = new FacesConversionService();
	}

	@Test
	public void testGetAbstractType() {
		ConversionExecutor executor = this.service.getConversionExecutor(List.class, DataModel.class);
		ArrayList<Object> list = new ArrayList<>();
		list.add("foo");
		executor.execute(list);
	}

}
