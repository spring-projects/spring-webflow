/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.ui;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class ExtClientNumberValidator extends ExtClientTextValidator {

	private static final String EXT_COMPONENT_TYPE = "Ext.form.NumberField";

	private static final String[] EXT_ATTRS_INTERNAL = new String[] { "allowDecimals", "allowNegative",
			"decimalPrecision", "decimalSeparator", "maxText", "maxValue", "minText", "minValue", "nanText" };

	protected static final String[] EXT_ATTRS;

	static {
		EXT_ATTRS = new String[ExtClientTextValidator.EXT_ATTRS.length + EXT_ATTRS_INTERNAL.length];
		System.arraycopy(ExtClientTextValidator.EXT_ATTRS, 0, EXT_ATTRS, 0, ExtClientTextValidator.EXT_ATTRS.length);
		System.arraycopy(EXT_ATTRS_INTERNAL, 0, EXT_ATTRS, ExtClientTextValidator.EXT_ATTRS.length,
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
