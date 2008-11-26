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
package org.springframework.webflow.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.style.StylerUtils;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.AnnotatedObject;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A single flow definition. A Flow definition is a reusable, self-contained controller module that provides the blue
 * print for a user dialog or conversation. Flows typically drive controlled navigations within web applications to
 * guide users through fulfillment of a business process/goal that takes place over a series of steps, modeled as
 * states.
 * <p>
 * A simple Flow definition could do nothing more than execute an action and display a view all in one request. A more
 * elaborate Flow definition may be long-lived and execute across a series of requests, invoking many possible paths,
 * actions, and subflows.
 * <p>
 * Especially in Intranet applications there are often "controlled navigations" where the user is not free to do what he
 * or she wants but must follow the guidelines provided by the system to complete a process that is transactional in
 * nature (the quintessential example would be a 'checkout' flow of a shopping cart application). This is a typical use
 * case appropriate to model as a flow.
 * <p>
 * Structurally a Flow is composed of a set of states. A {@link State} is a point in a flow where a behavior is
 * executed; for example, showing a view, executing an action, spawning a subflow, or terminating the flow. Different
 * types of states execute different behaviors in a polymorphic fashion.
 * <p>
 * Each {@link TransitionableState} type has one or more transitions that when executed move a flow to another state.
 * These transitions define the supported paths through the flow.
 * <p>
 * A state transition is triggered by the occurrence of an event. An event is something that happens the flow should
 * respond to, for example a user input event like ("submit") or an action execution result event like ("success"). When
 * an event occurs in a state of a Flow that event drives a state transition that decides what to do next.
 * <p>
 * Each Flow has exactly one start state. A start state is simply a marker noting the state executions of this Flow
 * definition should start in. The first state added to the flow will become the start state by default.
 * <p>
 * Flow definitions may have one or more flow exception handlers. A {@link FlowExecutionExceptionHandler} can execute
 * custom behavior in response to a specific exception (or set of exceptions) that occur in a state of one of this
 * flow's executions.
 * <p>
 * Instances of this class are typically built by {@link org.springframework.webflow.engine.builder.FlowBuilder}
 * implementations but may also be directly instantiated.
 * <p>
 * This class and the rest of the Spring Web Flow (SWF) engine have been designed with minimal dependencies on other
 * libraries. Spring Web Flow is usable in a standalone fashion. The engine system is fully usable outside an HTTP
 * servlet environment, for example in portlets, tests, or standalone applications. One of the major architectural
 * benefits of Spring Web Flow is the ability to design reusable, high-level controller modules that may be executed in
 * <i>any</i> environment.
 * <p>
 * Note: flows are singleton definition objects so they should be thread-safe. You can think a flow definition as
 * analogous to a Java class, defining all the behavior of an application module. The core behaviors
 * {@link #start(RequestControlContext, MutableAttributeMap) start}, {@link #resume(RequestControlContext)},
 * {@link #handleEvent(RequestControlContext) on event},
 * {@link #end(RequestControlContext, String, MutableAttributeMap) end}, and
 * {@link #handleException(FlowExecutionException, RequestControlContext)}. Each method accepts a {@link RequestContext
 * request context} that allows for this flow to access execution state in a thread safe manner. A flow execution is
 * what models a running instance of this flow definition, somewhat analogous to a java object that is an instance of a
 * class.
 * 
 * @see org.springframework.webflow.engine.State
 * @see org.springframework.webflow.engine.ActionState
 * @see org.springframework.webflow.engine.ViewState
 * @see org.springframework.webflow.engine.SubflowState
 * @see org.springframework.webflow.engine.EndState
 * @see org.springframework.webflow.engine.DecisionState
 * @see org.springframework.webflow.engine.Transition
 * @see org.springframework.webflow.engine.FlowExecutionExceptionHandler
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Colin Sampaleanu
 * @author Jeremy Grelle
 */
public class Flow extends AnnotatedObject implements FlowDefinition {

	/**
	 * Logger, can be used in subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * An assigned flow identifier uniquely identifying this flow among all other flows.
	 */
	private String id;

	/**
	 * The set of state definitions for this flow.
	 */
	private Set states = new LinkedHashSet(9);

	/**
	 * The default start state for this flow.
	 */
	private State startState;

	/**
	 * The set of flow variables created by this flow.
	 */
	private Map variables = new LinkedHashMap();

	/**
	 * The mapper to map flow input attributes.
	 */
	private Mapper inputMapper;

	/**
	 * The list of actions to execute when this flow starts.
	 * <p>
	 * Start actions should execute with care as during startup a flow session has not yet fully initialized and some
	 * properties like its "currentState" have not yet been set.
	 */
	private ActionList startActionList = new ActionList();

	/**
	 * The set of global transitions that are shared by all states of this flow.
	 */
	private TransitionSet globalTransitionSet = new TransitionSet();

	/**
	 * The list of actions to execute when this flow ends.
	 */
	private ActionList endActionList = new ActionList();

	/**
	 * The mapper to map flow output attributes.
	 */
	private Mapper outputMapper;

	/**
	 * The set of exception handlers for this flow.
	 */
	private FlowExecutionExceptionHandlerSet exceptionHandlerSet = new FlowExecutionExceptionHandlerSet();

	/**
	 * An optional application context hosting services needed by this flow.
	 */
	private ApplicationContext applicationContext;

	/**
	 * Construct a new flow definition with the given id. The id should be unique among all flows.
	 * @param id the flow identifier
	 */
	public Flow(String id) {
		Assert.hasText(id, "This flow must be uniquely identified");
		this.id = id;
	}

	// convenient static factory methods

	/**
	 * Create a new flow with the given id and attributes.
	 * @param id the flow id
	 * @param attributes the attributes
	 * @return the flow
	 */
	public static Flow create(String id, AttributeMap attributes) {
		Flow flow = new Flow(id);
		flow.getAttributes().putAll(attributes);
		return flow;
	}

	// implementing FlowDefinition

	public String getId() {
		return id;
	}

	public StateDefinition getStartState() {
		if (startState == null) {
			throw new IllegalStateException("No start state has been set for this flow ('" + getId()
					+ "') -- flow builder configuration error?");
		}
		return startState;
	}

	public StateDefinition getState(String stateId) {
		return getStateInstance(stateId);
	}

	public String[] getPossibleOutcomes() {
		List possibleOutcomes = new ArrayList();
		for (Iterator it = states.iterator(); it.hasNext();) {
			State state = (State) it.next();
			if (state instanceof EndState) {
				possibleOutcomes.add(state.getId());
			}
		}
		return (String[]) possibleOutcomes.toArray(new String[possibleOutcomes.size()]);
	}

	public ClassLoader getClassLoader() {
		if (applicationContext != null) {
			return applicationContext.getClassLoader();
		} else {
			return ClassUtils.getDefaultClassLoader();
		}
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public boolean inDevelopment() {
		return getAttributes().getBoolean("development", Boolean.FALSE).booleanValue();
	}

	/**
	 * Add given state definition to this flow definition. Marked protected, as this method is to be called by the
	 * (privileged) state definition classes themselves during state construction as part of a FlowBuilder invocation.
	 * @param state the state to add
	 * @throws IllegalArgumentException when the state cannot be added to the flow; for instance if another state shares
	 * the same id as the one provided or if given state already belongs to another flow
	 */
	protected void add(State state) throws IllegalArgumentException {
		if (this != state.getFlow() && state.getFlow() != null) {
			throw new IllegalArgumentException("State " + state + " cannot be added to this flow '" + getId()
					+ "' -- it already belongs to a different flow: '" + state.getFlow().getId() + "'");
		}
		if (this.states.contains(state) || this.containsState(state.getId())) {
			throw new IllegalArgumentException("This flow '" + getId() + "' already contains a state with id '"
					+ state.getId() + "' -- state ids must be locally unique to the flow definition; "
					+ "existing state-ids of this flow include: " + StylerUtils.style(getStateIds()));
		}
		boolean firstAdd = states.isEmpty();
		states.add(state);
		if (firstAdd) {
			setStartState(state);
		}
	}

	/**
	 * Returns the number of states defined in this flow.
	 * @return the state count
	 */
	public int getStateCount() {
		return states.size();
	}

	/**
	 * Is a state with the provided id present in this flow?
	 * @param stateId the state id
	 * @return true if yes, false otherwise
	 */
	public boolean containsState(String stateId) {
		Iterator it = states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			if (state.getId().equals(stateId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the start state for this flow to the state with the provided <code>stateId</code>; a state must exist by the
	 * provided <code>stateId</code>.
	 * @param stateId the id of the new start state
	 * @throws IllegalArgumentException when no state exists with the id you provided
	 */
	public void setStartState(String stateId) throws IllegalArgumentException {
		setStartState(getStateInstance(stateId));
	}

	/**
	 * Set the start state for this flow to the state provided; any state may be the start state.
	 * @param state the new start state
	 * @throws IllegalArgumentException given state has not been added to this flow
	 */
	public void setStartState(State state) throws IllegalArgumentException {
		if (!states.contains(state)) {
			throw new IllegalArgumentException("State '" + state + "' is not a state of flow '" + getId() + "'");
		}
		startState = state;
	}

	/**
	 * Return the <code>TransitionableState</code> with given <code>stateId</code>.
	 * @param stateId id of the state to look up
	 * @return the transitionable state
	 * @throws IllegalArgumentException if the identified state cannot be found
	 * @throws ClassCastException when the identified state is not transitionable
	 */
	public TransitionableState getTransitionableState(String stateId) throws IllegalArgumentException,
			ClassCastException {
		State state = getStateInstance(stateId);
		if (state != null && !(state instanceof TransitionableState)) {
			throw new ClassCastException("The state '" + stateId + "' of flow '" + getId() + "' must be transitionable");
		}
		return (TransitionableState) state;
	}

	/**
	 * Lookup the identified state instance of this flow.
	 * @param stateId the state id
	 * @return the state
	 * @throws IllegalArgumentException if the identified state cannot be found
	 */
	public State getStateInstance(String stateId) throws IllegalArgumentException {
		if (!StringUtils.hasText(stateId)) {
			throw new IllegalArgumentException("The specified stateId is invalid: state identifiers must be non-blank");
		}
		Iterator it = states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			if (state.getId().equals(stateId)) {
				return state;
			}
		}
		throw new IllegalArgumentException("Cannot find state with id '" + stateId + "' in flow '" + getId() + "' -- "
				+ "Known state ids are '" + StylerUtils.style(getStateIds()) + "'");
	}

	/**
	 * Convenience accessor that returns an ordered array of the String <code>ids</code> for the state definitions
	 * associated with this flow definition.
	 * @return the state ids
	 */
	public String[] getStateIds() {
		String[] stateIds = new String[getStateCount()];
		int i = 0;
		Iterator it = states.iterator();
		while (it.hasNext()) {
			stateIds[i++] = ((State) it.next()).getId();
		}
		return stateIds;
	}

	/**
	 * Adds a flow variable.
	 * @param variable the variable
	 */
	public void addVariable(FlowVariable variable) {
		variables.put(variable.getName(), variable);
	}

	/**
	 * Adds flow variables.
	 * @param variables the variables
	 */
	public void addVariables(FlowVariable[] variables) {
		if (variables == null) {
			return;
		}
		for (int i = 0; i < variables.length; i++) {
			addVariable(variables[i]);
		}
	}

	/**
	 * Returns the flow variable with the given name.
	 * @param name the name of the variable
	 */
	public FlowVariable getVariable(String name) {
		return (FlowVariable) variables.get(name);
	}

	/**
	 * Returns the flow variables.
	 */
	public FlowVariable[] getVariables() {
		return (FlowVariable[]) variables.values().toArray(new FlowVariable[variables.size()]);
	}

	/**
	 * Returns the configured flow input mapper, or null if none.
	 * @return the input mapper
	 */
	public Mapper getInputMapper() {
		return inputMapper;
	}

	/**
	 * Sets the mapper to map flow input attributes.
	 * @param inputMapper the input mapper
	 */
	public void setInputMapper(Mapper inputMapper) {
		this.inputMapper = inputMapper;
	}

	/**
	 * Returns the list of actions executed by this flow when an execution of the flow <i>starts</i>. The returned list
	 * is mutable.
	 * @return the start action list
	 */
	public ActionList getStartActionList() {
		return startActionList;
	}

	/**
	 * Returns the list of actions executed by this flow when an execution of the flow <i>ends</i>. The returned list is
	 * mutable.
	 * @return the end action list
	 */
	public ActionList getEndActionList() {
		return endActionList;
	}

	/**
	 * Returns the configured flow output mapper, or null if none.
	 * @return the output mapper
	 */
	public Mapper getOutputMapper() {
		return outputMapper;
	}

	/**
	 * Sets the mapper to map flow output attributes.
	 * @param outputMapper the output mapper
	 */
	public void setOutputMapper(Mapper outputMapper) {
		this.outputMapper = outputMapper;
	}

	/**
	 * Returns the set of exception handlers, allowing manipulation of how exceptions are handled when thrown during
	 * flow execution. Exception handlers are invoked when an exception occurs at execution time and can execute custom
	 * exception handling logic as well as select an error view to display. Exception handlers attached at the flow
	 * level have an opportunity to handle exceptions that aren't handled at the state level.
	 * @return the exception handler set
	 */
	public FlowExecutionExceptionHandlerSet getExceptionHandlerSet() {
		return exceptionHandlerSet;
	}

	/**
	 * Returns the set of transitions eligible for execution by this flow if no state-level transition is matched. The
	 * returned set is mutable.
	 * @return the global transition set
	 */
	public TransitionSet getGlobalTransitionSet() {
		return globalTransitionSet;
	}

	/**
	 * Returns the transition that matches the event with the provided id.
	 * @param eventId the event id
	 * @return the transition that matches, or null if no match is found.
	 */
	public TransitionDefinition getGlobalTransition(String eventId) {
		for (Iterator it = globalTransitionSet.iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			if (transition.getId().equals(eventId)) {
				return transition;
			}
		}
		return null;
	}

	/**
	 * Sets a reference to the application context hosting application objects needed by this flow.
	 * @param applicationContext the application context
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	// id based equality

	public boolean equals(Object o) {
		if (!(o instanceof Flow)) {
			return false;
		}
		Flow other = (Flow) o;
		return id.equals(other.id);
	}

	public int hashCode() {
		return id.hashCode();
	}

	// behavioral code, could be overridden in subclasses

	/**
	 * Start a new session for this flow in its start state. This boils down to the following:
	 * <ol>
	 * <li>Create (setup) all registered flow variables ({@link #addVariable(FlowVariable)}) in flow scope.</li>
	 * <li>Map provided input data into the flow. Typically data will be mapped into flow scope using the registered
	 * input mapper ({@link #setInputMapper(Mapper)}).</li>
	 * <li>Execute all registered start actions ( {@link #getStartActionList()}).</li>
	 * <li>Enter the configured start state ({@link #setStartState(State)})</li>
	 * </ol>
	 * @param context the flow execution control context
	 * @param input eligible input into the session
	 * @throws FlowExecutionException when an exception occurs starting the flow
	 */
	public void start(RequestControlContext context, MutableAttributeMap input) throws FlowExecutionException {
		assertStartStateSet();
		createVariables(context);
		if (inputMapper != null) {
			MappingResults results = inputMapper.map(input, context);
			if (results != null && results.hasErrorResults()) {
				throw new FlowInputMappingException(getId(), results);
			}
		}
		startActionList.execute(context);
		startState.enter(context);
	}

	/**
	 * Resume a paused session for this flow in its current view state.
	 * @param context the flow execution control context
	 * @throws FlowExecutionException when an exception occurs during the resume operation
	 */
	public void resume(RequestControlContext context) throws FlowExecutionException {
		restoreVariables(context);
		getCurrentViewState(context).resume(context);
	}

	/**
	 * Handle the last event that occurred against an active session of this flow.
	 * @param context the flow execution control context
	 */
	public boolean handleEvent(RequestControlContext context) {
		TransitionableState currentState = getCurrentTransitionableState(context);
		try {
			return currentState.handleEvent(context);
		} catch (NoMatchingTransitionException e) {
			// try the flow level transition set for a match
			Transition transition = globalTransitionSet.getTransition(context);
			if (transition != null) {
				return context.execute(transition);
				// return transition.execute(currentState, context);
			} else {
				// no matching global transition => let the original exception
				// propagate
				throw e;
			}
		}
	}

	/**
	 * Inform this flow definition that an execution session of itself has ended. As a result, the flow will do the
	 * following:
	 * <ol>
	 * <li>Execute all registered end actions ({@link #getEndActionList()}).</li>
	 * <li>Map data available in the flow execution control context into provided output map using a registered output
	 * mapper ( {@link #setOutputMapper(Mapper)}).</li>
	 * </ol>
	 * @param context the flow execution control context
	 * @param outcome the logical flow outcome that will be returned by the session, generally the id of the terminating
	 * end state
	 * @param output initial output produced by the session that is eligible for modification by this method
	 * @throws FlowExecutionException when an exception occurs ending this flow
	 */
	public void end(RequestControlContext context, String outcome, MutableAttributeMap output)
			throws FlowExecutionException {
		endActionList.execute(context);
		if (outputMapper != null) {
			MappingResults results = outputMapper.map(context, output);
			if (results != null && results.hasErrorResults()) {
				throw new FlowOutputMappingException(getId(), results);
			}
		}
	}

	public void destroy() {
		if (applicationContext != null && applicationContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) applicationContext).close();
		}
	}

	/**
	 * Handle an exception that occurred during an execution of this flow.
	 * @param exception the exception that occurred
	 * @param context the flow execution control context
	 */
	public boolean handleException(FlowExecutionException exception, RequestControlContext context)
			throws FlowExecutionException {
		return getExceptionHandlerSet().handleException(exception, context);
	}

	// internal helpers

	private void assertStartStateSet() {
		if (startState == null) {
			throw new IllegalStateException("Unable to start flow '" + id
					+ "'; the start state is not set -- flow builder configuration error?");
		}
	}

	private void createVariables(RequestContext context) {
		Iterator it = variables.values().iterator();
		while (it.hasNext()) {
			FlowVariable variable = (FlowVariable) it.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Creating " + variable);
			}
			variable.create(context);
		}
	}

	public void restoreVariables(RequestContext context) {
		Iterator it = variables.values().iterator();
		while (it.hasNext()) {
			FlowVariable variable = (FlowVariable) it.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Restoring " + variable);
			}
			variable.restore(context);
		}
	}

	private ViewState getCurrentViewState(RequestControlContext context) {
		State currentState = (State) context.getCurrentState();
		if (!(currentState instanceof ViewState)) {
			throw new IllegalStateException("You can only resume paused view states, and state "
					+ context.getCurrentState() + " is not a view state - programmer error");
		}
		return (ViewState) currentState;
	}

	private TransitionableState getCurrentTransitionableState(RequestControlContext context) {
		State currentState = (State) context.getCurrentState();
		if (!(currentState instanceof TransitionableState)) {
			throw new IllegalStateException("You can only signal events in transitionable states, and state "
					+ context.getCurrentState() + " is not transitionable - programmer error");
		}
		return (TransitionableState) currentState;
	}

	public String toString() {
		return new ToStringCreator(this).append("id", id).append("states", states).append("startState", startState)
				.append("variables", variables).append("inputMapper", inputMapper).append("startActionList",
						startActionList).append("exceptionHandlerSet", exceptionHandlerSet).append(
						"globalTransitionSet", globalTransitionSet).append("endActionList", endActionList).append(
						"outputMapper", outputMapper).toString();
	}

}