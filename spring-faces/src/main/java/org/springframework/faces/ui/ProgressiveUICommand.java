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

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.TransitionableState;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * {@link UIComponent} implementation that backs the {@code <sf:commandButton>} tag. Relies mainly on the use of
 * {@link UIComponent#getAttributes()} as opposed to JavaBean getters and setters, except for attribute that require
 * type conversion.
 * 
 * @author Jeremy Grelle
 * 
 */
public class ProgressiveUICommand extends UICommand {

	private String type = "submit";

	private Boolean disabled;

	private Boolean ajaxEnabled = Boolean.TRUE;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getDisabled() {
		if (disabled != null) {
			return disabled;
		}
		ValueBinding vb = getValueBinding("disabled");
		return vb != null ? (Boolean) vb.getValue(getFacesContext()) : Boolean.FALSE;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getAjaxEnabled() {
		return ajaxEnabled;
	}

	public void setAjaxEnabled(Boolean ajaxEnabled) {
		this.ajaxEnabled = ajaxEnabled;
	}

	public boolean isImmediate() {
		RequestContext context = RequestContextHolder.getRequestContext();
		if (context != null && getActionExpression().isLiteralText()
				&& context.getCurrentState() instanceof TransitionableState) {
			TransitionDefinition transition = context
					.getMatchingTransition(getActionExpression().getExpressionString());
			if (transition != null && transition.getAttributes().contains("bind")) {
				return Boolean.FALSE.equals(transition.getAttributes().getBoolean("bind"));
			}
		}
		return super.isImmediate();
	}

	public Object saveState(FacesContext context) {
		Object[] values = new Object[4];
		values[0] = super.saveState(context);
		values[1] = type;
		values[2] = disabled;
		values[3] = ajaxEnabled;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		type = (String) values[1];
		disabled = (Boolean) values[2];
		ajaxEnabled = (Boolean) values[3];
	}

}
