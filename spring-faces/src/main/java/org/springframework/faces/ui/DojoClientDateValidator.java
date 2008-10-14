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

import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;

import org.springframework.util.Assert;

/**
 * Component that uses the Dojo implementation of Spring JavaScript to decorate a child input component with client-side
 * date validation behavior.
 * 
 * @author Jeremy Grelle
 * 
 */
public class DojoClientDateValidator extends DojoDecoration {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.DateTextBox";

	private static final String[] DOJO_ATTRS_INTERNAL = new String[] { "datePattern" };

	private static final String[] DOJO_ATTRS;

	private String datePattern = null;

	static {
		DOJO_ATTRS = new String[DojoDecoration.DOJO_ATTRS.length + DOJO_ATTRS_INTERNAL.length];
		System.arraycopy(DojoDecoration.DOJO_ATTRS, 0, DOJO_ATTRS, 0, DojoDecoration.DOJO_ATTRS.length);
		System.arraycopy(DOJO_ATTRS_INTERNAL, 0, DOJO_ATTRS, DojoDecoration.DOJO_ATTRS.length,
				DOJO_ATTRS_INTERNAL.length);
	}

	public String getDatePattern() {
		Assert.isTrue(getChildren().get(0) instanceof ValueHolder,
				"Date validation can only be applied to an ValueHolder");
		ValueHolder child = (ValueHolder) getChildren().get(0);
		if (child.getConverter() instanceof DateTimeConverter) {
			return ((DateTimeConverter) child.getConverter()).getPattern();
		}
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
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
		values[1] = this.datePattern;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.datePattern = (String) values[1];
	}

}
