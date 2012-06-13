package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

public class JSFManagedBean {

	String prop1;
	JSFModel model;
	List<String> values = new ArrayList<String>();

	public JSFModel getModel() {
		return this.model;
	}

	public void setModel(JSFModel model) {
		this.model = model;
	}

	public String getProp1() {
		return this.prop1;
	}

	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}

	public void addValue(String value) {
		this.values.add(value);
	}

	public List<String> getValues() {
		return this.values;
	}
}
