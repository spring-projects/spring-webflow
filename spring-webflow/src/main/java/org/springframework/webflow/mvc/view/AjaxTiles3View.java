/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.webflow.mvc.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.Definition;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.webflow.context.servlet.AjaxHandler;
import org.springframework.webflow.context.servlet.DefaultAjaxHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.tiles3.TilesView;

/**
 * Tiles 3 view implementation that is able to handle partial rendering for Spring
 * Javascript Ajax requests.
 *
 * <p>This implementation uses the {@link DefaultAjaxHandler} by default
 * to determine whether the current request is an Ajax request. On an Ajax request,
 * a "fragments" parameter will be extracted from the request in order to
 * determine which attributes to render from the current tiles view.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class AjaxTiles3View extends TilesView {

	private static final String FRAGMENTS_PARAM = "fragments";

	private AjaxHandler ajaxHandler = new DefaultAjaxHandler();


	public AjaxHandler getAjaxHandler() {
		return this.ajaxHandler;
	}

	public void setAjaxHandler(AjaxHandler ajaxHandler) {
		this.ajaxHandler = ajaxHandler;
	}


	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

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

			Request tilesRequest = createTilesRequest(request, response);
			ApplicationContext tilesAppContext = tilesRequest.getApplicationContext();
			BasicTilesContainer container = (BasicTilesContainer) TilesAccess.getContainer(tilesAppContext);

			if (container == null) {
				throw new ServletException("Tiles container is not initialized. "
						+ "Have you added a TilesConfigurer to your web application context?");
			}

			exposeModelAsRequestAttributes(model, request);
			JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

			Definition compositeDefinition = container.getDefinitionsFactory().getDefinition(getUrl(), tilesRequest);

			Map<String, Attribute> flattenedAttributeMap = new HashMap<String, Attribute>();
			flattenAttributeMap(container, tilesRequest, flattenedAttributeMap, compositeDefinition);
			addRuntimeAttributes(container, tilesRequest, flattenedAttributeMap);

			if (fragmentsToRender.length > 1) {
				tilesRequest.getContext("request").put(ServletRequest.FORCE_INCLUDE_ATTRIBUTE_NAME, true);
			}

			for (String element : fragmentsToRender) {
				Attribute attributeToRender = flattenedAttributeMap.get(element);
				if (attributeToRender == null) {
					throw new ServletException("No tiles attribute with a name of '" + element
							+ "' could be found for the current view: " + this);
				}
				container.startContext(tilesRequest).inheritCascadedAttributes(compositeDefinition);
				container.render(attributeToRender, tilesRequest);
				container.endContext(tilesRequest);
			}
		} else {
			super.renderMergedOutputModel(model, request, response);
		}
	}

	protected String[] getRenderFragments(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) {

		String attrName = request.getParameter(FRAGMENTS_PARAM);
		String[] renderFragments = StringUtils.commaDelimitedListToStringArray(attrName);
		return StringUtils.trimArrayElements(renderFragments);
	}

	/**
	 * Iterate over all attributes in the given Tiles definition. Every attribute
	 * value that represents a template (i.e. start with "/") or is a nested
	 * definition is added to a Map. The method class itself recursively to traverse
	 * nested definitions.
	 *
	 * @param container the TilesContainer
	 * @param tilesRequest the Tiles Request
	 * @param resultMap the output Map where attributes of interest are added to.
	 * @param definition the definition to search for attributes of interest.
	 */
	protected void flattenAttributeMap(BasicTilesContainer container, Request tilesRequest,
			Map<String, Attribute> resultMap, Definition definition) {

		Set<String> attributeNames = new HashSet<String>();
		if (definition.getLocalAttributeNames() != null) {
			attributeNames.addAll(definition.getLocalAttributeNames());
		}
		if (definition.getCascadedAttributeNames() != null) {
			attributeNames.addAll(definition.getCascadedAttributeNames());
		}
		for (String attributeName : attributeNames) {
			Attribute attribute = definition.getAttribute(attributeName);
			if (attribute.getValue() == null || !(attribute.getValue() instanceof String)) {
				continue;
			}
			String value = attribute.getValue().toString();
			if (value.startsWith("/")) {
				resultMap.put(attributeName, attribute);
			} else if (container.isValidDefinition(value, tilesRequest)) {
				resultMap.put(attributeName, attribute);
				Definition nestedDefinition = container.getDefinitionsFactory().getDefinition(value, tilesRequest);
				Assert.isTrue(nestedDefinition != definition, "Circular nested definition: " + value);
				flattenAttributeMap(container, tilesRequest, resultMap, nestedDefinition);
			}
		}
	}

	/**
	 * Iterate over dynamically added Tiles attributes (see "Runtime Composition"
	 * in the Tiles documentation) and add them to the output Map passed as input.
	 *
	 * @param container the Tiles container
	 * @param tilesRequest the Tiles request
	 * @param resultMap the output Map where attributes of interest are added to.
	 */
	protected void addRuntimeAttributes(BasicTilesContainer container,
			Request tilesRequest, Map<String, Attribute> resultMap) {

		AttributeContext attributeContext = container.getAttributeContext(tilesRequest);
		Set<String> attributeNames = new HashSet<String>();
		if (attributeContext.getLocalAttributeNames() != null) {
			attributeNames.addAll(attributeContext.getLocalAttributeNames());
		}
		if (attributeContext.getCascadedAttributeNames() != null) {
			attributeNames.addAll(attributeContext.getCascadedAttributeNames());
		}
		for (String name : attributeNames) {
			Attribute attr = attributeContext.getAttribute(name);
			resultMap.put(name, attr);
		}
	}
}
