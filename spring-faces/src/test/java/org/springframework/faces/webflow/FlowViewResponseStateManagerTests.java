package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowViewResponseStateManagerTests extends TestCase {

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private FlowViewResponseStateManager responseStateManager;

	private RequestContext requestContext;
	private FlowExecutionContext flowExecutionContext;

	protected void setUp() throws Exception {
		jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(jsfMock.servletContext());

		requestContext = EasyMock.createMock(RequestContext.class);
		RequestContextHolder.setRequestContext(requestContext);
		flowExecutionContext = EasyMock.createMock(FlowExecutionContext.class);

		responseStateManager = new FlowViewResponseStateManager(null);
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public void testWriteFlowSerializedView() throws Exception {
		EasyMock.expect(flowExecutionContext.getKey()).andReturn(new MockFlowExecutionKey("e1s1"));
		LocalAttributeMap viewMap = new LocalAttributeMap();
		EasyMock.expect(requestContext.getViewScope()).andStubReturn(viewMap);
		EasyMock.expect(requestContext.getFlowExecutionContext()).andReturn(flowExecutionContext);
		EasyMock.replay(requestContext, flowExecutionContext);

		FlowSerializedView flowSerializedView = new FlowSerializedView("viewId", null, null);
		responseStateManager.writeState(jsfMock.facesContext(), flowSerializedView);

		assertEquals(flowSerializedView, viewMap.get(FlowViewStateManager.SERIALIZED_VIEW_STATE));
		assertEquals(
				"<input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"e1s1\" />",
				jsfMock.contentAsString());
		EasyMock.verify(flowExecutionContext, requestContext);
	}

	public void testGetState() throws Exception {
		Object treeStructure = new Object();
		Object componentState = new Object();
		FlowSerializedView flowSerializedView = new FlowSerializedView("viewId", treeStructure, componentState);

		LocalAttributeMap viewMap = new LocalAttributeMap();
		viewMap.put(FlowViewStateManager.SERIALIZED_VIEW_STATE, flowSerializedView);
		EasyMock.expect(requestContext.getViewScope()).andStubReturn(viewMap);
		EasyMock.replay(requestContext);

		Object state = responseStateManager.getState(jsfMock.facesContext(), "viewId");

		assertTrue(state instanceof Object[]);
		assertSame(treeStructure, ((Object[]) state)[0]);
		assertSame(componentState, ((Object[]) state)[1]);
		EasyMock.verify(requestContext);
	}

}
