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
		return attributes;
	}

	protected LinkedList parseVars(Element ele) {
		LinkedList vars = new LinkedList();
		for (Iterator varIt = DomUtils.getChildElementsByTagName(ele, "var").iterator(); varIt.hasNext();) {
			vars.add(parseVar((Element) varIt.next()));
		}
		return vars;
	}

	protected LinkedList parseInputs(Element ele) {
		LinkedList inputs = new LinkedList();
		for (Iterator inputIt = DomUtils.getChildElementsByTagName(ele, "input").iterator(); inputIt.hasNext();) {
			inputs.add(parseInput((Element) inputIt.next()));
		}
		return inputs;
	}

	protected LinkedList parseOutputs(Element ele) {
		LinkedList outputs = new LinkedList();
		for (Iterator outputIt = DomUtils.getChildElementsByTagName(ele, "output").iterator(); outputIt.hasNext();) {
			outputs.add(parseOutput((Element) outputIt.next()));
		}
		return outputs;
	}

	protected LinkedList parseActions(Element ele) {
		LinkedList actions = new LinkedList();
		for (Iterator actionIt = getChildElementsByTagNames(ele, new String[] { "evaluate", "render", "set" })
				.iterator(); actionIt.hasNext();) {
			actions.add(parseAction((Element) actionIt.next()));
		}
		return actions;
	}

	protected LinkedList parseStates(Element ele) {
		LinkedList states = new LinkedList();
		for (Iterator stateIt = getChildElementsByTagNames(ele,
				new String[] { "action-state", "view-state", "decision-state", "subflow-state", "end-state" })
				.iterator(); stateIt.hasNext();) {
			states.add(parseState((Element) stateIt.next()));
		}
		return states;
	}

	protected LinkedList parseTransitions(Element ele) {
		LinkedList transitions = new LinkedList();
		for (Iterator transitionIt = DomUtils.getChildElementsByTagName(ele, "transition").iterator(); transitionIt
				.hasNext();) {
			transitions.add(parseTransition((Element) transitionIt.next()));
		}
		return transitions;

	}

	protected LinkedList parseExceptionHandlers(Element ele) {
		LinkedList exceptionHandlers = new LinkedList();
		for (Iterator exceptionHandlerIt = DomUtils.getChildElementsByTagName(ele, "exception-handler").iterator(); exceptionHandlerIt
				.hasNext();) {
			exceptionHandlers.add(parseExceptionHandler((Element) exceptionHandlerIt.next()));
		}
		return exceptionHandlers;
	}

	protected LinkedList parseBeanImports(Element ele) {
		LinkedList beanImports = new LinkedList();
		for (Iterator beanImportIt = DomUtils.getChildElementsByTagName(ele, "bean-import").iterator(); beanImportIt
				.hasNext();) {
			beanImports.add(parseBeanImport((Element) beanImportIt.next()));
		}
		return beanImports;
	}

	protected LinkedList parseIfs(Element ele) {
		LinkedList ifs = new LinkedList();
		for (Iterator ifIt = DomUtils.getChildElementsByTagName(ele, "if").iterator(); ifIt.hasNext();) {
			ifs.add(parseIf((Element) ifIt.next()));
		}
		return ifs;
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
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "global-transitions")) {
			return parseGlobalTransitions(DomUtils.getChildElementByTagName(ele, "global-transitions"));
		} else {
			return parseTransitions(ele);
		}
	}

	protected AttributeModel parseAttribute(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "attribute")) {
			return parseAttribute(DomUtils.getChildElementByTagName(ele, "attribute"));
		} else {
			return new AttributeModel(ele.getAttribute("name"), parseValue(ele), ele.getAttribute("type"));
		}
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
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "secured")) {
			return parseSecured(DomUtils.getChildElementByTagName(ele, "secured"));
		} else {
			return new SecuredModel(ele.getAttribute("attributes"), ele.getAttribute("match"));
		}
	}

	protected PersistenceContextModel parsePersistenceContext(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "persistence-context")) {
			return parsePersistenceContext(DomUtils.getChildElementByTagName(ele, "persistence-context"));
		} else {
			return new PersistenceContextModel();
		}
	}

	protected VarModel parseVar(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "var")) {
			return parseVar(DomUtils.getChildElementByTagName(ele, "var"));
		} else {
			return new VarModel(ele.getAttribute("name"), ele.getAttribute("class"), ele.getAttribute("scope"));
		}
	}

	protected InputModel parseInput(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "input")) {
			return parseInput(DomUtils.getChildElementByTagName(ele, "input"));
		} else {
			return new InputModel(ele.getAttribute("name"), ele.getAttribute("value"), ele.getAttribute("type"), ele
					.getAttribute("required"));
		}
	}

	protected OutputModel parseOutput(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "output")) {
			return parseOutput(DomUtils.getChildElementByTagName(ele, "output"));
		} else {
			return new OutputModel(ele.getAttribute("name"), ele.getAttribute("value"), ele.getAttribute("type"), ele
					.getAttribute("required"));
		}
	}

	protected TransitionModel parseTransition(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "transition")) {
			return parseTransition(DomUtils.getChildElementByTagName(ele, "transition"));
		} else {
			return new TransitionModel(ele.getAttribute("on"), ele.getAttribute("to"),
					ele.getAttribute("on-exception"), ele.getAttribute("bind"), parseAttributes(ele),
					parseSecured(ele), parseActions(ele));
		}
	}

	protected ExceptionHandlerModel parseExceptionHandler(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "exception-handler")) {
			return parseExceptionHandler(DomUtils.getChildElementByTagName(ele, "exception-handler"));
		} else {
			return new ExceptionHandlerModel(ele.getAttribute("bean-name"));
		}
	}

	protected BeanImportModel parseBeanImport(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "bean-import")) {
			return parseBeanImport(DomUtils.getChildElementByTagName(ele, "bean-import"));
		} else {
			return new BeanImportModel(ele.getAttribute("resource"));
		}
	}

	protected IfModel parseIf(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "if")) {
			return parseIf(DomUtils.getChildElementByTagName(ele, "if"));
		} else {
			return new IfModel(ele.getAttribute("test"), ele.getAttribute("then"), ele.getAttribute("else"));
		}
	}

	protected LinkedList parseOnStartActions(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "on-start")) {
			return parseOnStartActions(DomUtils.getChildElementByTagName(ele, "on-start"));
		} else {
			return parseActions(ele);
		}
	}

	protected LinkedList parseOnEntryActions(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "on-entry")) {
			return parseOnEntryActions(DomUtils.getChildElementByTagName(ele, "on-entry"));
		} else {
			return parseActions(ele);
		}
	}

	protected LinkedList parseOnExitActions(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "on-exit")) {
			return parseOnExitActions(DomUtils.getChildElementByTagName(ele, "on-exit"));
		} else {
			return parseActions(ele);
		}
	}

	protected LinkedList parseOnRenderActions(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "on-render")) {
			return parseOnRenderActions(DomUtils.getChildElementByTagName(ele, "on-render"));
		} else {
			return parseActions(ele);
		}
	}

	protected LinkedList parseOnEndActions(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "on-end")) {
			return parseOnEndActions(DomUtils.getChildElementByTagName(ele, "on-end"));
		} else {
			return parseActions(ele);
		}
	}

	protected EvaluateModel parseEvaluate(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "evaluate")) {
			return parseEvaluate(DomUtils.getChildElementByTagName(ele, "evaluate"));
		} else {
			return new EvaluateModel(ele.getAttribute("expression"), ele.getAttribute("result"), ele
					.getAttribute("result-type"));
		}
	}

	protected RenderModel parseRender(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "render")) {
			return parseRender(DomUtils.getChildElementByTagName(ele, "render"));
		} else {
			return new RenderModel(ele.getAttribute("fragments"));
		}
	}

	protected SetModel parseSet(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "set")) {
			return parseSet(DomUtils.getChildElementByTagName(ele, "set"));
		} else {
			return new SetModel(ele.getAttribute("name"), ele.getAttribute("value"), ele.getAttribute("type"));
		}
	}

	protected ActionStateModel parseActionState(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "action-state")) {
			return parseActionState(DomUtils.getChildElementByTagName(ele, "action-state"));
		} else {
			return new ActionStateModel(ele.getAttribute("id"), parseAttributes(ele), parseSecured(ele),
					parseOnEntryActions(ele), parseTransitions(ele), parseOnExitActions(ele), parseActions(ele),
					parseExceptionHandlers(ele));
		}
	}

	protected ViewStateModel parseViewState(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "view-state")) {
			return parseViewState(DomUtils.getChildElementByTagName(ele, "view-state"));
		} else {
			return new ViewStateModel(ele.getAttribute("id"), ele.getAttribute("view"), ele.getAttribute("redirect"),
					ele.getAttribute("popup"), ele.getAttribute("model"), parseVars(ele), parseOnRenderActions(ele),
					parseAttributes(ele), parseSecured(ele), parseOnEntryActions(ele), parseExceptionHandlers(ele),
					parseTransitions(ele), parseOnExitActions(ele));
		}
	}

	protected DecisionStateModel parseDecisionState(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "decision-state")) {
			return parseDecisionState(DomUtils.getChildElementByTagName(ele, "decision-state"));
		} else {
			return new DecisionStateModel(ele.getAttribute("id"), parseIfs(ele), parseOnExitActions(ele),
					parseAttributes(ele), parseSecured(ele), parseOnEntryActions(ele), parseExceptionHandlers(ele));
		}
	}

	protected SubflowStateModel parseSubflowState(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "subflow-state")) {
			return parseSubflowState(DomUtils.getChildElementByTagName(ele, "subflow-state"));
		} else {
			return new SubflowStateModel(ele.getAttribute("id"), ele.getAttribute("subflow"), ele
					.getAttribute("subflow-attribute-mapper"), parseInputs(ele), parseOutputs(ele),
					parseAttributes(ele), parseSecured(ele), parseOnEntryActions(ele), parseExceptionHandlers(ele),
					parseTransitions(ele), parseOnExitActions(ele));
		}
	}

	protected EndStateModel parseEndState(Element ele) {
		if (ele == null) {
			return null;
		} else if (!DomUtils.nodeNameEquals(ele, "end-state")) {
			return parseEndState(DomUtils.getChildElementByTagName(ele, "end-state"));
		} else {
			return new EndStateModel(ele.getAttribute("id"), ele.getAttribute("view-factory"), ele
					.getAttribute("commit"), parseOutputs(ele), parseAttributes(ele), parseSecured(ele),
					parseOnEntryActions(ele), parseExceptionHandlers(ele));
		}
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