package org.springframework.faces.support;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestLoggingPhaseListener implements PhaseListener {

	private Log logger = LogFactory.getLog(RequestLoggingPhaseListener.class);

	public void afterPhase(PhaseEvent event) {
		// no-op
	}

	public void beforePhase(PhaseEvent event) {
		logger.debug("Entering JSF Phase: " + event.getPhaseId());
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
