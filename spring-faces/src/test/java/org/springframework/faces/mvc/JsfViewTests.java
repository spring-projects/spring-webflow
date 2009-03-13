package org.springframework.faces.mvc;

import java.util.HashMap;
import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.faces.webflow.MockViewHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

public class JsfViewTests extends TestCase {

	UrlBasedViewResolver resolver;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	public void setUp() throws Exception {
		jsfMock.setUp();
		jsfMock.facesContext().getApplication().setViewHandler(new ResourceCheckingViewHandler());

		resolver = new UrlBasedViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".xhtml");
		resolver.setViewClass(JsfView.class);
		resolver.setApplicationContext(new StaticWebApplicationContext());
	}

	public void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public void testViewResolution() throws Exception {
		View view = resolver.resolveViewName("intro", new Locale("EN"));
		assertTrue(view instanceof JsfView);
	}

	public void testViewRender() throws Exception {
		JsfView view = (JsfView) resolver.resolveViewName("intro", new Locale("EN"));
		view.setApplicationContext(new StaticWebApplicationContext());
		view.setServletContext(new MockServletContext());
		view.render(new HashMap(), new MockHttpServletRequest(), new MockHttpServletResponse());
	}

	private class ResourceCheckingViewHandler extends MockViewHandler {

		public UIViewRoot createView(FacesContext context, String viewId) {
			assertNotNull(viewId);
			assertEquals("/WEB-INF/views/intro.xhtml", viewId);
			return new UIViewRoot();
		}

	}
}
