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
package org.springframework.webflow.test.execution;

import junit.framework.TestCase;

import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Base class for integration tests that verify a flow executes as expected. Flow execution tests captured by subclasses
 * should test that a flow responds to all supported transition criteria correctly, transitioning to the correct states
 * and producing the expected results on the occurrence of possible external (user) events.
 * <p>
 * More specifically, a typical flow execution test case will test:
 * <ul>
 * <li>That the flow execution starts as expected (see {@link #startFlow(MutableAttributeMap, ExternalContext)}).
 * <li>That given the set of supported state transition criteria, a state executes the appropriate transition when a
 * matching event is signaled (with potential input request parameters, see the {@link #resumeFlow(ExternalContext)}
 * variants). A test case should be coded for each logical event that can occur, where an event drives a possible path
 * through the flow. The goal should be to exercise all possible paths of the flow. Use a test coverage tool like Clover
 * or Emma to assist with measuring your test's effectiveness.
 * <li>That given a transition that leads to an interactive state type (a view state or an end state) that the view
 * selection returned to the client matches what was expected and the current state of the flow matches what is
 * expected.
 * </ul>
 * <p>
 * A flow execution test can effectively automate and validate the orchestration required to drive an end-to-end
 * business task that spans several steps involving the user to complete. Such tests are a good way to test your system
 * top-down starting at the web-tier and pushing through all the way to the DB without having to deploy to a servlet or
 * portlet container. In addition, they can be used to effectively test a flow's execution (the web layer) standalone,
 * typically with a mock service layer. Both styles of testing are valuable and supported.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFlowExecutionTests extends TestCase {

	/**
	 * The factory that will create the flow execution to test.
	 */
	private FlowExecutionFactory flowExecutionFactory;

	/**
	 * The flow execution running the flow when the test is active (runtime object).
	 */
	private FlowExecution flowExecution;

	/**
	 * The outcome that was reached when the flow ends; initially null.
	 */
	private FlowExecutionOutcome flowExecutionOutcome;

	/**
	 * Constructs a default flow execution test.
	 * @see #setName(String)
	 */
	public AbstractFlowExecutionTests() {
		super();
	}

	/**
	 * Constructs a flow execution test with given name.
	 * @param name the name of the test
	 */
	public AbstractFlowExecutionTests(String name) {
		super(name);
	}

	/**
	 * Gets the factory that will create the flow execution to test. This method will create the factory if it is not
	 * already set.
	 * @return the flow execution factory
	 * @see #createFlowExecutionFactory()
	 */
	protected FlowExecutionFactory getFlowExecutionFactory() {
		if (flowExecutionFactory == null) {
			flowExecutionFactory = createFlowExecutionFactory();
		}
		return flowExecutionFactory;
	}

	/**
	 * Start the flow execution to be tested.
	 * @param context the external context providing information about the caller's environment, used by the flow
	 * execution during the start operation
	 * @throws FlowExecutionException if an exception was thrown while starting the flow execution
	 */
	protected void startFlow(ExternalContext context) throws FlowExecutionException {
		startFlow(null, context);
	}

	/**
	 * Start the flow execution to be tested.
	 * @param input input to pass the flow
	 * @param context the external context providing information about the caller's environment, used by the flow
	 * execution during the start operation
	 * @throws FlowExecutionException if an exception was thrown while starting the flow execution
	 */
	protected void startFlow(MutableAttributeMap input, ExternalContext context) throws FlowExecutionException {
		flowExecution = getFlowExecutionFactory().createFlowExecution(getFlowDefinition());
		flowExecution.start(input, context);
		if (flowExecution.hasEnded()) {
			flowExecutionOutcome = flowExecution.getOutcome();
		}
	}

	/**
	 * Resume the flow execution to be tested.
	 * @param context the external context providing information about the caller's environment, used by the flow
	 * execution during the start operation
	 * @throws FlowExecutionException if an exception was thrown while starting the flow execution
	 */
	protected void resumeFlow(ExternalContext context) throws FlowExecutionException {
		Assert.state(flowExecution != null, "The flow execution to test is [null]; "
				+ "you must start the flow execution before you can resume it!");
		flowExecution.resume(context);
		if (flowExecution.hasEnded()) {
			flowExecutionOutcome = flowExecution.getOutcome();
		}
	}

	/**
	 * Sets the current state of the flow execution being tested. If the execution has not been started, it will be
	 * created and activated.
	 * @param stateId the state id
	 */
	protected void setCurrentState(String stateId) {
		if (flowExecution == null) {
			flowExecution = getFlowExecutionFactory().createFlowExecution(getFlowDefinition());
		}
		((FlowExecutionImpl) flowExecution).setCurrentState(stateId);
	}

	// convenience accessors

	/**
	 * Returns the flow execution being tested.
	 * @return the flow execution
	 * @throws IllegalStateException the execution has not been started
	 */
	protected FlowExecution getFlowExecution() throws IllegalStateException {
		return flowExecution;
	}

	/**
	 * Returns the flow execution outcome that was reached.
	 * @return the flow execution outcome, or null if the flow execution has not ended
	 */
	protected FlowExecutionOutcome getFlowExecutionOutcome() {
		return flowExecutionOutcome;
	}

	/**
	 * Returns view scope.
	 * @return view scope
	 */
	protected MutableAttributeMap getViewScope() throws IllegalStateException {
		return getFlowExecution().getActiveSession().getViewScope();
	}

	/**
	 * Returns flow scope.
	 * @return flow scope
	 */
	protected MutableAttributeMap getFlowScope() throws IllegalStateException {
		return getFlowExecution().getActiveSession().getScope();
	}

	/**
	 * Returns conversation scope.
	 * @return conversation scope
	 */
	protected MutableAttributeMap getConversationScope() throws IllegalStateException {
		return getFlowExecution().getConversationScope();
	}

	/**
	 * Returns the attribute in view scope. View-scoped attributes are local to the current view state and are cleared
	 * when the view state exits.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 */
	protected Object getViewAttribute(String attributeName) {
		return getFlowExecution().getActiveSession().getViewScope().get(attributeName);
	}

	/**
	 * Returns the required attribute in view scope; asserts the attribute is present. View-scoped attributes are local
	 * to the current view state and are cleared when the view state exits.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present
	 */
	protected Object getRequiredViewAttribute(String attributeName) throws IllegalStateException {
		return getFlowExecution().getActiveSession().getViewScope().getRequired(attributeName);
	}

	/**
	 * Returns the required attribute in view scope; asserts the attribute is present and of the correct type.
	 * View-scoped attributes are local to the current view state and are cleared when the view state exits.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present or was of the wrong type
	 */
	protected Object getRequiredViewAttribute(String attributeName, Class requiredType) throws IllegalStateException {
		return getFlowExecution().getActiveSession().getViewScope().getRequired(attributeName, requiredType);
	}

	/**
	 * Returns the attribute in flow scope. Flow-scoped attributes are local to the active flow session.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 */
	protected Object getFlowAttribute(String attributeName) {
		return getFlowExecution().getActiveSession().getScope().get(attributeName);
	}

	/**
	 * Returns the required attribute in flow scope; asserts the attribute is present. Flow-scoped attributes are local
	 * to the active flow session.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present
	 */
	protected Object getRequiredFlowAttribute(String attributeName) throws IllegalStateException {
		return getFlowExecution().getActiveSession().getScope().getRequired(attributeName);
	}

	/**
	 * Returns the required attribute in flow scope; asserts the attribute is present and of the correct type.
	 * Flow-scoped attributes are local to the active flow session.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present or was of the wrong type
	 */
	protected Object getRequiredFlowAttribute(String attributeName, Class requiredType) throws IllegalStateException {
		return getFlowExecution().getActiveSession().getScope().getRequired(attributeName, requiredType);
	}

	/**
	 * Returns the attribute in conversation scope. Conversation-scoped attributes are shared by all flow sessions.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 */
	protected Object getConversationAttribute(String attributeName) {
		return getFlowExecution().getConversationScope().get(attributeName);
	}

	/**
	 * Returns the required attribute in conversation scope; asserts the attribute is present. Conversation-scoped
	 * attributes are shared by all flow sessions.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present
	 */
	protected Object getRequiredConversationAttribute(String attributeName) throws IllegalStateException {
		return getFlowExecution().getConversationScope().getRequired(attributeName);
	}

	/**
	 * Returns the required attribute in conversation scope; asserts the attribute is present and of the required type.
	 * Conversation-scoped attributes are shared by all flow sessions.
	 * @param attributeName the name of the attribute
	 * @return the attribute value
	 * @throws IllegalStateException if the attribute was not present or not of the required type
	 */
	protected Object getRequiredConversationAttribute(String attributeName, Class requiredType)
			throws IllegalStateException {
		return getFlowExecution().getConversationScope().getRequired(attributeName, requiredType);
	}

	// assert helpers

	/**
	 * Assert that the entire flow execution is active; that is, it has not ended and has been started.
	 */
	protected void assertFlowExecutionActive() {
		assertTrue("The flow execution is not active but it should be", getFlowExecution().isActive());
	}

	/**
	 * Assert that the active flow session is for the flow with the provided id.
	 * @param expectedActiveFlowId the flow id that should have a session active in the tested flow execution
	 */
	protected void assertActiveFlowEquals(String expectedActiveFlowId) {
		assertEquals("The active flow id '" + getFlowExecution().getActiveSession().getDefinition().getId()
				+ "' does not equal the expected active flow id '" + expectedActiveFlowId + "'", expectedActiveFlowId,
				getFlowExecution().getActiveSession().getDefinition().getId());
	}

	/**
	 * Assert that the entire flow execution has ended; that is, it is no longer active.
	 */
	protected void assertFlowExecutionEnded() {
		assertTrue("The flow execution is still active but it should have ended", getFlowExecution().hasEnded());
	}

	/**
	 * Assert that the entire flow execution has ended; that is, it is no longer active.
	 */
	protected void assertFlowExecutionOutcomeEquals(String outcome) {
		assertNotNull("There has been no flow execution outcome", flowExecutionOutcome);
		assertEquals("The flow execution outcome is wrong", flowExecutionOutcome.getId(), outcome);
	}

	/**
	 * Assert that the current state of the flow execution equals the provided state id.
	 * @param expectedCurrentStateId the expected current state
	 */
	protected void assertCurrentStateEquals(String expectedCurrentStateId) {
		assertEquals("The current state '" + getFlowExecution().getActiveSession().getState().getId()
				+ "' does not equal the expected state '" + expectedCurrentStateId + "'", expectedCurrentStateId,
				getFlowExecution().getActiveSession().getState().getId());
	}

	/**
	 * Assert that the response written to the mock context equals the response provided.
	 * @param response the expected response
	 * @param context the mock external context that was written to
	 */
	protected void assertResponseWrittenEquals(String response, MockExternalContext context) {
		assertEquals(response, context.getMockResponseWriter().getBuffer().toString());
	}

	/**
	 * Factory method to create the flow execution factory. Subclasses could override this if they want to use a custom
	 * flow execution factory or custom configuration of the flow execution factory, registering flow execution
	 * listeners for instance. The default implementation just returns a {@link FlowExecutionImplFactory} instance.
	 * @return the flow execution factory
	 */
	protected FlowExecutionFactory createFlowExecutionFactory() {
		return new FlowExecutionImplFactory();
	}

	/**
	 * Directly update the flow execution used by the test by setting it to given flow execution. Use this if you have
	 * somehow manipulated the flow execution being tested and want to continue the test with another flow execution.
	 * @param flowExecution the flow execution to use
	 */
	protected void updateFlowExecution(FlowExecution flowExecution) {
		this.flowExecution = flowExecution;
	}

	/**
	 * Returns the flow definition to be tested. Subclasses must implement.
	 * @return the flow definition
	 */
	protected abstract FlowDefinition getFlowDefinition();
}