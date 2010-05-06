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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.Definition;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletTilesRequestContextFactory;
import org.apache.tiles.servlet.context.ServletUtil;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.util.Assert;
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
 * @author David Winterfeldt
 */
public class AjaxTilesView extends TilesView {

	private static final String FRAGMENTS_PARAM = "fragments";

	private TilesRequestContextFactory tilesRequestContextFactory;

	private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		tilesRequestContextFactory = new ServletTilesRequestContextFactory();
		tilesRequestContextFactory.init(new HashMap());
	}

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

			String[] fragmentsToRender = getRenderFragments(model, request, response);
			if (fragmentsToRender.length == 0) {
				logger.warn("An Ajax request was detected, but no fragments were specified to be re-rendered.  "
						+ "Falling back to full page render.  This can cause unpredictable results when processing "
						+ "the ajax response on the client.");
				super.renderMergedOutputModel(model, request, response);
				return;
			}

			BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getCurrentContainer(request,
					servletContext);
			if (container == null) {
				throw new ServletException("Tiles container is not initialized. "
						+ "Have you added a TilesConfigurer to your web application context?");
			}

			exposeModelAsRequestAttributes(model, request);
			JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

			TilesRequestContext tilesRequestContext = tilesRequestContextFactory.createRequestContext(container
					.getApplicationContext(), new Object[] { request, response });
			Definition compositeDefinition = container.getDefinitionsFactory().getDefinition(getUrl(),
					tilesRequestContext);

			Map flattenedAttributeMap = new HashMap();
			flattenAttributeMap(container, tilesRequestContext, flattenedAttributeMap, compositeDefinition, request,
					response);
			addRuntimeAttributes(container, flattenedAttributeMap, request, response);

			// initialize the session before rendering any fragments. Otherwise views that require the session which has
			// not otherwise been initialized will fail to render
			// request.getSession();
			// response.flushBuffer();

			for (int i = 0; i < fragmentsToRender.length; i++) {
				Attribute attributeToRender = (Attribute) flattenedAttributeMap.get(fragmentsToRender[i]);

				if (attributeToRender == null) {
					throw new ServletException("No tiles attribute with a name of '" + fragmentsToRender[i]
							+ "' could be found for the current view: " + this);
				} else {
					container.render(attributeToRender, new Object[] { request, response });
				}
			}
		} else {
			super.renderMergedOutputModel(model, request, response);
		}
	}

	protected String[] getRenderFragments(Map model, HttpServletRequest request, HttpServletResponse response) {
		String attrName = request.getParameter(FRAGMENTS_PARAM);
		String[] renderFragments = StringUtils.commaDelimitedListToStringArray(attrName);
		return StringUtils.trimArrayElements(renderFragments);
	}

	/**
	 * <p>
	 * Iterate over all attributes in the given Tiles definition. Every attribute value that represents a template (i.e.
	 * start with "/") or is a nested definition is added to a Map. The method class itself recursively to traverse
	 * nested definitions.
	 * </p>
	 * 
	 * @param container the TilesContainer
	 * @param requestContext the TilesRequestContext
	 * @param resultMap the output Map where attributes of interest are added to.
	 * @param compositeDefinition the definition to search for attributes of interest.
	 * @param request the servlet request
	 * @param response the servlet response
	 */
	protected void flattenAttributeMap(BasicTilesContainer container, TilesRequestContext requestContext,
			Map resultMap, Definition compositeDefinition, HttpServletRequest request, HttpServletResponse response) {
		Iterator iterator = compositeDefinition.getAttributeNames();
		while (iterator.hasNext()) {
			String attributeName = (String) iterator.next();
			Attribute attribute = compositeDefinition.getAttribute(attributeName);
			if (attribute.getValue() == null || !(attribute.getValue() instanceof String)) {
				continue;
			}
			String value = attribute.getValue().toString();
			if (value.startsWith("/")) {
				resultMap.put(attributeName, attribute);
			} else if (container.isValidDefinition(value, new Object[] { request, response })) {
				resultMap.put(attributeName, attribute);
				Definition nestedDefinition = container.getDefinitionsFactory().getDefinition(value, requestContext);
				Assert.isTrue(nestedDefinition != compositeDefinition, "Circular nested definition: " + value);
				flattenAttributeMap(container, requestContext, resultMap, nestedDefinition, request, response);
			}
		}
	}

	/**
	 * <p>
	 * Iterate over dynamically added Tiles attributes (see "Runtime Composition" in the Tiles documentation) and add
	 * them to the output Map passed as input.
	 * </p>
	 * 
	 * @param container the Tiles container
	 * @param resultMap the output Map where attributes of interest are added to.
	 * @param request the Servlet request
	 * @param response the Servlet response
	 */
	protected void addRuntimeAttributes(BasicTilesContainer container, Map resultMap, HttpServletRequest request,
			HttpServletResponse response) {
		AttributeContext attributeContext = container.getAttributeContext(new Object[] { request, response });
		Set attributeNames = new HashSet();
		if (attributeContext.getLocalAttributeNames() != null) {
			attributeNames.addAll(attributeContext.getLocalAttributeNames());
		}
		if (attributeContext.getCascadedAttributeNames() != null) {
			attributeNames.addAll(attributeContext.getCascadedAttributeNames());
		}
		Iterator iterator = attributeNames.iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			Attribute attr = attributeContext.getAttribute(name);
			resultMap.put(name, attr);
		}
	}
}
