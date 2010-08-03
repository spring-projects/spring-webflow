package org.springframework.faces.webflow;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;

import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockApplicationFactory;
import org.apache.myfaces.test.mock.MockExternalContext;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.myfaces.test.mock.MockHttpServletResponse;
import org.apache.myfaces.test.mock.MockHttpSession;
import org.apache.myfaces.test.mock.MockPartialViewContextFactory;
import org.apache.myfaces.test.mock.MockPrintWriter;
import org.apache.myfaces.test.mock.MockRenderKit;
import org.apache.myfaces.test.mock.MockRenderKitFactory;
import org.apache.myfaces.test.mock.MockResponseWriter;
import org.apache.myfaces.test.mock.MockServletConfig;
import org.apache.myfaces.test.mock.MockServletContext;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycle;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycleFactory;

/**
 * Helper for using the mock JSF environment provided by shale-test inside unit tests that do not extend
 * {@link AbstractJsfTestCase}
 * 
 * @author Jeremy Grelle
 * @author Phil Webb
 */
public class JSFMockHelper {

	private JSFMock mock = new JSFMock();

	public Application application() {
		return mock.application();
	}

	public MockServletConfig config() {
		return mock.config();
	}

	public String contentAsString() throws IOException {
		return mock.contentAsString();
	}

	public MockExternalContext externalContext() {
		return mock.externalContext();
	}

	public FacesContext facesContext() {
		return mock.facesContext();
	}

	public FacesContextFactory facesContextFactory() {
		return mock.facesContextFactory();
	}

	public MockLifecycle lifecycle() {
		return mock.lifecycle();
	}

	public MockLifecycleFactory lifecycleFactory() {
		return mock.lifecycleFactory();
	}

	public MockRenderKit renderKit() {
		return mock.renderKit();
	}

	public MockHttpServletRequest request() {
		return mock.request();
	}

	public MockHttpServletResponse response() {
		return mock.response();
	}

	public MockServletContext servletContext() {
		return mock.servletContext();
	}

	public MockHttpSession session() {
		return mock.session();
	}

	public void setUp() throws Exception {
		mock.setUp();
	}

	public void tearDown() throws Exception {
		mock.tearDown();
	}

	private static class JSFMock extends AbstractJsfTestCase {

		private ClassLoader threadContextClassLoader;

		public JSFMock() {
			super("JSFMock");
		}

		FacesContext facesContext;
		FacesContextFactory facesContextFactory;

		public void setUp() throws Exception {

			// Set up a new thread context class loader
			threadContextClassLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					new URLClassLoader(new URL[0], this.getClass().getClassLoader()));

			// Set up Servlet API Objects
			servletContext = new MockServletContext();
			config = new MockServletConfig(servletContext);
			session = new MockHttpSession();
			session.setServletContext(servletContext);
			request = new MockHttpServletRequest(session);
			request.setServletContext(servletContext);
			response = new MockHttpServletResponse();

			// Set up JSF API Objects
			FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockBaseFacesContextFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, MockLifecycleFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, MockRenderKitFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY, MockPartialViewContextFactory.class
					.getName());
			lifecycleFactory = (MockLifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			lifecycle = (MockLifecycle) lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
			externalContext = (MockExternalContext) facesContext.getExternalContext();
			facesContext.setResponseWriter(new MockResponseWriter(response.getWriter()));

			UIViewRoot root = new UIViewRoot();
			root.setViewId("/viewId");
			root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			facesContext.setViewRoot(root);
			ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
					.getFactory(FactoryFinder.APPLICATION_FACTORY);
			application = (org.apache.myfaces.test.mock.MockApplication) applicationFactory.getApplication();
			RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
					.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
			renderKit = new MockRenderKit();
			renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT, renderKit);
		}

		public void tearDown() throws Exception {
			application = null;
			config = null;
			externalContext = null;
			if (facesContext != null) {
				facesContext.release();
			}
			facesContext = null;
			lifecycle = null;
			lifecycleFactory = null;
			renderKit = null;
			request = null;
			response = null;
			servletContext = null;
			session = null;
			FactoryFinder.releaseFactories();

			Thread.currentThread().setContextClassLoader(threadContextClassLoader);
			threadContextClassLoader = null;
		}

		public org.apache.myfaces.test.mock.MockApplication application() {
			return application;
		}

		public MockServletConfig config() {
			return config;
		}

		public String contentAsString() throws IOException {
			MockPrintWriter writer = (MockPrintWriter) response.getWriter();
			return new String(writer.content());
		}

		public MockExternalContext externalContext() {
			return externalContext;
		}

		public FacesContext facesContext() {
			return facesContext;
		}

		public FacesContextFactory facesContextFactory() {
			return facesContextFactory;
		}

		public MockLifecycle lifecycle() {
			return lifecycle;
		}

		public MockLifecycleFactory lifecycleFactory() {
			return lifecycleFactory;
		}

		public MockRenderKit renderKit() {
			return renderKit;
		}

		public MockHttpServletRequest request() {
			return request;
		}

		public MockHttpServletResponse response() {
			return response;
		}

		public MockServletContext servletContext() {
			return servletContext;
		}

		public MockHttpSession session() {
			return session;
		}

	}

}
