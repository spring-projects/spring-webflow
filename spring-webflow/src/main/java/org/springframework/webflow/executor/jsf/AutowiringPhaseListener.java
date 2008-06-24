package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.executor.support.AutowiringSupport;

/**
 * JSF phase listener that that can be combined with the {@link FlowPhaseListener} to automatically autowire objects in
 * the flow execution scopes every time a flow execution is restored. In faces-config.xml it should be declared <i>after</i>
 * the FlowPhaseListener to make sure the FlowPhaseListener first restores the flow execution before this listener tries
 * to autowire objects inside the flow execution.
 * <p>
 * This listener is particularly useful when you are storing stateful objects inside the flow execution that have
 * dependencies on other objects, typically singleton service objects managed by Spring in an application context. Since
 * Web Flow serializes the contents of flash and flow scope inbetween requests, those dependencies should be marked as
 * transient to avoid serializing the referenced objects (services). As a consequence however, those dependencies are
 * lost when the flow execution is restored. This listener makes it possible to rewire any required depencencies every
 * time the flow execution is restored.
 * <p>
 * By default, the listener will use autowiring by name. This behaviour can be changed by setting the "autowireMode"
 * property to the appropriate value.
 * <p>
 * Dependency checking is turned off by default: if no matching service layer bean can be found, the setter in question
 * will simply not get invoked. To enforce matching service layer beans, set the "dependencyCheck" property to true.
 * <p>
 * Autowiring will be attempted for all objects stored in flash, flow and conversation scope. Override the
 * {@link #shouldBeAutowired(ScopeType, String, Object)} method if you want to limit autowiring to specific objects.
 * <p>
 * Autowiring is done using {@link AutowireCapableBeanFactory#autowireBeanProperties(Object, int, boolean)}. If you
 * require special autowiring behaviour, consider overriding the {@link #autowire(ScopeType, String, Object)} method.
 * <p>
 * This phase listener will use the root web application context as {@link AutowireCapableBeanFactory}.
 * <p>
 * Note about customization: since PhaseListeners managed directly by the JSF provider cannot benefit from dependency
 * injection, See Spring's {@link org.springframework.web.jsf.DelegatingPhaseListenerMulticaster} when you need to
 * customize an AutowiringPhaseListener instance.
 * 
 * @see FlowPhaseListener
 * @see WebApplicationContextUtils#getWebApplicationContext(ServletContext)
 * 
 * @author Erwin Vervaet
 */
public class AutowiringPhaseListener extends AutowiringSupport implements PhaseListener {

	protected AutowireCapableBeanFactory getBeanFactory() {
		return WebApplicationContextUtils.getWebApplicationContext(
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getAutowireCapableBeanFactory();
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	public void beforePhase(PhaseEvent event) {
	}

	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (FlowExecutionHolderUtils.isFlowExecutionRestored(context)) {
			autowire(FlowExecutionHolderUtils.getCurrentFlowExecution(context));
		}
	}
}
