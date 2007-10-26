package org.springframework.faces.ui;

public class DojoClientDateValidator extends DojoAdvisor {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.DateTextBox";

	protected String[] getDojoAttributes() {
		return DojoAdvisor.DOJO_ATTRS;
	}

	public String getDojoComponentType() {
		return DOJO_COMPONENT_TYPE;
	}

}
