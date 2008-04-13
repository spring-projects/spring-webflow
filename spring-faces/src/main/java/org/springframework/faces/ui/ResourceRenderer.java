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
import org.springframework.util.Assert;

public class ResourceRenderer extends Renderer {

	private static final ResourceHelper resourceHelper = new ResourceHelper();

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		String resourcePath = (String) component.getAttributes().get("path");
		Assert.hasText(resourcePath, "Resource component " + component.getClientId(context) + " is missing a path.");
		if (!resourcePath.startsWith("/")) {
			resourcePath = "/" + resourcePath;
			component.getAttributes().put("path", resourcePath);
		}
		resourceHelper.renderResource(context, resourcePath);
	}

}
