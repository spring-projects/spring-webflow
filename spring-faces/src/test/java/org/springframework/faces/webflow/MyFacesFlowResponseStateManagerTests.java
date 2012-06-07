package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.context.FacesContext;

import org.apache.myfaces.renderkit.MyfacesResponseStateManager;
import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockResponseWriter;
import org.easymock.EasyMock;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.test.MockFlowExecutionContext;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Tests for {@link MyFacesFlowResponseStateManager}.
 * 
 * @author Phillip Webb
 */
public class MyFacesFlowResponseStateManagerTests extends AbstractJsfTestCase {

	private MockMyfacesResponseStateManager root;
	private FlowResponseStateManager flow;
	private MyFacesFlowResponseStateManager manager;
	private MockRequestContext requestContext;

	public MyFacesFlowResponseStateManagerTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.root = new MockMyfacesResponseStateManager();
		this.flow = new FlowResponseStateManager(this.root);
		this.manager = new MyFacesFlowResponseStateManager(this.flow);
		this.requestContext = new MockRequestContext();
		MockFlowExecutionContext executionContext = requestContext.getMockFlowExecutionContext();
		MockFlowSession session = executionContext.getMockActiveSession();
		ViewFactory viewFactory = EasyMock.createNiceMock(ViewFactory.class);
		session.setState(new ViewState(session.getDefinitionInternal(), "view", viewFactory));
		executionContext.setKey(new MockFlowExecutionKey("x"));
		RequestContextHolder.setRequestContext(requestContext);
	}

	protected void tearDown() throws Exception {
		RequestContextHolder.setRequestContext(null);
		super.tearDown();
	}

	public void testDelegatesIsWriteStateAfterRenderViewRequired() throws Exception {
		this.manager.isWriteStateAfterRenderViewRequired(this.facesContext);
		assertTrue(this.root.isWriteStateAfterRenderViewRequiredCalled);
	}

	public void testDelegatesWriteState() throws Exception {
		RequestContextHolder.setRequestContext(null);
		this.facesContext.setResponseWriter(new MockResponseWriter(new StringWriter()));
		this.manager.writeState(this.facesContext, new Object());
		assertTrue(this.root.writeStateCalled);
	}

	public void testTriggersSaveStateInFlowResponseStateManager() throws Exception {
		Object state = new Object();
		this.manager.saveState(this.facesContext, state);
		assertSame(state, requestContext.getViewScope().get(FlowResponseStateManager.FACES_VIEW_STATE));
		assertFalse(this.root.saveStateCalled);
	}

	private static class MockMyfacesResponseStateManager extends MyfacesResponseStateManager {
		private boolean isWriteStateAfterRenderViewRequiredCalled;
		private boolean saveStateCalled;
		private boolean writeStateCalled;

		public boolean isWriteStateAfterRenderViewRequired(FacesContext facesContext) {
			this.isWriteStateAfterRenderViewRequiredCalled = true;
			return true;
		}

		public void saveState(FacesContext facesContext, Object state) {
			this.saveStateCalled = true;
		}

		public void writeState(FacesContext context, Object state) throws IOException {
			this.writeStateCalled = true;
		}
	}
}
