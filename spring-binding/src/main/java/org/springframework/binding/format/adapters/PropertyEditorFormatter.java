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
package org.springframework.binding.format.adapters;

import java.beans.PropertyEditor;

import org.springframework.binding.format.factories.AbstractFormatter;
import org.springframework.util.Assert;

/**
 * Adapts a property editor to the formatter interface.
 * 
 * @author Keith Donald
 */
public class PropertyEditorFormatter extends AbstractFormatter {

	private PropertyEditor propertyEditor;

	/**
	 * Wrap given property editor in a formatter.
	 */
	public PropertyEditorFormatter(PropertyEditor propertyEditor) {
		Assert.notNull(propertyEditor, "Property editor is required");
		this.propertyEditor = propertyEditor;
	}

	/**
	 * Returns the wrapped property editor.
	 */
	public PropertyEditor getPropertyEditor() {
		return propertyEditor;
	}

	protected String doFormatValue(Object value) {
		propertyEditor.setValue(value);
		return propertyEditor.getAsText();
	}

	protected Object doParseValue(String formattedValue) {
		propertyEditor.setAsText(formattedValue);
		return propertyEditor.getValue();
	}
}