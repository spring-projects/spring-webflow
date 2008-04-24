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

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that provides common attributes for standard HTML elements.
 * 
 * @author Jeremy Grelle
 */
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
