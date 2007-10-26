package org.springframework.faces.ui;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class DojoClientCurrencyValidator extends DojoAdvisor {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.CurrencyTextBox";

	private static final String[] DOJO_ATTRS_INTERNAL = new String[] { "currency" };

	private static final String[] DOJO_ATTRS;

	static {
		DOJO_ATTRS = new String[DojoAdvisor.DOJO_ATTRS.length + DOJO_ATTRS_INTERNAL.length];
		System.arraycopy(DojoAdvisor.DOJO_ATTRS, 0, DOJO_ATTRS, 0, DojoAdvisor.DOJO_ATTRS.length);
		System.arraycopy(DOJO_ATTRS_INTERNAL, 0, DOJO_ATTRS, DojoAdvisor.DOJO_ATTRS.length, DOJO_ATTRS_INTERNAL.length);
	}

	private String currency;

	public String getCurrency() {
		if (currency != null) {
			return currency;
		}
		ValueExpression exp = getValueExpression("currency");
		return exp != null ? (String) exp.getValue(getFacesContext().getELContext()) : null;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	protected String[] getDojoAttributes() {
		return DOJO_ATTRS;
	}

	public String getDojoComponentType() {
		return DOJO_COMPONENT_TYPE;
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[2];
		values[0] = super.saveState(context);
		values[1] = this.currency;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.currency = (String) values[1];
	}

}
