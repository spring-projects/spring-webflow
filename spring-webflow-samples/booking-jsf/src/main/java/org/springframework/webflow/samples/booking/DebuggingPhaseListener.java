package org.springframework.webflow.samples.booking;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DebuggingPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    Log logger = LogFactory.getLog(DebuggingPhaseListener.class);

    public void afterPhase(PhaseEvent event) {
	// No-op

    }

    public void beforePhase(PhaseEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("Entering JSF Phase: " + event.getPhaseId());
	}

    }

    public PhaseId getPhaseId() {
	return PhaseId.ANY_PHASE;
    }

}
