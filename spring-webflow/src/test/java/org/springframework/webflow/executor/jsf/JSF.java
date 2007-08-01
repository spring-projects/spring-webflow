package org.springframework.webflow.executor.jsf;

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

public class JSF extends AbstractJsfTestCase {

	public JSF(String name) {
		super(name);
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
