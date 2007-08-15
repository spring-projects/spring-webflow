package org.springframework.faces.ui;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class ClientNumberValidator extends ClientTextValidator {

    private static final String EXT_COMPONENT_TYPE = "Ext.form.NumberField";

    private static final String[] EXT_ATTRS_INTERNAL = new String[] { "allowDecimals", "allowNegative",
	    "decimalPrecision", "decimalSeparator", "maxText", "maxValue", "minText", "minValue", "nanText" };

    protected static final String[] EXT_ATTRS;

    static {
	EXT_ATTRS = new String[ClientTextValidator.EXT_ATTRS.length + EXT_ATTRS_INTERNAL.length];
	System.arraycopy(ClientTextValidator.EXT_ATTRS, 0, EXT_ATTRS, 0, ClientTextValidator.EXT_ATTRS.length);
	System.arraycopy(EXT_ATTRS_INTERNAL, 0, EXT_ATTRS, ClientTextValidator.EXT_ATTRS.length,
		EXT_ATTRS_INTERNAL.length);
    }

    /**
     * False to disallow decimal values (defaults to true)
     */
    private Boolean allowDecimals;

    /**
     * False to prevent entering a negative sign (defaults to true)
     */
    private Boolean allowNegative;

    /**
     * The maximum precision to display after the decimal separator (defaults to 2)
     */
    private Integer decimalPrecision;

    /**
     * Character(s) to allow as the decimal separator (defaults to '.')
     */
    private String decimalSeparator;

    /**
     * The default CSS class for the field (defaults to "x-form-field x-form-num-field")
     */
    private String fieldClass;

    /**
     * Error text to display if the maximum value validation fails (defaults to "The maximum value for this field is
     * {maxValue}")
     */
    private String maxText;

    /**
     * The maximum allowed value (defaults to Number.MAX_VALUE)
     */
    private Integer maxValue;

    /**
     * Error text to display if the minimum value validation fails (defaults to "The minimum value for this field is
     * {minValue}")
     */
    private String minText;

    /**
     * The minimum allowed value (defaults to Number.NEGATIVE_INFINITY)
     */
    private Integer minValue;

    /**
     * Error text to display if the value is not a valid number. For example, this can happen if a valid character like
     * '.' or '-' is left in the field with no number (defaults to "{value} is not a valid number")
     */
    private String nanText;

    public Boolean getAllowDecimals() {
	return allowDecimals;
    }

    public void setAllowDecimals(Boolean allowDecimals) {
	this.allowDecimals = allowDecimals;
    }

    public Boolean getAllowNegative() {
	return allowNegative;
    }

    public void setAllowNegative(Boolean allowNegative) {
	this.allowNegative = allowNegative;
    }

    public Integer getDecimalPrecision() {
	return decimalPrecision;
    }

    public void setDecimalPrecision(Integer decimalPrecision) {
	this.decimalPrecision = decimalPrecision;
    }

    public String getDecimalSeparator() {
	return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
	this.decimalSeparator = decimalSeparator;
    }

    public String getFieldClass() {
	return fieldClass;
    }

    public void setFieldClass(String fieldClass) {
	this.fieldClass = fieldClass;
    }

    public String getMaxText() {
	if (maxText != null) {
	    return maxText;
	}
	ValueBinding vb = getValueBinding("maxText");
	return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void setMaxText(String maxText) {
	this.maxText = maxText;
    }

    public Integer getMaxValue() {
	return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
	this.maxValue = maxValue;
    }

    public String getMinText() {
	if (minText != null) {
	    return minText;
	}
	ValueBinding vb = getValueBinding("minText");
	return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void setMinText(String minText) {
	this.minText = minText;
    }

    public Integer getMinValue() {
	return minValue;
    }

    public void setMinValue(Integer minValue) {
	this.minValue = minValue;
    }

    public String getNanText() {
	if (nanText != null) {
	    return nanText;
	}
	ValueBinding vb = getValueBinding("nanText");
	return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void setNanText(String nanText) {
	this.nanText = nanText;
    }

    protected String[] getExtAttributes() {
	return EXT_ATTRS;
    }

    public String getExtComponentType() {
	return EXT_COMPONENT_TYPE;
    }

    public Object saveState(FacesContext context) {
	Object[] values = new Object[11];
	values[0] = super.saveState(context);
	values[1] = allowDecimals;
	values[2] = allowNegative;
	values[3] = decimalPrecision;
	values[4] = decimalSeparator;
	values[5] = fieldClass;
	values[6] = maxText;
	values[7] = maxValue;
	values[8] = minText;
	values[9] = minValue;
	values[10] = nanText;
	return values;
    }

    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	allowDecimals = (Boolean) values[1];
	allowNegative = (Boolean) values[2];
	decimalPrecision = (Integer) values[3];
	decimalSeparator = (String) values[4];
	fieldClass = (String) values[5];
	maxText = (String) values[6];
	maxValue = (Integer) values[7];
	minText = (String) values[8];
	minValue = (Integer) values[9];
	nanText = (String) values[10];
    }

}
