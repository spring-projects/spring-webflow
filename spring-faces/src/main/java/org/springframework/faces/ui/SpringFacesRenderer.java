package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.FlowResourceHelper;

public class SpringFacesRenderer extends Renderer {

	private String springFacesJsResourceUri = "/spring-faces/SpringFaces.js";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		resourceHelper.renderScriptLink(context, springFacesJsResourceUri);
	}

}
