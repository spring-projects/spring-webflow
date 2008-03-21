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
import org.springframework.webflow.engine.model.registry.FlowModelRegistry;
import org.springframework.webflow.engine.model.registry.NoSuchFlowModelException;
import org.springframework.webflow.util.ResourceHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlFlowModelBuilder implements FlowModelBuilder, ResourceHolder {

	/**
	 * The resource from which the document element being parsed was read. Used as a location for relative resource
	 * lookup.
	 */
	protected Resource resource;

	/**
	 * The flow model registry used to lookup other flows
	 */
	protected FlowModelRegistry registry;

	/**
	 * The loader for loading the flow definition resource XML document.
	 */
	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	/**
	 * The in-memory document object model (DOM) of the XML Document read from the flow definition resource.
	 */
	private Document document;

	/**
	 * The flow model.
	 */
	private FlowModel flowModel;

	/**
	 * Create a new XML flow builder parsing the document at the specified location, using the provided service locator
	 * to access externally managed flow artifacts.
	 */
	public XmlFlowModelBuilder(Resource resource, FlowModelRegistry registry) {
		this.resource = resource;
		this.registry = registry;
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
		} catch (IOException e) {
			throw new FlowModelBuilderException("Could not access the XML flow definition resource at " + resource, e);
		} catch (ParserConfigurationException e) {
			throw new FlowModelBuilderException("Could not configure the parser to parse the XML flow definition at "
					+ resource, e);
		} catch (SAXException e) {
			throw new FlowModelBuilderException("Could not parse the XML flow definition document at " + resource, e);
		}
	}

	public void build() throws FlowModelBuilderException {
		if (getDocumentElement() == null) {
			throw new FlowModelBuilderException("The FlowModelBuilder must be initialized first");
		}
		flowModel = parseFlow(getDocumentElement());
		if (flowModel.getParent() != null) {
			for (Iterator parentIt = Arrays.asList(StringUtils.trimArrayElements(flowModel.getParent().split(",")))
					.iterator(); parentIt.hasNext();) {
				String parentFlowId = (String) parentIt.next();
				if (StringUtils.hasText(parentFlowId)) {
					try {
						flowModel.merge(registry.getFlowModel(parentFlowId));
					} catch (NoSuchFlowModelException e) {
						throw new FlowModelBuilderException("Unable to find flow '" + parentFlowId
								+ "' to inherit from", e);
					}
				}
			}
		}
	}

	public FlowModel getFlowModel() throws FlowModelBuilderException {
		return flowModel;
	}

	public void dispose() throws FlowModelBuilderException {
		document = null;
		flowModel = null;
	}

	protected FlowModel parseFlow(Element ele) {
		FlowModel flow = new FlowModel();
		flow.setParent(ele.getAttribute("parent"));
		flow.setStartStateId(ele.getAttribute("start-state"));
		flow.addAttributes(parseAttributes(ele));
		flow.setSecured(parseSecured(ele));
		flow.setPersistenceContext(parsePersistenceContext(ele));
		flow.addVars(parseVars(ele));
		flow.addInputs(parseInputs(ele));
		flow.addOutputs(parseOutputs(ele));
		flow.addOnStartActions(parseOnStartActions(ele));
		flow.addStates(parseStates(ele));
		flow.addGlobalTransitions(parseGlobalTransitions(ele));
		flow.addOnEndActions(parseOnEndActions(ele));
		flow.addExceptionHandlers(parseExceptionHandlers(ele));
		flow.addBeanImports(parseBeanImports(ele));
		return flow;
	}

	protected LinkedList parseAttributes(Element ele) {
		LinkedList attributes = new LinkedList();
		for (Iterator attributeIt = DomUtils.getChildElementsByTagName(ele, "attribute").iterator(); attributeIt
				.hasNext();) {
			attributes.add(parseAttribute((Element) attributeIt.next()));
		}
		return !attributes.isEmpty() ? attributes : null;
	}

	protected LinkedList parseVars(Element ele) {
		LinkedList vars = new LinkedList();
		for (Iterator varIt = DomUtils.getChildElementsByTagName(ele, "var").iterator(); varIt.hasNext();) {
			vars.add(parseVar((Element) varIt.next()));
		}
		return !vars.isEmpty() ? vars : null;
	}

	protected LinkedList parseInputs(Element ele) {
		LinkedList inputs = new LinkedList();
		for (Iterator inputIt = DomUtils.getChildElementsByTagName(ele, "input").iterator(); inputIt.hasNext();) {
			inputs.add(parseInput((Element) inputIt.next()));
		}
		return !inputs.isEmpty() ? inputs : null;
	}

	protected LinkedList parseOutputs(Element ele) {
		LinkedList outputs = new LinkedList();
		for (Iterator outputIt = DomUtils.getChildElementsByTagName(ele, "output").iterator(); outputIt.hasNext();) {
			outputs.add(parseOutput((Element) outputIt.next()));
		}
		return !outputs.isEmpty() ? outputs : null;
	}

	protected LinkedList parseActions(Element ele) {
		if (ele == null) {
			return null;
		}
		LinkedList actions = new LinkedList();
		for (Iterator actionIt = getChildElementsByTagNames(ele, new String[] { "evaluate", "render", "set" })
				.iterator(); actionIt.hasNext();) {
			actions.add(parseAction((Element) actionIt.next()));
		}
		return !actions.isEmpty() ? actions : null;
	}

	protected LinkedList parseStates(Element ele) {
		LinkedList states = new LinkedList();
		for (Iterator stateIt = getChildElementsByTagNames(ele,
				new String[] { "action-state", "view-state", "decision-state", "subflow-state", "end-state" })
				.iterator(); stateIt.hasNext();) {
			states.add(parseState((Element) stateIt.next()));
		}
		return !states.isEmpty() ? states : null;
	}

	protected LinkedList parseTransitions(Element ele) {
		LinkedList transitions = new LinkedList();
		for (Iterator transitionIt = DomUtils.getChildElementsByTagName(ele, "transition").iterator(); transitionIt
				.hasNext();) {
			transitions.add(parseTransition((Element) transitionIt.next()));
		}
		return !transitions.isEmpty() ? transitions : null;
	}

	protected LinkedList parseExceptionHandlers(Element ele) {
		LinkedList exceptionHandlers = new LinkedList();
		for (Iterator exceptionHandlerIt = DomUtils.getChildElementsByTagName(ele, "exception-handler").iterator(); exceptionHandlerIt
				.hasNext();) {
			exceptionHandlers.add(parseExceptionHandler((Element) exceptionHandlerIt.next()));
		}
		return !exceptionHandlers.isEmpty() ? exceptionHandlers : null;
	}

	protected LinkedList parseBeanImports(Element ele) {
		LinkedList beanImports = new LinkedList();
		for (Iterator beanImportIt = DomUtils.getChildElementsByTagName(ele, "bean-import").iterator(); beanImportIt
				.hasNext();) {
			beanImports.add(parseBeanImport((Element) beanImportIt.next()));
		}
		return !beanImports.isEmpty() ? beanImports : null;
	}

	protected LinkedList parseIfs(Element ele) {
		LinkedList ifs = new LinkedList();
		for (Iterator ifIt = DomUtils.getChildElementsByTagName(ele, "if").iterator(); ifIt.hasNext();) {
			ifs.add(parseIf((Element) ifIt.next()));
		}
		return !ifs.isEmpty() ? ifs : null;
	}

	protected AbstractActionModel parseAction(Element ele) {
		if (DomUtils.nodeNameEquals(ele, "evaluate")) {
			return parseEvaluate(ele);
		} else if (DomUtils.nodeNameEquals(ele, "render")) {
			return parseRender(ele);
		} else if (DomUtils.nodeNameEquals(ele, "set")) {
			return parseSet(ele);
		} else {
			throw new UnsupportedOperationException("Unknown action element encountered '" + ele.getLocalName() + "'");
		}
	}

	protected AbstractStateModel parseState(Element ele) {
		if (DomUtils.nodeNameEquals(ele, "action-state")) {
			return parseActionState(ele);
		} else if (DomUtils.nodeNameEquals(ele, "view-state")) {
			return parseViewState(ele);
		} else if (DomUtils.nodeNameEquals(ele, "decision-state")) {
			return parseDecisionState(ele);
		} else if (DomUtils.nodeNameEquals(ele, "subflow-state")) {
			return parseSubflowState(ele);
		} else if (DomUtils.nodeNameEquals(ele, "end-state")) {
			return parseEndState(ele);
		} else {
			throw new UnsupportedOperationException("Unknown state element encountered '" + ele.getLocalName() + "'");
		}
	}

	protected LinkedList parseGlobalTransitions(Element ele) {
		ele = DomUtils.getChildElementByTagName(ele, "global-transitions");
		if (ele == null) {
			return null;
		} else {
			return parseTransitions(ele);
		}
	}

	protected AttributeModel parseAttribute(Element ele) {
		AttributeModel attribute = new AttributeModel(ele.getAttribute("name"), parseValue(ele));
		attribute.setType(ele.getAttribute("type"));
		return attribute;
	}

	protected String parseValue(Element ele) {
		if (ele.hasAttribute("value")) {
			return ele.getAttribute("value");
		} else {
			Element valueEle = DomUtils.getChildElementByTagName(ele, "value");
			return valueEle != null ? DomUtils.getTextValue(valueEle) : null;
		}
	}

	protected SecuredModel parseSecured(Element ele) {
		ele = DomUtils.getChildElementByTagName(ele, "secured");
		if (ele == null) {
			return null;
		} else {
			SecuredModel secured = new SecuredModel(ele.getAttribute("attributes"));
			secured.setMatch(ele.getAttribute("match"));
			return secured;
		}
	}

	protected PersistenceContextModel parsePersistenceContext(Element ele) {
		ele = DomUtils.getChildElementByTagName(ele, "persistence-context");
		if (ele == null) {
			return null;
		} else {
			return new PersistenceContextModel();
		}
	}

	protected VarModel parseVar(Element ele) {
		VarModel var = new VarModel(ele.getAttribute("name"), ele.getAttribute("class"));
		var.setScope(ele.getAttribute("scope"));
		return var;
	}

	protected InputModel parseInput(Element ele) {
		InputModel input = new InputModel(ele.getAttribute("name"), ele.getAttribute("value"));
		input.setType(ele.getAttribute("type"));
		input.setRequired(ele.getAttribute("required"));
		return input;
	}

	protected OutputModel parseOutput(Element ele) {
		OutputModel output = new OutputModel(ele.getAttribute("name"), ele.getAttribute("value"));
		output.setType(ele.getAttribute("type"));
		output.setRequired(ele.getAttribute("required"));
		return output;
	}

	protected TransitionModel parseTransition(Element ele) {
		TransitionModel transition = new TransitionModel();
		transition.setOn(ele.getAttribute("on"));
		transition.setTo(ele.getAttribute("to"));
		transition.setOnException(ele.getAttribute("on-exception"));
		transition.setBind(ele.getAttribute("bind"));
		transition.setAttributes(parseAttributes(ele));
		transition.setSecured(parseSecured(ele));
		transition.setActions(parseActions(ele));
		return transition;
	}

	protected ExceptionHandlerModel parseExceptionHandler(Element ele) {
		return new ExceptionHandlerModel(ele.getAttribute("bean-name"));
	}

	protected BeanImportModel parseBeanImport(Element ele) {
		return new BeanImportModel(ele.getAttribute("resource"));
	}

	protected IfModel parseIf(Element ele) {
		IfModel conditional = new IfModel(ele.getAttribute("test"), ele.getAttribute("then"));
		conditional.setElse(ele.getAttribute("else"));
		return conditional;
	}

	protected LinkedList parseOnStartActions(Element ele) {
		return parseActions(DomUtils.getChildElementByTagName(ele, "on-start"));
	}

	protected LinkedList parseOnEntryActions(Element ele) {
		return parseActions(DomUtils.getChildElementByTagName(ele, "on-entry"));
	}

	protected LinkedList parseOnRenderActions(Element ele) {
		return parseActions(DomUtils.getChildElementByTagName(ele, "on-render"));
	}

	protected LinkedList parseOnExitActions(Element ele) {
		return parseActions(DomUtils.getChildElementByTagName(ele, "on-exit"));
	}

	protected LinkedList parseOnEndActions(Element ele) {
		return parseActions(DomUtils.getChildElementByTagName(ele, "on-end"));
	}

	protected EvaluateModel parseEvaluate(Element ele) {
		EvaluateModel evaluate = new EvaluateModel(ele.getAttribute("expression"));
		evaluate.setResult(ele.getAttribute("result"));
		evaluate.setResultType(ele.getAttribute("result-type"));
		return evaluate;
	}

	protected RenderModel parseRender(Element ele) {
		return new RenderModel(ele.getAttribute("fragments"));
	}

	protected SetModel parseSet(Element ele) {
		SetModel set = new SetModel(ele.getAttribute("name"), ele.getAttribute("value"));
		set.setType(ele.getAttribute("type"));
		return set;
	}

	protected ActionStateModel parseActionState(Element ele) {
		ActionStateModel state = new ActionStateModel(ele.getAttribute("id"));
		state.setAttributes(parseAttributes(ele));
		state.setSecured(parseSecured(ele));
		state.setOnEntryActions(parseOnEntryActions(ele));
		state.setTransitions(parseTransitions(ele));
		state.setOnExitActions(parseOnExitActions(ele));
		state.setActions(parseActions(ele));
		state.setExceptionHandlers(parseExceptionHandlers(ele));
		return state;
	}

	protected ViewStateModel parseViewState(Element ele) {
		ViewStateModel state = new ViewStateModel(ele.getAttribute("id"));
		state.setView(ele.getAttribute("view"));
		state.setRedirect(ele.getAttribute("redirect"));
		state.setPopup(ele.getAttribute("popup"));
		state.setModel(ele.getAttribute("model"));
		state.setVars(parseVars(ele));
		state.setOnRenderActions(parseOnRenderActions(ele));
		state.setAttributes(parseAttributes(ele));
		state.setSecured(parseSecured(ele));
		state.setOnEntryActions(parseOnEntryActions(ele));
		state.setExceptionHandlers(parseExceptionHandlers(ele));
		state.setTransitions(parseTransitions(ele));
		state.setOnExitActions(parseOnExitActions(ele));
		return state;
	}

	protected DecisionStateModel parseDecisionState(Element ele) {
		DecisionStateModel state = new DecisionStateModel(ele.getAttribute("id"));
		state.setIfs(parseIfs(ele));
		state.setOnExitActions(parseOnExitActions(ele));
		state.setAttributes(parseAttributes(ele));
		state.setSecured(parseSecured(ele));
		state.setOnEntryActions(parseOnEntryActions(ele));
		state.setExceptionHandlers(parseExceptionHandlers(ele));
		return state;
	}

	protected SubflowStateModel parseSubflowState(Element ele) {
		SubflowStateModel state = new SubflowStateModel(ele.getAttribute("id"), ele.getAttribute("subflow"));
		state.setSubflowAttributeMapper(ele.getAttribute("subflow-attribute-mapper"));
		state.setInputs(parseInputs(ele));
		state.setOutputs(parseOutputs(ele));
		state.setAttributes(parseAttributes(ele));
		state.setSecured(parseSecured(ele));
		state.setOnEntryActions(parseOnEntryActions(ele));
		state.setExceptionHandlers(parseExceptionHandlers(ele));
		state.setTransitions(parseTransitions(ele));
		state.setOnExitActions(parseOnExitActions(ele));
		return state;
	}

	protected EndStateModel parseEndState(Element ele) {
		EndStateModel state = new EndStateModel(ele.getAttribute("id"));
		state.setView(ele.getAttribute("view"));
		state.setCommit(ele.getAttribute("commit"));
		state.setOutputs(parseOutputs(ele));
		state.setAttributes(parseAttributes(ele));
		state.setSecured(parseSecured(ele));
		state.setOnEntryActions(parseOnEntryActions(ele));
		state.setExceptionHandlers(parseExceptionHandlers(ele));
		return state;
	}

	// TODO: submit this to DomUtils
	private static List getChildElementsByTagNames(Element ele, String[] childEleNames) {
		List names = Arrays.asList(childEleNames);
		NodeList nl = ele.getChildNodes();
		List childEles = new LinkedList();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && (names.contains(node.getLocalName()) || names.contains(node.getNodeName()))) {
				childEles.add(node);
			}
		}
		return childEles;
	}

	public Resource getResource() {
		return resource;
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

	public String toString() {
		return new ToStringCreator(this).append("location", resource).toString();
	}

}