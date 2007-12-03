package org.springframework.faces.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public abstract class BaseParentComponentRenderer extends BaseHtmlParentTagRenderer {

	private Map attributeCallbacks;

	private RenderAttributeCallback idCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			writer.writeAttribute(attribute, component.getClientId(context), property);
		}
	};

	private RenderAttributeCallback disabledCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			if (Boolean.TRUE.equals(attributeValue)) {
				writer.writeAttribute(attribute, "disabled", property);
			}
		}
	};

	protected Map getAttributeCallbacks(UIComponent component) {
		if (attributeCallbacks == null) {
			attributeCallbacks = new HashMap();
			attributeCallbacks.put("id", idCallback);
			attributeCallbacks.put("name", idCallback);
			attributeCallbacks.put("disabled", disabledCallback);
		}
		return attributeCallbacks;
	}
}
