package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.faces.ui.resource.ResourceHelper;

public abstract class BaseSpringFacesParentComponentRenderer extends BaseParentComponentRenderer {

	private String springJsResourceUri = "/spring/Spring.js";

	private ResourceHelper resourceHelper = new ResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		resourceHelper.renderScriptLink(context, springJsResourceUri);
	}
}
