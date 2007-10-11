package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

class JsfFlowUtils {

	public static FacesContext getFacesContext(Lifecycle lifecycle) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = facesContextFactory.getFacesContext(requestContext.getExternalContext().getContext(),
					requestContext.getExternalContext().getRequest(),
					requestContext.getExternalContext().getResponse(), lifecycle);
		}
		return facesContext;
	}

	public static void notifyAfterListeners(PhaseId phaseId, Lifecycle lifecycle) {
		PhaseEvent afterPhaseEvent = new PhaseEvent(getFacesContext(lifecycle), phaseId, lifecycle);
		for (int i = 0; i < lifecycle.getPhaseListeners().length; i++) {
			PhaseListener listener = lifecycle.getPhaseListeners()[i];
			if (listener.getPhaseId() == phaseId || listener.getPhaseId() == PhaseId.ANY_PHASE) {
				listener.afterPhase(afterPhaseEvent);
			}
		}
	}

	public static void notifyBeforeListeners(PhaseId phaseId, Lifecycle lifecycle) {
		PhaseEvent beforePhaseEvent = new PhaseEvent(getFacesContext(lifecycle), phaseId, lifecycle);
		for (int i = 0; i < lifecycle.getPhaseListeners().length; i++) {
			PhaseListener listener = lifecycle.getPhaseListeners()[i];
			if (listener.getPhaseId() == phaseId || listener.getPhaseId() == PhaseId.ANY_PHASE) {
				listener.beforePhase(beforePhaseEvent);
			}
		}
	}
}
