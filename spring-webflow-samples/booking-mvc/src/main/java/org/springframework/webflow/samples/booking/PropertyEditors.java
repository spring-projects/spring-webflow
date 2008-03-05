package org.springframework.webflow.samples.booking;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomDateEditor;

public class PropertyEditors implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry registry) {
	registry.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

}
