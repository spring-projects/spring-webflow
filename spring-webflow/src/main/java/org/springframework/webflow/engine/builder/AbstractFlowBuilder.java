/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingBuilder;
import org.springframework.binding.method.MethodSignature;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.action.AbstractBeanInvokingAction;
import org.springframework.webflow.action.ActionResultExposer;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowAttributeMapper;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.ViewSelector;
import org.springframework.webflow.engine.support.ActionTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.execution.support.EventFactorySupport;

/**
 * Base class for flow builders that programmatically build flows in Java
 * configuration code.
 * <p>
 * To give you an example of what a simple Java-based web flow builder
 * definition might look like, the following example defines the 'dynamic' web
 * flow roughly equivalent to the work flow statically implemented in Spring
 * MVC's simple form controller:
 * 
 * <pre class="code">
 * public class CustomerDetailFlowBuilder extends AbstractFlowBuilder {
 * 	public void buildStates() {
 * 		// get customer information
 * 		addActionState(&quot;getDetails&quot;, action(&quot;customerAction&quot;), transition(on(success()), to(&quot;displayDetails&quot;)));
 * 
 * 		// view customer information               
 * 		addViewState(&quot;displayDetails&quot;, &quot;customerDetails&quot;, transition(on(submit()), to(&quot;bindAndValidate&quot;)));
 * 
 * 		// bind and validate customer information updates 
 * 		addActionState(&quot;bindAndValidate&quot;, action(&quot;customerAction&quot;), new Transition[] {
 * 				transition(on(error()), to(&quot;displayDetails&quot;)), transition(on(success()), to(&quot;finish&quot;)) });
 * 
 * 		// finish
 * 		addEndState(&quot;finish&quot;);
 * 	}
 * }
 * </pre>
 * 
 * What this Java-based FlowBuilder implementation does is add four states to a
 * flow. These include a "get" <code>ActionState</code> (the start state), a
 * <code>ViewState</code> state, a "bind and validate"
 * <code>ActionState</code>, and an end marker state (<code>EndState</code>).
 * <p>
 * The first state, an action state, will be assigned the indentifier
 * <code>getDetails</code>. This action state will automatically be
 * configured with the following defaults:
 * <ol>
 * <li>The action instance with id <code>customerAction</code>. This is the
 * <code>Action</code> implementation that will execute when this state is
 * entered. In this example, that <code>Action</code> will go out to the DB,
 * load the Customer, and put it in the Flow's request context.
 * <li>A <code>success</code> transition to a default view state, called
 * <code>displayDetails</code>. This means when the <code>Action</code>
 * returns a <code>success</code> result event (aka outcome), the
 * <code>displayDetails</code> state will be entered.
 * <li>It will act as the start state for this flow (by default, the first
 * state added to a flow during the build process is treated as the start
 * state).
 * </ol>
 * <p>
 * The second state, a view state, will be identified as
 * <code>displayDetails</code>. This view state will automatically be
 * configured with the following defaults:
 * <ol>
 * <li>A view name called <code>customerDetails</code>. This is the logical
 * name of a view resource. This logical view name gets mapped to a physical
 * view resource (jsp, etc.) by the calling front controller (via a Spring view
 * resolver, or a Struts action forward, for example).
 * <li>A <code>submit</code> transition to a bind and validate action state,
 * indentified by the default id <code>bindAndValidate</code>. This means
 * when a <code>submit</code> event is signaled by the view (for example, on a
 * submit button click), the bindAndValidate action state will be entered and
 * the <code>bindAndValidate</code> method of the
 * <code>customerAction</code> <code>Action</code> implementation will be
 * executed.
 * </ol>
 * <p>
 * The third state, an action state, will be indentified as
 * <code>bindAndValidate</code>. This action state will automatically be
 * configured with the following defaults:
 * <ol>
 * <li>An action bean named <code>customerAction</code> -- this is the name
 * of the <code>Action</code> implementation exported in the application
 * context that will execute when this state is entered. In this example, the
 * <code>Action</code> has a "bindAndValidate" method that will bind form
 * input in the HTTP request to a backing Customer form object, validate it, and
 * update the DB.
 * <li>A <code>success</code> transition to a default end state, called
 * <code>finish</code>. This means if the <code>Action</code> returns a
 * <code>success</code> result, the <code>finish</code> end state will be
 * transitioned to and the flow will terminate.
 * <li>An <code>error</code> transition back to the form view. This means if
 * the <code>Action</code> returns an <code>error</code> event, the <code>
 * displayDetails</code> view state will be transitioned back to.
 * </ol>
 * <p>
 * The fourth and last state, an end state, will be indentified with the default
 * end state id <code>finish</code>. This end state is a marker that signals
 * the end of the flow. When entered, the flow session terminates, and if this
 * flow is acting as a root flow in the current flow execution, any
 * flow-allocated resources will be cleaned up. An end state can optionally be
 * configured with a logical view name to forward to when entered. It will also
 * trigger a state transition in a resuming parent flow if this flow was
 * participating as a spawned 'subflow' within a suspended parent flow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowBuilder extends BaseFlowBuilder {

	/**
	 * A helper for creating commonly used event identifiers that drive
	 * transitions created by this builder.
	 */
	private EventFactorySupport eventFactorySupport = new EventFactorySupport();

	/**
	 * Default constructor for subclassing.
	 */
	protected AbstractFlowBuilder() {
		super();
	}

	/**
	 * Create an instance of an abstract flow builder, using the specified
	 * locator to obtain needed flow services at build time.
	 * @param flowServiceLocator the locator for services needed by this builder
	 * to build its Flow
	 */
	protected AbstractFlowBuilder(FlowServiceLocator flowServiceLocator) {
		super(flowServiceLocator);
	}

	/**
	 * Returns the configured event factory support helper for creating commonly
	 * used event identifiers that drive transitions created by this builder.
	 */
	public EventFactorySupport getEventFactorySupport() {
		return eventFactorySupport;
	}

	/**
	 * Sets the event factory support helper to use to create commonly used
	 * event identifiers that drive transitions created by this builder.
	 */
	public void setEventFactorySupport(EventFactorySupport eventFactorySupport) {
		this.eventFactorySupport = eventFactorySupport;
	}

	public void init(String flowId, AttributeMap attributes) throws FlowBuilderException {
		setFlow(getFlowArtifactFactory().createFlow(flowId, flowAttributes().union(attributes)));
	}

	/**
	 * Hook subclasses may override to provide additional properties for the
	 * flow built by this builder. Returns a empty collection by default.
	 * @return additional properties describing the flow being built, should not
	 * return null
	 */
	protected AttributeMap flowAttributes() {
		return CollectionUtils.EMPTY_ATTRIBUTE_MAP;
	}

	// view state

	/**
	 * Adds a view state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @param transition the sole transition (path) out of this state
	 * @return the fully constructed view state instance
	 */
	protected State addViewState(String stateId, String viewName, Transition transition) {
		return getFlowArtifactFactory().createViewState(stateId, getFlow(), null, viewSelector(viewName), null,
				new Transition[] { transition }, null, null, null);
	}

	/**
	 * Adds a view state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @param transitions the transitions (paths) out of this state
	 * @return the fully constructed view state instance
	 */
	protected State addViewState(String stateId, String viewName, Transition[] transitions) {
		return getFlowArtifactFactory().createViewState(stateId, getFlow(), null, viewSelector(viewName), null,
				transitions, null, null, null);
	}

	/**
	 * Adds a view state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @param renderAction the action to execute on state entry and refresh; may
	 * be null
	 * @param transition the sole transition (path) out of this state
	 * @return the fully constructed view state instance
	 */
	protected State addViewState(String stateId, String viewName, Action renderAction, Transition transition) {
		return getFlowArtifactFactory().createViewState(stateId, getFlow(), null, viewSelector(viewName),
				new Action[] { renderAction }, new Transition[] { transition }, null, null, null);
	}

	/**
	 * Adds a view state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @param renderAction the action to execute on state entry and refresh; may
	 * be null
	 * @param transitions the transitions (paths) out of this state
	 * @return the fully constructed view state instance
	 */
	protected State addViewState(String stateId, String viewName, Action renderAction, Transition[] transitions) {
		return getFlowArtifactFactory().createViewState(stateId, getFlow(), null, viewSelector(viewName),
				new Action[] { renderAction }, transitions, null, null, null);
	}

	/**
	 * Adds a view state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param entryActions the actions to execute when the state is entered
	 * @param viewSelector the view selector that will make the view selection
	 * when the state is entered
	 * @param renderActions any 'render actions' to execute on state entry and
	 * refresh; may be null
	 * @param transitions the transitions (path) out of this state
	 * @param exceptionHandlers any exception handlers to attach to the state
	 * @param exitActions the actions to execute when the state exits
	 * @param attributes attributes to assign to the state that may be used to
	 * affect state construction and execution
	 * @return the fully constructed view state instance
	 */
	protected State addViewState(String stateId, Action[] entryActions, ViewSelector viewSelector,
			Action[] renderActions, Transition[] transitions, FlowExecutionExceptionHandler[] exceptionHandlers,
			Action[] exitActions, AttributeMap attributes) {
		return getFlowArtifactFactory().createViewState(stateId, getFlow(), entryActions, viewSelector, renderActions,
				transitions, exceptionHandlers, exitActions, attributes);
	}

	// action state

	/**
	 * Adds an action state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param action the single action to execute when the state is entered
	 * @param transition the single transition (path) out of this state
	 * @return the fully constructed action state instance
	 */
	protected State addActionState(String stateId, Action action, Transition transition) {
		return getFlowArtifactFactory().createActionState(stateId, getFlow(), null, new Action[] { action },
				new Transition[] { transition }, null, null, null);
	}

	/**
	 * Adds an action state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param action the single action to execute when the state is entered
	 * @param transitions the transitions (paths) out of this state
	 * @return the fully constructed action state instance
	 */
	protected State addActionState(String stateId, Action action, Transition[] transitions) {
		return getFlowArtifactFactory().createActionState(stateId, getFlow(), null, new Action[] { action },
				transitions, null, null, null);
	}

	/**
	 * Adds an action state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param action the single action to execute when the state is entered
	 * @param transition the single transition (path) out of this state
	 * @param exceptionHandler the exception handler to handle exceptions thrown
	 * by the action
	 * @return the fully constructed action state instance
	 */
	protected State addActionState(String stateId, Action action, Transition transition,
			FlowExecutionExceptionHandler exceptionHandler) {
		return getFlowArtifactFactory().createActionState(stateId, getFlow(), null, new Action[] { action },
				new Transition[] { transition }, new FlowExecutionExceptionHandler[] { exceptionHandler }, null, null);
	}

	/**
	 * Adds an action state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param entryActions any generic entry actions to add to the state
	 * @param actions the actions to execute in a chain when the state is
	 * entered
	 * @param transitions the transitions (paths) out of this state
	 * @param exceptionHandlers the exception handlers to handle exceptions
	 * thrown by the actions
	 * @param exitActions the exit actions to execute when the state exits
	 * @param attributes attributes to assign to the state that may be used to
	 * affect state construction and execution
	 * @return the fully constructed action state instance
	 */
	protected State addActionState(String stateId, Action[] entryActions, Action[] actions, Transition[] transitions,
			FlowExecutionExceptionHandler[] exceptionHandlers, Action[] exitActions, AttributeMap attributes) {
		return getFlowArtifactFactory().createActionState(stateId, getFlow(), entryActions, actions, transitions,
				exceptionHandlers, exitActions, attributes);
	}

	// decision state

	/**
	 * Adds a decision state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param transitions the transitions (paths) out of this state
	 * @return the fully constructed decision state instance
	 */
	protected State addDecisionState(String stateId, Transition[] transitions) {
		return getFlowArtifactFactory().createDecisionState(stateId, getFlow(), null, transitions, null, null, null);
	}

	/**
	 * Adds a decision state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param decisionCriteria the criteria that defines the decision
	 * @param trueStateId the target state on a "true" decision
	 * @param falseStateId the target state on a "false" decision
	 * @return the fully constructed decision state instance
	 */
	protected State addDecisionState(String stateId, TransitionCriteria decisionCriteria, String trueStateId,
			String falseStateId) {
		Transition thenTransition = getFlowArtifactFactory()
				.createTransition(to(trueStateId), decisionCriteria, null, null);
		Transition elseTransition = getFlowArtifactFactory().createTransition(to(falseStateId), null, null, null);
		return getFlowArtifactFactory().createDecisionState(stateId, getFlow(), null,
				new Transition[] { thenTransition, elseTransition }, null, null, null);
	}

	/**
	 * Adds a decision state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param entryActions the entry actions to execute when the state enters
	 * @param transitions the transitions (paths) out of this state
	 * @param exceptionHandlers the exception handlers to handle exceptions
	 * thrown by the state
	 * @param exitActions the exit actions to execute when the state exits
	 * @param attributes attributes to assign to the state that may be used to
	 * affect state construction and execution
	 * @return the fully constructed decision state instance
	 */
	protected State addDecisionState(String stateId, Action[] entryActions, Transition[] transitions,
			FlowExecutionExceptionHandler[] exceptionHandlers, Action[] exitActions, AttributeMap attributes) {
		return getFlowArtifactFactory().createDecisionState(stateId, getFlow(), entryActions, transitions,
				exceptionHandlers, exitActions, attributes);
	}

	// subflow state

	/**
	 * Adds a subflow state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param subflow the flow that will act as the subflow
	 * @param attributeMapper the mapper to map subflow input and output
	 * attributes
	 * @param transition the single transition (path) out of the state
	 * @return the fully constructed subflow state instance
	 */
	protected State addSubflowState(String stateId, Flow subflow, FlowAttributeMapper attributeMapper,
			Transition transition) {
		return getFlowArtifactFactory().createSubflowState(stateId, getFlow(), null, subflow, attributeMapper,
				new Transition[] { transition }, null, null, null);
	}

	/**
	 * Adds a subflow state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param subflow the flow that will act as the subflow
	 * @param attributeMapper the mapper to map subflow input and output
	 * attributes
	 * @param transitions the transitions (paths) out of the state
	 * @return the fully constructed subflow state instance
	 */
	protected State addSubflowState(String stateId, Flow subflow, FlowAttributeMapper attributeMapper,
			Transition[] transitions) {
		return getFlowArtifactFactory().createSubflowState(stateId, getFlow(), null, subflow, attributeMapper,
				transitions, null, null, null);
	}

	/**
	 * Adds a subflow state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param entryActions the entry actions to execute when the state enters
	 * @param subflow the flow that will act as the subflow
	 * @param attributeMapper the mapper to map subflow input and output
	 * attributes
	 * @param transitions the transitions (paths) out of this state
	 * @param exceptionHandlers the exception handlers to handle exceptions
	 * thrown by the state
	 * @param exitActions the exit actions to execute when the state exits
	 * @param attributes attributes to assign to the state that may be used to
	 * affect state construction and execution
	 * @return the fully constructed subflow state instance
	 */
	protected State addSubflowState(String stateId, Action[] entryActions, Flow subflow,
			FlowAttributeMapper attributeMapper, Transition[] transitions,
			FlowExecutionExceptionHandler[] exceptionHandlers, Action[] exitActions, AttributeMap attributes) {
		return getFlowArtifactFactory().createSubflowState(stateId, getFlow(), entryActions, subflow, attributeMapper,
				transitions, exceptionHandlers, exitActions, attributes);
	}

	// end state

	/**
	 * Adds an end state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @return the fully constructed end state instance
	 */
	protected State addEndState(String stateId) {
		return getFlowArtifactFactory().createEndState(stateId, getFlow(), null, null, null, null, null);
	}

	/**
	 * Adds an end state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @return the fully constructed end state instance
	 */
	protected State addEndState(String stateId, String viewName) {
		return getFlowArtifactFactory().createEndState(stateId, getFlow(), null, viewSelector(viewName), null,
				null, null);
	}

	/**
	 * Adds an end state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param viewName the string-encoded view selector
	 * @param outputMapper the output mapper to map output attributes for the
	 * end state (a flow outcome)
	 * @return the fully constructed end state instance
	 */
	protected State addEndState(String stateId, String viewName, AttributeMapper outputMapper) {
		return getFlowArtifactFactory().createEndState(stateId, getFlow(), null, viewSelector(viewName),
				outputMapper, null, null);
	}

	/**
	 * Adds an end state to the flow built by this builder.
	 * @param stateId the state identifier
	 * @param entryActions the actions to execute when the state is entered
	 * @param viewSelector the view selector that will make the view selection
	 * when the state is entered
	 * @param outputMapper the output mapper to map output attributes for the
	 * end state (a flow outcome)
	 * @param exceptionHandlers any exception handlers to attach to the state
	 * @param attributes attributes to assign to the state that may be used to
	 * affect state construction and execution
	 * @return the fully constructed end state instance
	 */
	protected State addEndState(String stateId, Action[] entryActions, ViewSelector viewSelector,
			AttributeMapper outputMapper, FlowExecutionExceptionHandler[] exceptionHandlers, AttributeMap attributes) {
		return getFlowArtifactFactory().createEndState(stateId, getFlow(), entryActions, viewSelector, outputMapper,
				exceptionHandlers, attributes);
	}

	// helpers to create misc. flow artifacts

	/**
	 * Factory method that creates a view selector from an encoded
	 * view name. See {@link TextToViewSelector} for information on the
	 * conversion rules.
	 * @param viewName the encoded view selector
	 * @return the view selector
	 */
	public ViewSelector viewSelector(String viewName) {
		return (ViewSelector)fromStringTo(ViewSelector.class).execute(viewName);
	}

	/**
	 * Resolves the action with the specified id. Simply looks the action up by
	 * id and returns it.
	 * @param id the action id
	 * @return the action
	 * @throws FlowArtifactLookupException the action could not be resolved
	 */
	protected Action action(String id) throws FlowArtifactLookupException {
		return getFlowServiceLocator().getAction(id);
	}

	/**
	 * Creates a bean invoking action that invokes the method identified by the
	 * signature on the bean associated with the action identifier.
	 * @param beanId the id identifying an arbitrary
	 * <code>java.lang.Object</code> to be used as an action
	 * @param methodSignature the signature of the method to invoke on the POJO
	 * @return the adapted bean invoking action
	 * @throws FlowArtifactLookupException the action could not be resolved
	 */
	protected Action action(String beanId, MethodSignature methodSignature) throws FlowArtifactLookupException {
		return getBeanInvokingActionFactory().createBeanInvokingAction(beanId,
				getFlowServiceLocator().getBeanFactory(), methodSignature, null,
				getFlowServiceLocator().getConversionService(), null);
	}

	/**
	 * Creates a bean invoking action that invokes the method identified by the
	 * signature on the bean associated with the action identifier.
	 * @param beanId the id identifying an arbitrary
	 * <code>java.lang.Object</code> to be used as an action
	 * @param methodSignature the signature of the method to invoke on the POJO
	 * @return the adapted bean invoking action
	 * @throws FlowArtifactLookupException the action could not be resolved
	 */
	protected Action action(String beanId, MethodSignature methodSignature, ActionResultExposer resultExposer)
			throws FlowArtifactLookupException {
		return getBeanInvokingActionFactory().createBeanInvokingAction(beanId,
				getFlowServiceLocator().getBeanFactory(), methodSignature, resultExposer,
				getFlowServiceLocator().getConversionService(), null);
	}

	/**
	 * Creates an evaluate action that evaluates the expression when executed.
	 * @param expression the expression to evaluate
	 */
	protected Action action(Expression expression) {
		return action(expression, null);
	}

	/**
	 * Creates an evaluate action that evaluates the expression when executed.
	 * @param expression the expression to evaluate
	 * @param resultExposer the evaluation result exposer
	 */
	protected Action action(Expression expression, ActionResultExposer resultExposer) {
		return new EvaluateAction(expression, resultExposer);
	}

	/**
	 * Parses the expression string into a evaluatable {@link Expression}
	 * object.
	 * @param expressionString the expression string, e.g. flowScope.order.number
	 * @return the evaluatable expression
	 */
	protected Expression expression(String expressionString) {
		return getFlowServiceLocator().getExpressionParser().parseExpression(expressionString);
	}

	/**
	 * Convert the encoded method signature string to a {@link MethodSignature}
	 * object. Method signatures are used to match methods on POJO services to
	 * invoke on a {@link AbstractBeanInvokingAction bean invoking action}.
	 * <p>
	 * Encoded method signature format:
	 * 
	 * Method without arguments:
	 * <pre>
	 *       ${methodName}
	 * </pre>
	 * 
	 * Method with arguments:
	 * <pre>
	 *       ${methodName}(${arg1}, ${arg2}, ${arg n})
	 * </pre>
	 * 
	 * @param method the encoded method signature
	 * @return the method signature
	 * @see #action(String, MethodSignature, ActionResultExposer)
	 */
	protected MethodSignature method(String method) {
		return (MethodSignature)fromStringTo(MethodSignature.class).execute(method);
	}

	/**
	 * Factory method for a {@link ActionResultExposer result exposer}. A
	 * result exposer is used to expose an action result such as a method return
	 * value or expression evaluation result to the calling flow.
	 * @param resultName the result name
	 * @return the result exposer
	 * @see #action(String, MethodSignature, ActionResultExposer)
	 */
	protected ActionResultExposer result(String resultName) {
		return result(resultName, ScopeType.REQUEST);
	}

	/**
	 * Factory method for a {@link ActionResultExposer result exposer}. A
	 * result exposer is used to expose an action result such as a method return
	 * value or expression evaluation result to the calling flow.
	 * @param resultName the result name
	 * @param resultScope the scope of the result
	 * @return the result exposer
	 * @see #action(String, MethodSignature, ActionResultExposer)
	 */
	protected ActionResultExposer result(String resultName, ScopeType resultScope) {
		return new ActionResultExposer(resultName, resultScope);
	}

	/**
	 * Creates an annotated action decorator that instructs the specified method
	 * be invoked on the multi action when it is executed. Use this when working
	 * with MultiActions to specify the method on the MultiAction to invoke for
	 * a particular usage scenario. Use the {@link #method(String)} factory
	 * method when working with
	 * {@link AbstractBeanInvokingAction bean invoking actions}.
	 * @param methodName the name of the method on the multi action instance
	 * @param multiAction the multi action
	 * @return the annotated action that when invoked sets up a context property
	 * used by the multi action to instruct it with what method to invoke
	 */
	protected AnnotatedAction invoke(String methodName, MultiAction multiAction) throws FlowArtifactLookupException {
		AnnotatedAction action = new AnnotatedAction(multiAction);
		action.setMethod(methodName);
		return action;
	}

	/**
	 * Request that the attribute mapper with the specified name be used to map
	 * attributes between a parent flow and a spawning subflow when the subflow
	 * state being constructed is entered.
	 * @param id the id of the attribute mapper that will map attributes between
	 * the flow built by this builder and the subflow
	 * @return the attribute mapper
	 * @throws FlowArtifactLookupException no FlowAttributeMapper implementation
	 * was exported with the specified id
	 */
	protected FlowAttributeMapper attributeMapper(String id) throws FlowArtifactLookupException {
		return getFlowServiceLocator().getAttributeMapper(id);
	}

	/**
	 * Request that the <code>Flow</code> with the specified flowId be spawned
	 * as a subflow when the subflow state being built is entered. Simply
	 * resolves the subflow definition by id and returns it; throwing a
	 * fail-fast exception if it does not exist.
	 * @param id the flow definition id
	 * @return the flow to be used as a subflow, this should be passed to a
	 * addSubflowState call
	 * @throws FlowArtifactLookupException when the flow cannot be resolved
	 */
	protected Flow flow(String id) throws FlowArtifactLookupException {
		return getFlowServiceLocator().getSubflow(id);
	}

	/**
	 * Creates a transition criteria that is used to match a Transition. The
	 * criteria is based on the provided expression string.
	 * @param transitionCriteriaExpression the transition criteria expression,
	 * typically simply a static event identifier (e.g. "submit")
	 * @return the transition criteria
	 */
	protected TransitionCriteria on(String transitionCriteriaExpression) {
		return (TransitionCriteria)fromStringTo(TransitionCriteria.class).execute(transitionCriteriaExpression);
	}

	/**
	 * Creates a target state resolver for the given state id expression.
	 * @param targetStateIdExpression the target state id expression
	 * @return the target state resolver
	 */
	protected TargetStateResolver to(String targetStateIdExpression) {
		return (TargetStateResolver)fromStringTo(TargetStateResolver.class).execute(targetStateIdExpression);
	}

	/**
	 * Creates a new transition.
	 * @param matchingCriteria the criteria that determines when the transition
	 * matches
	 * @param targetStateResolver the resolver of the transition's target state
	 * @return the transition
	 */
	protected Transition transition(TransitionCriteria matchingCriteria, TargetStateResolver targetStateResolver) {
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null, null);
	}

	/**
	 * Creates a new transition.
	 * @param matchingCriteria the criteria that determines when the transition
	 * matches
	 * @param targetStateResolver the resolver of the transition's target state
	 * @param executionCriteria the criteria that determines if a matched
	 * transition is allowed to execute
	 * @return the transition
	 */
	protected Transition transition(TransitionCriteria matchingCriteria, TargetStateResolver targetStateResolver,
			TransitionCriteria executionCriteria) {
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, executionCriteria, null);
	}

	/**
	 * Creates a new transition.
	 * @param matchingCriteria the criteria that determines when the transition
	 * matches
	 * @param targetStateResolver the resolver of the transition's target state
	 * @param executionCriteria the criteria that determines if a matched
	 * transition is allowed to execute
	 * @param attributes transition attributes
	 * @return the transition
	 */
	protected Transition transition(TransitionCriteria matchingCriteria, TargetStateResolver targetStateResolver,
			TransitionCriteria executionCriteria, AttributeMap attributes) {
		return getFlowArtifactFactory()
				.createTransition(targetStateResolver, matchingCriteria, executionCriteria, attributes);
	}

	/**
	 * Creates a <code>TransitionCriteria</code> that will execute the
	 * specified action when the Transition is executed but before the
	 * transition's target state is entered.
	 * <p>
	 * This criteria will only allow the Transition to complete execution if the
	 * Action completes successfully.
	 * @param action the action to execute after a transition is matched but
	 * before it transitions to its target state
	 * @return the transition execution criteria
	 */
	protected TransitionCriteria ifReturnedSuccess(Action action) {
		return new ActionTransitionCriteria(action);
	}

	/**
	 * Creates the <code>success</code> event id. "Success" indicates that an
	 * action completed successfuly.
	 * @return the event id
	 */
	protected String success() {
		return eventFactorySupport.getSuccessEventId();
	}

	/**
	 * Creates the <code>error</code> event id. "Error" indicates that an
	 * action completed with an error status.
	 * @return the event id
	 */
	protected String error() {
		return eventFactorySupport.getErrorEventId();
	}

	/**
	 * Creates the <code>submit</code> event id. "Submit" indicates the user
	 * submitted a request (form) for processing.
	 * @return the event id
	 */
	protected String submit() {
		return "submit";
	}

	/**
	 * Creates the <code>back</code> event id. "Back" indicates the user wants
	 * to go to the previous step in the flow.
	 * @return the event id
	 */
	protected String back() {
		return "back";
	}

	/**
	 * Creates the <code>cancel</code> event id. "Cancel" indicates the flow
	 * was aborted because the user changed their mind.
	 * @return the event id
	 */
	protected String cancel() {
		return "cancel";
	}

	/**
	 * Creates the <code>finish</code> event id. "Finish" indicates the flow
	 * has finished processing.
	 * @return the event id
	 */
	protected String finish() {
		return "finish";
	}

	/**
	 * Creates the <code>select</code> event id. "Select" indicates an object
	 * was selected for processing or display.
	 * @return the event id
	 */
	protected String select() {
		return "select";
	}

	/**
	 * Creates the <code>edit</code> event id. "Edit" indicates an object was
	 * selected for creation or updating.
	 * @return the event id
	 */
	protected String edit() {
		return "edit";
	}

	/**
	 * Creates the <code>add</code> event id. "Add" indicates a child object
	 * is being added to a parent collection.
	 * @return the event id
	 */
	protected String add() {
		return "add";
	}

	/**
	 * Creates the <code>delete</code> event id. "Delete" indicates a object
	 * is being removed.
	 * @return the event id
	 */
	protected String delete() {
		return "delete";
	}

	/**
	 * Creates the <code>yes</code> event id. "Yes" indicates a true result
	 * was returned.
	 * @return the event id
	 */
	protected String yes() {
		return eventFactorySupport.getYesEventId();
	}

	/**
	 * Creates the <code>no</code> event id. "False" indicates a false result
	 * was returned.
	 * @return the event id
	 */
	protected String no() {
		return eventFactorySupport.getNoEventId();
	}

	/**
	 * Factory method that returns a new, fully configured mapping builder to
	 * assist with building {@link Mapping} objects used by a
	 * {@link FlowAttributeMapper} to map attributes.
	 * @return the mapping builder
	 */
	protected MappingBuilder mapping() {
		MappingBuilder mapping = new MappingBuilder(getFlowServiceLocator().getExpressionParser());
		mapping.setConversionService(getFlowServiceLocator().getConversionService());
		return mapping;
	}
	
	public String toString() {
		return new ToStringCreator(this).toString();
	}

	// internal helpers

	private FlowArtifactFactory getFlowArtifactFactory() {
		return getFlowServiceLocator().getFlowArtifactFactory();
	}

	private BeanInvokingActionFactory getBeanInvokingActionFactory() {
		return getFlowServiceLocator().getBeanInvokingActionFactory();
	}
}