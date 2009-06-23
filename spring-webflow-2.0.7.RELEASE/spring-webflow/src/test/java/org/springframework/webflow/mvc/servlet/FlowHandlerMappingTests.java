package org.springframework.webflow.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.execution.FlowExecutionOutcome;

public class FlowHandlerMappingTests extends TestCase {
	private FlowHandlerMapping mapping = new FlowHandlerMapping();

	public void setUp() {
		FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();
		registry.registerFlowDefinition(new FlowDefinitionImpl());
		registry.registerFlowDefinition(new FlowDefinitionImpl("foo/flow2"));
		StaticWebApplicationContext context = new StaticWebApplicationContext();
		context.getBeanFactory().registerSingleton("foo/flow2", new CustomFlowHandler());
		mapping.setFlowRegistry(registry);
		mapping.setServletContext(new MockServletContext());
		mapping.setApplicationContext(context);
	}

	public void testGetHandler() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/flow");
		request.setRequestURI("/springtravel/app/flow");
		request.setMethod("GET");
		HandlerExecutionChain chain = mapping.getHandler(request);
		FlowHandler handler = (FlowHandler) chain.getHandler();
		assertEquals("flow", handler.getFlowId());
	}

	public void testGetHandlerCustomFlowHandler() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo/flow2");
		request.setRequestURI("/springtravel/app/foo/flow2");
		request.setMethod("GET");
		HandlerExecutionChain chain = mapping.getHandler(request);
		assertNotNull(chain);
		FlowHandler handler = (FlowHandler) chain.getHandler();
		assertEquals("foo/flow2", handler.getFlowId());
		assertTrue(handler instanceof CustomFlowHandler);
	}

	public void testGetHandlerNoHandler() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/bogus");
		request.setRequestURI("/springtravel/app/bogus");
		request.setMethod("GET");
		HandlerExecutionChain chain = mapping.getHandler(request);
		assertNull(chain);
	}

	public void testGetHandlerNullFlowId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HandlerExecutionChain chain = mapping.getHandler(request);
		assertNull(chain);
	}

	private static class FlowDefinitionImpl implements FlowDefinition {

		private String flowId = "flow";

		public FlowDefinitionImpl() {

		}

		public FlowDefinitionImpl(String flowId) {
			super();
			this.flowId = flowId;
		}

		public ApplicationContext getApplicationContext() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ClassLoader getClassLoader() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getId() {
			return flowId;
		}

		public String[] getPossibleOutcomes() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public StateDefinition getStartState() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public StateDefinition getState(String id) throws IllegalArgumentException {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public boolean inDevelopment() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public MutableAttributeMap getAttributes() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getCaption() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getDescription() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public void destroy() {
		}

	}

	public static class CustomFlowHandler implements FlowHandler {

		public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getFlowId() {
			return "foo/flow2";
		}

		public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
				HttpServletResponse response) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}
}
