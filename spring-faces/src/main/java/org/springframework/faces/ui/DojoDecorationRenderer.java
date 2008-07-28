/*
 * Copyright 2004-2008 the original author or authors.
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
import java.util.Date;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.faces.webflow.JsfUtils;
import org.springframework.util.StringUtils;

/**
 * Generic renderer for components that use the Dojo implementation of Spring JavaScript to decorate a child component
 * with enhanced client-side behavior.
 * 
 * @author Jeremy Grelle
 * 
 */
public class DojoDecorationRenderer extends BaseSpringJavascriptDecorationRenderer {

	private String dojoJsResourceUri = "/dojo/dojo.js";

	private String dijitThemePath = "/dijit/themes/";

	private String dijitTheme = "tundra";

	private String springDojoJsResourceUri = "/spring/Spring-Dojo.js";

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		if (!JsfUtils.isAsynchronousFlowRequest()) {
			ResourceHelper.renderStyleLink(context, dijitThemePath + dijitTheme + "/" + dijitTheme + ".css");

			ResourceHelper.renderScriptLink(context, dojoJsResourceUri);

			ResourceHelper.renderScriptLink(context, springDojoJsResourceUri);
		}
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		if (component.getChildCount() == 0)
			throw new FacesException("A Spring Faces advisor expects to have at least one child component.");

		UIComponent advisedChild = (UIComponent) component.getChildren().get(0);

		ResourceHelper.renderDojoInclude(context, ((DojoDecoration) component).getDojoComponentType());

		ResourceHelper.beginScriptBlock(context);

		StringBuffer script = new StringBuffer();
		script.append("  Spring.addDecoration(new Spring.ElementDecoration({  ");
		script.append("  elementId : '" + advisedChild.getClientId(context) + "',  ");
		script.append("  widgetType : '" + ((DojoDecoration) component).getDojoComponentType() + "',  ");
		script.append("  widgetAttrs : { ");

		String nodeAttrs = getNodeAttributesAsString(context, advisedChild);
		String dojoAttrs = getDojoAttributesAsString(context, component);

		script.append(nodeAttrs);
		if (StringUtils.hasText(dojoAttrs)) {
			script.append(", ");
		}
		script.append(dojoAttrs);

		script.append("  }}));");

		writer.writeText(script, null);

		ResourceHelper.endScriptBlock(context);
	}

	protected String getNodeAttributesAsString(FacesContext context, UIComponent component) {

		StringBuffer attrs = new StringBuffer();

		attrs.append("name : '" + component.getClientId(context) + "'");

		ValueHolder valueHolder = (ValueHolder) component;

		if (valueHolder.getValue() != null) {
			attrs.append(", value : ");
			String strValue = "'" + getValueAsString(context, component) + "'";
			if (valueHolder.getValue() instanceof Date) {
				strValue = "dojo.date.locale.parse(" + strValue + ", {selector : 'date', datePattern : 'yyyy-MM-dd'})";
			}
			attrs.append(strValue);
		}

		return attrs.toString();
	}

	protected String getValueAsString(FacesContext context, UIComponent component) {

		ValueHolder valueHolder = (ValueHolder) component;

		if (valueHolder.getValue() instanceof String) {
			return valueHolder.getValue().toString();
		}

		Converter converter;
		if (valueHolder.getConverter() != null) {
			converter = valueHolder.getConverter();
		} else {
			converter = context.getApplication().createConverter(valueHolder.getValue().getClass());
		}

		if (converter == null) {
			throw new FacesException("A converter could not be found to convert the value of " + component
					+ " to a String.");
		}

		return converter.getAsString(context, component, valueHolder.getValue());

	}

	protected String getDojoAttributesAsString(FacesContext context, UIComponent component) {

		DojoDecoration advisor = (DojoDecoration) component;
		StringBuffer attrs = new StringBuffer();

		for (int i = 0; i < advisor.getDojoAttributes().length; i++) {

			String key = advisor.getDojoAttributes()[i];
			Object value = advisor.getAttributes().get(key);

			if (value != null) {

				if (attrs.length() > 0)
					attrs.append(", ");

				attrs.append(key + " : ");

				if (value instanceof String) {
					attrs.append("'" + value + "'");
				} else {
					attrs.append(value.toString());
				}

			}
		}
		return attrs.toString();
	}
}
