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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.springframework.beans.BeanUtils;
import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.faces.webflow.JsfUtils;
import org.springframework.util.Assert;

/**
 * {@link Renderer} for the {@code <sf:commandLink>} tag.
 * 
 * <p>
 * This renderer is unique in that it first renders a button that will still work if JavaScript is disabled on the
 * client, then progressively enhances the button and transforms it into a link if JavaScript is available.
 * </p>
 * 
 * @author Jeremy Grelle
 * 
 */
public class ProgressiveCommandLinkRenderer extends ProgressiveCommandButtonRenderer {

	private static String[] ATTRIBUTES_TO_RENDER;

	private static String ANCHOR_TAG_NAME = "a";

	static {
		List tempList = new ArrayList();
		tempList.addAll(Arrays.asList(HTML.STANDARD_ATTRIBUTES));
		tempList.addAll(Arrays.asList(HTML.ANCHOR_ATTRIBUTES));
		tempList.addAll(Arrays.asList(HTML.COMMON_ELEMENT_EVENTS));
		tempList.addAll(Arrays.asList(HTML.KEYBOARD_EVENTS));
		tempList.addAll(Arrays.asList(HTML.MOUSE_EVENTS));
		ATTRIBUTES_TO_RENDER = new String[tempList.size()];
		ListIterator i = tempList.listIterator();
		while (i.hasNext()) {
			ATTRIBUTES_TO_RENDER[i.nextIndex()] = (String) i.next();
		}
	}

	private Map attributeCallbacks;

	private RenderAttributeCallback hrefCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			writer.writeAttribute(attribute, "#", property);
		}
	};

	private RenderAttributeCallback classCallback = new RenderAttributeCallback() {
		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			String classToAdd = "progressiveLink";
			if (attributeValue != null) {
				attributeValue = attributeValue.toString() + " " + classToAdd;
			} else {
				attributeValue = classToAdd;
			}
			writer.writeAttribute(attribute, attributeValue, property);
		}
	};

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		// No need to be progressive if this is an AJAX request since it can be assumed JavaScript is enabled
		if (!JsfUtils.isAsynchronousFlowRequest()) {
			// Render a plain submit button first if this is not an ajax request
			ProgressiveUICommand button = new ProgressiveUICommand();
			button.getAttributes().putAll(component.getAttributes());
			BeanUtils.copyProperties(component, button);
			button.setRendererType("spring.faces.ProgressiveCommandButtonRenderer");
			button.setAjaxEnabled(Boolean.FALSE);
			button.encodeBegin(context);
			button.encodeChildren(context);
			button.encodeEnd(context);

			// Now render the link's HTML into a javascript variable
			ResourceHelper.beginScriptBlock(context);

			ResponseWriter writer = context.getResponseWriter();
			String scriptVarStart = "var " + component.getClientId(context).replaceAll(":", "_") + "_link = \"";
			writer.writeText(scriptVarStart, null);
			writer = new DoubleQuoteEscapingWriter(writer);
			context.setResponseWriter(writer);
		}
		super.encodeBegin(context, component);
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);

		StringBuffer decorationParams = new StringBuffer();
		decorationParams.append("{");
		decorationParams.append("elementId : '" + component.getClientId(context) + "'");

		ResponseWriter writer = context.getResponseWriter();
		// Close the script variable started in encodeBegin if this is not an AJAX request
		if (!JsfUtils.isAsynchronousFlowRequest()) {
			DoubleQuoteEscapingWriter tempWriter = (DoubleQuoteEscapingWriter) writer;
			String scriptVarValue = tempWriter.escapeResult();
			context.setResponseWriter(tempWriter.original);
			writer = tempWriter.original;
			writer.writeText(scriptVarValue, null);

			String scriptVarEnd = "\";\n";
			writer.writeText(scriptVarEnd, null);

			decorationParams.append(", linkHtml : " + component.getClientId(context).replaceAll(":", "_") + "_link");

			ResourceHelper.endScriptBlock(context);
		}

		decorationParams.append("}");
		StringBuffer advisorScript = new StringBuffer();
		advisorScript.append("Spring.addDecoration(new Spring.CommandLinkDecoration(" + decorationParams.toString()
				+ "));");
		ResourceHelper.beginScriptBlock(context);
		writer.writeText(advisorScript, null);
		ResourceHelper.endScriptBlock(context);
	}

	protected String[] getAttributesToRender(UIComponent component) {
		return ATTRIBUTES_TO_RENDER;
	}

	protected String getRenderedTagName(UIComponent component) {
		return ANCHOR_TAG_NAME;
	}

	protected Map getAttributeCallbacks(UIComponent component) {
		if (attributeCallbacks == null) {
			attributeCallbacks = new HashMap();
			attributeCallbacks.putAll(super.getAttributeCallbacks(component));
			attributeCallbacks.put("href", hrefCallback);
			attributeCallbacks.put("class", classCallback);
		}
		return attributeCallbacks;
	}

	protected String getOnClickNoAjax(FacesContext context, UIComponent component) {
		String params = encodeParamsAsArray(context, component);
		StringBuffer onclick = new StringBuffer();
		onclick.append("this.submitFormFromLink('" + RendererUtils.getFormId(context, component) + "','"
				+ component.getClientId(context) + "', " + params + "); return false;");
		return onclick.toString();
	}

	protected String encodeParamsAsArray(FacesContext context, UIComponent component) {
		StringBuffer paramArray = new StringBuffer();
		paramArray.append("[");
		for (int i = 0; i < component.getChildCount(); i++) {
			if (component.getChildren().get(i) instanceof UIParameter) {
				UIParameter param = (UIParameter) component.getChildren().get(i);
				Assert.hasText(param.getName(),
						"UIParameter requires a name when used as a child of a UICommand component");
				if (paramArray.length() > 1) {
					paramArray.append(", ");
				}
				paramArray.append("{name : '" + param.getName() + "'");
				paramArray.append(", value : '" + param.getValue() + "'}");
			}
		}
		paramArray.append("]");
		return paramArray.toString();
	}

	private class DoubleQuoteEscapingWriter extends ResponseWriter {

		private ResponseWriter original;

		private ResponseWriter clonedWriter;

		private StringWriter buffer = new StringWriter();

		public DoubleQuoteEscapingWriter(ResponseWriter original) {
			this.original = original;
			this.clonedWriter = original.cloneWithWriter(buffer);
		}

		public String escapeResult() {
			String result = buffer.toString();
			result = result.replaceAll("\\\"", "\\\\\"");
			return result;
		}

		public ResponseWriter cloneWithWriter(Writer arg0) {
			return clonedWriter.cloneWithWriter(arg0);
		}

		public void endDocument() throws IOException {
			clonedWriter.endDocument();
		}

		public void endElement(String arg0) throws IOException {
			clonedWriter.endElement(arg0);
		}

		public void flush() throws IOException {
			clonedWriter.flush();
		}

		public String getCharacterEncoding() {
			return clonedWriter.getCharacterEncoding();
		}

		public String getContentType() {
			return clonedWriter.getContentType();
		}

		public void startDocument() throws IOException {
			clonedWriter.startDocument();
		}

		public void startElement(String arg0, UIComponent arg1) throws IOException {
			clonedWriter.startElement(arg0, arg1);
		}

		public void writeAttribute(String arg0, Object arg1, String arg2) throws IOException {
			clonedWriter.writeAttribute(arg0, arg1, arg2);
		}

		public void writeComment(Object arg0) throws IOException {
			clonedWriter.writeComment(arg0);
		}

		public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
			clonedWriter.writeText(arg0, arg1, arg2);
		}

		public void writeText(Object arg0, String arg1) throws IOException {
			clonedWriter.writeText(arg0, arg1);
		}

		public void writeURIAttribute(String arg0, Object arg1, String arg2) throws IOException {
			clonedWriter.writeURIAttribute(arg0, arg1, arg2);
		}

		public void close() throws IOException {
			clonedWriter.close();
		}

		public void write(char[] cbuf, int off, int len) throws IOException {
			clonedWriter.write(cbuf, off, len);
		}

	}
}
