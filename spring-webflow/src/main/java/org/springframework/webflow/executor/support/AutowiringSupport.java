package org.springframework.webflow.executor.support;

import java.util.Iterator;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.ScopeType;

/**
 * Abstract support class for classes that can autowire objects found in one of flow execution scopes.
 * <p>
 * Subclasses are required to implement the {@link #getBeanFactory()} hook method, returning the
 * {@link AutowireCapableBeanFactory} to use for autowiring.
 * 
 * @see AutowireCapableBeanFactory
 * 
 * @since 1.0.6
 * 
 * @author Erwin Vervaet
 */
public abstract class AutowiringSupport {

	/**
	 * The autowiring mode to use.
	 */
	private int autowireMode = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * The dependency check setting to use.
	 */
	private boolean dependencyCheck = false;

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

	/**
	 * Autowire all objects in flash, flow and conversation scope for given flow execution.
	 * @see #autowire(ScopeType, MutableAttributeMap)
	 */
	public void autowire(FlowExecutionContext flowExecutionContext) {
		// no need to autowire request scope since that will be setup from scratch in this request

		// autowire flash and flow scope for all flow sessions in the flow execution
		FlowSession flowSession = flowExecutionContext.getActiveSession();
		while (flowSession != null) {
			autowire(ScopeType.FLASH, flowSession.getFlashMap());
			autowire(ScopeType.FLOW, flowSession.getScope());

			flowSession = flowSession.getParent();
		}

		// autowire conversation scope, which is not tied to the flow session
		autowire(ScopeType.CONVERSATION, flowExecutionContext.getConversationScope());
	}

	/**
	 * Autowire all objects in given flow execution scope.
	 * <p>
	 * Will invoke {@link #shouldBeAutowired(ScopeType, String, Object)} to determine whether or not an object should be
	 * autowirded and {@link #autowire(ScopeType, String, Object)} to actually autowire it.
	 */
	protected void autowire(ScopeType scopeType, MutableAttributeMap scope) {
		for (Iterator keys = scope.asMap().keySet().iterator(); keys.hasNext();) {
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
		getBeanFactory().autowireBeanProperties(value, autowireMode, dependencyCheck);
	}

	/**
	 * Returns the {@link AutowireCapableBeanFactory} that will be used for autowiring.
	 * <p>
	 * Subclasses are required to implement this hook method.
	 */
	protected abstract AutowireCapableBeanFactory getBeanFactory();
}
