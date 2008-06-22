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
package org.springframework.binding.format.formatters;

import java.beans.PropertyEditor;

import org.springframework.binding.format.Formatter;
import org.springframework.util.Assert;

/**
 * Adapts a {@link PropertyEditor} to the formatter interface. Useful for re-using pre-existing PropertyEditors as
 * Formatters.
 * 
 * Note: this class synchronizes calls into the configured PropertyEditor instance to ensure thread safety.
 * 
 * @author Keith Donald
 */
public class PropertyEditorFormatter implements Formatter {

	private Class objectType;

	private PropertyEditor propertyEditor;

	/**
	 * Wrap the given property editor in a formatter.
	 */
	public PropertyEditorFormatter(PropertyEditor propertyEditor, Class objectType) {
		Assert.notNull(propertyEditor, "The PropertyEditor is required");
		Assert.notNull(objectType, "The object type is required");
		this.propertyEditor = propertyEditor;
		this.objectType = objectType;
	}

	/**
	 * Returns the wrapped property editor.
	 */
	public PropertyEditor getPropertyEditor() {
		return propertyEditor;
	}

	// implementing Formatter

	public Class getObjectType() {
		return objectType;
	}

	public String format(Object value) {
		synchronized (propertyEditor) {
			propertyEditor.setValue(value);
			String text = propertyEditor.getAsText();
			propertyEditor.setValue(null);
			return text;
		}
	}

	public Object parse(String formattedValue) {
		synchronized (propertyEditor) {
			propertyEditor.setAsText(formattedValue);
			Object value = propertyEditor.getValue();
			propertyEditor.setValue(null);
			return value;
		}
	}
}