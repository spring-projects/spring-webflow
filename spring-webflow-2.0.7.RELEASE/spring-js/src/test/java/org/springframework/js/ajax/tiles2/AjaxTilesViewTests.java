package org.springframework.js.ajax.tiles2;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;

public class AjaxTilesViewTests extends TestCase {

	private AjaxTilesView ajaxTilesView;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockServletContext servletContext;

	protected void setUp() throws Exception {

		servletContext = new MockServletContext("/org/springframework/js/ajax/tiles2/");
		request = new MockHttpServletRequest(servletContext);
		response = new MockHttpServletResponse();

		TilesConfigurer tc = new TilesConfigurer();
		tc.setDefinitions(new String[] { "tiles-definitions.xml" });
		tc.setValidateDefinitions(true);
		tc.setServletContext(servletContext);
		tc.afterPropertiesSet();

		ajaxTilesView = new AjaxTilesView();
	}

	private void setupStaticWebApplicationContext() {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.refresh();
		request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		ajaxTilesView.setApplicationContext(wac);
	}

	public void testFullPageRendering() throws Exception {
		setupStaticWebApplicationContext();
		ajaxTilesView.setUrl("search");
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/layout.jsp", response.getForwardedUrl());
	}

	public void testAjaxRequestNoFragments() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		ajaxTilesView.setUrl("search");
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/layout.jsp", response.getForwardedUrl());
	}

	public void testRenderFragment_Template() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "searchResults");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/searchResults.jsp", response.getIncludedUrl());
	}

	public void testRenderFragment_Definition() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "body");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/search.jsp", response.getIncludedUrl());
	}

	public void testFlattenAttributeMap() throws Exception {
		BasicTilesContainer container = (BasicTilesContainer) TilesAccess.getContainer(servletContext);
		TilesRequestContext tilesRequestContext = container.getContextFactory().createRequestContext(
				container.getApplicationContext(), new Object[] { request, response });
		Definition compositeDefinition = container.getDefinitionsFactory().getDefinition("search", tilesRequestContext);
		Map resultMap = new HashMap();
		ajaxTilesView.flattenAttributeMap(container, tilesRequestContext, resultMap, compositeDefinition, request,
				response);
		assertNotNull(resultMap.get("body"));
		assertNotNull(resultMap.get("searchForm"));
		assertEquals("/WEB-INF/searchForm.jsp", ((Attribute) resultMap.get("searchForm")).getValue());
		assertNotNull(resultMap.get("searchResults"));
	}

	public void testGetRenderFragments() throws Exception {
		Map model = new HashMap();
		request.setParameter("fragments", "f1,f2,  f3");
		String[] fragments = ajaxTilesView.getRenderFragments(model, request, response);
		assertEquals("f1", fragments[0]);
		assertEquals("f2", fragments[1]);
		assertEquals("f3", fragments[2]);
	}
}
