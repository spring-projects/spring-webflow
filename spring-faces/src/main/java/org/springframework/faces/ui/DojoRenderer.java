package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.FlowResourceHelper;

public class DojoRenderer extends Renderer {

	private String dojoJsResourceUri = "/dojo/dojo.js";

	private String dojoStyleResourceUri = "/dojo/dojo.css";

	private String dijitThemePath = "/dijit/themes/";

	private String dijitTheme = "tundra";

	private String springFacesJsResourceUri = "/spring-faces/SpringFaces.js";

	private String springFacesDojoJsResourceUri = "/spring-faces/SpringFaces-Dojo.js";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		resourceHelper.renderStyleLink(context, dijitThemePath + "/" + dijitTheme + "/" + dijitTheme + ".css");

		resourceHelper.renderStyleLink(context, dojoStyleResourceUri);

		resourceHelper.renderScriptLink(context, dojoJsResourceUri);

		resourceHelper.renderScriptLink(context, springFacesJsResourceUri);

		resourceHelper.renderScriptLink(context, springFacesDojoJsResourceUri);
	}
}
