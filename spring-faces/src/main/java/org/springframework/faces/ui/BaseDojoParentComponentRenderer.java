package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.faces.ui.resource.FlowResourceHelper;

public abstract class BaseDojoParentComponentRenderer extends BaseSpringFacesParentComponentRenderer {

	private String dojoJsResourceUri = "/dojo/dojo.js";

	private String dijitThemePath = "/dijit/themes/";

	private String dijitTheme = "tundra";

	private String springFacesDojoJsResourceUri = "/spring-faces/SpringFaces-Dojo.js";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		resourceHelper.renderStyleLink(context, dijitThemePath + dijitTheme + "/" + dijitTheme + ".css");

		resourceHelper.renderScriptLink(context, dojoJsResourceUri);

		resourceHelper.renderScriptLink(context, springFacesDojoJsResourceUri);

		super.encodeBegin(context, component);
	}

}
