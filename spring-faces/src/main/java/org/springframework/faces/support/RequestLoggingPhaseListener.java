/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * {@link PhaseListener} that logs the execution of the individual phases of the JSF lifecycle. Useful during JSF
 * application development in order to detect unreported JSF errors that cause the lifecycle to short-circuit. Turn
 * logging level to DEBUG to see its output.
 *
 * @author Jeremy Grelle
 */
public class RequestLoggingPhaseListener implements PhaseListener {

	private static final Log logger = LogFactory.getLog(RequestLoggingPhaseListener.class);

	public void afterPhase(PhaseEvent event) {
		// no-op
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
