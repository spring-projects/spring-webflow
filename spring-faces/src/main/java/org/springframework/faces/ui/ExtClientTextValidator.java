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

public class ExtClientTextValidator extends ExtAdvisor {

	private static final String EXT_COMPONENT_TYPE = "Ext.form.TextField";

	private static final String[] EXT_ATTRS_INTERNAL = new String[] { "allowBlank", "blankText", "disableKeyFilter",
			"emptyClass", "emptyText", "grow", "growMax", "growMin", "maskRe", "maxLength", "maxLengthText",
			"minLength", "minLengthText", "regex", "regexText", "selectOnFocus" };

	protected static final String[] EXT_ATTRS;

	static {
		EXT_ATTRS = new String[ExtAdvisor.EXT_ATTRS.length + EXT_ATTRS_INTERNAL.length];
		System.arraycopy(ExtAdvisor.EXT_ATTRS, 0, EXT_ATTRS, 0, ExtAdvisor.EXT_ATTRS.length);
		System.arraycopy(EXT_ATTRS_INTERNAL, 0, EXT_ATTRS, ExtAdvisor.EXT_ATTRS.length, EXT_ATTRS_INTERNAL.length);
	}

	/**
	 * False to validate that the value length > 0 (defaults to true)
	 */
	private Boolean allowBlank;

	/**
	 * Error text to display if the allow blank validation fails (defaults to "This field is required")
	 */
	private String blankText;

	/**
	 * True to disable input keystroke filtering (defaults to false)
	 */
	private Boolean disableKeyFilter;

	/**
	 * The CSS class to apply to an empty field to style the emptyText (defaults to 'x-form-empty-field'). This class is
	 * automatically added and removed as needed depending on the current field value.
	 */
	private String emptyClass;

	/**
	 * The default text to display in an empty field (defaults to null).
	 */
	private String emptyText;

	/**
	 * True if this field should automatically grow and shrink to its content
	 */
	private Boolean grow;

	/**
	 * The maximum width to allow when grow = true (defaults to 800)
	 */
	private Integer growMax;

	/**
	 * The minimum width to allow when grow = true (defaults to 30)
	 */
	private Integer growMin;

	/**
	 * An input mask regular expression that will be used to filter keystrokes that don't match (defaults to null)
	 */
	private String maskRe;

	/**
	 * Maximum input field length allowed (defaults to Number.MAX_VALUE)
	 */
	private Integer maxLength;

	/**
	 * Error text to display if the maximum length validation fails (defaults to "The maximum length for this field is
	 * {maxLength}")
	 */
	private String maxLengthText;

	/**
	 * Minimum input field length required (defaults to 0)
	 */
	private Integer minLength;

	/**
	 * Error text to display if the minimum length validation fails (defaults to "The minimum length for this field is
	 * {minLength}")
	 */
	private String minLengthText;

	/**
	 * A JavaScript RegExp object to be tested against the field value during validation (defaults to null). If
	 * available, this regex will be evaluated only after the basic validators all return true, and will be passed the
	 * current field value. If the test fails, the field will be marked invalid using regexText.
	 */
	private String regex;

	/**
	 * The error text to display if regex is used and the test fails during validation (defaults to "")
	 */
	private String regexText;

	/**
	 * True to automatically select any existing field text when the field receives input focus (defaults to false)
	 */
	private Boolean selectOnFocus;

	public Boolean getAllowBlank() {
		return allowBlank;
	}

	public void setAllowBlank(Boolean allowBlank) {
		this.allowBlank = allowBlank;
	}

	public String getBlankText() {
		if (blankText != null) {
			return blankText;
		}
		ValueBinding vb = getValueBinding("blankText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setBlankText(String blankText) {
		this.blankText = blankText;
	}

	public Boolean getDisableKeyFilter() {
		return disableKeyFilter;
	}

	public void setDisableKeyFilter(Boolean disableKeyFilter) {
		this.disableKeyFilter = disableKeyFilter;
	}

	public String getEmptyClass() {
		return emptyClass;
	}

	public void setEmptyClass(String emptyClass) {
		this.emptyClass = emptyClass;
	}

	public String getEmptyText() {
		if (emptyText != null) {
			return emptyText;
		}
		ValueBinding vb = getValueBinding("emptyText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
	}

	public Boolean getGrow() {
		return grow;
	}

	public void setGrow(Boolean grow) {
		this.grow = grow;
	}

	public Integer getGrowMax() {
		return growMax;
	}

	public void setGrowMax(Integer growMax) {
		this.growMax = growMax;
	}

	public Integer getGrowMin() {
		return growMin;
	}

	public void setGrowMin(Integer growMin) {
		this.growMin = growMin;
	}

	public String getMaskRe() {
		return maskRe;
	}

	public void setMaskRe(String maskRe) {
		this.maskRe = maskRe;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getMaxLengthText() {
		if (maxLengthText != null) {
			return maxLengthText;
		}
		ValueBinding vb = getValueBinding("maxLengthText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setMaxLengthText(String maskLengthText) {
		this.maxLengthText = maskLengthText;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public String getMinLengthText() {
		if (minLengthText != null) {
			return minLengthText;
		}
		ValueBinding vb = getValueBinding("minLengthText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setMinLengthText(String minLengthText) {
		this.minLengthText = minLengthText;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRegexText() {
		if (regexText != null) {
			return regexText;
		}
		ValueBinding vb = getValueBinding("regexText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setRegexText(String regexText) {
		this.regexText = regexText;
	}

	public Boolean getSelectOnFocus() {
		return selectOnFocus;
	}

	public void setSelectOnFocus(Boolean selectOnFocus) {
		this.selectOnFocus = selectOnFocus;
	}

	protected String[] getExtAttributes() {
		return EXT_ATTRS;
	}

	public String getExtComponentType() {
		return EXT_COMPONENT_TYPE;
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[17];
		values[0] = super.saveState(context);
		values[1] = allowBlank;
		values[2] = blankText;
		values[3] = disableKeyFilter;
		values[4] = emptyClass;
		values[5] = emptyText;
		values[6] = grow;
		values[7] = growMax;
		values[8] = growMin;
		values[9] = maskRe;
		values[10] = maxLength;
		values[11] = maxLengthText;
		values[12] = minLength;
		values[13] = minLengthText;
		values[14] = regex;
		values[15] = regexText;
		values[16] = selectOnFocus;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		allowBlank = (Boolean) values[1];
		blankText = (String) values[2];
		disableKeyFilter = (Boolean) values[3];
		emptyClass = (String) values[4];
		emptyText = (String) values[5];
		grow = (Boolean) values[6];
		growMax = (Integer) values[7];
		growMin = (Integer) values[8];
		maskRe = (String) values[9];
		maxLength = (Integer) values[10];
		maxLengthText = (String) values[11];
		minLength = (Integer) values[12];
		minLengthText = (String) values[13];
		regex = (String) values[14];
		regexText = (String) values[15];
		selectOnFocus = (Boolean) values[16];
	}

}
