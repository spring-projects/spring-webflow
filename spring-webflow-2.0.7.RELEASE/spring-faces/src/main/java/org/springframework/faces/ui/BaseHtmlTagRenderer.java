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
import java.util.Collections;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract base {@link Renderer} for a component that renders a standard HTML element.
 * 
 * <p>
 * Uses a callback mechanism for customizing the rendering of tag attributes when logic is required beyond a simple
 * pass-through of the component attribute to the HTML attribute of the rendered element.
 * </p>
 * 
 * @author Jeremy Grelle
 */
abstract class BaseHtmlTagRenderer extends Renderer {

	protected Log log = LogFactory.getLog(BaseHtmlTagRenderer.class);

	/**
	 * Default {@link RenderAttributeCallback} that just renders the tag attribute as a pass-through value if the value
	 * is not null.
	 */
	private RenderAttributeCallback defaultRenderAttributeCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			if (attributeValue != null) {
				writer.writeAttribute(attribute, attributeValue, property);
			}
		}
	};

	/**
	 * Renders the opening portion of the tag, prior to any children.
	 */
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement(getRenderedTagName(null), component);
		writeAttributes(context, component);
	}

	/**
	 * Writes the attributes for this tag.
	 * @param context the current {@link FacesContext}
	 * @param component the {@link UIComponent} being rendered
	 * @throws IOException
	 */
	protected void writeAttributes(FacesContext context, UIComponent component) throws IOException {
		for (int i = 0; i < getAttributesToRender(component).length; i++) {
			try {
				String attribute = getAttributesToRender(component)[i];
				String property = attribute;
				if (getAttributeAliases(component).containsKey(attribute)) {
					property = (String) getAttributeAliases(component).get(attribute);
				}
				Object attributeValue = component.getAttributes().get(property);

				RenderAttributeCallback callback = defaultRenderAttributeCallback;
				if (getAttributeCallbacks(null).containsKey(attribute)) {
					callback = (RenderAttributeCallback) getAttributeCallbacks(component).get(attribute);
				}
				callback.doRender(context, context.getResponseWriter(), component, attribute, attributeValue, property);
			} catch (IllegalArgumentException ex) {
				// Attribute not found - Skip this attribute and continue
			}
		}
	}

	/**
	 * Closes the tag after children have been rendered.
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(getRenderedTagName(null));
	}

	/**
	 * @param component TODO
	 * @return the name of the tag to be rendered.
	 */
	protected abstract String getRenderedTagName(UIComponent component);

	/**
	 * @return an array of the tag attributes to be rendered
	 */
	protected abstract String[] getAttributesToRender(UIComponent component);

	/**
	 * @return a map that returns the bean property name for any attribute that doesn't map directly (i.e., the 'class'
	 * attribute maps to the 'styleClass' bean property)
	 */
	protected Map getAttributeAliases(UIComponent component) {
		return HTML.STANDARD_ATTRIBUTE_ALIASES;
	};

	/**
	 * @return a map of registered RenderAttributeCallbacks for attributes that require special rendering logic
	 */
	protected Map getAttributeCallbacks(UIComponent component) {
		return Collections.EMPTY_MAP;
	}

}
