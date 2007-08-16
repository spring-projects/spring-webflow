package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class ExtValidateAllRenderer extends ExtJsRenderer {

	private static final String SCRIPT_ELEMENT = "script";

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		if (component.getChildCount() == 0)
			throw new FacesException("A Spring Faces advisor expects to have at least one child component.");

		if (!(component.getChildren().get(0) instanceof UICommand))
			throw new FacesException("ValidateAll expects to have a child of type UICommand.");

		UIComponent advisedChild = (UIComponent) component.getChildren().get(0);

		String elementVar = advisedChild.getClientId(context).replaceAll(":", "_") + "_element";
		String handlerVar = advisedChild.getClientId(context).replaceAll(":", "_") + "_handler";

		writer.startElement(SCRIPT_ELEMENT, component);
		StringBuffer script = new StringBuffer();
		script
				.append(" var " + elementVar + " = document.getElementById('" + advisedChild.getClientId(context)
						+ "');");
		script.append(" var " + handlerVar + " = " + elementVar + ".onclick;");
		script.append(elementVar + ".onclick" + " = function(){");
		script.append(" if(!SpringFaces.validateAll()) return false; ");
		script.append(handlerVar + "();");
		script.append("};");

		writer.writeText(script, null);
		writer.endElement(SCRIPT_ELEMENT);
	}
}
