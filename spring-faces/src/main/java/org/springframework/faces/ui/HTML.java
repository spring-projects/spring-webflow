package org.springframework.faces.ui;

import java.util.HashMap;
import java.util.Map;

final class HTML {

	/**
	 * Standard HTML attributes
	 */
	public static final String[] STANDARD_ATTRIBUTES = new String[] { "id", "class", "style", "title", "dir", "lang",
			"accesskey", "tabindex" };

	public static final Map STANDARD_ATTRIBUTE_ALIASES = new HashMap();

	/**
	 * Standard window events - only valid in body and frameset elements
	 */
	public static final String[] WINDOW_EVENTS = new String[] { "onload", "onunload" };

	/**
	 * Standard form events
	 */
	public static final String[] FORM_EVENTS = new String[] { "onsubmit", "onreset" };

	/**
	 * Standard form element events
	 */
	public static final String[] COMMON_ELEMENT_EVENTS = new String[] { "onchange", "onselect", "onblur", "onfocus" };

	/**
	 * Standard keyboard events
	 */
	public static final String[] KEYBOARD_EVENTS = new String[] { "onkeydown", "onkeypress", "onkeyup" };

	/**
	 * Standard mouse events
	 */
	public static final String[] MOUSE_EVENTS = new String[] { "onclick", "ondblclick", "onmousedown", "onmousemove",
			"onmouseout", "onmouseover", "onmouseup" };

	/**
	 * Button attributes
	 */
	public static final String[] BUTTON_ATTRIBUTES = new String[] { "disabled", "name", "type", "value" };

	/**
	 * Anchor attributes
	 */
	public static final Object[] ANCHOR_ATTRIBUTES = new String[] { "charset", "coords", "href", "hreflang", "name",
			"rel", "rev", "shape", "target", "type" };

	static {
		STANDARD_ATTRIBUTE_ALIASES.put("class", "styleClass");
	}
}
