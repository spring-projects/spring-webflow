package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.faces.ui.resource.FlowResourceHelper;

public abstract class BaseSpringFacesParentComponentRenderer extends BaseParentComponentRenderer {

	private String springJsResourceUri = "/spring/Spring.js";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		resourceHelper.renderScriptLink(context, springJsResourceUri);
	}
}
