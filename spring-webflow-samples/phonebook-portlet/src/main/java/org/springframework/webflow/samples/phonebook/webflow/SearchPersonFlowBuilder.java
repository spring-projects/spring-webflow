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
package org.springframework.webflow.samples.phonebook.webflow;

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.builder.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.FlowServiceLocator;
import org.springframework.webflow.engine.support.ConfigurableFlowAttributeMapper;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.samples.phonebook.SearchCriteria;
import org.springframework.webflow.samples.phonebook.SearchCriteriaValidator;

/**
 * Java-based flow builder that searches for people in the phonebook. The flow
 * defined by this class is exactly the same as that defined in the
 * <code>search-flow.xml</code> XML flow definition.
 * <p>
 * This encapsulates the page flow of searching for some people, selecting a
 * person you care about, and viewing their person's details and those of their
 * collegues in a reusable, self-contained module.
 * 
 * @author Keith Donald
 */
class SearchPersonFlowBuilder extends AbstractFlowBuilder {

	public SearchPersonFlowBuilder(FlowServiceLocator flowServiceLocator) {
		super(flowServiceLocator);
	}

	public void buildStates() throws FlowBuilderException {
		// view search criteria
		MultiAction searchFormAction = createSearchFormAction();
		addViewState("enterCriteria", "searchCriteria", invoke("setupForm", searchFormAction),
				new Transition[] { transition(on("search"), to("executeSearch"), ifReturnedSuccess(invoke(
						"bindAndValidate", searchFormAction))) });

		// execute query
		addActionState("executeSearch", action("phonebook", method("search(${flowScope.searchCriteria})"),
				result("results")), transition(on(success()), to("displayResults")));

		// view results
		addViewState("displayResults", "searchResults", new Transition[] {
				transition(on("newSearch"), to("enterCriteria")), transition(on(select()), to("browseDetails")) });

		// view details for selected user id
		ConfigurableFlowAttributeMapper idMapper = new ConfigurableFlowAttributeMapper();
		idMapper.addInputMapping(mapping().source("requestParameters.id").target("id").from(String.class)
				.to(Long.class).value());
		addSubflowState("browseDetails", flow("detail-flow"), idMapper, transition(on(finish()), to("executeSearch")));

		// end - an error occured
		addEndState(error(), "error");
	}

	protected FormAction createSearchFormAction() {
		FormAction action = new FormAction(SearchCriteria.class);
		action.setFormObjectScope(ScopeType.FLOW);
		action.setValidator(new SearchCriteriaValidator());
		return action;
	}
}