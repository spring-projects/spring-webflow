package org.springframework.faces.webflow;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import jakarta.faces.FactoryFinder;
import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.lifecycle.LifecycleFactory;
import jakarta.faces.render.RenderKitFactory;
import org.apache.myfaces.test.base.junit.AbstractJsfTestCase;
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
import org.apache.myfaces.test.mock.visit.MockVisitContextFactory;

/**
 * Helper for using the mock JSF environment provided by shale-test inside unit tests that do not extend
 * {@link AbstractJsfTestCase}
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JSFMockHelper {

	private final JSFMock mock = new JSFMock();

	public Application application() {
		return this.mock.application();
	}

	public MockServletConfig config() {
		return this.mock.config();
	}

	public String contentAsString() throws IOException {
		return this.mock.contentAsString();
	}

	public MockExternalContext externalContext() {
		return this.mock.externalContext();
	}

	public FacesContext facesContext() {
		return this.mock.facesContext();
	}

	public FacesContextFactory facesContextFactory() {
		return this.mock.facesContextFactory();
	}

	public MockLifecycle lifecycle() {
		return this.mock.lifecycle();
	}

	public MockLifecycleFactory lifecycleFactory() {
		return this.mock.lifecycleFactory();
	}

	public MockRenderKit renderKit() {
		return this.mock.renderKit();
	}

	public MockHttpServletRequest request() {
		return this.mock.request();
	}

	public MockHttpServletResponse response() {
		return this.mock.response();
	}

	public MockServletContext servletContext() {
		return this.mock.servletContext();
	}

	public MockHttpSession session() {
		return this.mock.session();
	}

	public void setUp() throws Exception {
		this.mock.setUp();
	}

	public void tearDown() throws Exception {
		this.mock.tearDown();
	}

	private static class JSFMock extends AbstractJsfTestCase {

		private ClassLoader threadContextClassLoader;

		public JSFMock() {
			super();
		}

		FacesContext facesContext;
		FacesContextFactory facesContextFactory;

		public void setUp() throws Exception {

			// Ensure no pre-existing FacesContext ..
			if (FacesContext.getCurrentInstance() != null) {
				FacesContext.getCurrentInstance().release();
			}

			// Set up a new thread context class loader
			this.threadContextClassLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					new URLClassLoader(new URL[0], this.getClass().getClassLoader()));

			// Set up Servlet API Objects
			this.servletContext = new MockServletContext();
			this.config = new MockServletConfig(this.servletContext);
			this.session = new MockHttpSession();
			this.session.setServletContext(this.servletContext);
			this.request = new MockHttpServletRequest(this.session);
			this.request.setServletContext(this.servletContext);
			this.response = new MockHttpServletResponse();

			// Set up JSF API Objects
			FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockBaseFacesContextFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, MockLifecycleFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, MockRenderKitFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY,
					MockPartialViewContextFactory.class.getName());
			FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, MockVisitContextFactory.class.getName());
			this.lifecycleFactory = (MockLifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			this.lifecycle = (MockLifecycle) this.lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			this.facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			this.facesContext = this.facesContextFactory.getFacesContext(this.servletContext, this.request, this.response, this.lifecycle);
			this.externalContext = (MockExternalContext) this.facesContext.getExternalContext();
			this.facesContext.setResponseWriter(new MockResponseWriter(this.response.getWriter()));

			UIViewRoot root = new UIViewRoot();
			root.setViewId("/viewId");
			root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			this.facesContext.setViewRoot(root);
			ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
					.getFactory(FactoryFinder.APPLICATION_FACTORY);
			this.application = (org.apache.myfaces.test.mock.MockApplication) applicationFactory.getApplication();
			RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
					.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
			this.renderKit = new MockRenderKit();
			renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT, this.renderKit);
		}

		public void tearDown() throws Exception {
			this.application = null;
			this.config = null;
			this.externalContext = null;
			if (this.facesContext != null) {
				this.facesContext.release();
			}
			this.facesContext = null;
			this.lifecycle = null;
			this.lifecycleFactory = null;
			this.renderKit = null;
			this.request = null;
			this.response = null;
			this.servletContext = null;
			this.session = null;
			FactoryFinder.releaseFactories();

			Thread.currentThread().setContextClassLoader(this.threadContextClassLoader);
			this.threadContextClassLoader = null;
		}

		public org.apache.myfaces.test.mock.MockApplication application() {
			return this.application;
		}

		public MockServletConfig config() {
			return this.config;
		}

		public String contentAsString() throws IOException {
			MockPrintWriter writer = (MockPrintWriter) this.response.getWriter();
			return new String(writer.content());
		}

		public MockExternalContext externalContext() {
			return this.externalContext;
		}

		public FacesContext facesContext() {
			return this.facesContext;
		}

		public FacesContextFactory facesContextFactory() {
			return this.facesContextFactory;
		}

		public MockLifecycle lifecycle() {
			return this.lifecycle;
		}

		public MockLifecycleFactory lifecycleFactory() {
			return this.lifecycleFactory;
		}

		public MockRenderKit renderKit() {
			return this.renderKit;
		}

		public MockHttpServletRequest request() {
			return this.request;
		}

		public MockHttpServletResponse response() {
			return this.response;
		}

		public MockServletContext servletContext() {
			return this.servletContext;
		}

		public MockHttpSession session() {
			return this.session;
		}

	}

}
