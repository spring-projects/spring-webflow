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
package org.springframework.webflow.engine.builder.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.binding.expression.support.CollectionAddingExpression;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.RequiredMapping;
import org.springframework.binding.method.MethodSignature;
import org.springframework.binding.method.Parameter;
import org.springframework.binding.method.Parameters;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.webflow.action.ActionResultExposer;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.action.SetAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowAttributeMapper;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.FlowVariable;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.ViewSelector;
import org.springframework.webflow.engine.builder.BaseFlowBuilder;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.FlowServiceLocator;
import org.springframework.webflow.engine.support.AttributeExpression;
import org.springframework.webflow.engine.support.BeanFactoryFlowVariable;
import org.springframework.webflow.engine.support.BooleanExpressionTransitionCriteria;
import org.springframework.webflow.engine.support.SimpleFlowVariable;
import org.springframework.webflow.engine.support.TransitionCriteriaChain;
import org.springframework.webflow.engine.support.TransitionExecutingStateExceptionHandler;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.util.ResourceHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Flow builder that builds flows as defined in an XML document. The XML document
 * should adhere to the following format:
 * 
 * <pre>
 *     &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *     &lt;flow xmlns=&quot;http://www.springframework.org/schema/webflow&quot;
 *           xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *           xsi:schemaLocation=&quot;http://www.springframework.org/schema/webflow
 *                               http://www.springframework.org/schema/webflow/spring-webflow-1.0.xsd&quot;&gt;
 *                                        
 *         &lt;!-- Define your states here --&gt;
 *                  
 *     &lt;/flow&gt;
 * </pre>
 * 
 * <p>
 * Consult the <a
 * href="http://www.springframework.org/schema/webflow/spring-webflow-1.0.xsd">webflow
 * XML schema</a> for more information on the XML-based flow definition format.
 * <p>
 * This builder will setup a flow-local bean factory for the flow being
 * constructed. That flow-local bean factory will be populated with XML bean
 * definitions contained in files referenced using the "import" element. The
 * flow-local bean factory will use the bean factory defing this flow builder as
 * a parent. As such, the flow can access artifacts in either its flow-local
 * bean factory or in the parent bean factory hierarchy, e.g. the bean factory
 * of the dispatcher.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class XmlFlowBuilder extends BaseFlowBuilder implements ResourceHolder {

	// recognized XML elements and attributes

	private static final String ID_ATTRIBUTE = "id";

	private static final String IDREF_ATTRIBUTE = "idref";

	private static final String BEAN_ATTRIBUTE = "bean";

	private static final String FLOW_ELEMENT = "flow";

	private static final String START_STATE_ELEMENT = "start-state";

	private static final String ACTION_STATE_ELEMENT = "action-state";

	private static final String ACTION_ELEMENT = "action";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String METHOD_ATTRIBUTE = "method";

	private static final String BEAN_ACTION_ELEMENT = "bean-action";

	private static final String METHOD_ARGUMENTS_ELEMENT = "method-arguments";

	private static final String ARGUMENT_ELEMENT = "argument";

	private static final String EXPRESSION_ATTRIBUTE = "expression";

	private static final String PARAMETER_TYPE_ATTRIBUTE = "parameter-type";

	private static final String METHOD_RESULT_ELEMENT = "method-result";

	private static final String EVALUATE_ACTION_ELEMENT = "evaluate-action";

	private static final String SET_ELEMENT = "set";

	private static final String ATTRIBUTE_ATTRIBUTE = "attribute";

	private static final String EVALUATION_RESULT_ELEMENT = "evaluation-result";

	private static final String DEFAULT_VALUE = "default";

	private static final String VIEW_STATE_ELEMENT = "view-state";

	private static final String VIEW_ATTRIBUTE = "view";

	private static final String DECISION_STATE_ELEMENT = "decision-state";

	private static final String IF_ELEMENT = "if";

	private static final String TEST_ATTRIBUTE = "test";

	private static final String THEN_ATTRIBUTE = "then";

	private static final String ELSE_ATTRIBUTE = "else";

	private static final String SUBFLOW_STATE_ELEMENT = "subflow-state";

	private static final String FLOW_ATTRIBUTE = "flow";

	private static final String ATTRIBUTE_MAPPER_ELEMENT = "attribute-mapper";

	private static final String OUTPUT_MAPPER_ELEMENT = "output-mapper";

	private static final String OUTPUT_ATTRIBUTE_ELEMENT = "output-attribute";

	private static final String INPUT_MAPPER_ELEMENT = "input-mapper";

	private static final String INPUT_ATTRIBUTE_ELEMENT = "input-attribute";

	private static final String MAPPING_ELEMENT = "mapping";

	private static final String SOURCE_ATTRIBUTE = "source";

	private static final String TARGET_ATTRIBUTE = "target";

	private static final String FROM_ATTRIBUTE = "from";

	private static final String TO_ATTRIBUTE = "to";

	private static final String REQUIRED_ATTRIBUTE = "required";

	private static final String TARGET_COLLECTION_ATTRIBUTE = "target-collection";

	private static final String END_STATE_ELEMENT = "end-state";

	private static final String TRANSITION_ELEMENT = "transition";

	private static final String GLOBAL_TRANSITIONS_ELEMENT = "global-transitions";

	private static final String ON_ATTRIBUTE = "on";

	private static final String ON_EXCEPTION_ATTRIBUTE = "on-exception";

	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private static final String TYPE_ATTRIBUTE = "type";

	private static final String VALUE_ELEMENT = "value";

	private static final String VALUE_ATTRIBUTE = "value";

	private static final String VAR_ELEMENT = "var";

	private static final String SCOPE_ATTRIBUTE = "scope";

	private static final String CLASS_ATTRIBUTE = "class";

	private static final String START_ACTIONS_ELEMENT = "start-actions";

	private static final String END_ACTIONS_ELEMENT = "end-actions";

	private static final String ENTRY_ACTIONS_ELEMENT = "entry-actions";

	private static final String RENDER_ACTIONS_ELEMENT = "render-actions";

	private static final String EXIT_ACTIONS_ELEMENT = "exit-actions";

	private static final String EXCEPTION_HANDLER_ELEMENT = "exception-handler";

	private static final String INLINE_FLOW_ELEMENT = "inline-flow";

	private static final String IMPORT_ELEMENT = "import";

	private static final String RESOURCE_ATTRIBUTE = "resource";

	/**
	 * The resource from which the document element being parsed was read. Used
	 * as a location for relative resource lookup.
	 */
	protected Resource location;

	/**
	 * A flow service locator local to this builder that first looks in a
	 * locally-managed Spring application context for services before searching
	 * the externally managed {@link #getFlowServiceLocator()}.
	 */
	private LocalFlowServiceLocator localFlowServiceLocator;

	/**
	 * The loader for loading the flow definition resource XML document.
	 */
	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	/**
	 * The in-memory document object model (DOM) of the XML Document read from
	 * the flow definition resource.
	 */
	private Document document;

	/**
	 * Create a new XML flow builder parsing the document at the specified
	 * location.
	 * @param location the location of the XML-based flow definition resource
	 */
	public XmlFlowBuilder(Resource location) {
		setLocation(location);
	}

	/**
	 * Create a new XML flow builder parsing the document at the specified
	 * location, using the provided service locator to access externally managed
	 * flow artifacts.
	 * @param location the location of the XML-based flow definition resource
	 * @param flowServiceLocator the locator for services needed by this builder
	 * to build its Flow
	 */
	public XmlFlowBuilder(Resource location, FlowServiceLocator flowServiceLocator) {
		super(flowServiceLocator);
		setLocation(location);
	}

	/**
	 * Returns the resource from which the document element was loaded. This is
	 * used for location relative loading of other resources.
	 */
	public Resource getLocation() {
		return location;
	}

	/**
	 * Sets the resource from which the document element was loaded. This is
	 * used for location relative loading of other resources.
	 */
	public void setLocation(Resource location) {
		Assert.notNull(location, "The resource location of the XML-based flow definition is required");
		this.location = location;
	}

	/**
	 * Sets the loader that will load the XML-based flow definition document.
	 * Optional, defaults to {@link DefaultDocumentLoader}.
	 * @param documentLoader the document loader
	 */
	public void setDocumentLoader(DocumentLoader documentLoader) {
		Assert.notNull(documentLoader, "The XML document loader is required");
		this.documentLoader = documentLoader;
	}

	public String toString() {
		return new ToStringCreator(this).append("location", location).toString();
	}

	// implementing FlowBuilder

	public void init(String id, AttributeMap attributes) throws FlowBuilderException {
		localFlowServiceLocator = new LocalFlowServiceLocator(getFlowServiceLocator());
		try {
			document = documentLoader.loadDocument(location);
		}
		catch (IOException e) {
			throw new FlowBuilderException("Could not access the XML flow definition resource at " + location, e);
		}
		catch (ParserConfigurationException e) {
			throw new FlowBuilderException("Could not configure the parser to parse the XML flow definition at "
					+ location, e);
		}
		catch (SAXException e) {
			throw new FlowBuilderException("Could not parse the XML flow definition document at " + location, e);
		}
		setFlow(parseFlow(id, attributes, getDocumentElement()));
	}

	public void buildVariables() throws FlowBuilderException {
		parseAndAddFlowVariables(getDocumentElement(), getFlow());
	}

	public void buildInputMapper() throws FlowBuilderException {
		getFlow().setInputMapper(parseInputMapper(getDocumentElement()));
	}

	public void buildStartActions() throws FlowBuilderException {
		parseAndAddStartActions(getDocumentElement(), getFlow());
	}

	public void buildInlineFlows() throws FlowBuilderException {
		parseAndAddInlineFlowDefinitions(getDocumentElement(), getFlow());
	}

	public void buildStates() throws FlowBuilderException {
		parseAndAddStateDefinitions(getDocumentElement(), getFlow());
	}

	public void buildGlobalTransitions() throws FlowBuilderException {
		parseAndAddGlobalTransitions(getDocumentElement(), getFlow());
	}

	public void buildEndActions() throws FlowBuilderException {
		parseAndAddEndActions(getDocumentElement(), getFlow());
	}

	public void buildOutputMapper() throws FlowBuilderException {
		getFlow().setOutputMapper(parseOutputMapper(getDocumentElement()));
	}

	public void buildExceptionHandlers() throws FlowBuilderException {
		getFlow().getExceptionHandlerSet().addAll(parseExceptionHandlers(getDocumentElement()));
	}

	public void dispose() {
		super.dispose();
		localFlowServiceLocator.diposeOfAnyRegistries();
		document = null;
	}

	// implementing ResourceHolder

	public Resource getResource() {
		return location;
	}
	
	// helpers

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
		return document.getDocumentElement();
	}

	/**
	 * Returns the flow service locator local to this builder.
	 */
	protected FlowServiceLocator getLocalFlowServiceLocator() {
		return localFlowServiceLocator;
	}

	/**
	 * Returns the artifact factory of the flow service locator local
	 * to this builder.
	 */
	protected FlowArtifactFactory getFlowArtifactFactory() {
		return getLocalFlowServiceLocator().getFlowArtifactFactory();
	}

	// utility (from Spring 2.x DomUtils)

	/**
	 * Utility method that returns the first child element identified by its
	 * name.
	 * @param ele the DOM element to analyze
	 * @param childEleName the child element name to look for
	 * @return the <code>org.w3c.dom.Element</code> instance, or
	 * <code>null</code> if none found
	 */
	protected Element getChildElementByTagName(Element ele, String childEleName) {
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameEquals(node, childEleName)) {
				return (Element)node;
			}
		}
		return null;
	}

	/**
	 * Namespace-aware equals comparison. Returns <code>true</code> if either
	 * {@link Node#getLocalName} or {@link Node#getNodeName} equals
	 * <code>desiredName</code>, otherwise returns <code>false</code>.
	 */
	protected boolean nodeNameEquals(Node node, String desiredName) {
		return desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName());
	}

	// internal parsing logic

	private Flow parseFlow(String id, AttributeMap attributes, Element flowElement) {
		if (!isFlowElement(flowElement)) {
			throw new IllegalStateException("This is not the '" + FLOW_ELEMENT + "' element");
		}
		Flow flow = getFlowArtifactFactory().createFlow(id, parseAttributes(flowElement).union(attributes));
		initLocalServiceRegistry(flowElement, flow);
		return flow;
	}

	private boolean isFlowElement(Element flowElement) {
		return nodeNameEquals(flowElement, FLOW_ELEMENT);
	}

	private void initLocalServiceRegistry(Element flowElement, Flow flow) {
		List importElements = DomUtils.getChildElementsByTagName(flowElement, IMPORT_ELEMENT);
		Resource[] resources = new Resource[importElements.size()];
		for (int i = 0; i < importElements.size(); i++) {
			Element importElement = (Element)importElements.get(i);
			try {
				resources[i] = getLocation().createRelative(importElement.getAttribute(RESOURCE_ATTRIBUTE));
			}
			catch (IOException e) {
				throw new FlowBuilderException("Could not access flow-relative artifact resource '"
						+ importElement.getAttribute(RESOURCE_ATTRIBUTE) + "'", e);
			}
		}
		localFlowServiceLocator.push(new LocalFlowServiceRegistry(flow, resources));
	}

	private void destroyLocalServiceRegistry(Flow flow) {
		localFlowServiceLocator.pop();
	}

	private void parseAndAddFlowVariables(Element flowElement, Flow flow) {
		List varElements = DomUtils.getChildElementsByTagName(flowElement, VAR_ELEMENT);
		for (Iterator it = varElements.iterator(); it.hasNext();) {
			flow.addVariable(parseVariable((Element)it.next()));
		}
	}

	private FlowVariable parseVariable(Element element) {
		ScopeType scope = parseScope(element, ScopeType.FLOW);
		if (StringUtils.hasText(element.getAttribute(BEAN_ATTRIBUTE))) {
			BeanFactory beanFactory = getLocalFlowServiceLocator().getBeanFactory();
			return new BeanFactoryFlowVariable(element.getAttribute(NAME_ATTRIBUTE),
					element.getAttribute(BEAN_ATTRIBUTE), beanFactory, scope);
		}
		else {
			if (StringUtils.hasText(element.getAttribute(CLASS_ATTRIBUTE))) {
				Class variableClass = (Class)fromStringTo(Class.class).execute(element.getAttribute(CLASS_ATTRIBUTE));
				return new SimpleFlowVariable(element.getAttribute(NAME_ATTRIBUTE), variableClass, scope);
			}
			else {
				BeanFactory beanFactory = getLocalFlowServiceLocator().getBeanFactory();
				return new BeanFactoryFlowVariable(element.getAttribute(NAME_ATTRIBUTE), null, beanFactory, scope);
			}
		}
	}

	private void parseAndAddStartActions(Element element, Flow flow) {
		Element startElement = getChildElementByTagName(element, START_ACTIONS_ELEMENT);
		if (startElement != null) {
			flow.getStartActionList().addAll(parseAnnotatedActions(startElement));
		}
	}

	private void parseAndAddEndActions(Element element, Flow flow) {
		Element endElement = getChildElementByTagName(element, END_ACTIONS_ELEMENT);
		if (endElement != null) {
			flow.getEndActionList().addAll(parseAnnotatedActions(endElement));
		}
	}

	private void parseAndAddGlobalTransitions(Element element, Flow flow) {
		Element globalTransitionsElement = getChildElementByTagName(element, GLOBAL_TRANSITIONS_ELEMENT);
		if (globalTransitionsElement != null) {
			flow.getGlobalTransitionSet().addAll(parseTransitions(globalTransitionsElement));
		}
	}

	private void parseAndAddInlineFlowDefinitions(Element parentFlowElement, Flow flow) {
		List inlineFlowElements = DomUtils.getChildElementsByTagName(parentFlowElement, INLINE_FLOW_ELEMENT);
		for (Iterator it = inlineFlowElements.iterator(); it.hasNext();) {
			Element inlineFlowElement = (Element)it.next();
			String inlineFlowId = inlineFlowElement.getAttribute(ID_ATTRIBUTE);
			Element flowElement = getChildElementByTagName(inlineFlowElement, FLOW_ATTRIBUTE);
			Flow inlineFlow = parseFlow(inlineFlowId, null, flowElement);
			buildInlineFlow(flowElement, inlineFlow);
			flow.addInlineFlow(inlineFlow);
		}
	}

	private void buildInlineFlow(Element flowElement, Flow inlineFlow) {
		parseAndAddFlowVariables(flowElement, inlineFlow);
		inlineFlow.setInputMapper(parseInputMapper(flowElement));
		parseAndAddStartActions(flowElement, inlineFlow);
		parseAndAddInlineFlowDefinitions(flowElement, inlineFlow);
		parseAndAddStateDefinitions(flowElement, inlineFlow);
		parseAndAddGlobalTransitions(flowElement, inlineFlow);
		parseAndAddEndActions(flowElement, inlineFlow);
		inlineFlow.setOutputMapper(parseOutputMapper(flowElement));
		inlineFlow.getExceptionHandlerSet().addAll(parseExceptionHandlers(flowElement));
		
		destroyLocalServiceRegistry(inlineFlow);
	}

	private void parseAndAddStateDefinitions(Element flowElement, Flow flow) {
		NodeList childNodeList = flowElement.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (childNode instanceof Element) {
				Element stateElement = (Element)childNode;
				if (nodeNameEquals(stateElement, ACTION_STATE_ELEMENT)) {
					parseAndAddActionState(stateElement, flow);
				}
				else if (nodeNameEquals(stateElement, VIEW_STATE_ELEMENT)) {
					parseAndAddViewState(stateElement, flow);
				}
				else if (nodeNameEquals(stateElement, DECISION_STATE_ELEMENT)) {
					parseAndAddDecisionState(stateElement, flow);
				}
				else if (nodeNameEquals(stateElement, SUBFLOW_STATE_ELEMENT)) {
					parseAndAddSubflowState(stateElement, flow);
				}
				else if (nodeNameEquals(stateElement, END_STATE_ELEMENT)) {
					parseAndAddEndState(stateElement, flow);
				}
			}
		}
		parseAndSetStartState(flowElement, flow);
	}

	private void parseAndSetStartState(Element element, Flow flow) {
		String startStateId = getStartStateId(element);
		flow.setStartState(startStateId);
	}

	private String getStartStateId(Element element) {
		Element startStateElement = getChildElementByTagName(element, START_STATE_ELEMENT);
		return startStateElement.getAttribute(IDREF_ATTRIBUTE);
	}

	private void parseAndAddActionState(Element element, Flow flow) {
		getFlowArtifactFactory().createActionState(parseId(element), flow, parseEntryActions(element),
				parseAnnotatedActions(element), parseTransitions(element), parseExceptionHandlers(element),
				parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddViewState(Element element, Flow flow) {
		getFlowArtifactFactory().createViewState(parseId(element), flow, parseEntryActions(element),
				parseViewSelector(element), parseRenderActions(element), parseTransitions(element),
				parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddDecisionState(Element element, Flow flow) {
		getFlowArtifactFactory().createDecisionState(
				parseId(element), flow, parseEntryActions(element), parseIfs(element),
				parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddSubflowState(Element element, Flow flow) {
		getFlowArtifactFactory().createSubflowState(parseId(element), flow, parseEntryActions(element),
				parseSubflow(element), parseFlowAttributeMapper(element), parseTransitions(element),
				parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddEndState(Element element, Flow flow) {
		getFlowArtifactFactory().createEndState(parseId(element), flow, parseEntryActions(element),
				parseViewSelector(element), parseOutputMapper(element), parseExceptionHandlers(element),
				parseAttributes(element));
	}

	private String parseId(Element element) {
		return element.getAttribute(ID_ATTRIBUTE);
	}

	private Action[] parseEntryActions(Element element) {
		Element entryActionsElement = getChildElementByTagName(element, ENTRY_ACTIONS_ELEMENT);
		if (entryActionsElement != null) {
			return parseAnnotatedActions(entryActionsElement);
		}
		else {
			return null;
		}
	}

	private Action[] parseRenderActions(Element element) {
		Element renderActionsElement = getChildElementByTagName(element, RENDER_ACTIONS_ELEMENT);
		if (renderActionsElement != null) {
			return parseAnnotatedActions(renderActionsElement);
		}
		else {
			return null;
		}
	}

	private Action[] parseExitActions(Element element) {
		Element exitActionsElement = getChildElementByTagName(element, EXIT_ACTIONS_ELEMENT);
		if (exitActionsElement != null) {
			return parseAnnotatedActions(exitActionsElement);
		}
		else {
			return null;
		}
	}

	private Transition[] parseTransitions(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, TRANSITION_ELEMENT);
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element)it.next();
			if (!StringUtils.hasText(transitionElement.getAttribute(ON_EXCEPTION_ATTRIBUTE))) {
				// the "on-exception transition" is not really a transition but rather
				// a FlowExecutionExceptionHandler (see parseTransitionExecutingExceptionHandlers)
				transitions.add(parseTransition(transitionElement));
			}
		}
		return (Transition[])transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition parseTransition(Element element) {
		TransitionCriteria matchingCriteria = (TransitionCriteria)fromStringTo(TransitionCriteria.class).execute(
				element.getAttribute(ON_ATTRIBUTE));
		TargetStateResolver targetStateResolver = (TargetStateResolver)fromStringTo(TargetStateResolver.class).execute(
				element.getAttribute(TO_ATTRIBUTE));
		TransitionCriteria executionCriteria = TransitionCriteriaChain.criteriaChainFor(parseAnnotatedActions(element));
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, executionCriteria,
				parseAttributes(element));
	}

	private ViewSelector parseViewSelector(Element element) {
		String viewName = element.getAttribute(VIEW_ATTRIBUTE);
		return (ViewSelector)fromStringTo(ViewSelector.class).execute(viewName);
	}

	private Flow parseSubflow(Element element) {
		return getLocalFlowServiceLocator().getSubflow(element.getAttribute(FLOW_ATTRIBUTE));
	}

	private AnnotatedAction[] parseAnnotatedActions(Element element) {
		List actions = new LinkedList();
		NodeList childNodeList = element.getChildNodes();
		for (int i=0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (!(childNode instanceof Element)) {
				continue;
			}
			
			if (nodeNameEquals(childNode, ACTION_ELEMENT)) {
				// parse standard action
				actions.add(parseAnnotatedAction((Element)childNode));
			}
			else if (nodeNameEquals(childNode, BEAN_ACTION_ELEMENT)) {
				// parse bean invoking action
				actions.add(parseAnnotatedBeanInvokingAction((Element)childNode));
			}
			else if (nodeNameEquals(childNode, EVALUATE_ACTION_ELEMENT)) {
				// parse evaluate action
				actions.add(parseAnnotatedEvaluateAction((Element)childNode));
			}
			else if (nodeNameEquals(childNode, SET_ELEMENT)) {
				// parse set action
				actions.add(parseAnnotatedSetAction((Element)childNode));
			}
		}
		return (AnnotatedAction[])actions.toArray(new AnnotatedAction[actions.size()]);
	}

	private AnnotatedAction parseAnnotatedAction(Element element) {
		AnnotatedAction annotated = new AnnotatedAction(parseAction(element));
		parseCommonProperties(element, annotated);
		if (element.hasAttribute(METHOD_ATTRIBUTE)) {
			annotated.setMethod(element.getAttribute(METHOD_ATTRIBUTE));
		}
		return annotated;
	}

	private Action parseAction(Element element) {
		String actionId = element.getAttribute(BEAN_ATTRIBUTE);
		return getLocalFlowServiceLocator().getAction(actionId);
	}

	private AnnotatedAction parseCommonProperties(Element element, AnnotatedAction annotated) {
		if (element.hasAttribute(NAME_ATTRIBUTE)) {
			annotated.setName(element.getAttribute(NAME_ATTRIBUTE));
		}
		annotated.getAttributeMap().putAll(parseAttributes(element));
		return annotated;
	}

	private AnnotatedAction parseAnnotatedBeanInvokingAction(Element element) {
		AnnotatedAction annotated = new AnnotatedAction(parseBeanInvokingAction(element));
		return parseCommonProperties(element, annotated);
	}

	private Action parseBeanInvokingAction(Element element) {
		String beanId = element.getAttribute(BEAN_ATTRIBUTE);
		String methodName = element.getAttribute(METHOD_ATTRIBUTE);
		Parameters parameters = parseMethodParameters(element);
		MethodSignature methodSignature = new MethodSignature(methodName, parameters);
		ActionResultExposer resultExposer = parseMethodResultExposer(element);
		return getLocalFlowServiceLocator().getBeanInvokingActionFactory().createBeanInvokingAction(beanId,
				getLocalFlowServiceLocator().getBeanFactory(), methodSignature, resultExposer,
				getLocalFlowServiceLocator().getConversionService(), null);
	}

	private Parameters parseMethodParameters(Element element) {
		Element methodArgumentsElement = getChildElementByTagName(element, METHOD_ARGUMENTS_ELEMENT);
		if (methodArgumentsElement == null) {
			return Parameters.NONE;
		}
		Parameters parameters = new Parameters();
		Iterator it = DomUtils.getChildElementsByTagName(methodArgumentsElement, ARGUMENT_ELEMENT).iterator();
		while (it.hasNext()) {
			Element argumentElement = (Element)it.next();
			Expression name = getLocalFlowServiceLocator().getExpressionParser()
				.parseExpression(argumentElement.getAttribute(EXPRESSION_ATTRIBUTE));
			Class type = null;
			if (argumentElement.hasAttribute(PARAMETER_TYPE_ATTRIBUTE)) {
				type = (Class)fromStringTo(Class.class).execute(argumentElement.getAttribute(PARAMETER_TYPE_ATTRIBUTE));
			}
			parameters.add(new Parameter(type, name));
		}
		return parameters;
	}

	private ActionResultExposer parseMethodResultExposer(Element element) {
		Element resultElement = getChildElementByTagName(element, METHOD_RESULT_ELEMENT);
		if (resultElement != null) {
			return parseActionResultExposer(resultElement);
		}
		else {
			return null;
		}
	}

	private ActionResultExposer parseActionResultExposer(Element element) {
		String resultName = element.getAttribute(NAME_ATTRIBUTE);
		return new ActionResultExposer(resultName, parseScope(element, ScopeType.REQUEST));
	}

	private AnnotatedAction parseAnnotatedEvaluateAction(Element element) {
		AnnotatedAction annotated = new AnnotatedAction(parseEvaluateAction(element));
		return parseCommonProperties(element, annotated);
	}

	private Action parseEvaluateAction(Element element) {
		String expressionString = element.getAttribute(EXPRESSION_ATTRIBUTE);
		Expression expression = getLocalFlowServiceLocator().getExpressionParser()
			.parseExpression(expressionString);
		return new EvaluateAction(expression, parseEvaluationResultExposer(element));
	}

	private ActionResultExposer parseEvaluationResultExposer(Element element) {
		Element resultElement = getChildElementByTagName(element, EVALUATION_RESULT_ELEMENT);
		if (resultElement != null) {
			return parseActionResultExposer(resultElement);
		}
		else {
			return null;
		}
	}

	private AnnotatedAction parseAnnotatedSetAction(Element element) {
		AnnotatedAction annotated = new AnnotatedAction(parseSetAction(element));
		return parseCommonProperties(element, annotated);
	}

	private Action parseSetAction(Element element) {
		String attributeExpressionString = element.getAttribute(ATTRIBUTE_ATTRIBUTE);
		SettableExpression attributeExpression = getLocalFlowServiceLocator().getExpressionParser()
			.parseSettableExpression(attributeExpressionString);
		Expression valueExpression = getLocalFlowServiceLocator().getExpressionParser()
			.parseExpression(element.getAttribute(VALUE_ATTRIBUTE));
		return new SetAction(attributeExpression, parseScope(element, ScopeType.REQUEST), valueExpression);
	}

	private ScopeType parseScope(Element element, ScopeType defaultValue) {
		if (element.hasAttribute(SCOPE_ATTRIBUTE) && !element.getAttribute(SCOPE_ATTRIBUTE).equals(DEFAULT_VALUE)) {
			return (ScopeType)fromStringTo(ScopeType.class).execute(element.getAttribute(SCOPE_ATTRIBUTE));
		}
		else {
			return defaultValue;
		}
	}

	private AttributeMap parseAttributes(Element element) {
		LocalAttributeMap attributes = new LocalAttributeMap();
		List propertyElements = DomUtils.getChildElementsByTagName(element, ATTRIBUTE_ELEMENT);
		for (int i = 0; i < propertyElements.size(); i++) {
			parseAndSetAttribute((Element)propertyElements.get(i), attributes);
		}
		return attributes;
	}

	private void parseAndSetAttribute(Element element, MutableAttributeMap attributes) {
		String name = element.getAttribute(NAME_ATTRIBUTE);
		String value = null;
		if (element.hasAttribute(VALUE_ATTRIBUTE)) {
			value = element.getAttribute(VALUE_ATTRIBUTE);
		}
		else {
			List valueElements = DomUtils.getChildElementsByTagName(element, VALUE_ELEMENT);
			Assert.state(valueElements.size() == 1, "A property value should be specified for property '" + name + "'");
			value = DomUtils.getTextValue((Element)valueElements.get(0));
		}
		attributes.put(name, convertPropertyValue(element, value));
	}

	private Object convertPropertyValue(Element element, String stringValue) {
		if (element.hasAttribute(TYPE_ATTRIBUTE)) {
			Class targetClass = (Class)fromStringTo(Class.class).execute(element.getAttribute(TYPE_ATTRIBUTE));
			// convert string value to instance of target class
			return fromStringTo(targetClass).execute(stringValue);
		}
		else {
			return stringValue;
		}
	}

	private Transition[] parseIfs(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, IF_ELEMENT);
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			transitions.addAll(Arrays.asList(parseIf((Element)it.next())));
		}
		return (Transition[])transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition[] parseIf(Element element) {
		Transition thenTransition = parseThen(element);
		if (StringUtils.hasText(element.getAttribute(ELSE_ATTRIBUTE))) {
			Transition elseTransition = parseElse(element);
			return new Transition[] { thenTransition, elseTransition };
		}
		else {
			return new Transition[] { thenTransition };
		}
	}

	private Transition parseThen(Element element) {
		Expression expression = getLocalFlowServiceLocator().getExpressionParser()
			.parseExpression(element.getAttribute(TEST_ATTRIBUTE));
		TransitionCriteria matchingCriteria = new BooleanExpressionTransitionCriteria(expression);
		TargetStateResolver targetStateResolver = (TargetStateResolver)fromStringTo(TargetStateResolver.class).execute(
				element.getAttribute(THEN_ATTRIBUTE));
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null, null);
	}

	private Transition parseElse(Element element) {
		TargetStateResolver targetStateResolver = (TargetStateResolver)fromStringTo(TargetStateResolver.class).execute(
				element.getAttribute(ELSE_ATTRIBUTE));
		return getFlowArtifactFactory().createTransition(targetStateResolver, null, null, null);
	}

	private FlowAttributeMapper parseFlowAttributeMapper(Element element) {
		Element mapperElement = getChildElementByTagName(element, ATTRIBUTE_MAPPER_ELEMENT);
		if (mapperElement == null) {
			return null;
		}
		if (StringUtils.hasText(mapperElement.getAttribute(BEAN_ATTRIBUTE))) {
			return getLocalFlowServiceLocator().getAttributeMapper(mapperElement.getAttribute(BEAN_ATTRIBUTE));
		}
		else {
			return new ImmutableFlowAttributeMapper(parseInputMapper(mapperElement), parseOutputMapper(mapperElement));
		}
	}

	private AttributeMapper parseInputMapper(Element element) {
		Element mapperElement = getChildElementByTagName(element, INPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleAttributeMappings(mapper,
					DomUtils.getChildElementsByTagName(mapperElement, INPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement);
			return mapper;
		}
		else {
			return null;
		}
	}

	private AttributeMapper parseOutputMapper(Element element) {
		Element mapperElement = getChildElementByTagName(element, OUTPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleAttributeMappings(mapper,
					DomUtils.getChildElementsByTagName(mapperElement, OUTPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement);
			return mapper;
		}
		else {
			return null;
		}
	}

	private void parseMappings(DefaultAttributeMapper mapper, Element element) {
		ExpressionParser parser = getLocalFlowServiceLocator().getExpressionParser();
		List mappingElements = DomUtils.getChildElementsByTagName(element, MAPPING_ELEMENT);
		for (Iterator it = mappingElements.iterator(); it.hasNext();) {
			Element mappingElement = (Element)it.next();
			Expression source = parser.parseExpression(mappingElement.getAttribute(SOURCE_ATTRIBUTE));
			SettableExpression target = null;
			if (StringUtils.hasText(mappingElement.getAttribute(TARGET_ATTRIBUTE))) {
				target = parser.parseSettableExpression(mappingElement.getAttribute(TARGET_ATTRIBUTE));
			}
			else if (StringUtils.hasText(mappingElement.getAttribute(TARGET_COLLECTION_ATTRIBUTE))) {
				target = new CollectionAddingExpression(
						parser.parseSettableExpression(mappingElement.getAttribute(TARGET_COLLECTION_ATTRIBUTE)));
			}
			if (getRequired(mappingElement, false)) {
				mapper.addMapping(new RequiredMapping(source, target, parseTypeConverter(mappingElement)));
			}
			else {
				mapper.addMapping(new Mapping(source, target, parseTypeConverter(mappingElement)));
			}
		}
	}

	private void parseSimpleAttributeMappings(DefaultAttributeMapper mapper, List elements) {
		ExpressionParser parser = getLocalFlowServiceLocator().getExpressionParser();
		for (Iterator it = elements.iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			SettableExpression attribute = parser.parseSettableExpression(element.getAttribute(NAME_ATTRIBUTE));
			SettableExpression expression = new AttributeExpression(attribute, parseScope(element, ScopeType.FLOW));
			if (getRequired(element, false)) {
				mapper.addMapping(new RequiredMapping(expression, expression, null));
			}
			else {
				mapper.addMapping(new Mapping(expression, expression, null));
			}
		}
	}

	private boolean getRequired(Element element, boolean defaultValue) {
		if (StringUtils.hasText(element.getAttribute(REQUIRED_ATTRIBUTE))) {
			return ((Boolean)fromStringTo(Boolean.class).execute(element.getAttribute(REQUIRED_ATTRIBUTE)))
					.booleanValue();
		}
		else {
			return defaultValue;
		}
	}

	private ConversionExecutor parseTypeConverter(Element element) {
		String from = element.getAttribute(FROM_ATTRIBUTE);
		String to = element.getAttribute(TO_ATTRIBUTE);
		if (StringUtils.hasText(from)) {
			if (StringUtils.hasText(to)) {
				ConversionService service = getLocalFlowServiceLocator().getConversionService();
				Class sourceClass = (Class)fromStringTo(Class.class).execute(from);
				Class targetClass = (Class)fromStringTo(Class.class).execute(to);
				return service.getConversionExecutor(sourceClass, targetClass);
			}
			else {
				throw new IllegalArgumentException("Use of the 'from' attribute requires use of the 'to' attribute");
			}
		}
		else {
			Assert.isTrue(!StringUtils.hasText(to), "Use of the 'to' attribute requires use of the 'from' attribute");
		}
		return null;
	}

	private FlowExecutionExceptionHandler[] parseExceptionHandlers(Element element) {
		FlowExecutionExceptionHandler[] transitionExecutingHandlers = parseTransitionExecutingExceptionHandlers(element);
		FlowExecutionExceptionHandler[] customHandlers = parseCustomExceptionHandlers(element);
		FlowExecutionExceptionHandler[] exceptionHandlers =
			new FlowExecutionExceptionHandler[transitionExecutingHandlers.length + customHandlers.length];
		System.arraycopy(transitionExecutingHandlers, 0, exceptionHandlers, 0, transitionExecutingHandlers.length);
		System.arraycopy(customHandlers, 0, exceptionHandlers, transitionExecutingHandlers.length,
			customHandlers.length);
		return exceptionHandlers;
	}

	private FlowExecutionExceptionHandler[] parseTransitionExecutingExceptionHandlers(Element element) {
		List transitionElements = Collections.EMPTY_LIST;
		if (isFlowElement(element)) {
			Element globalTransitionsElement = getChildElementByTagName(element, GLOBAL_TRANSITIONS_ELEMENT);
			if (globalTransitionsElement != null) {
				transitionElements = DomUtils.getChildElementsByTagName(globalTransitionsElement, TRANSITION_ELEMENT);
			}
		}
		else {
			transitionElements = DomUtils.getChildElementsByTagName(element, TRANSITION_ELEMENT);
		}
		List exceptionHandlers = new LinkedList();
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element)it.next();
			if (StringUtils.hasText(transitionElement.getAttribute(ON_EXCEPTION_ATTRIBUTE))) {
				// the "on-exception transitions" are not really transitions but rather
				// FlowExecutionExceptionHandlers
				exceptionHandlers.add(parseTransitionExecutingExceptionHandler(transitionElement));
			}
		}
		return (FlowExecutionExceptionHandler[])exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseTransitionExecutingExceptionHandler(Element element) {
		TransitionExecutingStateExceptionHandler handler = new TransitionExecutingStateExceptionHandler();
		Class exceptionClass = (Class)fromStringTo(Class.class).execute(element.getAttribute(ON_EXCEPTION_ATTRIBUTE));
		TargetStateResolver targetStateResolver = (TargetStateResolver)fromStringTo(TargetStateResolver.class).execute(
				element.getAttribute(TO_ATTRIBUTE));
		handler.add(exceptionClass, targetStateResolver);
		handler.getActionList().addAll(parseAnnotatedActions(element));
		return handler;
	}

	private FlowExecutionExceptionHandler[] parseCustomExceptionHandlers(Element element) {
		List exceptionHandlers = new LinkedList();
		List handlerElements = DomUtils.getChildElementsByTagName(element, EXCEPTION_HANDLER_ELEMENT);
		for (int i = 0; i < handlerElements.size(); i++) {
			Element handlerElement = (Element)handlerElements.get(i);
			exceptionHandlers.add(parseCustomExceptionHandler(handlerElement));
		}
		return (FlowExecutionExceptionHandler[])exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseCustomExceptionHandler(Element element) {
		return getLocalFlowServiceLocator().getExceptionHandler(element.getAttribute(BEAN_ATTRIBUTE));
	}
}