/*
 * Copyright 2004-2008 the original author or authors.
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Base {@link UIComponent} for a component that uses the Dojo implementation of Spring JavaScript to decorate a child
 * component with enhanced client-side behavior.
 * 
 * @author Jeremy Grelle
 * 
 */
public abstract class DojoDecoration extends UIComponentBase {

	protected static final String[] DOJO_ATTRS = new String[] { "disabled", "intermediateChanges", "tabIndex",
			"required", "promptMessage", "invalidMessage", "constraints", "regExp", "regExpGen", "propercase",
			"lowercase", "uppercase" };

	private Boolean disabled;

	private Boolean intermediateChanges;

	private Integer tabIndex;

	private Boolean required;

	private String promptMessage;

	private String invalidMessage;

	private String constraints;

	private String regExp;

	private String regExpGen;

	private Boolean lowercase;

	private Boolean propercase;

	private Boolean uppercase;

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

	public Boolean getIntermediateChanges() {
		return intermediateChanges;
	}

	public void setIntermediateChanges(Boolean intermediateChanges) {
		this.intermediateChanges = intermediateChanges;
	}

	public Integer getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getPromptMessage() {
		if (promptMessage != null) {
			return promptMessage;
		}
		ValueBinding exp = getValueBinding("promptMessage");
		return exp != null ? (String) exp.getValue(getFacesContext()) : null;
	}

	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	public String getInvalidMessage() {
		if (invalidMessage != null) {
			return invalidMessage;
		}
		ValueBinding exp = getValueBinding("invalidMessage");
		return exp != null ? (String) exp.getValue(getFacesContext()) : null;
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getRegExpGen() {
		return regExpGen;
	}

	public void setRegExpGen(String regExpGen) {
		this.regExpGen = regExpGen;
	}

	public Boolean getLowercase() {
		return lowercase;
	}

	public void setLowercase(Boolean lowercase) {
		this.lowercase = lowercase;
	}

	public Boolean getUppercase() {
		return uppercase;
	}

	public void setUppercase(Boolean uppercase) {
		this.uppercase = uppercase;
	}

	public Boolean getPropercase() {
		return propercase;
	}

	public void setPropercase(Boolean propercase) {
		this.propercase = propercase;
	}

	protected abstract String[] getDojoAttributes();

	public abstract String getDojoComponentType();

	public Object saveState(FacesContext context) {
		Object[] values = new Object[11];
		values[0] = super.saveState(context);
		values[1] = this.constraints;
		values[2] = this.disabled;
		values[3] = this.intermediateChanges;
		values[4] = this.invalidMessage;
		values[5] = this.promptMessage;
		values[6] = this.regExp;
		values[7] = this.regExpGen;
		values[8] = this.required;
		values[9] = this.tabIndex;
		values[10] = this.propercase;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.constraints = (String) values[1];
		this.disabled = (Boolean) values[2];
		this.intermediateChanges = (Boolean) values[3];
		this.invalidMessage = (String) values[4];
		this.promptMessage = (String) values[5];
		this.regExp = (String) values[6];
		this.regExpGen = (String) values[7];
		this.required = (Boolean) values[8];
		this.tabIndex = (Integer) values[9];
		this.propercase = (Boolean) values[10];
	}

	public String getFamily() {

		return "spring.faces.Decoration";
	}

}
