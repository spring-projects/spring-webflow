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
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.faces.ui.resource.FlowResourceHelper;

public class DojoRenderer extends SpringFacesRenderer {

	private String dojoJsResourceUri = "/dojo/dojo.js";

	private String dojoCssResourceUri = "/dojo/resources/dojo.css";

	private String dijitThemePath = "/dijit/themes/";

	private String dijitTheme = "tundra";

	private String springFacesDojoJsResourceUri = "/spring-faces/SpringFaces-Dojo.js";

	private FlowResourceHelper resourceHelper = new FlowResourceHelper();

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		resourceHelper.renderStyleLink(context, dojoCssResourceUri);
		resourceHelper.renderStyleLink(context, dijitThemePath + dijitTheme + "/" + dijitTheme + ".css");

		Map dojoAttributes = new HashMap();
		dojoAttributes.put("djConfig", "parseOnLoad: true");
		resourceHelper.renderScriptLink(context, dojoJsResourceUri, dojoAttributes);

		resourceHelper.renderScriptLink(context, springFacesDojoJsResourceUri);
	}
}
