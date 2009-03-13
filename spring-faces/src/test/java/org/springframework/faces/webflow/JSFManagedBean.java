package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

public class JSFManagedBean {

	String prop1;
	JSFModel model;
	List values = new ArrayList();

	public JSFModel getModel() {
		return model;
	}

	public void setModel(JSFModel model) {
		this.model = model;
	}

	public String getProp1() {
		return prop1;
	}

	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}

	public void addValue(String value) {
		values.add(value);
	}

	public List getValues() {
		return values;
	}
}
