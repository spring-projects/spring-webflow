package org.springframework.webflow.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.view.MvcViewFactoryCreator;
import org.springframework.webflow.test.GeneratedFlowExecutionKey;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

public class MvcViewFactoryTests extends TestCase {
	private MvcViewFactoryCreator creator;
	private StaticApplicationContext context;

	protected void setUp() {
		creator = new MvcViewFactoryCreator();
		context = new StaticApplicationContext();
	}

	public void testNoResolversGetResource() throws Exception {
		creator.setApplicationContext(context);
		ResourceLoader viewResourceLoader = new ResourceLoader() {
			public ClassLoader getClassLoader() {
				return ClassUtils.getDefaultClassLoader();
			}

			public Resource getResource(String name) {
				return new TestContextResource("/parent/" + name);
			}
		};
		Expression viewId = new StaticExpression("myview.jsp");
		ViewFactory viewFactory = creator.createViewFactory(viewId, viewResourceLoader);
		MockRequestContext context = new MockRequestContext();
		MockExternalContext externalContext = new MockExternalContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		externalContext.setNativeRequest(request);
		externalContext.setNativeResponse(response);
		context.setExternalContext(externalContext);
		context.getMockFlowExecutionContext().setKey(new GeneratedFlowExecutionKey());
		View view = viewFactory.getView(context);
		assertEquals(false, view.eventSignaled());
		view.render();
		assertEquals("/parent/myview.jsp", response.getForwardedUrl());
	}

	public void testViewResolversGetResource() throws Exception {
		MockViewResolver viewResolver = new MockViewResolver("myview");
		creator.setApplicationContext(context);
		creator.setViewResolvers(Collections.singletonList(viewResolver));
		Expression viewId = new StaticExpression("myview");
		ViewFactory viewFactory = creator.createViewFactory(viewId, null);
		MockRequestContext context = new MockRequestContext();
		MockExternalContext externalContext = new MockExternalContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		externalContext.setNativeRequest(request);
		externalContext.setNativeResponse(response);
		context.setExternalContext(externalContext);
		context.getMockFlowExecutionContext().setKey(new GeneratedFlowExecutionKey());
		View view = viewFactory.getView(context);
		assertEquals(false, view.eventSignaled());
		view.render();
		assertEquals("myview", response.getForwardedUrl());
	}

	public void testRestoreView() throws Exception {
		creator.setApplicationContext(context);
		ResourceLoader viewResourceLoader = new ResourceLoader() {
			public ClassLoader getClassLoader() {
				return ClassUtils.getDefaultClassLoader();
			}

			public Resource getResource(String name) {
				return new TestContextResource("/parent/" + name);
			}
		};
		Expression viewId = new StaticExpression("myview.jsp");
		ViewFactory viewFactory = creator.createViewFactory(viewId, viewResourceLoader);
		MockRequestContext context = new MockRequestContext();
		MockExternalContext externalContext = new MockExternalContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		externalContext.putRequestParameter("_eventId", "foo");
		externalContext.setNativeRequest(request);
		externalContext.setNativeResponse(response);
		context.setExternalContext(externalContext);
		context.getMockFlowExecutionContext().setKey(new GeneratedFlowExecutionKey());
		View view = viewFactory.getView(context);
		view.resume();
		assertEquals(true, view.eventSignaled());
		Event e = view.getEvent();
		assertEquals(view, e.getSource());
		assertEquals("foo", e.getId());
		view.render();
		assertEquals("/parent/myview.jsp", response.getForwardedUrl());
	}

	public void testRestoreViewButtonEventIdFormat() throws Exception {
		creator.setApplicationContext(context);
		ResourceLoader viewResourceLoader = new ResourceLoader() {
			public ClassLoader getClassLoader() {
				return ClassUtils.getDefaultClassLoader();
			}

			public Resource getResource(String name) {
				return new TestContextResource("/parent/" + name);
			}
		};
		Expression viewId = new StaticExpression("myview.jsp");
		ViewFactory viewFactory = creator.createViewFactory(viewId, viewResourceLoader);
		MockRequestContext context = new MockRequestContext();
		MockExternalContext externalContext = new MockExternalContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		externalContext.putRequestParameter("_eventId_foo", "true");
		externalContext.setNativeRequest(request);
		externalContext.setNativeResponse(response);
		context.setExternalContext(externalContext);
		context.getMockFlowExecutionContext().setKey(new GeneratedFlowExecutionKey());
		View view = viewFactory.getView(context);
		view.resume();
		assertEquals(true, view.eventSignaled());
		Event e = view.getEvent();
		assertEquals(view, e.getSource());
		assertEquals("foo", e.getId());
		view.render();
		assertEquals("/parent/myview.jsp", response.getForwardedUrl());
	}

	private static class MockViewResolver implements ViewResolver {

		private String expectedViewName;

		public MockViewResolver(String expectedViewName) {
			this.expectedViewName = expectedViewName;
		}

		public org.springframework.web.servlet.View resolveViewName(String viewName, Locale arg1) throws Exception {
			assertEquals(expectedViewName, viewName);
			return new MockView();
		}

		class MockView implements org.springframework.web.servlet.View {
			boolean renderCalled;

			public String getContentType() {
				return "text/html";
			}

			public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				request.getRequestDispatcher(expectedViewName).forward(request, response);
				renderCalled = true;
			}
		}

	}

	private static class TestContextResource extends AbstractResource implements ContextResource {
		private String path;

		public TestContextResource(String path) {
			this.path = path;
		}

		public String getDescription() {
			return "test context resource";
		}

		public InputStream getInputStream() throws IOException {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getPathWithinContext() {
			return path;
		}
	}
}