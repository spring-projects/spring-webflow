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
package org.springframework.webflow.engine.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.engine.ActionList;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A flow execution exception handler that maps the occurrence of a specific type of exception to a transition to a new
 * {@link org.springframework.webflow.engine.State}.
 * <p>
 * The handled {@link FlowExecutionException} will be exposed in flash scope as
 * {@link #FLOW_EXECUTION_EXCEPTION_ATTRIBUTE}. The underlying root cause of that exception will be exposed in flash
 * scope as {@link #ROOT_CAUSE_EXCEPTION_ATTRIBUTE}.
 * 
 * @author Keith Donald
 */
public class TransitionExecutingFlowExecutionExceptionHandler implements FlowExecutionExceptionHandler {

	private static final Log logger = LogFactory.getLog(TransitionExecutingFlowExecutionExceptionHandler.class);

	/**
	 * The name of the attribute to expose a handled exception under in flash scope ("flowExecutionException").
	 */
	public static final String FLOW_EXECUTION_EXCEPTION_ATTRIBUTE = "flowExecutionException";

	/**
	 * The name of the attribute to expose a root cause of a handled exception under in flash scope
	 * ("rootCauseException").
	 */
	public static final String ROOT_CAUSE_EXCEPTION_ATTRIBUTE = "rootCauseException";

	/**
	 * The exceptionType to targetStateResolver map.
	 */
	private Map exceptionTargetStateMappings = new HashMap();

	/**
	 * The list of actions to execute when this handler handles an exception.
	 */
	private ActionList actionList = new ActionList();

	/**
	 * Adds an exception-to-target state mapping to this handler.
	 * @param exceptionClass the type of exception to map
	 * @param targetStateId the id of the state to transition to if the specified type of exception is handled
	 * @return this handler, to allow for adding multiple mappings in a single statement
	 */
	public TransitionExecutingFlowExecutionExceptionHandler add(Class exceptionClass, String targetStateId) {
		return add(exceptionClass, new DefaultTargetStateResolver(targetStateId));
	}

	/**
	 * Adds a exception-to-target state resolver mapping to this handler.
	 * @param exceptionClass the type of exception to map
	 * @param targetStateResolver the resolver to calculate the state to transition to if the specified type of
	 * exception is handled
	 * @return this handler, to allow for adding multiple mappings in a single statement
	 */
	public TransitionExecutingFlowExecutionExceptionHandler add(Class exceptionClass,
			TargetStateResolver targetStateResolver) {
		Assert.notNull(exceptionClass, "The exception class is required");
		Assert.notNull(targetStateResolver, "The target state resolver is required");
		exceptionTargetStateMappings.put(exceptionClass, targetStateResolver);
		return this;
	}

	/**
	 * Returns the list of actions to execute when this handler handles an exception. The returned list is mutable.
	 */
	public ActionList getActionList() {
		return actionList;
	}

	public boolean canHandle(FlowExecutionException e) {
		return getTargetStateResolver(e) != null;
	}

	public void handle(FlowExecutionException exception, RequestControlContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("Handling flow execution exception " + exception, exception);
		}
		exposeException(context, exception, findRootCause(exception));
		actionList.execute(context);
		context.execute(new Transition(getTargetStateResolver(exception)));
	}

	// helpers

	/**
	 * Exposes the given flow exception and root cause in flash scope to make them available for response rendering.
	 * Subclasses can override this if they want to expose the exceptions in a different way or do special processing
	 * before the exceptions are exposed.
	 * @param context the request control context
	 * @param exception the exception being handled
	 * @param rootCause root cause of the exception being handled (could be null)
	 */
	protected void exposeException(RequestContext context, FlowExecutionException exception, Throwable rootCause) {
		// note that all Throwables are Serializable so putting them in flash
		// scope should not be a problem
		context.getFlashScope().put(FLOW_EXECUTION_EXCEPTION_ATTRIBUTE, exception);
		if (logger.isDebugEnabled()) {
			logger.debug("Exposing flow execution exception root cause " + rootCause + " under attribute '"
					+ ROOT_CAUSE_EXCEPTION_ATTRIBUTE + "'");
		}
		context.getFlashScope().put(ROOT_CAUSE_EXCEPTION_ATTRIBUTE, rootCause);
	}

	/**
	 * Find the mapped target state resolver for given exception. Returns <code>null</code> if no mapping can be found
	 * for given exception. Will try all exceptions in the exception cause chain.
	 */
	protected TargetStateResolver getTargetStateResolver(Throwable e) {
		TargetStateResolver targetStateResolver;
		if (isRootCause(e)) {
			return findTargetStateResolver(e.getClass());
		} else {
			targetStateResolver = (TargetStateResolver) exceptionTargetStateMappings.get(e.getClass());
			if (targetStateResolver != null) {
				return targetStateResolver;
			} else {
				return getTargetStateResolver(e.getCause());
			}
		}
	}

	/**
	 * Check if given exception is the root of the exception cause chain. For use with JDK 1.4 or later.
	 */
	private boolean isRootCause(Throwable t) {
		return t.getCause() == null;
	}

	/**
	 * Try to find a mapped target state resolver for given exception type. Will also try to lookup using the class
	 * hierarchy of given exception type.
	 * @param exceptionType the exception type to lookup
	 * @return the target state id or null if not found
	 */
	private TargetStateResolver findTargetStateResolver(Class exceptionType) {
		while (exceptionType != null && exceptionType != Object.class) {
			if (exceptionTargetStateMappings.containsKey(exceptionType)) {
				return (TargetStateResolver) exceptionTargetStateMappings.get(exceptionType);
			} else {
				exceptionType = exceptionType.getSuperclass();
			}
		}
		return null;
	}

	/**
	 * Find the root cause of given throwable. For use on JDK 1.4 or later.
	 */
	private Throwable findRootCause(Throwable e) {
		Throwable cause = e.getCause();
		if (cause == null) {
			return e;
		} else {
			return findRootCause(cause);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("exceptionHandlingMappings", exceptionTargetStateMappings).toString();
	}
}