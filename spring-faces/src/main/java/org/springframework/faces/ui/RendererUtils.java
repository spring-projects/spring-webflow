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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Helper class for common renderer functionality.
 * 
 * @author Jeremy Grelle
 * 
 */
class RendererUtils {

	public static String getFormId(FacesContext context, UIComponent component) {
		if (component.getParent() instanceof UIForm) {
			return component.getParent().getClientId(context);
		} else if (component.getParent() instanceof UIViewRoot) {
			throw new FacesException("Could not render " + component.getClass().getName() + " component with id "
					+ component.getId() + " - no enclosing UIForm was found.");
		} else {
			return getFormId(context, component.getParent());
		}
	}
}
