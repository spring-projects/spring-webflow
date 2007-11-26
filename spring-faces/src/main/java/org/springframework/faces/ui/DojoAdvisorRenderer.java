/*
 * Copyright 2004-2007 the original author or authors.
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

import org.springframework.faces.ui.resource.FlowResourceHelper;
import org.springframework.util.StringUtils;

public class DojoAdvisorRenderer extends DojoRenderer {

	private static final String SCRIPT_ELEMENT = "script";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		if (component.getChildCount() == 0)
			throw new FacesException("A Spring Faces advisor expects to have at least one child component.");

		UIComponent advisedChild = (UIComponent) component.getChildren().get(0);

		resourceHelper.renderDojoInclude(context, ((DojoAdvisor) component).getDojoComponentType());

		writer.startElement(SCRIPT_ELEMENT, component);
		StringBuffer script = new StringBuffer();
		script.append("  SpringFaces.advisors.push(new SpringFaces.DojoGenericFieldAdvisor({  ");
		script.append("  targetElId : '" + advisedChild.getClientId(context) + "',  ");
		script.append("  msgElId : '" + advisedChild.getClientId(context) + ":msg',  ");
		script.append("  decoratorType : '" + ((DojoAdvisor) component).getDojoComponentType() + "',  ");
		script.append("  decoratorAttrs : \"{ ");

		String nodeAttrs = getNodeAttributesAsString(context, advisedChild);
		String dojoAttrs = getDojoAttributesAsString(context, component);

		script.append(nodeAttrs);
		if (StringUtils.hasText(dojoAttrs)) {
			script.append(", ");
		}
		script.append(dojoAttrs);

		script.append("  }\"}));  ");

		writer.writeText(script, null);
		writer.endElement(SCRIPT_ELEMENT);
	}

	protected String getNodeAttributesAsString(FacesContext context, UIComponent component) {

		StringBuffer attrs = new StringBuffer();

		attrs.append("name : '" + component.getClientId(context) + "'");

		ValueHolder valueHolder = (ValueHolder) component;

		if (valueHolder.getValue() != null) {
			attrs.append(", value : ");
			String strValue;
			if (valueHolder.getValue() instanceof String) {
				strValue = "'" + (String) valueHolder.getValue() + "'";
			} else {
				strValue = "'" + valueHolder.getConverter().getAsString(context, component, valueHolder.getValue())
						+ "'";
				if (valueHolder.getValue() instanceof Date) {
					strValue = "dojo.date.locale.parse(" + strValue
							+ ", {selector : 'date', datePattern : 'yyyy-MM-dd'})";
				}
			}
			attrs.append(strValue);
		}

		return attrs.toString();
	}

	protected String getDojoAttributesAsString(FacesContext context, UIComponent component) {

		DojoAdvisor advisor = (DojoAdvisor) component;
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
