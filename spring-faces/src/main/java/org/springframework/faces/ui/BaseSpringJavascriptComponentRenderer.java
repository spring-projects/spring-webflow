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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.faces.ui.resource.ResourceHelper;
import org.springframework.faces.webflow.JsfUtils;

/**
 * Base {@link Renderer} for components that require the Spring JavaScript library on the client.
 * 
 * @author Jeremy Grelle
 * 
 */
public abstract class BaseSpringJavascriptComponentRenderer extends BaseComponentRenderer {

	private String springJsResourceUri = "/spring/Spring.js";

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		if (!JsfUtils.isAsynchronousFlowRequest()) {
			ResourceHelper.renderScriptLink(context, springJsResourceUri);
		}
	}
}
