package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.ResourceHelper;

public class ResourceGroupRenderer extends Renderer {

	private static final ResourceHelper resourceHelper = new ResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		if (component.getChildCount() > 0) {
			resourceHelper.beginCombineStyles(context);
		}
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (component.getChildCount() > 0) {
			resourceHelper.endCombineStyles(context);
		}
	}
}
