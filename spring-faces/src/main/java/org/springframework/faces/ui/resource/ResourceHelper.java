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
package org.springframework.faces.ui.resource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Helper used by Spring Faces component renderers to add links to javascript and css resources. The resource links will
 * be rendered in the correct format for the requests to be handled by Web Flow and routed to a special "resources" flow
 * that is engineered at runtime. The resource paths are cached so that a particular resource link is only rendered once
 * per request.
 * @author Jeremy Grelle
 * 
 */
public class ResourceHelper {

	private static final String RENDERED_RESOURCES_KEY = "org.springframework.faces.RenderedResources";

	/**
	 * Render a <code><script/></code> tag for a given script resource.
	 * @param facesContext
	 * @param scriptPath
	 * @throws IOException
	 */
	public void renderScriptLink(FacesContext facesContext, String scriptPath) throws IOException {
		renderScriptLink(facesContext, scriptPath, Collections.EMPTY_MAP);
	}

	/**
	 * Render a <code><script/></code> tag for a given script resource.
	 * @param facesContext
	 * @param scriptPath
	 * @param attributes - a map of additional attributes to render on the script tag
	 * @throws IOException
	 */
	public void renderScriptLink(FacesContext facesContext, String scriptPath, Map attributes) throws IOException {
		if (alreadyRendered(facesContext, scriptPath)) {
			return;
		}
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		Iterator i = attributes.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			writer.writeAttribute(key, attributes.get(key), null);
		}
		String src = facesContext.getExternalContext().getRequestContextPath() + "/resources" + scriptPath;
		writer.writeAttribute("src", src, null);
		writer.endElement("script");
		markRendered(facesContext, scriptPath);
	}

	/**
	 * Render a <code><link/></code> tag for a given stylesheet resource.
	 * @param facesContext
	 * @param cssPath
	 * @throws IOException
	 */
	public void renderStyleLink(FacesContext facesContext, String cssPath) throws IOException {
		if (alreadyRendered(facesContext, cssPath)) {
			return;
		}
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("link", null);
		writer.writeAttribute("type", "text/css", null);
		writer.writeAttribute("rel", "stylesheet", null);
		String src = facesContext.getExternalContext().getRequestContextPath() + "/resources" + cssPath;
		writer.writeAttribute("href", src, null);
		writer.endElement("link");
		markRendered(facesContext, cssPath);
	}

	/**
	 * Render a <code><script/></code> tag for a given dojo include.
	 * @param facesContext
	 * @param module
	 * @throws IOException
	 */
	public void renderDojoInclude(FacesContext facesContext, String module) throws IOException {
		if (alreadyRendered(facesContext, module)) {
			return;
		}
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.writeText("dojo.require('" + module + "');", null);
		writer.endElement("script");
		markRendered(facesContext, module);
	}

	private void markRendered(FacesContext facesContext, String scriptPath) {
		Set renderedResources = (Set) facesContext.getExternalContext().getRequestMap().get(RENDERED_RESOURCES_KEY);
		if (renderedResources == null) {
			renderedResources = new HashSet();
			facesContext.getExternalContext().getRequestMap().put(RENDERED_RESOURCES_KEY, renderedResources);
		}
		renderedResources.add(scriptPath);
	}

	private boolean alreadyRendered(FacesContext facesContext, String scriptPath) {
		Set renderedResources = (Set) facesContext.getExternalContext().getRequestMap().get(RENDERED_RESOURCES_KEY);
		return renderedResources != null && renderedResources.contains(scriptPath);
	}

}
