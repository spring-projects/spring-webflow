/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom {@link Lifecycle} for Spring Web Flow that only executes the APPLY_REQUEST_VALUES through INVOKE_APPLICATION
 * phases.
 * <p>
 * This Lifecycle does not execute the RESTORE_VIEW phase since view creation and restoration are now handled by the
 * {@link JsfViewFactory}.
 * </p>
 * 
 * @author Jeremy Grelle
 */
public class FlowLifecycle extends Lifecycle {

	private static final Log logger = LogFactory.getLog(FlowLifecycle.class);

	private final Lifecycle delegate;

	public static Lifecycle newInstance() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle defaultLifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		return new FlowLifecycle(defaultLifecycle);

	}

	FlowLifecycle(Lifecycle delegate) {
		this.delegate = delegate;
	}

	/**
	 * Executes APPLY_REQUEST_VALUES through INVOKE_APPLICATION.
	 */
	public void execute(FacesContext context) throws FacesException {
		logger.debug("Executing view post back lifecycle");
		for (int p = PhaseId.APPLY_REQUEST_VALUES.getOrdinal(); p <= PhaseId.INVOKE_APPLICATION.getOrdinal(); p++) {
			PhaseId phaseId = (PhaseId) PhaseId.VALUES.get(p);
			if (!skipPhase(context, phaseId)) {
				invokePhase(context, phaseId);
			}
		}
	}

	/**
	 * Delegates to the wrapped {@link Lifecycle}.
	 * @throws FacesException
	 */
	public void render(FacesContext context) throws FacesException {
		delegate.render(context);
	}

	/**
	 * Delegates to the wrapped {@link Lifecycle}.
	 */
	public void addPhaseListener(PhaseListener listener) {
		delegate.addPhaseListener(listener);
	}

	/**
	 * Delegates to the wrapped {@link Lifecycle}.
	 */
	public PhaseListener[] getPhaseListeners() {
		return delegate.getPhaseListeners();
	}

	/**
	 * Delegates to the wrapped {@link Lifecycle}.
	 */
	public void removePhaseListener(PhaseListener listener) {
		delegate.removePhaseListener(listener);
	}

	private boolean skipPhase(FacesContext context, PhaseId phaseId) {
		if (context.getResponseComplete()) {
			return true;
		} else if (context.getRenderResponse()) {
			return true;
		} else {
			return false;
		}
	}

	private void invokePhase(FacesContext context, PhaseId phaseId) {
		JsfUtils.notifyBeforeListeners(phaseId, this, context);
		if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
			logger.debug("Processing decodes");
			context.getViewRoot().processDecodes(context);
		} else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
			logger.debug("Processing validators");
			context.getViewRoot().processValidators(context);
		} else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
			logger.debug("Processing model updates");
			context.getViewRoot().processUpdates(context);
		} else {
			logger.debug("Processing application");
			context.getViewRoot().processApplication(context);
		}
		JsfUtils.notifyAfterListeners(phaseId, this, context);
	}
}
