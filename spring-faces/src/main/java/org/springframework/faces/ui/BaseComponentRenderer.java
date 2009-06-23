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
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * Base {@link Renderer} for typical faces components, handling the rendering for common {@link UIComponent} attributes.
 * 
 * @author Jeremy Grelle
 * 
 */
public abstract class BaseComponentRenderer extends BaseHtmlTagRenderer {

	private Map attributeCallbacks;

	private RenderAttributeCallback idCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			writer.writeAttribute(attribute, component.getClientId(context), property);
		}
	};

	private RenderAttributeCallback disabledCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			if (Boolean.TRUE.equals(attributeValue)) {
				writer.writeAttribute(attribute, "disabled", property);
			}
		}
	};

	protected Map getAttributeCallbacks(UIComponent component) {
		if (attributeCallbacks == null) {
			attributeCallbacks = new HashMap();
			attributeCallbacks.put("id", idCallback);
			attributeCallbacks.put("name", idCallback);
			attributeCallbacks.put("disabled", disabledCallback);
		}
		return attributeCallbacks;
	}
}
