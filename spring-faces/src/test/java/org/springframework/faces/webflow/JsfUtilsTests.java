package org.springframework.faces.webflow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;

import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.MockApplicationFactory;
import org.apache.myfaces.test.mock.MockFacesContext20;
import org.apache.myfaces.test.mock.MockFacesContextFactory;
import org.apache.myfaces.test.mock.MockRenderKitFactory;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycle;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycleFactory;

public class JsfUtilsTests extends AbstractJsfTestCase {

	public JsfUtilsTests(String name) {
		super(name);
	}

	public void testBeforeListenersCalledInForwardOrder() throws Exception {
		List<OrderVerifyingPhaseListener> list = new ArrayList<>();
		MockLifecycle lifecycle = new MockLifecycle();
		PhaseListener listener1 = new OrderVerifyingPhaseListener(null, list);
		lifecycle.addPhaseListener(listener1);
		PhaseListener listener2 = new OrderVerifyingPhaseListener(null, list);
		lifecycle.addPhaseListener(listener2);
		PhaseListener listener3 = new OrderVerifyingPhaseListener(null, list);
		lifecycle.addPhaseListener(listener3);
		JsfUtils.notifyBeforeListeners(PhaseId.ANY_PHASE, lifecycle, new MockFacesContext20());
		assertEquals(listener1, list.get(0));
		assertEquals(listener2, list.get(1));
		assertEquals(listener3, list.get(2));
	}

	public void testAfterListenersCalledInReverseOrder() throws Exception {
		List<OrderVerifyingPhaseListener> list = new ArrayList<>();
		MockLifecycle lifecycle = new MockLifecycle();
		PhaseListener listener1 = new OrderVerifyingPhaseListener(list, null);
		lifecycle.addPhaseListener(listener1);
		PhaseListener listener2 = new OrderVerifyingPhaseListener(list, null);
		lifecycle.addPhaseListener(listener2);
		PhaseListener listener3 = new OrderVerifyingPhaseListener(list, null);
		lifecycle.addPhaseListener(listener3);
		JsfUtils.notifyAfterListeners(PhaseId.ANY_PHASE, lifecycle, new MockFacesContext20());
		assertEquals(listener3, list.get(0));
		assertEquals(listener2, list.get(1));
		assertEquals(listener1, list.get(2));
	}

	public void testGetFactory() throws Exception {
		// Not testing all but at least test the mocked factories
		assertTrue(JsfUtils.findFactory(ApplicationFactory.class) instanceof MockApplicationFactory);
		assertTrue(JsfUtils.findFactory(FacesContextFactory.class) instanceof MockFacesContextFactory);
		assertTrue(JsfUtils.findFactory(LifecycleFactory.class) instanceof MockLifecycleFactory);
		assertTrue(JsfUtils.findFactory(RenderKitFactory.class) instanceof MockRenderKitFactory);
	}

	public void testGetUnknowFactory() throws Exception {
		try {
			JsfUtils.findFactory(InputStream.class);
			fail("Did not throw");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	private class OrderVerifyingPhaseListener implements PhaseListener {

		private final List<OrderVerifyingPhaseListener> afterPhaseList;
		private final List<OrderVerifyingPhaseListener> beforePhaseList;

		public OrderVerifyingPhaseListener(List<OrderVerifyingPhaseListener> afterPhaseList,
				List<OrderVerifyingPhaseListener> beforePhaseList) {
			this.afterPhaseList = afterPhaseList;
			this.beforePhaseList = beforePhaseList;
		}

		public void afterPhase(PhaseEvent event) {
			this.afterPhaseList.add(this);
		}

		public void beforePhase(PhaseEvent event) {
			this.beforePhaseList.add(this);
		}

		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}

	}

}
