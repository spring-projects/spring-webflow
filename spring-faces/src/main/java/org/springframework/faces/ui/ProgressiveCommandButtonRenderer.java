package org.springframework.faces.ui;

import java.io.IOException;
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
import javax.faces.event.ActionEvent;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ProgressiveCommandButtonRenderer extends BaseDojoParentComponentRenderer {

	private static String[] ATTRIBUTES_TO_RENDER;

	private static String BUTTON_TAG_NAME = "button";

	static {
		List tempList = new ArrayList();
		tempList.addAll(Arrays.asList(HTML.STANDARD_ATTRIBUTES));
		tempList.addAll(Arrays.asList(HTML.BUTTON_ATTRIBUTES));
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

	private RenderAttributeCallback onclickCallback = new RenderAttributeCallback() {

		public void doRender(FacesContext context, ResponseWriter writer, UIComponent component, String attribute,
				Object attributeValue, String property) throws IOException {
			StringBuffer onclick = new StringBuffer();
			if (attributeValue != null) {
				String originalOnclick = attributeValue.toString().trim();
				if (!originalOnclick.endsWith(";")) {
					originalOnclick += ";";
				}
				onclick.append(originalOnclick);
			}

			Boolean ajaxEnabled = (Boolean) component.getAttributes().get("ajaxEnabled");
			String processIds = (String) component.getAttributes().get("processIds");
			String renderIds = (String) component.getAttributes().get("renderIds");
			if (Boolean.TRUE.equals(ajaxEnabled)) {
				if (StringUtils.hasText(processIds) && !processIds.contains(component.getClientId(context))) {
					processIds = component.getClientId(context) + ", " + processIds;
				} else if (!StringUtils.hasText(processIds)) {
					processIds = component.getClientId(context);
				}
				if (!StringUtils.hasText(renderIds)) {
					renderIds = processIds;
				}
				onclick.append("Spring.RemotingHandler.submitForm('" + component.getClientId(context) + "', ");
				onclick.append("'" + RendererUtils.getFormId(context, component) + "', ");
				onclick.append("'" + processIds + "', '" + renderIds + "', " + encodeParams(context, component)
						+ "); return false;");
			} else {
				onclick.append(getOnClickNoAjax(context, component));
			}

			if (onclick.length() > 0) {
				writer.writeAttribute(attribute, onclick.toString(), property);
			}
		}

	};

	protected Map getAttributeCallbacks(UIComponent component) {
		if (attributeCallbacks == null) {
			attributeCallbacks = new HashMap();
			attributeCallbacks.putAll(super.getAttributeCallbacks(component));
			attributeCallbacks.put("onclick", onclickCallback);
		}
		return attributeCallbacks;
	}

	/**
	 * This is a hook for subclasses to provide special onclick behavior in the non-ajax case
	 * @return
	 */
	protected String getOnClickNoAjax(FacesContext context, UIComponent component) {
		// No special behavior necessary for CommandButton
		return "";
	}

	protected String[] getAttributesToRender(UIComponent component) {
		return ATTRIBUTES_TO_RENDER;
	}

	protected String getRenderedTagName(UIComponent component) {
		return BUTTON_TAG_NAME;
	}

	public void decode(FacesContext context, UIComponent component) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(component.getClientId(context))) {
			component.queueEvent(new ActionEvent(component));
		}
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// If the button has no children, render out the "value" as text.
		ResponseWriter writer = context.getResponseWriter();
		String valueAttr = "value";
		if (component.getAttributes().get(valueAttr) != null) {
			writer.writeText(component.getAttributes().get(valueAttr), valueAttr);
		}
		super.encodeChildren(context, component);
	}

	public boolean getRendersChildren() {
		return true;
	}

	protected String encodeParams(FacesContext context, UIComponent component) {
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

}
