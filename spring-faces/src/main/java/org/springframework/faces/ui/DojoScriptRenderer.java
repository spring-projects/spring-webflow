package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.ResourceHelper;

/**
 * {@link Renderer} implementation that renders the JavaScript resources required by the Dojo versions of the Spring
 * Faces components.
 * 
 * @author Jeremy Grelle
 * 
 */
public class DojoScriptRenderer extends Renderer {

	private static final String SPRING_JS_RESOURCE_URI = "/spring/Spring.js";

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		ResourceHelper.renderScriptLink(context, SPRING_JS_RESOURCE_URI);

		ResourceHelper.renderScriptLink(context, DojoConstants.DOJO_JS_RESOURCE_URI);

		ResourceHelper.renderScriptLink(context, DojoConstants.SPRING_DOJO_JS_RESOURCE_URI);
	}
}
