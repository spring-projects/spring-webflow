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
package org.springframework.webflow.definition;

import org.springframework.context.ApplicationContext;
import org.springframework.webflow.core.Annotated;

/**
 * The definition of a flow, a program that when executed carries out a task on behalf of a single client.
 * <p>
 * A flow definition is a reusable, self-contained controller module that defines a blue print for an executable user
 * task. Flows typically orchestrate controlled navigations or dialogs within web applications to guide users through
 * fulfillment of a business process/goal that takes place over a series of steps, modeled as states.
 * <p>
 * Structurally a flow definition is composed of a set of states. A {@link StateDefinition state} is a point in a flow
 * where a behavior is executed; for example, showing a view, executing an action, spawning a subflow, or terminating
 * the flow. Different types of states execute different behaviors in a polymorphic fashion. Most states are
 * {@link TransitionableStateDefinition transitionable states}, meaning they can respond to events by taking the flow
 * from one state to another.
 * <p>
 * Each flow has exactly one {@link #getStartState() start state} which defines the starting point of the program.
 * <p>
 * This interface exposes the flow's identifier, states, and other definitional attributes. It is suitable for
 * introspection by tools as well as user-code at flow execution time.
 * <p>
 * Flow definitions may be annotated with attributes.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowDefinition extends Annotated {

	/**
	 * Returns the unique id of this flow.
	 * @return the flow id
	 */
	public String getId();

	/**
	 * Return this flow's starting point.
	 * @return the start state
	 */
	public StateDefinition getStartState();

	/**
	 * Returns the state definition with the specified id.
	 * @param id the state id
	 * @return the state definition
	 * @throws IllegalArgumentException if a state with this id does not exist
	 */
	public StateDefinition getState(String id) throws IllegalArgumentException;

	/**
	 * Returns the outcomes that are possible for this flow to reach.
	 * @return the possible outcomes
	 */
	public String[] getPossibleOutcomes();

	/**
	 * Returns the class loader used by this flow definition to load classes.
	 * @return the class loader
	 */
	public ClassLoader getClassLoader();

	/**
	 * Returns a reference to application context hosting application objects and services used by this flow definition.
	 * @return the application context
	 */
	public ApplicationContext getApplicationContext();

	/**
	 * Returns true if this flow definition is currently in development (running in development mode).
	 * @return the development flag
	 */
	public boolean inDevelopment();

	/**
	 * Destroy this flow definition, releasing any resources. After the flow is destroyed it cannot be started again.
	 */
	public void destroy();

}