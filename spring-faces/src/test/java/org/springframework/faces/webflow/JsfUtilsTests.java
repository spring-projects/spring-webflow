package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import junit.framework.TestCase;

import org.apache.myfaces.test.mock.MockFacesContext20;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycle;

public class JsfUtilsTests extends TestCase {

	public void testBeforeListenersCalledInForwardOrder() throws Exception {
		List<OrderVerifyingPhaseListener> list = new ArrayList<OrderVerifyingPhaseListener>();
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
		List<OrderVerifyingPhaseListener> list = new ArrayList<OrderVerifyingPhaseListener>();
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

	private class OrderVerifyingPhaseListener implements PhaseListener {

		private List<OrderVerifyingPhaseListener> afterPhaseList;
		private List<OrderVerifyingPhaseListener> beforePhaseList;

		public OrderVerifyingPhaseListener(List<OrderVerifyingPhaseListener> afterPhaseList,
				List<OrderVerifyingPhaseListener> beforePhaseList) {
			this.afterPhaseList = afterPhaseList;
			this.beforePhaseList = beforePhaseList;
		}

		public void afterPhase(PhaseEvent event) {
			afterPhaseList.add(this);
		}

		public void beforePhase(PhaseEvent event) {
			beforePhaseList.add(this);
		}

		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}

	}

}
