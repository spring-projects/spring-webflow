/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.config.scope;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionContextHolder;
import org.springframework.webflow.execution.RequestContext;

/**
 * Abstract {@link Scope} implementation that reads from a particular scope in
 * the current thread-bound {@link FlowExecutionContext} object.
 * 
 * <p>
 * Subclasses simply need to implement {@link #getScope()} to instruct this
 * class which {@link FlowExecutionContext} scope to read attributes from.
 * 
 * <p>
 * Relies on a thread-bound
 * @{link FlowExecutionContext} instance wich is located through a
 * @{link FlowExecutionContextHolder}.
 * 
 * @see FlowExecutionContext
 * @see FlowExecutionContextHolder
 * 
 * @author Ben Hale
 * @since 1.1
 */
public abstract class AbstractWebFlowScope implements Scope {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Template method that determines the actual target scope.
	 * @return the target scope
	 * @see RequestContext#getConversationScope()
	 * @see RequestContext#getFlowScope()
	 * @see RequestContext#getRequestScope()
	 * @see RequestContext#getFlashScope()
	 */
	protected abstract MutableAttributeMap getScope();

	public Object get(String name, ObjectFactory objectFactory) {
		MutableAttributeMap scope;
		try {
			scope = getScope();
		} catch (IllegalStateException e) {
			throw new ScopedBeanException("Cannot retrieve scoped bean '" + name
					+ "' before the scope has been populated");
		}
		Object scopedObject = scope.get(name);
		if (scopedObject == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not find existing scoped instance of '" + name + "'; creating new instace");
			}
			scopedObject = objectFactory.getObject();
			scope.put(name, scopedObject);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Found existing scoped instance of '" + name + "'");
			}
		}
		return scopedObject;
	}

	public Object remove(String name) {
		try {
			return getScope().remove(name);
		} catch (IllegalStateException e) {
			throw new ScopedBeanException("Cannot remove scoped bean '" + name
					+ "' before the scope has been populated");
		}
	}

	/**
	 * Always returns <code>null</code> as most Spring Web Flow scopes do not
	 * have obvious conversation ids. Subclasses should override this method
	 * where conversation ids can be intelligently returned.
	 * @return always returns <code>null</code>
	 */
	public String getConversationId() {
		return null;
	}

	/**
	 * Will not register a destruction callback as Spring Web Flow does not
	 * support destruction of scoped beans. Subclasses should override this
	 * method where where destruction can adequately be accomplished.
	 * @param name the name of the bean to register the callback for
	 * @param callback the callback to execute
	 */
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.warn("Destruction callback for '" + name + "' was not registered. Spring Web Flow does not "
				+ "support destruction of scoped beans.");
	}

	/**
	 * Returns the current flow execution context. Used by subclasses to easily
	 * get access to the thread-bound flow execution context.
	 * @return the current thread-bound flow execution context
	 */
	protected FlowExecutionContext getFlowExecutionContext() {
		return FlowExecutionContextHolder.getFlowExecutionContext();
	}

}