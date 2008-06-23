package org.springframework.webflow.executor.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.EnterStateVetoException;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.execution.ViewSelection;

/**
 * Flow execution listener that will autowire objects found in the flow execution scopes when a flow execution
 * {@link FlowExecutionListener#resumed(RequestContext) resumes}.
 * <p>
 * This listener is particularly useful when you are storing stateful objects inside the flow execution that have
 * dependencies on other objects, typically singleton service objects managed by Spring in an application context. Since
 * Web Flow serializes the contents of flash and flow scope inbetween requests, those dependencies should be marked as
 * transient to avoid serializing the referenced objects (services). As a consequence however, those dependencies are
 * lost when the flow execution is restored. This listener makes it possible to rewire any required depencencies every
 * time the flow execution resumes.
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
 * This listener is {@link ApplicationContextAware} and needs to be defined in a Spring application context to function
 * properly.
 * 
 * @see AutowireCapableBeanFactory
 * 
 * @since 1.0.6
 * 
 * @author Erwin Vervaet
 */
public class AutowiringFlowExecutionListener extends AutowiringSupport implements ApplicationContextAware,
		FlowExecutionListener {

	/**
	 * The bean factory we will delegate autowiring to.
	 */
	private AutowireCapableBeanFactory beanFactory;

	protected AutowireCapableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

	// implementing FlowExecutionListener

	public void requestSubmitted(RequestContext context) {
	}

	public void requestProcessed(RequestContext context) {
	}

	public void sessionStarting(RequestContext context, FlowDefinition definition, MutableAttributeMap input) {
	}

	public void sessionCreated(RequestContext context, FlowSession session) {
	}

	public void sessionStarted(RequestContext context, FlowSession session) {
	}

	public void eventSignaled(RequestContext context, Event event) {
	}

	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
	}

	public void stateEntered(RequestContext context, StateDefinition previousState, StateDefinition newState) {
	}

	public void resumed(RequestContext context) {
		autowire(context.getFlowExecutionContext());
	}

	public void paused(RequestContext context, ViewSelection selectedView) {
	}

	public void sessionEnding(RequestContext context, FlowSession session, MutableAttributeMap output) {
	}

	public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
	}

	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
	}
}
