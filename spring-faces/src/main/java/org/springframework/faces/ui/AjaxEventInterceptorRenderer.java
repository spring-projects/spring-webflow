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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Renderer for the {@code <sf:ajaxEvent>} tag.
 * 
 * @author Jeremy Grelle
 * 
 */
public class AjaxEventInterceptorRenderer extends DojoDecorationRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		String event = (String) component.getAttributes().get("event");
		Assert.hasText(event, "The event attribute is required on " + component);
		Assert.isTrue(component.getChildCount() == 1, "Exactly one child component is required for " + component);

		ResourceHelper.beginScriptBlock(context);

		ResponseWriter writer = context.getResponseWriter();

		String processIds = (String) component.getAttributes().get("processIds");
		if (StringUtils.hasText(processIds) && !processIds.contains(component.getClientId(context))) {
			processIds = component.getClientId(context) + ", " + processIds;
		} else if (!StringUtils.hasText(processIds)) {
			processIds = component.getClientId(context);
		}
		String childId = getElementId(context, component);
		StringBuffer script = new StringBuffer();
		script.append("Spring.addDecoration(new Spring.AjaxEventDecoration({");
		script.append("event:'" + event + "'");
		script.append(", elementId: '" + childId + "'");
		script.append(", sourceId: '" + component.getClientId(context) + "'");
		script.append(", formId : '" + RendererUtils.getFormId(context, component) + "'");
		script.append(", params: {processIds : '" + processIds + "'");
		script.append(", ajaxSource : '" + component.getClientId(context) + "'} }));");

		writer.writeText(script.toString(), null);

		ResourceHelper.endScriptBlock(context);
	}

	private String getElementId(FacesContext context, UIComponent component) {
		if (component.getChildCount() > 0) {
			UIComponent child = (UIComponent) component.getChildren().get(0);
			if (!(child instanceof DojoDecoration)) {
				return child.getClientId(context);
			} else {
				return getElementId(context, child);
			}
		} else {
			throw new FacesException("Could not locate a proper child element to trigger the ajax event.");
		}
	}

	public void decode(FacesContext context, UIComponent component) {
		if (context.getExternalContext().getRequestParameterMap().containsKey("ajaxSource")
				&& context.getExternalContext().getRequestParameterMap().get("ajaxSource").equals(
						component.getClientId(context))) {
			component.queueEvent(new ActionEvent(component));
		}
	}
}
