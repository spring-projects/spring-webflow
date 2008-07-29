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
package org.springframework.webflow.engine.model.builder.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.webflow.engine.model.AbstractActionModel;
import org.springframework.webflow.engine.model.AbstractStateModel;
import org.springframework.webflow.engine.model.ActionStateModel;
import org.springframework.webflow.engine.model.AttributeModel;
import org.springframework.webflow.engine.model.BeanImportModel;
import org.springframework.webflow.engine.model.BinderModel;
import org.springframework.webflow.engine.model.BindingModel;
import org.springframework.webflow.engine.model.DecisionStateModel;
import org.springframework.webflow.engine.model.EndStateModel;
import org.springframework.webflow.engine.model.EvaluateModel;
import org.springframework.webflow.engine.model.ExceptionHandlerModel;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.IfModel;
import org.springframework.webflow.engine.model.InputModel;
import org.springframework.webflow.engine.model.OutputModel;
import org.springframework.webflow.engine.model.PersistenceContextModel;
import org.springframework.webflow.engine.model.RenderModel;
import org.springframework.webflow.engine.model.SecuredModel;
import org.springframework.webflow.engine.model.SetModel;
import org.springframework.webflow.engine.model.SubflowStateModel;
import org.springframework.webflow.engine.model.TransitionModel;
import org.springframework.webflow.engine.model.VarModel;
import org.springframework.webflow.engine.model.ViewStateModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.engine.model.registry.FlowModelLocator;
import org.springframework.webflow.engine.model.registry.NoSuchFlowModelException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Builds a flow model from a XML-based flow definition resource.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class XmlFlowModelBuilder implements FlowModelBuilder {

	private Resource resource;

	private FlowModelLocator modelLocator;

	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	private Document document;

	private long lastModifiedTimestamp;

	private FlowModel flowModel;

	/**
	 * Create a new XML flow model builder that will parse the XML document at the specified resource location and use
	 * the provided locator to access parent flow models.
	 * @param resource the path to the XML flow definition (required)
	 */
	public XmlFlowModelBuilder(Resource resource) {
		init(resource, null);
	}

	/**
	 * Create a new XML flow model builder that will parse the XML document at the specified resource location and use
	 * the provided locator to access parent flow models.
	 * @param resource the path to the XML flow definition (required)
	 * @param modelLocator a locator for parent flow models to support flow inheritance
	 */
	public XmlFlowModelBuilder(Resource resource, FlowModelLocator modelLocator) {
		init(resource, modelLocator);
	}

	/**
	 * Sets the loader that will load the XML-based flow definition document. Optional, defaults to
	 * {@link DefaultDocumentLoader}.
	 * @param documentLoader the document loader
	 */
	public void setDocumentLoader(DocumentLoader documentLoader) {
		Assert.notNull(documentLoader, "The XML document loader is required");
		this.documentLoader = documentLoader;
	}

	public void init() throws FlowModelBuilderException {
		try {
			document = documentLoader.loadDocument(resource);
			initLastModifiedTimestamp();
		} catch (IOException e) {
			throw new FlowModelBuilderException("Could not access the XML flow definition at " + resource, e);
		} catch (ParserConfigurationException e) {
			throw new FlowModelBuilderException("Could not configure the parser to parse the XML flow definition at "
					+ resource, e);
		} catch (SAXException e) {
			throw new FlowModelBuilderException("Could not parse the XML flow definition document at " + resource, e);
		}
	}

	public void build() throws FlowModelBuilderException {
		if (getDocumentElement() == null) {
			throw new FlowModelBuilderException(
					"The FlowModelBuilder must be initialized first -- called init() before calling build()");
		}
		flowModel = parseFlow(getDocumentElement());
		mergeFlows();
		mergeStates();
	}

	public FlowModel getFlowModel() throws FlowModelBuilderException {
		if (flowModel == null) {
			throw new FlowModelBuilderException(
					"The FlowModel must be built first -- called init() and build() before calling getFlowModel()");
		}
		return flowModel;
	}

	public void dispose() throws FlowModelBuilderException {
		document = null;
		flowModel = null;
	}

	public Resource getFlowModelResource() {
		return resource;
	}

	public boolean hasFlowModelResourceChanged() {
		if (lastModifiedTimestamp == -1) {
			return false;
		}
		try {
			long lastModified = resource.lastModified();
			if (lastModified > lastModifiedTimestamp) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Returns the DOM document parsed from the XML file.
	 */
	protected Document getDocument() {
		return document;
	}

	/**
	 * Returns the root document element.
	 */
	protected Element getDocumentElement() {
		return document != null ? document.getDocumentElement() : null;
	}

	private void init(Resource resource, FlowModelLocator modelLocator) {
		Assert.notNull(resource, "The location of the XML-based flow definition is required");
		this.resource = resource;
		this.modelLocator = modelLocator;
	}

	private void initLastModifiedTimestamp() {
		try {
			lastModifiedTimestamp = resource.lastModified();
		} catch (IOException e) {
			lastModifiedTimestamp = -1;
		}
	}

	private FlowModel parseFlow(Element element) {
		FlowModel flow = new FlowModel();
		flow.setAbstract(element.getAttribute("abstract"));
		flow.setParent(element.getAttribute("parent"));
		flow.setStartStateId(element.getAttribute("start-state"));
		flow.addAttributes(parseAttributes(element));
		flow.setSecured(parseSecured(element));
		flow.setPersistenceContext(parsePersistenceContext(element));
		flow.addVars(parseVars(element));
		flow.addInputs(parseInputs(element));
		flow.addOnStartActions(parseOnStartActions(element));
		flow.addStates(parseStates(element));
		flow.addGlobalTransitions(parseGlobalTransitions(element));
		flow.addOnEndActions(parseOnEndActions(element));
		flow.addOutputs(parseOutputs(element));
		flow.addExceptionHandlers(parseExceptionHandlers(element));
		flow.addBeanImports(parseBeanImports(element));
		return flow;
	}

	private LinkedList parseAttributes(Element element) {
		List attributeElements = DomUtils.getChildElementsByTagName(element, "attribute");
		if (attributeElements.isEmpty()) {
			return null;
		}
		LinkedList attributes = new LinkedList();
		for (Iterator it = attributeElements.iterator(); it.hasNext();) {
			attributes.add(parseAttribute((Element) it.next()));
		}
		return attributes;
	}

	private LinkedList parseVars(Element element) {
		List varElements = DomUtils.getChildElementsByTagName(element, "var");
		if (varElements.isEmpty()) {
			return null;
		}
		LinkedList vars = new LinkedList();
		for (Iterator it = varElements.iterator(); it.hasNext();) {
			vars.add(parseVar((Element) it.next()));
		}
		return vars;
	}

	private LinkedList parseInputs(Element element) {
		List inputElements = DomUtils.getChildElementsByTagName(element, "input");
		if (inputElements.isEmpty()) {
			return null;
		}
		LinkedList inputs = new LinkedList();
		for (Iterator it = inputElements.iterator(); it.hasNext();) {
			inputs.add(parseInput((Element) it.next()));
		}
		return inputs;
	}

	private LinkedList parseOutputs(Element element) {
		List outputElements = DomUtils.getChildElementsByTagName(element, "output");
		if (outputElements.isEmpty()) {
			return null;
		}
		LinkedList outputs = new LinkedList();
		for (Iterator it = outputElements.iterator(); it.hasNext();) {
			outputs.add(parseOutput((Element) it.next()));
		}
		return outputs;
	}

	private LinkedList parseActions(Element element) {
		List actionElements = DomUtils.getChildElementsByTagName(element, new String[] { "evaluate", "render", "set" });
		if (actionElements.isEmpty()) {
			return null;
		}
		LinkedList actions = new LinkedList();
		for (Iterator it = actionElements.iterator(); it.hasNext();) {
			actions.add(parseAction((Element) it.next()));
		}
		return actions;
	}

	private LinkedList parseStates(Element element) {
		List stateElements = DomUtils.getChildElementsByTagName(element, new String[] { "view-state", "action-state",
				"decision-state", "subflow-state", "end-state" });
		if (stateElements.isEmpty()) {
			return null;
		}
		LinkedList states = new LinkedList();
		for (Iterator it = stateElements.iterator(); it.hasNext();) {
			states.add(parseState((Element) it.next()));
		}
		return states;
	}

	private LinkedList parseTransitions(Element element) {
		List transitionElements = DomUtils.getChildElementsByTagName(element, "transition");
		if (transitionElements.isEmpty()) {
			return null;
		}
		LinkedList transitions = new LinkedList();
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			transitions.add(parseTransition((Element) it.next()));
		}
		return transitions;
	}

	private LinkedList parseExceptionHandlers(Element element) {
		List exceptionHandlerElements = DomUtils.getChildElementsByTagName(element, "exception-handler");
		if (exceptionHandlerElements.isEmpty()) {
			return null;
		}
		LinkedList exceptionHandlers = new LinkedList();
		for (Iterator it = exceptionHandlerElements.iterator(); it.hasNext();) {
			exceptionHandlers.add(parseExceptionHandler((Element) it.next()));
		}
		return exceptionHandlers;
	}

	private LinkedList parseBeanImports(Element element) {
		List importElements = DomUtils.getChildElementsByTagName(element, "bean-import");
		if (importElements.isEmpty()) {
			return null;
		}
		LinkedList beanImports = new LinkedList();
		for (Iterator it = importElements.iterator(); it.hasNext();) {
			beanImports.add(parseBeanImport((Element) it.next()));
		}
		return beanImports;
	}

	private LinkedList parseIfs(Element element) {
		List ifElements = DomUtils.getChildElementsByTagName(element, "if");
		if (ifElements.isEmpty()) {
			return null;
		}
		LinkedList ifs = new LinkedList();
		for (Iterator it = ifElements.iterator(); it.hasNext();) {
			ifs.add(parseIf((Element) it.next()));
		}
		return ifs;
	}

	private AbstractActionModel parseAction(Element element) {
		if (DomUtils.nodeNameEquals(element, "evaluate")) {
			return parseEvaluate(element);
		} else if (DomUtils.nodeNameEquals(element, "render")) {
			return parseRender(element);
		} else if (DomUtils.nodeNameEquals(element, "set")) {
			return parseSet(element);
		} else {
			throw new FlowModelBuilderException("Unknown action element encountered '" + element.getLocalName() + "'");
		}
	}

	private AbstractStateModel parseState(Element element) {
		if (DomUtils.nodeNameEquals(element, "view-state")) {
			return parseViewState(element);
		} else if (DomUtils.nodeNameEquals(element, "action-state")) {
			return parseActionState(element);
		} else if (DomUtils.nodeNameEquals(element, "decision-state")) {
			return parseDecisionState(element);
		} else if (DomUtils.nodeNameEquals(element, "subflow-state")) {
			return parseSubflowState(element);
		} else if (DomUtils.nodeNameEquals(element, "end-state")) {
			return parseEndState(element);
		} else {
			throw new FlowModelBuilderException("Unknown state element encountered '" + element.getLocalName() + "'");
		}
	}

	private LinkedList parseGlobalTransitions(Element element) {
		element = DomUtils.getChildElementByTagName(element, "global-transitions");
		if (element == null) {
			return null;
		} else {
			return parseTransitions(element);
		}
	}

	private AttributeModel parseAttribute(Element element) {
		AttributeModel attribute = new AttributeModel(element.getAttribute("name"), parseAttributeValue(element));
		attribute.setType(element.getAttribute("type"));
		return attribute;
	}

	private String parseAttributeValue(Element element) {
		if (element.hasAttribute("value")) {
			return element.getAttribute("value");
		} else {
			Element valueElement = DomUtils.getChildElementByTagName(element, "value");
			if (valueElement != null) {
				return DomUtils.getTextValue(valueElement);
			} else {
				return null;
			}
		}
	}

	private SecuredModel parseSecured(Element element) {
		element = DomUtils.getChildElementByTagName(element, "secured");
		if (element == null) {
			return null;
		} else {
			SecuredModel secured = new SecuredModel(element.getAttribute("attributes"));
			secured.setMatch(element.getAttribute("match"));
			return secured;
		}
	}

	private PersistenceContextModel parsePersistenceContext(Element element) {
		element = DomUtils.getChildElementByTagName(element, "persistence-context");
		if (element == null) {
			return null;
		} else {
			return new PersistenceContextModel();
		}
	}

	private VarModel parseVar(Element element) {
		return new VarModel(element.getAttribute("name"), element.getAttribute("class"));
	}

	private InputModel parseInput(Element element) {
		InputModel input = new InputModel(element.getAttribute("name"), element.getAttribute("value"));
		input.setType(element.getAttribute("type"));
		input.setRequired(element.getAttribute("required"));
		return input;
	}

	private OutputModel parseOutput(Element element) {
		OutputModel output = new OutputModel(element.getAttribute("name"), element.getAttribute("value"));
		output.setType(element.getAttribute("type"));
		output.setRequired(element.getAttribute("required"));
		return output;
	}

	private TransitionModel parseTransition(Element element) {
		TransitionModel transition = new TransitionModel();
		transition.setOn(element.getAttribute("on"));
		transition.setTo(element.getAttribute("to"));
		transition.setOnException(element.getAttribute("on-exception"));
		transition.setBind(element.getAttribute("bind"));
		transition.setValidate(element.getAttribute("validate"));
		transition.setHistory(element.getAttribute("history"));
		transition.setAttributes(parseAttributes(element));
		transition.setSecured(parseSecured(element));
		transition.setActions(parseActions(element));
		return transition;
	}

	private ExceptionHandlerModel parseExceptionHandler(Element element) {
		return new ExceptionHandlerModel(element.getAttribute("bean"));
	}

	private BeanImportModel parseBeanImport(Element element) {
		return new BeanImportModel(element.getAttribute("resource"));
	}

	private IfModel parseIf(Element element) {
		IfModel ifModel = new IfModel(element.getAttribute("test"), element.getAttribute("then"));
		ifModel.setElse(element.getAttribute("else"));
		return ifModel;
	}

	private LinkedList parseOnStartActions(Element element) {
		Element onStartElement = DomUtils.getChildElementByTagName(element, "on-start");
		if (onStartElement != null) {
			return parseActions(onStartElement);
		} else {
			return null;
		}
	}

	private LinkedList parseOnEntryActions(Element element) {
		Element onEntryElement = DomUtils.getChildElementByTagName(element, "on-entry");
		if (onEntryElement != null) {
			return parseActions(onEntryElement);
		} else {
			return null;
		}
	}

	private LinkedList parseOnRenderActions(Element element) {
		Element onRenderElement = DomUtils.getChildElementByTagName(element, "on-render");
		if (onRenderElement != null) {
			return parseActions(onRenderElement);
		} else {
			return null;
		}
	}

	private BinderModel parseBinder(Element element) {
		Element binderElement = DomUtils.getChildElementByTagName(element, "binder");
		if (binderElement != null) {
			BinderModel binder = new BinderModel();
			binder.setBindings(parseBindings(binderElement));
			return binder;
		} else {
			return null;
		}
	}

	private LinkedList parseBindings(Element element) {
		List bindingElements = DomUtils.getChildElementsByTagName(element, "binding");
		if (bindingElements.isEmpty()) {
			return null;
		}
		LinkedList bindings = new LinkedList();
		for (Iterator it = bindingElements.iterator(); it.hasNext();) {
			bindings.add(parseBinding((Element) it.next()));
		}
		return bindings;
	}

	private BindingModel parseBinding(Element element) {
		return new BindingModel(element.getAttribute("property"), element.getAttribute("converter"), element
				.getAttribute("required"));
	}

	private LinkedList parseOnExitActions(Element element) {
		Element onExitElement = DomUtils.getChildElementByTagName(element, "on-exit");
		if (onExitElement != null) {
			return parseActions(onExitElement);
		} else {
			return null;
		}
	}

	private LinkedList parseOnEndActions(Element element) {
		Element onEndElement = DomUtils.getChildElementByTagName(element, "on-end");
		if (onEndElement != null) {
			return parseActions(onEndElement);
		} else {
			return null;
		}
	}

	private EvaluateModel parseEvaluate(Element element) {
		EvaluateModel evaluate = new EvaluateModel(element.getAttribute("expression"));
		evaluate.setResult(element.getAttribute("result"));
		evaluate.setResultType(element.getAttribute("result-type"));
		evaluate.addAttributes(parseAttributes(element));
		return evaluate;
	}

	private RenderModel parseRender(Element element) {
		RenderModel render = new RenderModel(element.getAttribute("fragments"));
		render.addAttributes(parseAttributes(element));
		return render;
	}

	private SetModel parseSet(Element element) {
		SetModel set = new SetModel(element.getAttribute("name"), element.getAttribute("value"));
		set.setType(element.getAttribute("type"));
		set.addAttributes(parseAttributes(element));
		return set;
	}

	private ActionStateModel parseActionState(Element element) {
		ActionStateModel state = new ActionStateModel(element.getAttribute("id"));
		state.setParent(element.getAttribute("parent"));
		state.setAttributes(parseAttributes(element));
		state.setSecured(parseSecured(element));
		state.setOnEntryActions(parseOnEntryActions(element));
		state.setTransitions(parseTransitions(element));
		state.setOnExitActions(parseOnExitActions(element));
		state.setActions(parseActions(element));
		state.setExceptionHandlers(parseExceptionHandlers(element));
		return state;
	}

	private ViewStateModel parseViewState(Element element) {
		ViewStateModel state = new ViewStateModel(element.getAttribute("id"));
		state.setParent(element.getAttribute("parent"));
		state.setView(element.getAttribute("view"));
		state.setRedirect(element.getAttribute("redirect"));
		state.setPopup(element.getAttribute("popup"));
		state.setModel(element.getAttribute("model"));
		state.setVars(parseVars(element));
		state.setBinder(parseBinder(element));
		state.setOnRenderActions(parseOnRenderActions(element));
		state.setAttributes(parseAttributes(element));
		state.setSecured(parseSecured(element));
		state.setOnEntryActions(parseOnEntryActions(element));
		state.setExceptionHandlers(parseExceptionHandlers(element));
		state.setTransitions(parseTransitions(element));
		state.setOnExitActions(parseOnExitActions(element));
		return state;
	}

	private DecisionStateModel parseDecisionState(Element element) {
		DecisionStateModel state = new DecisionStateModel(element.getAttribute("id"));
		state.setParent(element.getAttribute("parent"));
		state.setIfs(parseIfs(element));
		state.setOnExitActions(parseOnExitActions(element));
		state.setAttributes(parseAttributes(element));
		state.setSecured(parseSecured(element));
		state.setOnEntryActions(parseOnEntryActions(element));
		state.setExceptionHandlers(parseExceptionHandlers(element));
		return state;
	}

	private SubflowStateModel parseSubflowState(Element element) {
		SubflowStateModel state = new SubflowStateModel(element.getAttribute("id"), element.getAttribute("subflow"));
		state.setParent(element.getAttribute("parent"));
		state.setSubflowAttributeMapper(element.getAttribute("subflow-attribute-mapper"));
		state.setInputs(parseInputs(element));
		state.setOutputs(parseOutputs(element));
		state.setAttributes(parseAttributes(element));
		state.setSecured(parseSecured(element));
		state.setOnEntryActions(parseOnEntryActions(element));
		state.setExceptionHandlers(parseExceptionHandlers(element));
		state.setTransitions(parseTransitions(element));
		state.setOnExitActions(parseOnExitActions(element));
		return state;
	}

	private EndStateModel parseEndState(Element element) {
		EndStateModel state = new EndStateModel(element.getAttribute("id"));
		state.setParent(element.getAttribute("parent"));
		state.setView(element.getAttribute("view"));
		state.setCommit(element.getAttribute("commit"));
		state.setOutputs(parseOutputs(element));
		state.setAttributes(parseAttributes(element));
		state.setSecured(parseSecured(element));
		state.setOnEntryActions(parseOnEntryActions(element));
		state.setExceptionHandlers(parseExceptionHandlers(element));
		return state;
	}

	private void mergeFlows() {
		if (flowModel.getParent() != null) {
			List parents = Arrays.asList(StringUtils.trimArrayElements(flowModel.getParent().split(",")));
			for (Iterator it = parents.iterator(); it.hasNext();) {
				String parentFlowId = (String) it.next();
				if (StringUtils.hasText(parentFlowId)) {
					try {
						flowModel.merge(modelLocator.getFlowModel(parentFlowId));
					} catch (NoSuchFlowModelException e) {
						throw new FlowModelBuilderException("Unable to find flow '" + parentFlowId
								+ "' to inherit from", e);
					}
				}
			}
		}
	}

	private void mergeStates() {
		if (flowModel.getStates() == null) {
			return;
		}
		for (Iterator it = flowModel.getStates().iterator(); it.hasNext();) {
			AbstractStateModel childState = (AbstractStateModel) it.next();
			String parent = childState.getParent();
			if (childState.getParent() != null) {
				String flowId;
				String stateId;
				AbstractStateModel parentState = null;
				if (!parent.contains("#")) {
					throw new FlowModelBuilderException("Invalid parent syntax '" + parent
							+ "', should take form 'flowId#stateId'");
				}
				flowId = parent.substring(0, parent.indexOf("#")).trim();
				stateId = parent.substring(parent.indexOf("#") + 1).trim();
				try {
					parentState = modelLocator.getFlowModel(flowId).getStateById(stateId);
					if (parentState == null) {
						throw new FlowModelBuilderException("Unable to find state '" + stateId + "' in flow '" + flowId
								+ "'");
					}
					childState.merge(parentState);
				} catch (NoSuchFlowModelException e) {
					throw new FlowModelBuilderException("Unable to find flow '" + flowId + "' to inherit from", e);
				} catch (ClassCastException e) {
					throw new FlowModelBuilderException("Parent state type '" + parentState.getClass().getName()
							+ "' cannot be merged with state type '" + childState.getClass().getName() + "'", e);

				}
			}
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("resource", resource).toString();
	}

}