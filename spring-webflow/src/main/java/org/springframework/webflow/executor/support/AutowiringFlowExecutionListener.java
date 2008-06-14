package org.springframework.webflow.executor.support;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

/**
 * Flow execution listener that will autowire objects found in the flow execution scopes when a flow execution
 * {@link FlowExecutionListener#resumed(RequestContext) resumes}. It will run through all flow execution scopes in the
 * order request, flash, flow and finally conversation scope.
 * <p>
 * This listener is particularly useful when you are storing stateful objects inside the flow execution that have
 * dependencies on other objects, typically singleton service objects managed by Spring in an application context. Since
 * Web Flow serializes the contents of flash and flow scope inbetween requests, those dependencies should be marked as
 * transient to avoid serializing the referenced objects (services). As a consequence however, those dependencies are
 * lost when the flow execution is restored. This listener makes it possible to rewire any required depencencies every
 * time the flow execution resumes.
 * <p>
 * By default, the listener will use autowiring by name. This behaviour can be changed by setting the "autowireMode"
 * property to the approriate value.
 * <p>
 * Dependency checking is turned off by default: If no matching service layer bean can be found, the setter in question
 * will simply not get invoked. To enforce matching service layer beans, set the "dependencyCheck" property to true.
 * <p>
 * Autowiring will be attempted for all objects stored in any of the flow execution scopes. Override the
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
public class AutowiringFlowExecutionListener extends FlowExecutionListenerAdapter implements ApplicationContextAware {

	/**
	 * The autowiring mode to use.
	 */
	private int autowireMode = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * The dependency check setting to use.
	 */
	private boolean dependencyCheck = false;

	/**
	 * The bean factory we will delegate autowiring to.
	 */
	private AutowireCapableBeanFactory beanFactory;

	/**
	 * Returns the configured autowiring mode. The default is autowiring by name.
	 */
	public int getAutowireMode() {
		return autowireMode;
	}

	/**
	 * Set the autowiring mode to use, either by name or by type. Defaults to autowiring by name.
	 * @see AutowireCapableBeanFactory#AUTOWIRE_BY_NAME
	 * @see AutowireCapableBeanFactory#AUTOWIRE_BY_TYPE
	 * @see AutowireCapableBeanFactory#AUTOWIRE_NO
	 */
	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	/**
	 * Returns the configured dependency check setting. Defaults to false.
	 */
	public boolean getDependencyCheck() {
		return dependencyCheck;
	}

	/**
	 * Set whether to perform a dependency check for object references in the instance being autowired. Defaults to
	 * false.
	 * @see AutowireCapableBeanFactory#autowireBeanProperties(Object, int, boolean)
	 */
	public void setDependencyCheck(boolean dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

	public void resumed(RequestContext context) {
		autowire(ScopeType.REQUEST, context);
		autowire(ScopeType.FLASH, context);
		autowire(ScopeType.FLOW, context);
		autowire(ScopeType.CONVERSATION, context);
	}

	/**
	 * Autowire all objects in specified flow execution scope.
	 */
	private void autowire(ScopeType scopeType, RequestContext context) {
		Map scope = scopeType.getScope(context).asMap();
		for (Iterator keys = scope.keySet().iterator(); keys.hasNext();) {
			String key = (String) keys.next();
			Object value = scope.get(key);
			if (shouldBeAutowired(scopeType, key, value)) {
				autowire(scopeType, key, value);
			}
		}
	}

	/**
	 * Determine whether or not given value, indexed using given key in specified flow execution scope, should be
	 * autowired. The default implementation simply returns true, i.e. it tries to autowire all objects found in the
	 * flow execution scopes.
	 * <p>
	 * Subclasses can override this method if they want to limit autowiring to a particular scope or to particular
	 * objects, for instance objects implementing a paritcular marker interface, having a certain marker annotation or
	 * objects whose class name follows some naming convention.
	 */
	protected boolean shouldBeAutowired(ScopeType scopeType, String key, Object value) {
		return true;
	}

	/**
	 * Autowire given value indexed using given key in specified flow execution scope. By default, this method will
	 * simply call {@link AutowireCapableBeanFactory#autowireBeanProperties(Object, int, boolean)} using the configured
	 * {@link #setAutowireMode(int) autowiring mode} and {@link #setDependencyCheck(boolean) dependency check} settings.
	 * <p>
	 * Subclasses can override this method when special autowiring is required, for instance using any of the other
	 * method offered by {@link AutowireCapableBeanFactory}.
	 */
	protected void autowire(ScopeType scopeType, String key, Object value) {
		beanFactory.autowireBeanProperties(value, autowireMode, dependencyCheck);
	}
}
