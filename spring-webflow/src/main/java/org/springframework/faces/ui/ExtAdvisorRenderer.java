package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * A base implementation for use in rendering Ext based components that enhance existing DOM elements.
 * 
 * @author Jeremy Grelle
 * 
 */
public class ExtAdvisorRenderer extends ExtJsRenderer {

    private static final String CLASS_ATTR = "class";

    private static final String ID_ATTR = "id";

    private static final String SCRIPT_ELEMENT = "script";

    private static final String DIV_ELEMENT = "div";

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

	ResponseWriter writer = context.getResponseWriter();

	if (component.getChildCount() == 0)
	    throw new FacesException("A Spring Faces advisor expects to have at least one child component.");

	UIComponent advisedChild = (UIComponent) component.getChildren().get(0);

	writer.startElement(DIV_ELEMENT, component);
	writer.writeAttribute(ID_ATTR, advisedChild.getClientId(context) + ":msg", null);
	writer.writeAttribute(CLASS_ATTR, ((ExtAdvisor) component).getMsgClass(), null);
	writer.endElement(DIV_ELEMENT);

	writer.startElement(SCRIPT_ELEMENT, component);
	StringBuffer script = new StringBuffer();
	script.append("  SpringFaces.advisors.push(new SpringFaces.ExtGenericFieldAdvisor({  ");
	script.append("  targetElId : '" + advisedChild.getClientId(context) + "',  ");
	script.append("  msgElId : '" + advisedChild.getClientId(context) + ":msg',  ");
	script.append("  decoratorType : '" + ((ExtAdvisor) component).getExtComponentType() + "',  ");
	script.append("  decoratorAttrs : \"{  ");

	script.append(getExtAttributesAsString(context, component));

	script.append("  }\"}));  ");

	writer.writeText(script, null);
	writer.endElement(SCRIPT_ELEMENT);
    }

    protected String getExtAttributesAsString(FacesContext context, UIComponent component) {

	ExtAdvisor advisor = (ExtAdvisor) component;
	StringBuffer attrs = new StringBuffer();

	for (int i = 0; i < advisor.getExtAttributes().length; i++) {

	    String key = advisor.getExtAttributes()[i];
	    Object value = advisor.getAttributes().get(key);

	    if (value != null) {

		if (attrs.length() > 0)
		    attrs.append(", ");

		attrs.append(key + " : ");

		if (value instanceof String) {
		    attrs.append("'" + value + "'");
		} else {
		    attrs.append(value);
		}

	    }
	}
	return attrs.toString();
    }
}
