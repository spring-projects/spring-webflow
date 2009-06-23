package org.springframework.faces.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class TestConverter implements Converter {

	public TestConverter() {
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ((TestValue) value).getStringValue();
	}
}
