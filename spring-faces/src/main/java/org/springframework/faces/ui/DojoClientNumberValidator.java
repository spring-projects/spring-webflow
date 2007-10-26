package org.springframework.faces.ui;

public class DojoClientNumberValidator extends DojoAdvisor {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.NumberTextBox";

	protected String[] getDojoAttributes() {
		return DojoAdvisor.DOJO_ATTRS;
	}

	public String getDojoComponentType() {
		return DOJO_COMPONENT_TYPE;
	}

}
