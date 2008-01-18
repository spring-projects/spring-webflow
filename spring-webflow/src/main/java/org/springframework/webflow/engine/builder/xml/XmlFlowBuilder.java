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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.CollectionAddingExpression;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.RequiredMapping;
import org.springframework.binding.method.MethodSignature;
import org.springframework.binding.method.Parameter;
import org.springframework.binding.method.Parameters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.webflow.action.ActionResultExposer;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.action.ExternalRedirectAction;
import org.springframework.webflow.action.FlowDefinitionRedirectAction;
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
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.support.ActionInvokingViewFactory;
import org.springframework.webflow.engine.support.BeanFactoryFlowVariable;
import org.springframework.webflow.engine.support.BooleanExpressionTransitionCriteria;
import org.springframework.webflow.engine.support.SimpleFlowVariable;
import org.springframework.webflow.engine.support.TransitionCriteriaChain;
import org.springframework.webflow.engine.support.TransitionExecutingFlowExecutionExceptionHandler;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.util.ResourceHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Flow builder that builds flows as defined in an XML document. The XML document should adhere to the following format:
 * 
 * <pre>
 *     &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *     &lt;flow xmlns=&quot;http://www.springframework.org/schema/webflow&quot;
 *           xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *           xsi:schemaLocation=&quot;http://www.springframework.org/schema/webflow
 *                               http://www.springframework.org/schema/webflow/spring-webflow-1.0.xsd&quot;&gt;
 *         &lt;!-- Define your states here --&gt;
 *     &lt;/flow&gt;
 * </pre>
 * 
 * <p>
 * Consult the <a href="http://www.springframework.org/schema/webflow/spring-webflow-1.0.xsd">web flow XML schema</a>
 * for more information on the XML-based flow definition format.
 * <p>
 * This builder will setup a flow-local bean factory for the flow being constructed. That flow-local bean factory will
 * be populated with XML bean definitions contained in files referenced using the "import" element. The flow-local bean
 * factory will use the bean factory of this flow builder as a parent. As such, the flow can access artifacts in either
 * its flow-local bean factory or in the parent bean factory hierarchy, e.g. the bean factory of the dispatcher.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class XmlFlowBuilder extends AbstractFlowBuilder implements ResourceHolder {

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

	private static final String IMPORT_ELEMENT = "import";

	private static final String RESOURCE_ATTRIBUTE = "resource";

	private static final String VIEW_ATTRIBUTE = "view";

	/**
	 * Prefix used when the encoded view name wants to specify that a redirect is required. ("redirect:")
	 */
	private static final String REDIRECT_PREFIX = "redirect:";

	/**
	 * Prefix used when the encoded view name wants to specify that a redirect to an external URL is required.
	 * ("externalRedirect:")
	 */
	private static final String EXTERNAL_REDIRECT_PREFIX = "externalRedirect:";

	/**
	 * Prefix used when the encoded view name wants to specify that a redirect to a flow definition is requred.
	 * ("flowRedirect:")
	 */
	private static final String FLOW_DEFINITION_REDIRECT_PREFIX = "flowRedirect:";

	/**
	 * Prefix used when the user wants to use a ViewSelector implementation managed by a bean factory. ("bean:")
	 */
	private static final String BEAN_PREFIX = "bean:";

	/**
	 * The resource from which the document element being parsed was read. Used as a location for relative resource
	 * lookup.
	 */
	protected Resource resource;

	/**
	 * A flow service locator local to this builder that first looks in a locally-managed Spring bean factory for
	 * services before searching the externally managed {@link #getFlowServiceLocator() service locator}.
	 */
	private LocalFlowBuilderContext localFlowBuilderContext;

	/**
	 * The loader for loading the flow definition resource XML document.
	 */
	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	/**
	 * The in-memory document object model (DOM) of the XML Document read from the flow definition resource.
	 */
	private Document document;

	/**
	 * Create a new XML flow builder parsing the document at the specified location, using the provided service locator
	 * to access externally managed flow artifacts.
	 * @param resource the location of the XML-based flow definition resource
	 */
	public XmlFlowBuilder(Resource resource) {
		Assert.notNull(resource, "The resource location of the XML-based flow definition is required");
		this.resource = resource;
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

	// implementing FlowBuilder

	protected void doInit() throws FlowBuilderException {
		try {
			document = documentLoader.loadDocument(resource);
			initLocalFlowContext(getDocumentElement());
		} catch (IOException e) {
			throw new FlowBuilderException("Could not access the XML flow definition resource at " + resource, e);
		} catch (ParserConfigurationException e) {
			throw new FlowBuilderException("Could not configure the parser to parse the XML flow definition at "
					+ resource, e);
		} catch (SAXException e) {
			throw new FlowBuilderException("Could not parse the XML flow definition document at " + resource, e);
		}
	}

	protected Flow createFlow() {
		return parseFlow(getDocumentElement());
	}

	public void buildVariables() throws FlowBuilderException {
		parseAndAddFlowVariables(getDocumentElement(), getFlow());
	}

	public void buildInputMapper() throws FlowBuilderException {
		AttributeMapper inputMapper = parseInputMapper(getDocumentElement());
		if (inputMapper != null) {
			getFlow().setInputMapper(inputMapper);
		}
	}

	public void buildStartActions() throws FlowBuilderException {
		parseAndAddStartActions(getDocumentElement(), getFlow());
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
		AttributeMapper outputMapper = parseOutputMapper(getDocumentElement());
		if (outputMapper != null) {
			getFlow().setOutputMapper(outputMapper);
		}
	}

	public void buildExceptionHandlers() throws FlowBuilderException {
		getFlow().getExceptionHandlerSet().addAll(parseExceptionHandlers(getDocumentElement()));
	}

	protected void doDispose() {
		document = null;
	}

	// implementing ResourceHolder

	public Resource getResource() {
		return resource;
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
	protected LocalFlowBuilderContext getLocalContext() {
		return localFlowBuilderContext;
	}

	/**
	 * Returns the artifact factory of the flow service locator local to this builder.
	 */
	protected FlowArtifactFactory getFlowArtifactFactory() {
		return getLocalContext().getFlowArtifactFactory();
	}

	// internal parsing logic and hook methods

	private Flow parseFlow(Element flowElement) {
		if (!isFlowElement(flowElement)) {
			throw new IllegalArgumentException("This is not the '" + FLOW_ELEMENT + "' element");
		}
		String flowId = getLocalContext().getFlowId();
		AttributeMap externallyAssignedAttributes = getLocalContext().getFlowAttributes();
		AttributeMap flowAttributes = parseAttributes(flowElement).union(externallyAssignedAttributes);
		return getFlowArtifactFactory().createFlow(flowId, flowAttributes);
	}

	private boolean isFlowElement(Element flowElement) {
		return DomUtils.nodeNameEquals(flowElement, FLOW_ELEMENT);
	}

	private void initLocalFlowContext(Element flowElement) {
		List importElements = DomUtils.getChildElementsByTagName(flowElement, IMPORT_ELEMENT);
		Resource[] resources = new Resource[importElements.size()];
		for (int i = 0; i < importElements.size(); i++) {
			Element importElement = (Element) importElements.get(i);
			try {
				resources[i] = getResource().createRelative(importElement.getAttribute(RESOURCE_ATTRIBUTE));
			} catch (IOException e) {
				throw new FlowBuilderException("Could not access flow-relative artifact resource '"
						+ importElement.getAttribute(RESOURCE_ATTRIBUTE) + "'", e);
			}
		}
		this.localFlowBuilderContext = new LocalFlowBuilderContext(getContext(), createFlowBeanFactory(resources));
	}

	private BeanFactory createFlowBeanFactory(Resource[] resources) {
		// see if this factory has a parent
		BeanFactory parent = getContext().getBeanFactory();
		// determine the context implementation based on the current environment
		GenericApplicationContext flowContext;
		if (parent instanceof WebApplicationContext) {
			GenericWebApplicationContext webContext = new GenericWebApplicationContext();
			webContext.setServletContext(((WebApplicationContext) parent).getServletContext());
			flowContext = webContext;
		} else {
			flowContext = new GenericApplicationContext();
		}
		// set the parent if necessary
		if (parent instanceof ApplicationContext) {
			flowContext.setParent((ApplicationContext) parent);
		} else {
			if (parent != null) {
				flowContext.getBeanFactory().setParentBeanFactory(parent);
			}
		}
		flowContext.setResourceLoader(new FlowRelativeResourceLoader(resource));
		new XmlBeanDefinitionReader(flowContext).loadBeanDefinitions(resources);
		registerFlowBeans(flowContext.getDefaultListableBeanFactory());
		flowContext.refresh();
		return flowContext;
	}

	/**
	 * Register beans in the bean factory local to the flow definition being built.
	 * <p>
	 * Subclasses may override this method to customize the population of the bean factory local to the flow definition
	 * being built; for example, to register mock implementations of services in a test environment.
	 * @param beanFactory the bean factory; register local beans with it using
	 * {@link ConfigurableBeanFactory#registerSingleton(String, Object)}
	 */
	protected void registerFlowBeans(ConfigurableBeanFactory beanFactory) {
	}

	private void parseAndAddFlowVariables(Element flowElement, Flow flow) {
		List varElements = DomUtils.getChildElementsByTagName(flowElement, VAR_ELEMENT);
		for (Iterator it = varElements.iterator(); it.hasNext();) {
			flow.addVariable(parseVariable((Element) it.next()));
		}
	}

	private FlowVariable parseVariable(Element element) {
		ScopeType scope = parseScope(element, ScopeType.FLOW);
		if (StringUtils.hasText(element.getAttribute(BEAN_ATTRIBUTE))) {
			return new BeanFactoryFlowVariable(element.getAttribute(NAME_ATTRIBUTE), element
					.getAttribute(BEAN_ATTRIBUTE), getLocalContext().getBeanFactory(), scope);
		} else {
			if (StringUtils.hasText(element.getAttribute(CLASS_ATTRIBUTE))) {
				Class variableClass = (Class) fromStringTo(Class.class).execute(element.getAttribute(CLASS_ATTRIBUTE));
				return new SimpleFlowVariable(element.getAttribute(NAME_ATTRIBUTE), variableClass, scope);
			} else {
				return new BeanFactoryFlowVariable(element.getAttribute(NAME_ATTRIBUTE), null, getLocalContext()
						.getBeanFactory(), scope);
			}
		}
	}

	private void parseAndAddStartActions(Element element, Flow flow) {
		Element startElement = DomUtils.getChildElementByTagName(element, START_ACTIONS_ELEMENT);
		if (startElement != null) {
			flow.getStartActionList().addAll(parseAnnotatedActions(startElement));
		}
	}

	private void parseAndAddEndActions(Element element, Flow flow) {
		Element endElement = DomUtils.getChildElementByTagName(element, END_ACTIONS_ELEMENT);
		if (endElement != null) {
			flow.getEndActionList().addAll(parseAnnotatedActions(endElement));
		}
	}

	private void parseAndAddGlobalTransitions(Element element, Flow flow) {
		Element globalTransitionsElement = DomUtils.getChildElementByTagName(element, GLOBAL_TRANSITIONS_ELEMENT);
		if (globalTransitionsElement != null) {
			flow.getGlobalTransitionSet().addAll(parseTransitions(globalTransitionsElement));
		}
	}

	private void parseAndAddStateDefinitions(Element flowElement, Flow flow) {
		NodeList childNodeList = flowElement.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (childNode instanceof Element) {
				Element stateElement = (Element) childNode;
				if (DomUtils.nodeNameEquals(stateElement, ACTION_STATE_ELEMENT)) {
					parseAndAddActionState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, VIEW_STATE_ELEMENT)) {
					parseAndAddViewState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, DECISION_STATE_ELEMENT)) {
					parseAndAddDecisionState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, SUBFLOW_STATE_ELEMENT)) {
					parseAndAddSubflowState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, END_STATE_ELEMENT)) {
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
		Element startStateElement = DomUtils.getChildElementByTagName(element, START_STATE_ELEMENT);
		return startStateElement.getAttribute(IDREF_ATTRIBUTE);
	}

	private void parseAndAddActionState(Element element, Flow flow) {
		getFlowArtifactFactory().createActionState(parseId(element), flow, parseEntryActions(element),
				parseAnnotatedActions(element), parseTransitions(element), parseExceptionHandlers(element),
				parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddViewState(Element element, Flow flow) {
		ViewInfo viewInfo = parseViewInfo(element);
		boolean redirect = false;
		if (viewInfo.redirect != null) {
			redirect = viewInfo.redirect.booleanValue();
		}
		getFlowArtifactFactory().createViewState(parseId(element), flow, parseEntryActions(element),
				viewInfo.viewFactory, redirect, parseRenderActions(element), parseTransitions(element),
				parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddDecisionState(Element element, Flow flow) {
		getFlowArtifactFactory()
				.createDecisionState(parseId(element), flow, parseEntryActions(element), parseIfs(element),
						parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddSubflowState(Element element, Flow flow) {
		getFlowArtifactFactory().createSubflowState(parseId(element), flow, parseEntryActions(element),
				parseSubflow(element), parseFlowAttributeMapper(element), parseTransitions(element),
				parseExceptionHandlers(element), parseExitActions(element), parseAttributes(element));
	}

	private void parseAndAddEndState(Element element, Flow flow) {
		getFlowArtifactFactory().createEndState(parseId(element), flow, parseEntryActions(element),
				parseFinalResponseAction(element), parseOutputMapper(element), parseExceptionHandlers(element),
				parseAttributes(element));
	}

	private String parseId(Element element) {
		return element.getAttribute(ID_ATTRIBUTE);
	}

	private Action[] parseEntryActions(Element element) {
		Element entryActionsElement = DomUtils.getChildElementByTagName(element, ENTRY_ACTIONS_ELEMENT);
		if (entryActionsElement != null) {
			return parseAnnotatedActions(entryActionsElement);
		} else {
			return null;
		}
	}

	private ViewInfo parseViewInfo(Element element) {
		String encodedView = element.getAttribute(VIEW_ATTRIBUTE);
		if (encodedView == null || encodedView.length() == 0) {
			// TODO what to do here?
			return null;
		} else if (encodedView.startsWith(REDIRECT_PREFIX)) {
			String encodedViewName = encodedView.substring(REDIRECT_PREFIX.length());
			Expression viewName = getExpressionParser().parseExpression(encodedViewName,
					new ParserContextImpl().eval(RequestContext.class).expect(String.class));
			ViewFactory viewFactory = getLocalContext().getViewFactoryCreator().createViewFactory(viewName,
					getLocalContext().getResourceLoader());
			return new ViewInfo(viewFactory, Boolean.TRUE);
		} else if (encodedView.startsWith(EXTERNAL_REDIRECT_PREFIX)) {
			String encodedUrl = encodedView.substring(EXTERNAL_REDIRECT_PREFIX.length());
			Expression externalUrl = getExpressionParser().parseExpression(encodedUrl,
					new ParserContextImpl().eval(RequestContext.class).expect(String.class));
			ViewFactory viewFactory = new ActionInvokingViewFactory(new ExternalRedirectAction(externalUrl));
			return new ViewInfo(viewFactory, Boolean.FALSE);
		} else if (encodedView.startsWith(FLOW_DEFINITION_REDIRECT_PREFIX)) {
			String flowRedirect = encodedView.substring(FLOW_DEFINITION_REDIRECT_PREFIX.length());
			ViewFactory viewFactory = new ActionInvokingViewFactory(FlowDefinitionRedirectAction.create(flowRedirect));
			return new ViewInfo(viewFactory, Boolean.FALSE);
		} else if (encodedView.startsWith(BEAN_PREFIX)) {
			ViewFactory viewFactory = (ViewFactory) getLocalContext().getBeanFactory().getBean(
					encodedView.substring(BEAN_PREFIX.length()), ViewFactory.class);
			return new ViewInfo(viewFactory, Boolean.FALSE);
		} else {
			Expression viewName = getExpressionParser().parseExpression(encodedView,
					new ParserContextImpl().eval(RequestContext.class).expect(String.class));
			ViewFactory viewFactory = getLocalContext().getViewFactoryCreator().createViewFactory(viewName,
					getLocalContext().getResourceLoader());
			return new ViewInfo(viewFactory, null);
		}
	}

	private Action parseFinalResponseAction(Element element) {
		String encodedView = element.getAttribute(VIEW_ATTRIBUTE);
		if (encodedView == null || encodedView.length() == 0) {
			// null final responses are allowed
			return null;
		} else if (encodedView.startsWith(EXTERNAL_REDIRECT_PREFIX)) {
			String encodedUrl = encodedView.substring(EXTERNAL_REDIRECT_PREFIX.length());
			Expression externalUrl = getExpressionParser().parseExpression(encodedUrl,
					new ParserContextImpl().eval(RequestContext.class).expect(String.class));
			return new ExternalRedirectAction(externalUrl);
		} else if (encodedView.startsWith(FLOW_DEFINITION_REDIRECT_PREFIX)) {
			String flowRedirect = encodedView.substring(FLOW_DEFINITION_REDIRECT_PREFIX.length());
			return FlowDefinitionRedirectAction.create(flowRedirect);
		} else if (encodedView.startsWith(BEAN_PREFIX)) {
			return (Action) getLocalContext().getBeanFactory().getBean(encodedView.substring(BEAN_PREFIX.length()),
					Action.class);
		} else {
			Expression viewName = getExpressionParser().parseExpression(encodedView,
					new ParserContextImpl().eval(RequestContext.class).expect(String.class));
			return getLocalContext().getViewFactoryCreator().createFinalResponseAction(viewName,
					getLocalContext().getResourceLoader());
		}
	}

	private Action[] parseRenderActions(Element element) {
		Element renderActionsElement = DomUtils.getChildElementByTagName(element, RENDER_ACTIONS_ELEMENT);
		if (renderActionsElement != null) {
			return parseAnnotatedActions(renderActionsElement);
		} else {
			return null;
		}
	}

	private Action[] parseExitActions(Element element) {
		Element exitActionsElement = DomUtils.getChildElementByTagName(element, EXIT_ACTIONS_ELEMENT);
		if (exitActionsElement != null) {
			return parseAnnotatedActions(exitActionsElement);
		} else {
			return null;
		}
	}

	private Transition[] parseTransitions(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, TRANSITION_ELEMENT);
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element) it.next();
			if (!StringUtils.hasText(transitionElement.getAttribute(ON_EXCEPTION_ATTRIBUTE))) {
				transitions.add(parseTransition(transitionElement));
			}
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition parseTransition(Element element) {
		TransitionCriteria matchingCriteria = (TransitionCriteria) fromStringTo(TransitionCriteria.class).execute(
				element.getAttribute(ON_ATTRIBUTE));
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute(TO_ATTRIBUTE));
		TransitionCriteria executionCriteria = TransitionCriteriaChain.criteriaChainFor(parseAnnotatedActions(element));
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, executionCriteria,
				parseAttributes(element));
	}

	private Flow parseSubflow(Element element) {
		return (Flow) getLocalContext().getFlowDefinitionLocator().getFlowDefinition(
				element.getAttribute(FLOW_ATTRIBUTE));
	}

	private AnnotatedAction[] parseAnnotatedActions(Element element) {
		List actions = new LinkedList();
		NodeList childNodeList = element.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (!(childNode instanceof Element)) {
				continue;
			}
			if (DomUtils.nodeNameEquals(childNode, ACTION_ELEMENT)) {
				// parse standard action
				actions.add(parseAnnotatedAction((Element) childNode));
			} else if (DomUtils.nodeNameEquals(childNode, BEAN_ACTION_ELEMENT)) {
				// parse bean invoking action
				actions.add(parseAnnotatedBeanInvokingAction((Element) childNode));
			} else if (DomUtils.nodeNameEquals(childNode, EVALUATE_ACTION_ELEMENT)) {
				// parse evaluate action
				actions.add(parseAnnotatedEvaluateAction((Element) childNode));
			} else if (DomUtils.nodeNameEquals(childNode, SET_ELEMENT)) {
				// parse set action
				actions.add(parseAnnotatedSetAction((Element) childNode));
			}
		}
		return (AnnotatedAction[]) actions.toArray(new AnnotatedAction[actions.size()]);
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
		return (Action) getLocalContext().getBeanFactory().getBean(actionId, Action.class);
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
		return getLocalContext().getBeanInvokingActionFactory().createBeanInvokingAction(beanId,
				getLocalContext().getBeanFactory(), methodSignature, resultExposer,
				getLocalContext().getConversionService(), null);
	}

	private Parameters parseMethodParameters(Element element) {
		Element methodArgumentsElement = DomUtils.getChildElementByTagName(element, METHOD_ARGUMENTS_ELEMENT);
		if (methodArgumentsElement == null) {
			return Parameters.NONE;
		}
		Parameters parameters = new Parameters();
		Iterator it = DomUtils.getChildElementsByTagName(methodArgumentsElement, ARGUMENT_ELEMENT).iterator();
		ExpressionParser parser = getLocalContext().getExpressionParser();
		while (it.hasNext()) {
			Element argumentElement = (Element) it.next();
			Expression name = parser.parseExpression(argumentElement.getAttribute(EXPRESSION_ATTRIBUTE),
					new ParserContextImpl().eval(RequestContext.class));
			Class type = null;
			if (argumentElement.hasAttribute(PARAMETER_TYPE_ATTRIBUTE)) {
				type = (Class) fromStringTo(Class.class)
						.execute(argumentElement.getAttribute(PARAMETER_TYPE_ATTRIBUTE));
			}
			parameters.add(new Parameter(type, name));
		}
		return parameters;
	}

	private ActionResultExposer parseMethodResultExposer(Element element) {
		Element resultElement = DomUtils.getChildElementByTagName(element, METHOD_RESULT_ELEMENT);
		if (resultElement != null) {
			return parseActionResultExposer(resultElement);
		} else {
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
		Expression expression = getExpressionParser().parseExpression(element.getAttribute(EXPRESSION_ATTRIBUTE),
				new ParserContextImpl().eval(RequestContext.class));
		return new EvaluateAction(expression, parseEvaluationResultExposer(element));
	}

	private ExpressionParser getExpressionParser() {
		return getLocalContext().getExpressionParser();
	}

	private ActionResultExposer parseEvaluationResultExposer(Element element) {
		Element resultElement = DomUtils.getChildElementByTagName(element, EVALUATION_RESULT_ELEMENT);
		if (resultElement != null) {
			return parseActionResultExposer(resultElement);
		} else {
			return null;
		}
	}

	private AnnotatedAction parseAnnotatedSetAction(Element element) {
		AnnotatedAction annotated = new AnnotatedAction(parseSetAction(element));
		return parseCommonProperties(element, annotated);
	}

	private Action parseSetAction(Element element) {
		String attributeExpressionString = element.getAttribute(ATTRIBUTE_ATTRIBUTE);
		Expression attributeExpression = getExpressionParser().parseExpression(attributeExpressionString,
				new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression valueExpression = getExpressionParser().parseExpression(element.getAttribute(VALUE_ATTRIBUTE),
				new ParserContextImpl().eval(RequestContext.class));
		return new SetAction(attributeExpression, parseScope(element, ScopeType.REQUEST), valueExpression);
	}

	private ScopeType parseScope(Element element, ScopeType defaultValue) {
		if (element.hasAttribute(SCOPE_ATTRIBUTE) && !element.getAttribute(SCOPE_ATTRIBUTE).equals(DEFAULT_VALUE)) {
			return (ScopeType) fromStringTo(ScopeType.class).execute(element.getAttribute(SCOPE_ATTRIBUTE));
		} else {
			return defaultValue;
		}
	}

	private AttributeMap parseAttributes(Element element) {
		LocalAttributeMap attributes = new LocalAttributeMap();
		List propertyElements = DomUtils.getChildElementsByTagName(element, ATTRIBUTE_ELEMENT);
		for (int i = 0; i < propertyElements.size(); i++) {
			parseAndSetAttribute((Element) propertyElements.get(i), attributes);
		}
		return attributes;
	}

	private void parseAndSetAttribute(Element element, MutableAttributeMap attributes) {
		String name = element.getAttribute(NAME_ATTRIBUTE);
		String value = null;
		if (element.hasAttribute(VALUE_ATTRIBUTE)) {
			value = element.getAttribute(VALUE_ATTRIBUTE);
		} else {
			List valueElements = DomUtils.getChildElementsByTagName(element, VALUE_ELEMENT);
			Assert.state(valueElements.size() == 1, "A property value should be specified for property '" + name + "'");
			value = DomUtils.getTextValue((Element) valueElements.get(0));
		}
		attributes.put(name, convertPropertyValue(element, value));
	}

	private Object convertPropertyValue(Element element, String stringValue) {
		if (element.hasAttribute(TYPE_ATTRIBUTE)) {
			Class targetClass = (Class) fromStringTo(Class.class).execute(element.getAttribute(TYPE_ATTRIBUTE));
			return fromStringTo(targetClass).execute(stringValue);
		} else {
			return stringValue;
		}
	}

	private Transition[] parseIfs(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, IF_ELEMENT);
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			transitions.addAll(Arrays.asList(parseIf((Element) it.next())));
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition[] parseIf(Element element) {
		Transition thenTransition = parseThen(element);
		if (StringUtils.hasText(element.getAttribute(ELSE_ATTRIBUTE))) {
			Transition elseTransition = parseElse(element);
			return new Transition[] { thenTransition, elseTransition };
		} else {
			return new Transition[] { thenTransition };
		}
	}

	private Transition parseThen(Element element) {
		Expression expression = getExpressionParser().parseExpression(element.getAttribute(TEST_ATTRIBUTE),
				new ParserContextImpl().eval(RequestContext.class).expect(Boolean.class));
		TransitionCriteria matchingCriteria = new BooleanExpressionTransitionCriteria(expression);
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute(THEN_ATTRIBUTE));
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null, null);
	}

	private Transition parseElse(Element element) {
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute(ELSE_ATTRIBUTE));
		return getFlowArtifactFactory().createTransition(targetStateResolver, null, null, null);
	}

	private FlowAttributeMapper parseFlowAttributeMapper(Element element) {
		Element mapperElement = DomUtils.getChildElementByTagName(element, ATTRIBUTE_MAPPER_ELEMENT);
		if (mapperElement == null) {
			return null;
		}
		if (StringUtils.hasText(mapperElement.getAttribute(BEAN_ATTRIBUTE))) {
			return (FlowAttributeMapper) getLocalContext().getBeanFactory().getBean(
					mapperElement.getAttribute(BEAN_ATTRIBUTE), FlowAttributeMapper.class);
		} else {
			return new ImmutableFlowAttributeMapper(parseSubflowInputMapper(mapperElement),
					parseSubflowOutputMapper(mapperElement));
		}
	}

	private AttributeMapper parseInputMapper(Element element) {
		Element mapperElement = DomUtils.getChildElementByTagName(element, INPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleInputAttributeMappings(mapper, DomUtils.getChildElementsByTagName(mapperElement,
					INPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement, MutableAttributeMap.class, RequestContext.class);
			return mapper;
		} else {
			return null;
		}
	}

	private AttributeMapper parseSubflowInputMapper(Element element) {
		Element mapperElement = DomUtils.getChildElementByTagName(element, INPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleInputAttributeMappings(mapper, DomUtils.getChildElementsByTagName(mapperElement,
					INPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement, RequestContext.class, MutableAttributeMap.class);
			return mapper;
		} else {
			return null;
		}
	}

	private AttributeMapper parseOutputMapper(Element element) {
		Element mapperElement = DomUtils.getChildElementByTagName(element, OUTPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleOutputAttributeMappings(mapper, DomUtils.getChildElementsByTagName(mapperElement,
					OUTPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement, RequestContext.class, MutableAttributeMap.class);
			return mapper;
		} else {
			return null;
		}
	}

	private AttributeMapper parseSubflowOutputMapper(Element element) {
		Element mapperElement = DomUtils.getChildElementByTagName(element, OUTPUT_MAPPER_ELEMENT);
		if (mapperElement != null) {
			DefaultAttributeMapper mapper = new DefaultAttributeMapper();
			parseSimpleOutputAttributeMappings(mapper, DomUtils.getChildElementsByTagName(mapperElement,
					OUTPUT_ATTRIBUTE_ELEMENT));
			parseMappings(mapper, mapperElement, MutableAttributeMap.class, RequestContext.class);
			return mapper;
		} else {
			return null;
		}
	}

	private void parseMappings(DefaultAttributeMapper mapper, Element element, Class sourceClass, Class targetClass) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		List mappingElements = DomUtils.getChildElementsByTagName(element, MAPPING_ELEMENT);
		for (Iterator it = mappingElements.iterator(); it.hasNext();) {
			Element mappingElement = (Element) it.next();
			Expression source = parser.parseExpression(mappingElement.getAttribute(SOURCE_ATTRIBUTE),
					new ParserContextImpl().eval(sourceClass));
			Expression target = null;
			if (StringUtils.hasText(mappingElement.getAttribute(TARGET_ATTRIBUTE))) {
				target = parser.parseExpression(mappingElement.getAttribute(TARGET_ATTRIBUTE), new ParserContextImpl()
						.eval(targetClass));
			} else if (StringUtils.hasText(mappingElement.getAttribute(TARGET_COLLECTION_ATTRIBUTE))) {
				target = new CollectionAddingExpression(parser.parseExpression(mappingElement
						.getAttribute(TARGET_COLLECTION_ATTRIBUTE), new ParserContextImpl().eval(targetClass)));
			}
			if (getRequired(mappingElement, false)) {
				mapper.addMapping(new RequiredMapping(source, target, parseTypeConverter(mappingElement)));
			} else {
				mapper.addMapping(new Mapping(source, target, parseTypeConverter(mappingElement)));
			}
		}
	}

	// this looks really complicated and possibly wrong
	private void parseSimpleInputAttributeMappings(DefaultAttributeMapper mapper, List elements) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		for (Iterator it = elements.iterator(); it.hasNext();) {
			Element element = (Element) it.next();
			Expression attributeExpression = parser.parseExpression(element.getAttribute(NAME_ATTRIBUTE),
					new ParserContextImpl().eval(RequestContext.class));
			Expression scopedAttributeExpression = new ScopedAttributeExpression(attributeExpression, parseScope(
					element, ScopeType.FLOW));
			if (getRequired(element, false)) {
				mapper.addMapping(new RequiredMapping(attributeExpression, scopedAttributeExpression, null));
			} else {
				mapper.addMapping(new Mapping(attributeExpression, scopedAttributeExpression, null));
			}
		}
	}

	// this looks really complicated and possibly wrong
	private void parseSimpleOutputAttributeMappings(DefaultAttributeMapper mapper, List elements) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		for (Iterator it = elements.iterator(); it.hasNext();) {
			Element element = (Element) it.next();
			Expression attributeExpression = parser.parseExpression(element.getAttribute(NAME_ATTRIBUTE),
					new ParserContextImpl().eval(RequestContext.class));
			Expression scopedAttributeExpression = new ScopedAttributeExpression(attributeExpression, parseScope(
					element, ScopeType.FLOW));
			if (getRequired(element, false)) {
				mapper.addMapping(new RequiredMapping(scopedAttributeExpression, attributeExpression, null));
			} else {
				mapper.addMapping(new Mapping(scopedAttributeExpression, attributeExpression, null));
			}
		}
	}

	private boolean getRequired(Element element, boolean defaultValue) {
		if (StringUtils.hasText(element.getAttribute(REQUIRED_ATTRIBUTE))) {
			return ((Boolean) fromStringTo(Boolean.class).execute(element.getAttribute(REQUIRED_ATTRIBUTE)))
					.booleanValue();
		} else {
			return defaultValue;
		}
	}

	private ConversionExecutor parseTypeConverter(Element element) {
		String from = element.getAttribute(FROM_ATTRIBUTE);
		String to = element.getAttribute(TO_ATTRIBUTE);
		if (StringUtils.hasText(from)) {
			if (StringUtils.hasText(to)) {
				ConversionService service = getLocalContext().getConversionService();
				Class sourceClass = (Class) fromStringTo(Class.class).execute(from);
				Class targetClass = (Class) fromStringTo(Class.class).execute(to);
				return service.getConversionExecutor(sourceClass, targetClass);
			} else {
				throw new IllegalArgumentException("Use of the 'from' attribute requires use of the 'to' attribute");
			}
		} else {
			Assert.isTrue(!StringUtils.hasText(to), "Use of the 'to' attribute requires use of the 'from' attribute");
		}
		return null;
	}

	private FlowExecutionExceptionHandler[] parseExceptionHandlers(Element element) {
		FlowExecutionExceptionHandler[] transitionExecutingHandlers = parseTransitionExecutingExceptionHandlers(element);
		FlowExecutionExceptionHandler[] customHandlers = parseCustomExceptionHandlers(element);
		FlowExecutionExceptionHandler[] exceptionHandlers = new FlowExecutionExceptionHandler[transitionExecutingHandlers.length
				+ customHandlers.length];
		System.arraycopy(transitionExecutingHandlers, 0, exceptionHandlers, 0, transitionExecutingHandlers.length);
		System.arraycopy(customHandlers, 0, exceptionHandlers, transitionExecutingHandlers.length,
				customHandlers.length);
		return exceptionHandlers;
	}

	private FlowExecutionExceptionHandler[] parseTransitionExecutingExceptionHandlers(Element element) {
		List transitionElements = Collections.EMPTY_LIST;
		if (isFlowElement(element)) {
			Element globalTransitionsElement = DomUtils.getChildElementByTagName(element, GLOBAL_TRANSITIONS_ELEMENT);
			if (globalTransitionsElement != null) {
				transitionElements = DomUtils.getChildElementsByTagName(globalTransitionsElement, TRANSITION_ELEMENT);
			}
		} else {
			transitionElements = DomUtils.getChildElementsByTagName(element, TRANSITION_ELEMENT);
		}
		List exceptionHandlers = new LinkedList();
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element) it.next();
			if (StringUtils.hasText(transitionElement.getAttribute(ON_EXCEPTION_ATTRIBUTE))) {
				exceptionHandlers.add(parseTransitionExecutingExceptionHandler(transitionElement));
			}
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseTransitionExecutingExceptionHandler(Element element) {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		Class exceptionClass = (Class) fromStringTo(Class.class).execute(element.getAttribute(ON_EXCEPTION_ATTRIBUTE));
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute(TO_ATTRIBUTE));
		handler.add(exceptionClass, targetStateResolver);
		handler.getActionList().addAll(parseAnnotatedActions(element));
		return handler;
	}

	private FlowExecutionExceptionHandler[] parseCustomExceptionHandlers(Element element) {
		List exceptionHandlers = new LinkedList();
		List handlerElements = DomUtils.getChildElementsByTagName(element, EXCEPTION_HANDLER_ELEMENT);
		for (int i = 0; i < handlerElements.size(); i++) {
			Element handlerElement = (Element) handlerElements.get(i);
			exceptionHandlers.add(parseCustomExceptionHandler(handlerElement));
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseCustomExceptionHandler(Element element) {
		return (FlowExecutionExceptionHandler) getLocalContext().getBeanFactory().getBean(
				element.getAttribute(BEAN_ATTRIBUTE), FlowExecutionExceptionHandler.class);
	}

	private ConversionExecutor fromStringTo(Class targetType) throws ConversionException {
		return getLocalContext().getConversionService().getConversionExecutor(String.class, targetType);
	}

	private static class ViewInfo {

		private ViewFactory viewFactory;

		private Boolean redirect;

		public ViewInfo(ViewFactory viewFactory, Boolean redirect) {
			this.viewFactory = viewFactory;
			this.redirect = redirect;
		}
	}

	private static class ScopedAttributeExpression implements Expression {

		private Expression scopeMapExpression;

		private ScopeType scopeType;

		public ScopedAttributeExpression(Expression scopeMapExpression, ScopeType scopeType) {
			this.scopeMapExpression = scopeMapExpression;
			this.scopeType = scopeType;
		}

		public Object getValue(Object target) throws EvaluationException {
			MutableAttributeMap scopeMap = scopeType.getScope((RequestContext) target);
			return scopeMapExpression.getValue(scopeMap);
		}

		public void setValue(Object target, Object value) throws EvaluationException {
			MutableAttributeMap scopeMap = scopeType.getScope((RequestContext) target);
			scopeMapExpression.setValue(scopeMap, value);
		}
	}

	private static class FlowRelativeResourceLoader implements ResourceLoader {
		private Resource resource;

		public FlowRelativeResourceLoader(Resource resource) {
			this.resource = resource;
		}

		public ClassLoader getClassLoader() {
			return resource.getClass().getClassLoader();
		}

		public Resource getResource(String location) {
			try {
				return resource.createRelative(location);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("location", resource).toString();
	}
}