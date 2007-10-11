package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

public class FlowLifecycle extends Lifecycle {

	private final Lifecycle delegate;

	public FlowLifecycle(Lifecycle delegate) {
		this.delegate = delegate;
	}

	/**
	 * Executes APPLY_REQUEST_VALUES through INVOKE_APPLICATION.
	 */
	public void execute(FacesContext context) throws FacesException {

		for (int p = PhaseId.APPLY_REQUEST_VALUES.getOrdinal(); p <= PhaseId.INVOKE_APPLICATION.getOrdinal(); p++) {
			PhaseId phaseId = (PhaseId) PhaseId.VALUES.get(p);
			if (!skipPhase(context, phaseId)) {
				invokePhase(context, phaseId);
			}
		}
	}

	public void render(FacesContext context) throws FacesException {
		delegate.render(context);
	}

	public void addPhaseListener(PhaseListener listener) {
		delegate.addPhaseListener(listener);
	}

	public PhaseListener[] getPhaseListeners() {
		return delegate.getPhaseListeners();
	}

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
		JsfFlowUtils.notifyBeforeListeners(phaseId, this);
		if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
			context.getViewRoot().processDecodes(context);
		} else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
			context.getViewRoot().processValidators(context);
		} else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
			context.getViewRoot().processUpdates(context);
		} else {
			context.getViewRoot().processApplication(context);
		}
		JsfFlowUtils.notifyAfterListeners(phaseId, this);
	}
}
