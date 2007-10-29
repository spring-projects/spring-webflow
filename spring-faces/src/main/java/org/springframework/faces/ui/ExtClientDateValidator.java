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

public class ExtClientDateValidator extends ExtClientTextValidator {

	private static final String EXT_COMPONENT_TYPE = "Ext.form.DateField";

	private static final String[] EXT_ATTRS_INTERNAL = new String[] { "altFormats", "disabledDates",
			"disabledDatesText", "disabledDays", "disabledDaysText", "format", "maxText", "maxValue", "minText",
			"minValue", "triggerClass" };

	protected static final String[] EXT_ATTRS;

	static {
		EXT_ATTRS = new String[ExtClientTextValidator.EXT_ATTRS.length + EXT_ATTRS_INTERNAL.length];
		System.arraycopy(ExtClientTextValidator.EXT_ATTRS, 0, EXT_ATTRS, 0, ExtClientTextValidator.EXT_ATTRS.length);
		System.arraycopy(EXT_ATTRS_INTERNAL, 0, EXT_ATTRS, ExtClientTextValidator.EXT_ATTRS.length,
				EXT_ATTRS_INTERNAL.length);
	}

	/**
	 * Multiple date formats separated by "|" to try when parsing a user input value and it doesn't match the defined
	 * format (defaults to 'm/d/Y|m-d-y|m-d-Y|m/d|m-d|d').
	 */
	private String altFormats;

	/**
	 * An array of "dates" to disable, as strings. These strings will be used to build a dynamic regular expression so
	 * they are very powerful. Some examples:
	 * 
	 * ["03/08/2003", "09/16/2003"] would disable those exact dates ["03/08", "09/16"] would disable those days for
	 * every year ["^03/08"] would only match the beginning (useful if you are using short years) ["03/../2006"] would
	 * disable every day in March 2006 ["^03"] would disable every day in every March
	 * 
	 * In order to support regular expressions, if you are using a date format that has "." in it, you will have to
	 * escape the dot when restricting dates. For example: ["03\\.08\\.03"].
	 */
	private String disabledDates;

	/**
	 * The tooltip text to display when the date falls on a disabled date (defaults to 'Disabled')
	 */
	private String disabledDatesText;

	/**
	 * An array of days to disable, 0 based. For example, [0, 6] disables Sunday and Saturday (defaults to null).
	 */
	private String disabledDays;

	/**
	 * The tooltip to display when the date falls on a disabled day (defaults to 'Disabled')
	 */
	private String disabledDaysText;

	/**
	 * The default date format string which can be overriden for localization support. The format must be valid
	 * according to Date.parseDate (defaults to 'm/d/y').
	 */
	private String format;

	/**
	 * The error text to display when the date in the field is invalid (defaults to '{value} is not a valid date - it
	 * must be in the format {format}').
	 */
	private String invalidText;

	/**
	 * The error text to display when the date in the cell is after maxValue (defaults to 'The date in this field must
	 * be before {maxValue}').
	 */
	private String maxText;

	/**
	 * The maximum allowed date. Can be either a Javascript date object or a string date in a valid format (defaults to
	 * null).
	 */
	private String maxValue;

	/**
	 * The error text to display when the date in the cell is before minValue (defaults to 'The date in this field must
	 * be after {minValue}').
	 */
	private String minText;

	/**
	 * The minimum allowed date. Can be either a Javascript date object or a string date in a valid format (defaults to
	 * null).
	 */
	private String minValue;

	/**
	 * An additional CSS class used to style the trigger button. The trigger will always get the class 'x-form-trigger'
	 * and triggerClass will be appended if specified (defaults to 'x-form-date-trigger' which displays a calendar
	 * icon).
	 */
	private String triggerClass;

	public String getAltFormats() {
		return altFormats;
	}

	public void setAltFormats(String altFormats) {
		this.altFormats = altFormats;
	}

	public String getDisabledDates() {
		return disabledDates;
	}

	public void setDisabledDates(String disabledDates) {
		this.disabledDates = disabledDates;
	}

	public String getDisabledDatesText() {
		if (disabledDatesText != null) {
			return disabledDatesText;
		}
		ValueBinding vb = getValueBinding("disabledDatesText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setDisabledDatesText(String disabledDatesText) {
		this.disabledDatesText = disabledDatesText;
	}

	public String getDisabledDays() {
		return disabledDays;
	}

	public void setDisabledDays(String disabledDays) {
		this.disabledDays = disabledDays;
	}

	public String getDisabledDaysText() {
		if (disabledDaysText != null) {
			return disabledDaysText;
		}
		ValueBinding vb = getValueBinding("disabledDaysText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setDisabledDaysText(String disabledDaysText) {
		this.disabledDaysText = disabledDaysText;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getInvalidText() {
		if (invalidText != null) {
			return invalidText;
		}
		ValueBinding vb = getValueBinding("invalidText");
		return vb != null ? (String) vb.getValue(getFacesContext()) : null;
	}

	public void setInvalidText(String invalidText) {
		this.invalidText = invalidText;
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

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
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

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getTriggerClass() {
		return triggerClass;
	}

	public void setTriggerClass(String triggerClass) {
		this.triggerClass = triggerClass;
	}

	protected String[] getExtAttributes() {
		return EXT_ATTRS;
	}

	public String getExtComponentType() {
		return EXT_COMPONENT_TYPE;
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[13];
		values[0] = super.saveState(context);
		values[1] = altFormats;
		values[2] = disabledDates;
		values[3] = disabledDatesText;
		values[4] = disabledDays;
		values[5] = disabledDaysText;
		values[6] = format;
		values[7] = invalidText;
		values[8] = maxText;
		values[9] = maxValue;
		values[10] = minText;
		values[11] = minValue;
		values[12] = triggerClass;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		altFormats = (String) values[1];
		disabledDates = (String) values[2];
		disabledDatesText = (String) values[3];
		disabledDays = (String) values[4];
		disabledDaysText = (String) values[5];
		format = (String) values[6];
		invalidText = (String) values[7];
		maxText = (String) values[8];
		maxValue = (String) values[9];
		minText = (String) values[10];
		minValue = (String) values[11];
		triggerClass = (String) values[12];
	}
}