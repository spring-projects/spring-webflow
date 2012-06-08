package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowResponseStateManagerTests extends TestCase {

	private final JSFMockHelper jsfMock = new JSFMockHelper();

	private FlowResponseStateManager responseStateManager;

	private RequestContext requestContext;
	private FlowExecutionContext flowExecutionContext;

	protected void setUp() throws Exception {
		this.jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(this.jsfMock.servletContext());

		this.requestContext = EasyMock.createMock(RequestContext.class);
		RequestContextHolder.setRequestContext(this.requestContext);
		this.flowExecutionContext = EasyMock.createMock(FlowExecutionContext.class);

		this.responseStateManager = new FlowResponseStateManager(null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public void testname() throws Exception {

	}

	public void testWriteFlowSerializedView() throws Exception {
		EasyMock.expect(this.flowExecutionContext.getKey()).andReturn(new MockFlowExecutionKey("e1s1"));
		LocalAttributeMap<Object> viewMap = new LocalAttributeMap<Object>();
		EasyMock.expect(this.requestContext.getViewScope()).andStubReturn(viewMap);
		EasyMock.expect(this.requestContext.getFlowExecutionContext()).andReturn(this.flowExecutionContext);
		EasyMock.replay(this.requestContext, this.flowExecutionContext);

		Object state = new Object();
		this.responseStateManager.writeState(this.jsfMock.facesContext(), state);

		assertEquals(state, viewMap.get(FlowResponseStateManager.FACES_VIEW_STATE));
		assertEquals(
				"<input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"e1s1\" />",
				this.jsfMock.contentAsString());
		EasyMock.verify(this.flowExecutionContext, this.requestContext);
	}

	public void testGetState() throws Exception {
		Object state = new Object();

		LocalAttributeMap<Object> viewMap = new LocalAttributeMap<Object>();
		viewMap.put(FlowResponseStateManager.FACES_VIEW_STATE, state);
		EasyMock.expect(this.requestContext.getViewScope()).andStubReturn(viewMap);
		EasyMock.replay(this.requestContext);

		Object actual = this.responseStateManager.getState(this.jsfMock.facesContext(), "viewId");

		assertSame(state, actual);
		EasyMock.verify(this.requestContext);
	}
}
