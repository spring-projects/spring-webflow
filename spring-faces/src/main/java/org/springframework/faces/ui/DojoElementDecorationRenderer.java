/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.faces.webflow.JsfUtils;

/**
 * Generic renderer for components that use the Dojo implementation of Spring JavaScript to decorate a child component
 * with enhanced client-side behavior.
 * 
 * @author Jeremy Grelle
 * 
 */
public class DojoElementDecorationRenderer extends BaseSpringJavascriptDecorationRenderer {

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		if (!JsfUtils.isAsynchronousFlowRequest()) {

			if (!context.getViewRoot().getAttributes().containsKey(DojoConstants.CUSTOM_THEME_PATH_SET)
					&& !context.getViewRoot().getAttributes().containsKey(DojoConstants.CUSTOM_THEME_SET)) {
				ResourceHelper.renderStyleLink(context, DojoConstants.DIJIT_THEME_PATH
						+ DojoConstants.DEFAULT_DIJIT_THEME + "/" + DojoConstants.DEFAULT_DIJIT_THEME + ".css");
			}

			ResourceHelper.renderScriptLink(context, DojoConstants.DOJO_JS_RESOURCE_URI);

			ResourceHelper.renderScriptLink(context, DojoConstants.SPRING_DOJO_JS_RESOURCE_URI);
		}
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		String selector;
		if (component.getAttributes().containsKey("selector")) {
			selector = "\"" + (String) component.getAttributes().get("selector") + "\"";
		} else {
			if (component.getChildCount() == 0) {
				throw new FacesException(
						"A Spring Faces elementDecoration expects either have a specified selector or at least one child component.");
			}
			selector = "dojo.byId('" + component.getChildren().get(0).getClientId(context) + "')";
		}

		ResourceHelper.beginScriptBlock(context);

		StringBuilder script = new StringBuilder();
		script.append("  dojo.addOnLoad(function(){dojo.query(" + selector + ").forEach(function(element){");
		script.append("  Spring.addDecoration(new Spring.ElementDecoration({  ");
		script.append("  elementId : element,  ");
		script.append("  widgetType : '" + component.getAttributes().get("widgetType") + "',  ");
		if (component.getAttributes().containsKey("widgetModule")) {
			script.append("  widgetModule : '" + component.getAttributes().get("widgetModule") + "',  ");
		}
		script.append("  widgetAttrs : { ");

		String dojoAttrs = getDojoAttributesAsString(context, component);

		script.append(dojoAttrs);

		script.append("  }}));})});");

		writer.writeText(script, null);

		ResourceHelper.endScriptBlock(context);
	}

	protected String getDojoAttributesAsString(FacesContext context, UIComponent component) {

		if (component.getAttributes().containsKey("widgetAttrs")) {
			return (String) component.getAttributes().get("widgetAttrs");
		} else {
			return "";
		}
	}
}
