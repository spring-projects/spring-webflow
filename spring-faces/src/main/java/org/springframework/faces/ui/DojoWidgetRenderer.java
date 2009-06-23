package org.springframework.faces.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class DojoWidgetRenderer extends DojoElementDecorationRenderer {

	protected String getDojoAttributesAsString(FacesContext context, UIComponent component) {

		DojoWidget advisor = (DojoWidget) component;
		StringBuffer attrs = new StringBuffer();

		for (int i = 0; i < advisor.getDojoAttributes().length; i++) {

			String key = advisor.getDojoAttributes()[i];
			Object value = advisor.getAttributes().get(key);

			if (value != null) {

				if (attrs.length() > 0)
					attrs.append(", ");

				attrs.append(key + " : ");

				if (value instanceof String) {
					attrs.append("'" + value + "'");
				} else {
					attrs.append(value.toString());
				}

			}
		}
		return attrs.toString();
	}
}
