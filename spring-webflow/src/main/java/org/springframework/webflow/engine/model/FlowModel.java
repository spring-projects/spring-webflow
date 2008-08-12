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
package org.springframework.webflow.engine.model;

import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.util.StringUtils;

/**
 * Model support for flows.
 * <p>
 * Defines exactly one flow definition. A flow is composed of one or more states that define the steps of a
 * conversation. One of those steps is the start state, which defines the conversation's starting point. <br>
 * A flow may also exhibit the following characteristics:
 * <ul>
 * <li>Be annotated with attributes that define descriptive properties that may affect flow execution. (See
 * {@link AttributeModel})
 * <li>Be secured (See {@link SecuredModel})
 * <li>Be a persistence context for managing persistent objects during the course of flow execution. (See
 * {@link PersistenceContextModel})
 * <li>Instantiate a set of instance variables when started. (See {@link VarModel})
 * <li>Map input provided by callers that start it (See {@link InputModel})
 * <li>Return output to callers that end it. (See {@link OutputModel})
 * <li>Execute actions at start time and end time. (See {@link EvaluateModel}, {@link RenderModel} and
 * {@link SetModel})
 * <li>Define transitions shared by all states. (See {@link TransitionModel})
 * <li>Handle exceptions thrown by during flow execution. (See {@link ExceptionHandlerModel})
 * <li>Import one or more local bean definition files defining custom flow artifacts (such as actions, exception
 * handlers, view factories, transition criteria, etc). (See {@link BeanImportModel})
 * </ul>
 * 
 * @author Scott Andrews
 */
public class FlowModel extends AbstractModel {

	private String abztract;

	private String parent;

	private String startStateId;

	private LinkedList attributes;

	private SecuredModel secured;

	private PersistenceContextModel persistenceContext;

	private LinkedList vars;

	private LinkedList inputs;

	private LinkedList outputs;

	private LinkedList onStartActions;

	private LinkedList states;

	private LinkedList globalTransitions;

	private LinkedList onEndActions;

	private LinkedList exceptionHandlers;

	private LinkedList beanImports;

	/**
	 * Create a flow model
	 */
	public FlowModel() {
	}

	public boolean isMergeableWith(Model model) {
		if ((model instanceof FlowModel)) {
			return true;
		} else {
			return false;
		}
	}

	public void merge(Model model) {
		FlowModel flow = (FlowModel) model;
		setParent(null);
		setStartStateId(merge(getStartStateId(), flow.getStartStateId()));
		setAttributes(merge(getAttributes(), flow.getAttributes()));
		setSecured((SecuredModel) merge(getSecured(), flow.getSecured()));
		setPersistenceContext((PersistenceContextModel) merge(getPersistenceContext(), flow.getPersistenceContext()));
		setVars(merge(getVars(), flow.getVars(), false));
		setInputs(merge(getInputs(), flow.getInputs()));
		setOutputs(merge(getOutputs(), flow.getOutputs()));
		setOnStartActions(merge(getOnStartActions(), flow.getOnStartActions(), false));
		setStates(merge(getStates(), flow.getStates()));
		setGlobalTransitions(merge(getGlobalTransitions(), flow.getGlobalTransitions()));
		setOnEndActions(merge(getOnEndActions(), flow.getOnEndActions(), false));
		setExceptionHandlers(merge(getExceptionHandlers(), flow.getExceptionHandlers()));
		setBeanImports(merge(getBeanImports(), flow.getBeanImports()));
	}

	/**
	 * @return the abstract
	 */
	public String getAbstract() {
		return abztract;
	}

	/**
	 * @param abztract the abstract to set
	 */
	public void setAbstract(String abztract) {
		if (StringUtils.hasText(abztract)) {
			this.abztract = abztract;
		} else {
			this.abztract = null;
		}
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		if (StringUtils.hasText(parent)) {
			this.parent = parent;
		} else {
			this.parent = null;
		}
	}

	/**
	 * @return the id of the flow's start state
	 */
	public String getStartStateId() {
		return startStateId;
	}

	/**
	 * @param startStateId the id of the flow's start state to set
	 */
	public void setStartStateId(String startStateId) {
		if (StringUtils.hasText(startStateId)) {
			this.startStateId = startStateId;
		} else {
			this.startStateId = null;
		}
	}

	/**
	 * @return the attributes
	 */
	public LinkedList getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(LinkedList attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the secured
	 */
	public SecuredModel getSecured() {
		return secured;
	}

	/**
	 * @param secured the secured to set
	 */
	public void setSecured(SecuredModel secured) {
		this.secured = secured;
	}

	/**
	 * @return the persistence context
	 */
	public PersistenceContextModel getPersistenceContext() {
		return persistenceContext;
	}

	/**
	 * @param persistenceContext the persistence context to set
	 */
	public void setPersistenceContext(PersistenceContextModel persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	/**
	 * @return the vars
	 */
	public LinkedList getVars() {
		return vars;
	}

	/**
	 * @param vars the vars to set
	 */
	public void setVars(LinkedList vars) {
		this.vars = vars;
	}

	/**
	 * @return the input mappings
	 */
	public LinkedList getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the input mappings to set
	 */
	public void setInputs(LinkedList inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the output mappings
	 */
	public LinkedList getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the output mappings to set
	 */
	public void setOutputs(LinkedList outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the on start actions
	 */
	public LinkedList getOnStartActions() {
		return onStartActions;
	}

	/**
	 * @param onStartActions the on start actions to set
	 */
	public void setOnStartActions(LinkedList onStartActions) {
		this.onStartActions = onStartActions;
	}

	/**
	 * @return the states
	 */
	public LinkedList getStates() {
		return states;
	}

	/**
	 * Get the state model for an identifier
	 * @param id the state identifier to find
	 * @return the state or null if the identifier was not found
	 */
	public AbstractStateModel getStateById(String id) {
		if (states != null) {
			for (Iterator it = states.iterator(); it.hasNext();) {
				AbstractStateModel state = (AbstractStateModel) it.next();
				if (id.equals(state.getId())) {
					return state;
				}
			}
		}
		return null;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(LinkedList states) {
		this.states = states;
	}

	/**
	 * @return the global transitions
	 */
	public LinkedList getGlobalTransitions() {
		return globalTransitions;
	}

	/**
	 * @param globalTransitions the global transitions to set
	 */
	public void setGlobalTransitions(LinkedList globalTransitions) {
		this.globalTransitions = globalTransitions;
	}

	/**
	 * @return the on end actions
	 */
	public LinkedList getOnEndActions() {
		return onEndActions;
	}

	/**
	 * @param onEndActions the on end actions to set
	 */
	public void setOnEndActions(LinkedList onEndActions) {
		this.onEndActions = onEndActions;
	}

	/**
	 * @return the exception handlers
	 */
	public LinkedList getExceptionHandlers() {
		return exceptionHandlers;
	}

	/**
	 * @param exceptionHandlers the exception handlers to set
	 */
	public void setExceptionHandlers(LinkedList exceptionHandlers) {
		this.exceptionHandlers = exceptionHandlers;
	}

	/**
	 * @return the bean imports
	 */
	public LinkedList getBeanImports() {
		return beanImports;
	}

	/**
	 * @param beanImports the bean imports to set
	 */
	public void setBeanImports(LinkedList beanImports) {
		this.beanImports = beanImports;
	}

}