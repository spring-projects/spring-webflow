package org.springframework.faces.ui;

public class DojoClientTextValidator extends DojoAdvisor {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.ValidationTextBox";

	protected String[] getDojoAttributes() {
		return DojoAdvisor.DOJO_ATTRS;
	}

	public String getDojoComponentType() {
		return DOJO_COMPONENT_TYPE;
	}

}
