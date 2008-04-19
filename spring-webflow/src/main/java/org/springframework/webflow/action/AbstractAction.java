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
package org.springframework.webflow.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Base action that provides assistance commonly needed by action implementations. This includes:
 * <ul>
 * <li>Implementing {@link InitializingBean} to receive an init callback when deployed within a Spring bean factory.
 * <li>Exposing convenient event factory methods to create common result {@link Event} objects such as "success" and
 * "error".
 * <li>A hook for inserting action pre and post execution logic.
 * </ul>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class AbstractAction implements Action, InitializingBean {

	/**
	 * Logger, usable in subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Returns the helper delegate for creating action execution result events.
	 * @return the event factory support
	 */
	public EventFactorySupport getEventFactorySupport() {
		return new EventFactorySupport();
	}

	public void afterPropertiesSet() throws Exception {
		try {
			initAction();
		} catch (Exception ex) {
			throw new BeanInitializationException("Initialization of this Action failed: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Action initializing callback, may be overridden by subclasses to perform custom initialization logic.
	 * <p>
	 * Keep in mind that this hook will only be invoked when this action is deployed in a Spring application context
	 * since it uses the Spring {@link InitializingBean} mechanism to trigger action initialisation.
	 */
	protected void initAction() throws Exception {
	}

	/**
	 * Returns a "success" result event.
	 */
	protected Event success() {
		return getEventFactorySupport().success(this);
	}

	/**
	 * Returns a "success" result event with the provided result object as a parameter.
	 * @param result the action success result
	 */
	protected Event success(Object result) {
		return getEventFactorySupport().success(this, result);
	}

	/**
	 * Returns an "error" result event.
	 */
	protected Event error() {
		return getEventFactorySupport().error(this);
	}

	/**
	 * Returns an "error" result event caused by the provided exception.
	 * @param e the exception that caused the error event, to be configured as an event attribute
	 */
	protected Event error(Exception e) {
		return getEventFactorySupport().error(this, e);
	}

	/**
	 * Returns a "yes" result event.
	 */
	protected Event yes() {
		return getEventFactorySupport().yes(this);
	}

	/**
	 * Returns a "no" result event.
	 */
	protected Event no() {
		return getEventFactorySupport().no(this);
	}

	/**
	 * Returns yes() if the boolean result is true, no() if false.
	 * @param booleanResult the boolean
	 * @return yes or no
	 */
	protected Event result(boolean booleanResult) {
		return getEventFactorySupport().event(this, booleanResult);
	}

	/**
	 * Returns a result event for this action with the specified identifier. Typically called as part of return, for
	 * example:
	 * 
	 * <pre>
	 *     protected Event doExecute(RequestContext context) {
	 *         // do some work
	 *         if (some condition) {
	 *             return result(&quot;success&quot;);
	 *         } else {
	 *             return result(&quot;error&quot;);
	 *         }
	 *     }
	 * </pre>
	 * 
	 * Consider calling the error() or success() factory methods for returning common results.
	 * @param eventId the result event identifier
	 * @return the action result event
	 */
	protected Event result(String eventId) {
		return getEventFactorySupport().event(this, eventId);
	}

	/**
	 * Returns a result event for this action with the specified identifier and the specified set of attributes.
	 * Typically called as part of return, for example:
	 * 
	 * <pre>
	 *     protected Event doExecute(RequestContext context) {
	 *         // do some work
	 *         AttributeMap resultAttributes = new AttributeMap();
	 *         resultAttributes.put(&quot;name&quot;, &quot;value&quot;);
	 *         if (some condition) {
	 *             return result(&quot;success&quot;, resultAttributes);
	 *         } else {
	 *             return result(&quot;error&quot;, resultAttributes);
	 *         }
	 *     }
	 * </pre>
	 * 
	 * Consider calling the error() or success() factory methods for returning common results.
	 * @param eventId the result event identifier
	 * @param resultAttributes the event attributes
	 * @return the action result event
	 */
	protected Event result(String eventId, AttributeMap resultAttributes) {
		return getEventFactorySupport().event(this, eventId, resultAttributes);
	}

	/**
	 * Returns a result event for this action with the specified identifier and a single attribute.
	 * @param eventId the result id
	 * @param resultAttributeName the attribute name
	 * @param resultAttributeValue the attribute value
	 * @return the action result event
	 */
	protected Event result(String eventId, String resultAttributeName, Object resultAttributeValue) {
		return getEventFactorySupport().event(this, eventId, resultAttributeName, resultAttributeValue);
	}

	public final Event execute(RequestContext context) throws Exception {
		Event result = doPreExecute(context);
		if (result == null) {
			result = doExecute(context);
			doPostExecute(context);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Action execution disallowed; pre-execution result is '" + result.getId() + "'");
			}
		}
		return result;
	}

	// subclassing hooks

	/**
	 * Internal helper to return the name of this action for logging purposes. Defaults to the short class name.
	 * @see ClassUtils#getShortName(java.lang.Class)
	 */
	protected String getActionNameForLogging() {
		return ClassUtils.getShortName(getClass());
	}

	/**
	 * Pre-action-execution hook, subclasses may override. If this method returns a non-<code>null</code> event, the
	 * <code>doExecute()</code> method will <b>not</b> be called and the returned event will be used to select a
	 * transition to trigger in the calling action state. If this method returns <code>null</code>,
	 * <code>doExecute()</code> will be called to obtain an action result event.
	 * <p>
	 * This implementation just returns <code>null</code>.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return the non-<code>null</code> action result, in which case the <code>doExecute()</code> will not be
	 * called, or <code>null</code> if the <code>doExecute()</code> method should be called to obtain the action
	 * result
	 * @throws Exception an <b>unrecoverable</b> exception occured, either checked or unchecked
	 */
	protected Event doPreExecute(RequestContext context) throws Exception {
		return null;
	}

	/**
	 * Template hook method subclasses should override to encapsulate their specific action execution logic.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @return the action result event
	 * @throws Exception an <b>unrecoverable</b> exception occured, either checked or unchecked
	 */
	protected abstract Event doExecute(RequestContext context) throws Exception;

	/**
	 * Post-action execution hook, subclasses may override. Will only be called if <code>doExecute()</code> was
	 * called, e.g. when <code>doPreExecute()</code> returned <code>null</code>.
	 * <p>
	 * This implementation does nothing.
	 * @param context the action execution context, for accessing and setting data in "flow scope" or "request scope"
	 * @throws Exception an <b>unrecoverable</b> exception occured, either checked or unchecked
	 */
	protected void doPostExecute(RequestContext context) throws Exception {
	}
}