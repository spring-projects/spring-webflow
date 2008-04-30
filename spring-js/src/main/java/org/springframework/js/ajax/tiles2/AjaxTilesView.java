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
package org.springframework.js.ajax.tiles2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.tiles2.TilesView;

/**
 * Tiles view implementation that is able to handle partial rendering for Spring Javascript Ajax requests.
 * 
 * <p>
 * This implementation uses the {@link SpringJavascriptAjaxHandler} by default to determine whether the current request
 * is an Ajax request. On an Ajax request, a "fragments" parameter will be extracted from the request in order to
 * determine which attributes to render from the current tiles view.
 * </p>
 * 
 * @author Jeremy Grelle
 */
public class AjaxTilesView extends TilesView {

	private static final String FRAGMENTS_PARAM = "fragments";

	private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

	public AjaxHandler getAjaxHandler() {
		return ajaxHandler;
	}

	public void setAjaxHandler(AjaxHandler ajaxHandler) {
		this.ajaxHandler = ajaxHandler;
	}

	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ServletContext servletContext = getServletContext();
		if (ajaxHandler.isAjaxRequest(request, response)) {

			String[] attrNames = getRenderFragments(model, request, response);
			if (attrNames.length == 0) {
				logger.warn("An Ajax request was detected, but no fragments were specified to be re-rendered.  "
						+ "Falling back to full page render.");
				super.renderMergedOutputModel(model, request, response);
			}

			BasicTilesContainer container = (BasicTilesContainer) TilesAccess.getContainer(servletContext);
			if (container == null) {
				throw new ServletException("Tiles container is not initialized. "
						+ "Have you added a TilesConfigurer to your web application context?");
			}

			exposeModelAsRequestAttributes(model, request);
			JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

			TilesRequestContext tilesRequestContext = container.getContextFactory().createRequestContext(
					container.getApplicationContext(), new Object[] { request, response });
			Definition compositeDefinition = container.getDefinitionsFactory().getDefinition(getUrl(),
					tilesRequestContext);
			Map flattenedAttributeMap = new HashMap();
			flattenAttributeMap(container, tilesRequestContext, flattenedAttributeMap, compositeDefinition);

			response.flushBuffer();
			for (int i = 0; i < attrNames.length; i++) {
				Attribute attributeToRender = (Attribute) flattenedAttributeMap.get(attrNames[i]);

				if (attributeToRender == null) {
					throw new ServletException("No tiles attribute with a name of '" + attrNames[i]
							+ "' could be found for the current view: " + this);
				} else {
					container.render(attributeToRender, response.getWriter(), new Object[] { request, response });
				}
			}
		} else {
			super.renderMergedOutputModel(model, request, response);
		}
	}

	protected String[] getRenderFragments(Map model, HttpServletRequest request, HttpServletResponse response) {
		String attrName = request.getParameter(FRAGMENTS_PARAM);
		return StringUtils.commaDelimitedListToStringArray(attrName);
	}

	private void flattenAttributeMap(BasicTilesContainer container, TilesRequestContext requestContext, Map resultMap,
			Definition compositeDefinition) throws Exception {
		Iterator i = compositeDefinition.getAttributes().keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			Attribute attr = (Attribute) compositeDefinition.getAttributes().get(key);
			Definition nestedDefinition = container.getDefinitionsFactory().getDefinition(attr.getValue().toString(),
					requestContext);
			if (nestedDefinition != null) {
				flattenAttributeMap(container, requestContext, resultMap, nestedDefinition);
			} else {
				resultMap.put(key, attr);
			}

		}
	}
}
