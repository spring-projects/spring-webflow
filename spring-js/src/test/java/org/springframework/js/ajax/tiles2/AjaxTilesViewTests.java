package org.springframework.js.ajax.tiles2;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.Definition;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.servlet.context.ServletTilesRequestContextFactory;
import org.apache.tiles.servlet.context.ServletUtil;
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
		tc.setUseMutableTilesContainer(true);
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
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/layout.jsp", response.getForwardedUrl());
	}

	public void testAjaxRequestNoFragments() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/layout.jsp", response.getForwardedUrl());
	}

	public void testRenderFragment_Template() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "searchResults");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/searchResults.jsp", response.getForwardedUrl());
	}

	public void testRenderFragment_Definition() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "body");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/search.jsp", response.getForwardedUrl());
	}

	public void testRenderFragment_CascadedAttribute() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "searchNavigation");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertEquals("/WEB-INF/searchNavigation.jsp", response.getForwardedUrl());
	}

	public void testRenderFragment_InheritCascadedAttribute() throws Exception {
		BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getCurrentContainer(request, servletContext);
		ServletTilesRequestContextFactory tilesRequestContextFactory = new ServletTilesRequestContextFactory();
		tilesRequestContextFactory.init(new HashMap());
		TilesRequestContext tilesRequestContext = tilesRequestContextFactory.createRequestContext(container
				.getApplicationContext(), new Object[] { request, response });
		Definition definition = container.getDefinitionsFactory().getDefinition("search.body", tilesRequestContext);
		definition.setPreparer("org.springframework.js.ajax.tiles2.AjaxTilesViewTests$AttributeTestingPreparer");

		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "body");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertTrue(AttributeTestingPreparer.invoked);
	}

	public void testRenderFragment_DynamicAttribute() throws Exception {
		BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getCurrentContainer(request, servletContext);
		Object[] requestItems = new Object[] { request, response };
		AttributeContext attributeContext = container.startContext(requestItems);
		attributeContext.putAttribute("body", new Attribute("/WEB-INF/dynamicTemplate.jsp"));
		Map resultMap = new HashMap();
		ajaxTilesView.addRuntimeAttributes(container, resultMap, request, response);
		assertNotNull(resultMap.get("body"));
		assertEquals("/WEB-INF/dynamicTemplate.jsp", resultMap.get("body").toString());
		container.endContext(requestItems);
	}

	public void testRenderFragment_Multiple() throws Exception {
		setupStaticWebApplicationContext();
		request.addHeader("Accept", SpringJavascriptAjaxHandler.AJAX_ACCEPT_CONTENT_TYPE);
		request.addParameter("fragments", "body,searchNavigation");
		ajaxTilesView.setUrl("search");
		ajaxTilesView.afterPropertiesSet();
		ajaxTilesView.renderMergedOutputModel(new HashMap(), request, response);
		assertTrue("Multiple fragments should result in include, not forward", response.getIncludedUrls().size() == 2);
		assertEquals("/WEB-INF/search.jsp", response.getIncludedUrls().get(0));
		assertEquals("/WEB-INF/searchNavigation.jsp", response.getIncludedUrls().get(1));
	}

	public void testFlattenAttributeMap() throws Exception {
		TilesRequestContextFactory tilesRequestContextFactory = new ServletTilesRequestContextFactory();
		tilesRequestContextFactory.init(new HashMap());
		BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getCurrentContainer(request, servletContext);
		TilesRequestContext tilesRequestContext = tilesRequestContextFactory.createRequestContext(container
				.getApplicationContext(), new Object[] { request, response });
		Definition compositeDefinition = container.getDefinitionsFactory().getDefinition("search", tilesRequestContext);
		Map resultMap = new HashMap();
		ajaxTilesView.flattenAttributeMap(container, tilesRequestContext, resultMap, compositeDefinition, request,
				response);
		assertNotNull(resultMap.get("body"));
		assertNotNull(resultMap.get("searchForm"));
		assertEquals("/WEB-INF/searchForm.jsp", resultMap.get("searchForm").toString());
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

	public static class AttributeTestingPreparer implements ViewPreparer {
		public static boolean invoked;

		public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
			invoked = true;
			Assert.assertTrue(attributeContext.getAttribute("searchNavigation") != null);
		}
	}

}
