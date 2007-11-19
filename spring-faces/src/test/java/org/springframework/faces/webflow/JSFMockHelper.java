package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;

import org.apache.shale.test.base.AbstractJsfTestCase;
import org.apache.shale.test.mock.MockApplication;
import org.apache.shale.test.mock.MockExternalContext;
import org.apache.shale.test.mock.MockHttpServletRequest;
import org.apache.shale.test.mock.MockHttpServletResponse;
import org.apache.shale.test.mock.MockHttpSession;
import org.apache.shale.test.mock.MockLifecycle;
import org.apache.shale.test.mock.MockLifecycleFactory;
import org.apache.shale.test.mock.MockRenderKit;
import org.apache.shale.test.mock.MockServletConfig;
import org.apache.shale.test.mock.MockServletContext;

/**
 * Helper for using the mock JSF environment provided by shale-test inside unit tests that do not extend
 * {@link AbstractJsfTestCase}
 * @author Jeremy Grelle
 * 
 */
public class JSFMockHelper {

	private JSFMock mock = new JSFMock();

	public Application application() {
		return mock.application();
	}

	public MockServletConfig config() {
		return mock.config();
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

		public JSFMock() {
			super("JSFMock");
		}

		FacesContextFactory facesContextFactory;
		FacesContext facesContext;

		public void setUp() throws Exception {

			// Thread.currentThread().setContextClassLoader(
			// new URLClassLoader(new URL[0], this.getClass().getClassLoader()));

			// Set up Servlet API Objects
			servletContext = new MockServletContext();
			config = new MockServletConfig(servletContext);
			session = new MockHttpSession();
			session.setServletContext(servletContext);
			request = new MockHttpServletRequest(session);
			request.setServletContext(servletContext);
			response = new MockHttpServletResponse();

			// Set up JSF API Objects
			FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY,
					"org.apache.shale.test.mock.MockApplicationFactory");
			FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY,
					"org.springframework.faces.webflow.MockBaseFacesContextFactory");
			/*
			 * FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY,
			 * "org.apache.shale.test.mock.MockFacesContextFactory");
			 */
			FactoryFinder
					.setFactory(FactoryFinder.LIFECYCLE_FACTORY, "org.apache.shale.test.mock.MockLifecycleFactory");
			FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY,
					"org.apache.shale.test.mock.MockRenderKitFactory");

			application = new MockApplication();
			externalContext = new MockExternalContext(servletContext, request, response);
			lifecycleFactory = (MockLifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			lifecycle = (MockLifecycle) lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
			externalContext = (MockExternalContext) facesContext.getExternalContext();
			UIViewRoot root = new UIViewRoot();
			root.setViewId("/viewId");
			root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			facesContext.setViewRoot(root);
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

		}

		public MockApplication application() {
			return application;
		}

		public MockServletConfig config() {
			return config;
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
