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
package org.springframework.webflow.execution;

/**
 * A command that executes a behavior and returns a logical execution result a calling flow execution can respond to.
 * <p>
 * Actions typically delegate down to the application (or service) layer to perform business operations. They often
 * retrieve data to support response rendering. They act as a bridge between a SWF web-tier and your middle-tier
 * business logic layer.
 * <p>
 * When an action completes execution it signals a result event describing the outcome of that execution (for example,
 * "success", "error", "yes", "no", "tryAgain", etc). In addition to providing a logical outcome the flow can respond
 * to, a result event may have payload associated with it, for example a "success" return value or an "error" error
 * code. The result event is typically used as grounds for a state transition out of the current state of the calling
 * Flow.
 * <p>
 * Action implementations are often application-scoped singletons instantiated and managed by a web-tier Spring
 * application context to take advantage of Spring's externalized configuration and dependency injection capabilities
 * (which is a form of Inversion of Control [IoC]). Actions may also be stateful prototypes, storing conversational
 * state as instance variables. Action instance definitions may also be locally scoped to a specific flow definition
 * (see use of the "import" element of the root XML flow definition element.)
 * <p>
 * Note: Actions are directly instantiatable for use in a standalone test environment and can be parameterized with
 * mocks or stubs, as they are simple POJOs. Action proxies may also be generated at runtime for delegating to POJO
 * business operations that have no dependency on the Spring Web Flow API.
 * <p>
 * Note: if an Action is a singleton managed in application scope, take care not to store and/or modify caller-specific
 * state in a unsafe manner. The Action {@link #execute(RequestContext)} method runs in an independently executing
 * thread on each invocation so make sure you deal only with local data or internal, thread-safe services.
 * <p>
 * Note: an Action is not a controller like a Spring MVC controller or a Struts action is a controller. Flow actions are
 * <i>commands</i>. Such commands do not select views, they execute arbitrary behavioral logic and then return an
 * logical execution result. The flow that invokes an Action is responsible for responding to the execution result to
 * decide what to do next. In Spring Web Flow, the flow <i>is</i> the controller.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface Action {

	/**
	 * Execute this action. Action execution will occur in the context of a request associated with an active flow
	 * execution.
	 * <p>
	 * Action invocation is typically triggered in a production environment by a state within a flow carrying out the
	 * execution of a flow definition. The result of action execution, a logical outcome event, can be used as grounds
	 * for a transition out of the calling state.
	 * <p>
	 * Note: The {@link RequestContext} argument to this method provides access to data about the active flow execution
	 * in the context of the currently executing thread. Among other things, this allows this action to access
	 * {@link RequestContext#getRequestScope() data} set by other actions, as well as set its own attributes it wishes
	 * to expose in a given scope.
	 * <p>
	 * Some notes about actions and their usage of the attribute scope types:
	 * <ul>
	 * <li>Attributes set in {@link RequestContext#getRequestScope() request scope} exist for the life of the currently
	 * executing request only.
	 * <li>Attributes set in {@link RequestContext#getFlashScope() flash scope} exist until the next external user
	 * event is signaled. That time includes the current request plus any redirect or additional refreshes to the next
	 * view.
	 * <li>Attributes set in {@link RequestContext#getFlowScope() flow scope} exist for the life of the flow session
	 * and will be cleaned up automatically when the flow session ends.
	 * <li>Attributes set in {@link RequestContext#getConversationScope() conversation scope} exist for the life of the
	 * entire flow execution representing a single logical "conversation" with a user.
	 * </ul>
	 * <p>
	 * All attributes present in any scope are typically exposed in a model for access by a view when an "interactive"
	 * state type such as a view state is entered.
	 * <p>
	 * Note: flow scope should generally not be used as a general purpose cache, but rather as a context for data needed
	 * locally by other states of the flow this action participates in. For example, it would be inappropriate to stuff
	 * large collections of objects (like those returned to support a search results view) into flow scope. Instead, put
	 * such result collections in request scope, and ensure you execute this action again each time you wish to view
	 * those results. 2nd level caches managed outside of SWF are more general cache solutions.
	 * <p>
	 * Note: as flow scoped attributes are eligible for serialization they should be <code>Serializable</code>.
	 * 
	 * @param context the action execution context, for accessing and setting data in a {@link ScopeType scope type},
	 * as well as obtaining other flow contextual information (e.g. request context attributes and flow execution
	 * context information)
	 * @return a logical result outcome, used as grounds for a transition in the calling flow (e.g. "success", "error",
	 * "yes", "no", * ...)
	 * @throws Exception a exception occurred during action execution, either checked or unchecked; note, any
	 * <i>recoverable</i> exceptions should be caught within this method and an appropriate result outcome returned
	 * <i>or</i> be handled by the current state of the calling flow execution.
	 */
	public Event execute(RequestContext context) throws Exception;
}