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
package org.springframework.binding.convert.converters;

import java.beans.PropertyEditor;

/**
 * Adapts a PropertyEditor to the {@link TwoWayConverter} interface.
 * @author Keith Donald
 */
public class PropertyEditorConverter extends StringToObject {

	private PropertyEditor propertyEditor;

	/**
	 * Creates a new property editor converter.
	 * @param propertyEditor the property editor to adapt
	 * @param targetClass the {@link PropertyEditor#getValue() value class} this property editor converts to from string
	 */
	public PropertyEditorConverter(PropertyEditor propertyEditor, Class targetClass) {
		super(targetClass);
		this.propertyEditor = propertyEditor;
	}

	protected Object toObject(String string, Class targetClass) throws Exception {
		synchronized (propertyEditor) {
			propertyEditor.setAsText(string);
			return propertyEditor.getValue();
		}
	}

	protected String toString(Object object) throws Exception {
		synchronized (propertyEditor) {
			propertyEditor.setValue(object);
			return propertyEditor.getAsText();
		}
	}

}
