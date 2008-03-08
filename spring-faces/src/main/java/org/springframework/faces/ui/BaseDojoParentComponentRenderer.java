package org.springframework.faces.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.faces.ui.resource.ResourceHelper;

public abstract class BaseDojoParentComponentRenderer extends BaseSpringFacesParentComponentRenderer {

	private String dojoJsResourceUri = "/dojo/dojo.js";

	private String dijitThemePath = "/dijit/themes/";

	private String dijitTheme = "tundra";

	private String springDojoJsResourceUri = "/spring/Spring-Dojo.js";

	private ResourceHelper resourceHelper = new ResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);

		resourceHelper.renderStyleLink(context, dijitThemePath + dijitTheme + "/" + dijitTheme + ".css");

		Map dojoAttributes = new HashMap();
		dojoAttributes.put("djConfig", "parseOnLoad: true");
		resourceHelper.renderScriptLink(context, dojoJsResourceUri, dojoAttributes);

		resourceHelper.renderScriptLink(context, springDojoJsResourceUri);
	}

}
