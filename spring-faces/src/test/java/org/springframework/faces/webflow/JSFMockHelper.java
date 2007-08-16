package org.springframework.faces.webflow;

import org.apache.shale.test.base.AbstractJsfTestCase;
import org.apache.shale.test.mock.MockApplication;
import org.apache.shale.test.mock.MockExternalContext;
import org.apache.shale.test.mock.MockFacesContextFactory;
import org.apache.shale.test.mock.MockHttpServletRequest;
import org.apache.shale.test.mock.MockHttpServletResponse;
import org.apache.shale.test.mock.MockHttpSession;
import org.apache.shale.test.mock.MockLifecycle;
import org.apache.shale.test.mock.MockLifecycleFactory;
import org.apache.shale.test.mock.MockRenderKit;
import org.apache.shale.test.mock.MockServletConfig;
import org.apache.shale.test.mock.MockFacesContext;
import org.apache.shale.test.mock.MockServletContext;

/**
 * Helper for using the mock JSF environment provided by shale-test inside unit tests that do not extend
 * {@link AbstractJsfTestCase}
 * @author Jeremy Grelle
 * 
 */
public class JSFMockHelper {

	private JSFMock mock = new JSFMock();

	public MockApplication application() {
		return mock.application();
	}

	public MockServletConfig config() {
		return mock.config();
	}

	public MockExternalContext externalContext() {
		return mock.externalContext();
	}

	public MockFacesContext facesContext() {
		return mock.facesContext();
	}

	public MockFacesContextFactory facesContextFactory() {
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

		public void setUp() throws Exception {
			super.setUp();
		}

		public void tearDown() throws Exception {
			super.tearDown();
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

		public MockFacesContext facesContext() {
			return facesContext;
		}

		public MockFacesContextFactory facesContextFactory() {
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
