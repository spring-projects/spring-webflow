package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.util.Assert;

public class ResourceRenderer extends Renderer {

	private static final ResourceHelper resourceHelper = new ResourceHelper();

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		String resourcePath = (String) component.getAttributes().get("resourcePath");
		Assert.hasText(resourcePath, "Resource component " + component.getClientId(context)
				+ " is missing a resourcePath.");
		if (!resourcePath.startsWith("/")) {
			resourcePath = "/" + resourcePath;
			component.getAttributes().put("resourcePath", resourcePath);
		}
		resourceHelper.renderResource(context, resourcePath);
	}

}
