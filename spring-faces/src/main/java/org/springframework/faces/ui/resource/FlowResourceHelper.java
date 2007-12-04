package org.springframework.faces.ui.resource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.springframework.webflow.context.FlowDefinitionRequestInfo;
import org.springframework.webflow.context.RequestPath;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Helper used by Spring Faces component renderers to add links to javascript and css resources. The resource links will
 * be rendered in the correct format for the requests to be handled by Web Flow and routed to a special "resources" flow
 * that is engineered at runtime. The resource paths are cached so that a particular resource link is only rendered once
 * per request.
 * @author Jeremy Grelle
 * 
 */
public class FlowResourceHelper {

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

		RequestContext requestContext = RequestContextHolder.getRequestContext();

		ResponseWriter writer = facesContext.getResponseWriter();

		writer.startElement("script", null);

		writer.writeAttribute("type", "text/javascript", null);

		Iterator i = attributes.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			writer.writeAttribute(key, attributes.get(key), null);
		}

		FlowDefinitionRequestInfo requestInfo = new FlowDefinitionRequestInfo("resources", new RequestPath(scriptPath),
				null, null);
		String src = requestContext.getExternalContext().buildFlowDefinitionUrl(requestInfo);

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

		RequestContext requestContext = RequestContextHolder.getRequestContext();

		ResponseWriter writer = facesContext.getResponseWriter();

		writer.startElement("link", null);

		writer.writeAttribute("type", "text/css", null);
		writer.writeAttribute("rel", "stylesheet", null);

		FlowDefinitionRequestInfo requestInfo = new FlowDefinitionRequestInfo("resources", new RequestPath(cssPath),
				null, null);
		String src = requestContext.getExternalContext().buildFlowDefinitionUrl(requestInfo);

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

		RequestContext requestContext = RequestContextHolder.getRequestContext();

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
