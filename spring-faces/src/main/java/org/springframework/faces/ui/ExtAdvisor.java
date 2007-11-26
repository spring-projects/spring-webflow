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

public abstract class ExtAdvisor extends ExtJsComponent {

	protected static final String[] EXT_ATTRS = new String[] { "cls", "disableClass", "disabled", "fieldClass",
			"focusClass", "hideMode", "invalidClass", "invalidText", "msgDisplay", "readOnly", "validateOnBlur",
			"validationDelay", "validationEvent", "width" };

	/**
	 * A CSS class to apply to the field's underlying element.
	 */
	private String cls;

	/**
	 * CSS class added to the component when it is disabled (defaults to "x-item-disabled").
	 */
	private String disableClass;

	/**
	 * True to disable the field (defaults to false).
	 */
	private Boolean disabled;

	/**
	 * The default CSS class for the field (defaults to "x-form-field")
	 */
	private String fieldClass;

	/**
	 * The CSS class to use when the field receives focus (defaults to "x-form-focus")
	 */
	private String focusClass;

	/**
	 * How this component should hidden. Supported values are "visibility" (css visibility), "offsets" (negative offset
	 * position) and "display" (css display) - defaults to "display".
	 */
	private String hideMode;

	/**
	 * The CSS class to use when marking a field invalid (defaults to "x-form-invalid")
	 */
	private String invalidClass;

	/**
	 * The error text to use when marking a field invalid and no message is provided (defaults to "The value in this
	 * field is invalid")
	 */
	private String invalidText;

	/**
	 * The CSS class to be applied to the message div when displaying validation messages
	 */
	private String msgClass;

	/**
	 * The 'display' style to be applied to the message div when displaying validation messages.
	 */
	private String msgDisplay;

	/**
	 * True to mark the field as readOnly in HTML (defaults to false) -- Note: this only sets the element's readOnly DOM
	 * attribute.
	 */
	private Boolean readOnly;

	/**
	 * Whether the field should validate when it loses focus (defaults to true).
	 */
	private Boolean validateOnBlur;

	/**
	 * The length of time in milliseconds after user input begins until validation is initiated (defaults to 250)
	 */
	private Integer validationDelay;

	/**
	 * The event that should initiate field validation. Set to false to disable automatic validation (defaults to
	 * "keyup").
	 */
	private String validationEvent;

	/**
	 * The width to be applied to the field
	 */
	private Integer width;

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getDisableClass() {
		return disableClass;
	}

	public void setDisableClass(String disableClass) {
		this.disableClass = disableClass;
	}

	public Boolean getDisabled() {
		if (disabled != null) {
			return disabled;
		}
		ValueBinding exp = getValueBinding("disabled");
		return exp != null ? (Boolean) exp.getValue(getFacesContext()) : null;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(String fieldClass) {
		this.fieldClass = fieldClass;
	}

	public String getFocusClass() {
		return focusClass;
	}

	public void setFocusClass(String focusClass) {
		this.focusClass = focusClass;
	}

	public String getHideMode() {
		return hideMode;
	}

	public void setHideMode(String hideMode) {
		this.hideMode = hideMode;
	}

	public String getInvalidClass() {
		return invalidClass;
	}

	public void setInvalidClass(String invalidClass) {
		this.invalidClass = invalidClass;
	}

	public String getInvalidText() {
		if (invalidText != null) {
			return invalidText;
		}
		ValueBinding exp = getValueBinding("invalidText");
		return exp != null ? (String) exp.getValue(getFacesContext()) : null;
	}

	public void setInvalidText(String invalidText) {
		this.invalidText = invalidText;
	}

	public String getMsgClass() {
		if (msgClass != null) {
			return msgClass;
		}
		ValueBinding exp = getValueBinding("msgClass");
		return exp != null ? (String) exp.getValue(getFacesContext()) : null;
	}

	public void setMsgClass(String msgClass) {
		this.msgClass = msgClass;
	}

	public String getMsgDisplay() {
		return msgDisplay;
	}

	public void setMsgDisplay(String msgDisplay) {
		this.msgDisplay = msgDisplay;
	}

	public Boolean getReadOnly() {
		if (readOnly != null) {
			return readOnly;
		}
		ValueBinding exp = getValueBinding("readOnly");
		return exp != null ? (Boolean) exp.getValue(getFacesContext()) : null;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getValidateOnBlur() {
		return validateOnBlur;
	}

	public void setValidateOnBlur(Boolean validateOnBlur) {
		this.validateOnBlur = validateOnBlur;
	}

	public Integer getValidationDelay() {
		return validationDelay;
	}

	public void setValidationDelay(Integer validationDelay) {
		this.validationDelay = validationDelay;
	}

	public String getValidationEvent() {
		return validationEvent;
	}

	public void setValidationEvent(String validationEvent) {
		this.validationEvent = validationEvent;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	protected abstract String[] getExtAttributes();

	public abstract String getExtComponentType();

	public Object saveState(FacesContext context) {
		Object[] values = new Object[16];
		values[0] = super.saveState(context);
		values[1] = cls;
		values[2] = disableClass;
		values[3] = disabled;
		values[4] = fieldClass;
		values[5] = focusClass;
		values[6] = hideMode;
		values[7] = invalidClass;
		values[8] = invalidText;
		values[9] = msgClass;
		values[10] = msgDisplay;
		values[11] = readOnly;
		values[12] = validateOnBlur;
		values[13] = validationDelay;
		values[14] = validationEvent;
		values[15] = width;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		cls = (String) values[1];
		disableClass = (String) values[2];
		disabled = (Boolean) values[3];
		fieldClass = (String) values[4];
		focusClass = (String) values[5];
		hideMode = (String) values[6];
		invalidClass = (String) values[7];
		invalidText = (String) values[8];
		msgClass = (String) values[9];
		msgDisplay = (String) values[10];
		readOnly = (Boolean) values[11];
		validateOnBlur = (Boolean) values[12];
		validationDelay = (Integer) values[13];
		validationEvent = (String) values[14];
		width = (Integer) values[15];
	}
}
