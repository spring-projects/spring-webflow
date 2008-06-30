package org.springframework.binding.convert.converters;

import java.beans.PropertyEditor;

public class PropertyEditorConverter extends StringToObject {

	private PropertyEditor propertyEditor;

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
