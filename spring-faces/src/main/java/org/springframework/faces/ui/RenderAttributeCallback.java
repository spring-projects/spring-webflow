package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public interface RenderAttributeCallback {

	public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
			Object attributeValue, String property) throws IOException;
}
