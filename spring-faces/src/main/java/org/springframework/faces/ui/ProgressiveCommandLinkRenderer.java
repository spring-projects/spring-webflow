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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.springframework.beans.BeanUtils;
import org.springframework.faces.webflow.JsfUtils;

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
			ProgressiveCommandButton button = new ProgressiveCommandButton();
			button.getAttributes().putAll(component.getAttributes());
			BeanUtils.copyProperties(component, button);
			button.setAjaxEnabled(Boolean.FALSE);
			button.encodeBegin(context);
			button.encodeChildren(context);
			button.encodeEnd(context);

			// Now render the link's HTML into a javascript variable
			ResponseWriter writer = context.getResponseWriter();
			writer.startElement("script", component);
			String scriptVarStart = "var " + component.getClientId(context).replaceAll(":", "_") + "_link = \"";
			writer.writeText(scriptVarStart, null);
			writer = new DoubleQuoteEscapingWriter(writer);
			context.setResponseWriter(writer);
		}
		super.encodeBegin(context, component);
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);

		StringBuffer advisorParams = new StringBuffer();
		advisorParams.append("{");
		advisorParams.append("targetElId : '" + component.getClientId(context) + "'");

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

			advisorParams.append(", linkHtml : " + component.getClientId(context).replaceAll(":", "_") + "_link");
			writer.endElement("script");
		}

		advisorParams.append("}");
		StringBuffer advisorScript = new StringBuffer();
		advisorScript.append("Spring.advisors.push(new Spring.CommandLinkAdvisor(" + advisorParams.toString() + ")");
		// Apply the advisor immediately if this is an AJAX request
		if (JsfUtils.isAsynchronousFlowRequest()) {
			advisorScript.append(".apply()");
		}
		advisorScript.append(");");
		writer.startElement("script", component);
		writer.writeText(advisorScript, null);
		writer.endElement("script");
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
		String params = encodeParams(context, component);
		StringBuffer onclick = new StringBuffer();
		onclick.append("this.submitFormFromLink('" + RendererUtils.getFormId(context, component) + "','"
				+ component.getClientId(context) + "', " + params + "); return false;");
		return onclick.toString();
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
