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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.RuntimeBindingConversionExecutor;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
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
import org.springframework.webflow.action.RenderAction;
import org.springframework.webflow.action.SetAction;
import org.springframework.webflow.action.ViewFactoryActionAdapter;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.FlowVariable;
import org.springframework.webflow.engine.SubflowAttributeMapper;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.VariableValueFactory;
import org.springframework.webflow.engine.ViewVariable;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.support.ActionExecutingViewFactory;
import org.springframework.webflow.engine.support.BeanFactoryVariableValueFactory;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.engine.support.GenericSubflowAttributeMapper;
import org.springframework.webflow.engine.support.TransitionCriteriaChain;
import org.springframework.webflow.engine.support.TransitionExecutingFlowExecutionExceptionHandler;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.security.SecurityRule;
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
 *                               http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd&quot;&gt;
 *         &lt;!-- Define your states here --&gt;
 *     &lt;/flow&gt;
 * </pre>
 * 
 * <p>
 * Consult the <a href="http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd">web flow XML schema</a>
 * for more information on the XML-based flow definition format.
 * <p>
 * This builder will setup a flow-local bean factory for the flow being constructed. That flow-local bean factory will
 * be populated with XML bean definitions contained in files referenced using the "import" element. The flow-local bean
 * factory will use the bean factory of this flow builder as a parent. As such, the flow can access artifacts in either
 * its flow-local bean factory or in the parent bean factory hierarchy, e.g. the bean factory of the dispatcher.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 * @author Scott Andrews
 */
public class XmlFlowBuilder extends AbstractFlowBuilder implements ResourceHolder {

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
		Flow flow = parseFlow(getDocumentElement());
		flow.setBeanFactory(getLocalContext().getBeanFactory());
		flow.setResourceLoader(getLocalContext().getResourceLoader());
		return flow;
	}

	public void buildVariables() throws FlowBuilderException {
		parseAndAddFlowVariables(getDocumentElement(), getFlow());
	}

	public void buildInputMapper() throws FlowBuilderException {
		AttributeMapper inputMapper = parseFlowInputMapper(getDocumentElement());
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
		AttributeMapper outputMapper = parseFlowOutputMapper(getDocumentElement());
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
		if (!isRootFlowElement(flowElement)) {
			throw new IllegalArgumentException("This is not the root 'flow' element");
		}
		String flowId = getLocalContext().getFlowId();
		AttributeMap externallyAssignedAttributes = getLocalContext().getFlowAttributes();
		MutableAttributeMap flowAttributes = parseMetaAttributes(flowElement);
		parseAndSetPersistenceContextAttribute(flowElement, flowAttributes);
		parseAndSetSecuredAttribute(flowElement, flowAttributes);
		return getFlowArtifactFactory().createFlow(flowId, flowAttributes.union(externallyAssignedAttributes));
	}

	private boolean isRootFlowElement(Element flowElement) {
		return DomUtils.nodeNameEquals(flowElement, "flow");
	}

	private void parseAndSetPersistenceContextAttribute(Element flowElement, MutableAttributeMap flowAttributes) {
		Element element = DomUtils.getChildElementByTagName(flowElement, "persistence-context");
		if (element != null) {
			flowAttributes.put("persistenceContext", Boolean.TRUE);
		}
	}

	private void initLocalFlowContext(Element flowElement) {
		List importElements = DomUtils.getChildElementsByTagName(flowElement, "bean-import");
		Resource[] resources = new Resource[importElements.size()];
		for (int i = 0; i < importElements.size(); i++) {
			Element importElement = (Element) importElements.get(i);
			try {
				resources[i] = getResource().createRelative(importElement.getAttribute("resource"));
			} catch (IOException e) {
				throw new FlowBuilderException("Could not access flow-relative artifact resource '"
						+ importElement.getAttribute("resource") + "'", e);
			}
		}
		this.localFlowBuilderContext = new LocalFlowBuilderContext(getContext(),
				createFlowApplicationContext(resources));
	}

	private GenericApplicationContext createFlowApplicationContext(Resource[] resources) {
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
		AnnotationConfigUtils.registerAnnotationConfigProcessors(flowContext);
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
		List varElements = DomUtils.getChildElementsByTagName(flowElement, "var");
		for (Iterator it = varElements.iterator(); it.hasNext();) {
			flow.addVariable(parseFlowVariable((Element) it.next()));
		}
	}

	private FlowVariable parseFlowVariable(Element element) {
		Class clazz = (Class) fromStringTo(Class.class).execute(element.getAttribute("class"));
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz,
				(AutowireCapableBeanFactory) getFlow().getBeanFactory());
		ScopeType scope = parseScopeAttribute(element, ScopeType.FLOW);
		if (!(scope == ScopeType.FLOW || scope == ScopeType.CONVERSATION)) {
			throw new IllegalArgumentException("Only " + ScopeType.FLOW + " or " + ScopeType.CONVERSATION
					+ " scope is allowed for flow variables");
		}
		return new FlowVariable(element.getAttribute("name"), valueFactory, scope == ScopeType.FLOW ? true : false);
	}

	private ScopeType parseScopeAttribute(Element element, ScopeType defaultScope) {
		if (element.hasAttribute("scope")) {
			return (ScopeType) fromStringTo(ScopeType.class).execute(element.getAttribute("scope"));
		} else {
			return defaultScope;
		}
	}

	private AttributeMapper parseFlowInputMapper(Element element) {
		Collection inputs = DomUtils.getChildElementsByTagName(element, "input");
		if (inputs.size() == 0) {
			return null;
		}
		DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();
		for (Iterator it = inputs.iterator(); it.hasNext();) {
			inputMapper.addMapping(parseFlowInputMapping((Element) it.next()));
		}
		return inputMapper;
	}

	private Mapping parseFlowInputMapping(Element element) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = element.getAttribute("name");
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		return new Mapping(source, target, parseMappingConversionExecutor(element), parseMappingRequired(element));
	}

	private AttributeMapper parseFlowOutputMapper(Element element) {
		Collection inputs = DomUtils.getChildElementsByTagName(element, "output");
		if (inputs.size() == 0) {
			return null;
		}
		DefaultAttributeMapper outputMapper = new DefaultAttributeMapper();
		for (Iterator it = inputs.iterator(); it.hasNext();) {
			outputMapper.addMapping(parseFlowOutputMapping((Element) it.next()));
		}
		return outputMapper;
	}

	private Mapping parseFlowOutputMapping(Element element) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = element.getAttribute("name");
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		Expression target = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		return new Mapping(source, target, parseMappingConversionExecutor(element), parseMappingRequired(element));
	}

	private ConversionExecutor parseMappingConversionExecutor(Element element) {
		if (element.hasAttribute("type")) {
			Class type = (Class) fromStringTo(Class.class).execute(element.getAttribute("type"));
			return new RuntimeBindingConversionExecutor(type, getConversionService());
		} else {
			return null;
		}
	}

	private boolean parseMappingRequired(Element element) {
		if (element.hasAttribute("required")) {
			return ((Boolean) fromStringTo(Boolean.class).execute(element.getAttribute("required"))).booleanValue();
		} else {
			return false;
		}
	}

	private void parseAndAddStartActions(Element element, Flow flow) {
		Element startElement = DomUtils.getChildElementByTagName(element, "on-start");
		if (startElement != null) {
			flow.getStartActionList().addAll(parseActions(startElement));
		}
	}

	private void parseAndAddEndActions(Element element, Flow flow) {
		Element endElement = DomUtils.getChildElementByTagName(element, "on-end");
		if (endElement != null) {
			flow.getEndActionList().addAll(parseActions(endElement));
		}
	}

	private void parseAndAddGlobalTransitions(Element element, Flow flow) {
		Element globalTransitionsElement = DomUtils.getChildElementByTagName(element, "global-transitions");
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
				if (DomUtils.nodeNameEquals(stateElement, "action-state")) {
					parseAndAddActionState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, "view-state")) {
					parseAndAddViewState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, "decision-state")) {
					parseAndAddDecisionState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, "subflow-state")) {
					parseAndAddSubflowState(stateElement, flow);
				} else if (DomUtils.nodeNameEquals(stateElement, "end-state")) {
					parseAndAddEndState(stateElement, flow);
				}
			}
		}
		parseAndSetStartState(flowElement, flow);
	}

	private void parseAndSetStartState(Element element, Flow flow) {
		String startStateId = getStartStateId(element);
		if (StringUtils.hasText(startStateId)) {
			flow.setStartState(startStateId);
		}
	}

	private String getStartStateId(Element element) {
		String startState = "start-state";
		if (element.hasAttribute(startState)) {
			return element.getAttribute(startState);
		} else {
			return null;
		}
	}

	private void parseAndAddActionState(Element element, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(element);
		parseAndSetSecuredAttribute(element, attributes);
		getFlowArtifactFactory().createActionState(parseId(element), flow, parseEntryActions(element),
				parseActions(element), parseTransitions(element), parseExceptionHandlers(element),
				parseExitActions(element), attributes);
	}

	private void parseAndAddViewState(Element element, Flow flow) {
		ViewFactory viewFactory = parseViewFactory(element, false);
		Boolean redirect = null;
		if (element.hasAttribute("redirect")) {
			redirect = (Boolean) fromStringTo(Boolean.class).execute(element.getAttribute("redirect"));
		}
		boolean popup = false;
		if (element.hasAttribute("popup")) {
			popup = ((Boolean) fromStringTo(Boolean.class).execute(element.getAttribute("popup"))).booleanValue();
		}
		MutableAttributeMap attributes = parseMetaAttributes(element);
		parseAndSetSecuredAttribute(element, attributes);
		getFlowArtifactFactory().createViewState(parseId(element), flow, parseViewVariables(element),
				parseEntryActions(element), viewFactory, redirect, popup, parseRenderActions(element),
				parseTransitions(element), parseExceptionHandlers(element), parseExitActions(element), attributes);
	}

	private void parseAndAddDecisionState(Element element, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(element);
		parseAndSetSecuredAttribute(element, attributes);
		getFlowArtifactFactory().createDecisionState(parseId(element), flow, parseEntryActions(element),
				parseIfs(element), parseExceptionHandlers(element), parseExitActions(element), attributes);
	}

	private void parseAndAddSubflowState(Element element, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(element);
		parseAndSetSecuredAttribute(element, attributes);
		getFlowArtifactFactory().createSubflowState(parseId(element), flow, parseEntryActions(element),
				parseSubflowExpression(element), parseSubflowAttributeMapper(element), parseTransitions(element),
				parseExceptionHandlers(element), parseExitActions(element), attributes);
	}

	private Expression parseSubflowExpression(Element element) {
		String subflow = element.getAttribute("subflow");
		Expression subflowId = getExpressionParser().parseExpression(subflow,
				new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
		return new SubflowExpression(subflowId, getLocalContext().getFlowDefinitionLocator());
	}

	private void parseAndAddEndState(Element element, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(element);
		if (element.hasAttribute("commit")) {
			attributes.put("commit", fromStringTo(Boolean.class).execute(element.getAttribute("commit")));
		}
		parseAndSetSecuredAttribute(element, attributes);
		getFlowArtifactFactory().createEndState(parseId(element), flow, parseEntryActions(element),
				new ViewFactoryActionAdapter(parseViewFactory(element, true)), parseFlowOutputMapper(element),
				parseExceptionHandlers(element), attributes);
	}

	private String parseId(Element element) {
		return element.getAttribute("id");
	}

	private ViewVariable[] parseViewVariables(Element viewStateElement) {
		List varElements = DomUtils.getChildElementsByTagName(viewStateElement, "var");
		List variables = new ArrayList(varElements.size());
		for (Iterator it = varElements.iterator(); it.hasNext();) {
			variables.add(parseViewVariable((Element) it.next()));
		}
		return (ViewVariable[]) variables.toArray(new ViewVariable[variables.size()]);
	}

	private ViewVariable parseViewVariable(Element element) {
		Class clazz = (Class) fromStringTo(Class.class).execute(element.getAttribute("class"));
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz,
				(AutowireCapableBeanFactory) getFlow().getBeanFactory());
		return new ViewVariable(element.getAttribute("name"), valueFactory);
	}

	private Action[] parseEntryActions(Element element) {
		Element entryActionsElement = DomUtils.getChildElementByTagName(element, "on-entry");
		if (entryActionsElement != null) {
			return parseActions(entryActionsElement);
		} else {
			return null;
		}
	}

	private ViewFactory parseViewFactory(Element element, boolean endState) {
		String encodedView = element.getAttribute("view");
		if (!StringUtils.hasText(encodedView)) {
			if (endState) {
				return null;
			} else {
				encodedView = getLocalContext().getViewFactoryCreator().getViewIdByConvention(parseId(element));
				Expression viewName = getExpressionParser().parseExpression(encodedView,
						new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
				return getLocalContext().getViewFactoryCreator().createViewFactory(viewName,
						getLocalContext().getResourceLoader());
			}
		} else if (encodedView.startsWith("externalRedirect:")) {
			String encodedUrl = encodedView.substring("externalRedirect:".length());
			Expression externalUrl = getExpressionParser().parseExpression(encodedUrl,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return new ActionExecutingViewFactory(new ExternalRedirectAction(externalUrl));
		} else if (encodedView.startsWith("flowRedirect:")) {
			String flowRedirect = encodedView.substring("flowRedirect:".length());
			Expression expression = getExpressionParser().parseExpression(flowRedirect,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return new ActionExecutingViewFactory(new FlowDefinitionRedirectAction(expression));
		} else {
			Expression viewId = getExpressionParser().parseExpression(encodedView,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return getLocalContext().getViewFactoryCreator().createViewFactory(viewId,
					getLocalContext().getResourceLoader());
		}
	}

	private Action[] parseRenderActions(Element element) {
		Element renderActionsElement = DomUtils.getChildElementByTagName(element, "on-render");
		if (renderActionsElement != null) {
			return parseActions(renderActionsElement);
		} else {
			return null;
		}
	}

	private Action[] parseExitActions(Element element) {
		Element exitActionsElement = DomUtils.getChildElementByTagName(element, "on-exit");
		if (exitActionsElement != null) {
			return parseActions(exitActionsElement);
		} else {
			return null;
		}
	}

	private Transition[] parseTransitions(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, "transition");
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element) it.next();
			if (!StringUtils.hasText(transitionElement.getAttribute("on-exception"))) {
				transitions.add(parseTransition(transitionElement));
			}
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition parseTransition(Element element) {
		TransitionCriteria matchingCriteria = (TransitionCriteria) fromStringTo(TransitionCriteria.class).execute(
				element.getAttribute("on"));
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute("to"));
		TransitionCriteria executionCriteria = TransitionCriteriaChain.criteriaChainFor(parseActions(element));
		MutableAttributeMap attributes = parseMetaAttributes(element);
		parseAndSetSecuredAttribute(element, attributes);
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, executionCriteria,
				attributes);
	}

	private Action[] parseActions(Element element) {
		List actions = new LinkedList();
		NodeList childNodeList = element.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (!(childNode instanceof Element)) {
				continue;
			}
			if (DomUtils.nodeNameEquals(childNode, "evaluate")) {
				actions.add(parseEvaluateAction((Element) childNode));
			} else if (DomUtils.nodeNameEquals(childNode, "render")) {
				actions.add(parseRenderAction((Element) childNode));
			} else if (DomUtils.nodeNameEquals(childNode, "set")) {
				actions.add(parseSetAction((Element) childNode));
			}
		}
		return (Action[]) actions.toArray(new Action[actions.size()]);
	}

	private Action parseEvaluateAction(Element element) {
		String expressionString = element.getAttribute("expression");
		Expression expression = getExpressionParser().parseExpression(expressionString,
				new ParserContextImpl().eval(RequestContext.class));
		return new EvaluateAction(expression, parseEvaluationActionResultExposer(element));
	}

	private ActionResultExposer parseEvaluationActionResultExposer(Element element) {
		if (element.hasAttribute("result")) {
			String resultExpressionString = element.getAttribute("result");
			Expression resultExpression = getExpressionParser().parseExpression(resultExpressionString,
					new ParserContextImpl().eval(RequestContext.class));
			Class expectedResultType = null;
			if (element.hasAttribute("result-type")) {
				expectedResultType = (Class) fromStringTo(Class.class).execute(element.getAttribute("result-type"));
			}
			return new ActionResultExposer(resultExpression, expectedResultType, getConversionService());
		} else {
			return null;
		}
	}

	private Action parseRenderAction(Element element) {
		String[] fragmentExpressionStrings = StringUtils.commaDelimitedListToStringArray(element
				.getAttribute("fragments"));
		fragmentExpressionStrings = StringUtils.trimArrayElements(fragmentExpressionStrings);
		ExpressionParser parser = getExpressionParser();
		ParserContext context = new ParserContextImpl().template().eval(RequestContext.class).expect(String.class);
		Expression[] fragments = new Expression[fragmentExpressionStrings.length];
		for (int i = 0; i < fragmentExpressionStrings.length; i++) {
			String fragment = fragmentExpressionStrings[i];
			fragments[i] = parser.parseExpression(fragment, context);
		}
		return new RenderAction(fragments);
	}

	private Action parseSetAction(Element element) {
		Expression nameExpression = getExpressionParser().parseExpression(element.getAttribute("name"),
				new ParserContextImpl().eval(RequestContext.class));
		Expression valueExpression = getExpressionParser().parseExpression(element.getAttribute("value"),
				new ParserContextImpl().eval(RequestContext.class));
		Class expectedType = null;
		if (element.hasAttribute("type")) {
			expectedType = (Class) fromStringTo(Class.class).execute(element.getAttribute("type"));
		}
		return new SetAction(nameExpression, valueExpression, expectedType, getConversionService());
	}

	private MutableAttributeMap parseMetaAttributes(Element element) {
		LocalAttributeMap attributes = new LocalAttributeMap();
		List propertyElements = DomUtils.getChildElementsByTagName(element, "attribute");
		for (int i = 0; i < propertyElements.size(); i++) {
			parseAndSetMetaAttribute((Element) propertyElements.get(i), attributes);
		}
		return attributes;
	}

	private void parseAndSetMetaAttribute(Element element, MutableAttributeMap attributes) {
		String name = element.getAttribute("name");
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			List valueElements = DomUtils.getChildElementsByTagName(element, "value");
			Assert.state(valueElements.size() == 1, "A property value should be specified for property '" + name + "'");
			value = DomUtils.getTextValue((Element) valueElements.get(0));
		}
		attributes.put(name, convertAttributeValueIfNecessary(element, value));
	}

	private Object convertAttributeValueIfNecessary(Element element, String stringValue) {
		if (element.hasAttribute("type")) {
			Class targetClass = (Class) fromStringTo(Class.class).execute(element.getAttribute("type"));
			return fromStringTo(targetClass).execute(stringValue);
		} else {
			return stringValue;
		}
	}

	private Transition[] parseIfs(Element element) {
		List transitions = new LinkedList();
		List transitionElements = DomUtils.getChildElementsByTagName(element, "if");
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			transitions.addAll(Arrays.asList(parseIf((Element) it.next())));
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition[] parseIf(Element element) {
		Transition thenTransition = parseThen(element);
		if (StringUtils.hasText(element.getAttribute("else"))) {
			Transition elseTransition = parseElse(element);
			return new Transition[] { thenTransition, elseTransition };
		} else {
			return new Transition[] { thenTransition };
		}
	}

	private Transition parseThen(Element element) {
		Expression expression = getExpressionParser().parseExpression(element.getAttribute("test"),
				new ParserContextImpl().eval(RequestContext.class).expect(Boolean.class));
		TransitionCriteria matchingCriteria = new DefaultTransitionCriteria(expression);
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute("then"));
		return getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null, null);
	}

	private Transition parseElse(Element element) {
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute("else"));
		return getFlowArtifactFactory().createTransition(targetStateResolver, null, null, null);
	}

	private SubflowAttributeMapper parseSubflowAttributeMapper(Element element) {
		if (element.hasAttribute("subflow-attribute-mapper")) {
			String attributeMapperBeanId = element.getAttribute("subflow-attribute-mapper");
			return (SubflowAttributeMapper) getLocalContext().getBeanFactory().getBean(attributeMapperBeanId,
					SubflowAttributeMapper.class);
		} else {
			AttributeMapper inputMapper = parseSubflowInputMapper(element);
			AttributeMapper outputMapper = parseSubflowOutputMapper(element);
			return new GenericSubflowAttributeMapper(inputMapper, outputMapper);
		}
	}

	private AttributeMapper parseSubflowInputMapper(Element element) {
		Collection inputs = DomUtils.getChildElementsByTagName(element, "input");
		if (inputs.size() == 0) {
			return null;
		}
		DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();
		for (Iterator it = inputs.iterator(); it.hasNext();) {
			inputMapper.addMapping(parseSubflowInputMapping((Element) it.next()));
		}
		return inputMapper;
	}

	private Mapping parseSubflowInputMapping(Element element) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = element.getAttribute("name");
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		Expression target = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		return new Mapping(source, target, parseMappingConversionExecutor(element), parseMappingRequired(element));
	}

	private AttributeMapper parseSubflowOutputMapper(Element element) {
		Collection inputs = DomUtils.getChildElementsByTagName(element, "output");
		if (inputs.size() == 0) {
			return null;
		}
		DefaultAttributeMapper outputMapper = new DefaultAttributeMapper();
		for (Iterator it = inputs.iterator(); it.hasNext();) {
			outputMapper.addMapping(parseSubflowOutputMapping((Element) it.next()));
		}
		return outputMapper;
	}

	private Mapping parseSubflowOutputMapping(Element element) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = element.getAttribute("name");
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		return new Mapping(source, target, parseMappingConversionExecutor(element), parseMappingRequired(element));
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
		if (isRootFlowElement(element)) {
			Element globalTransitionsElement = DomUtils.getChildElementByTagName(element, "global-transitions");
			if (globalTransitionsElement != null) {
				transitionElements = DomUtils.getChildElementsByTagName(globalTransitionsElement, "transition");
			}
		} else {
			transitionElements = DomUtils.getChildElementsByTagName(element, "transition");
		}
		List exceptionHandlers = new LinkedList();
		for (Iterator it = transitionElements.iterator(); it.hasNext();) {
			Element transitionElement = (Element) it.next();
			if (StringUtils.hasText(transitionElement.getAttribute("on-exception"))) {
				exceptionHandlers.add(parseTransitionExecutingExceptionHandler(transitionElement));
			}
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseTransitionExecutingExceptionHandler(Element element) {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		Class exceptionClass = (Class) fromStringTo(Class.class).execute(element.getAttribute("on-exception"));
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(element.getAttribute("to"));
		handler.add(exceptionClass, targetStateResolver);
		handler.getActionList().addAll(parseActions(element));
		return handler;
	}

	private FlowExecutionExceptionHandler[] parseCustomExceptionHandlers(Element element) {
		List exceptionHandlers = new LinkedList();
		List handlerElements = DomUtils.getChildElementsByTagName(element, "exception-handler");
		for (int i = 0; i < handlerElements.size(); i++) {
			Element handlerElement = (Element) handlerElements.get(i);
			exceptionHandlers.add(parseCustomExceptionHandler(handlerElement));
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler parseCustomExceptionHandler(Element element) {
		return (FlowExecutionExceptionHandler) getLocalContext().getBeanFactory().getBean(element.getAttribute("bean"),
				FlowExecutionExceptionHandler.class);
	}

	private void parseAndSetSecuredAttribute(Element element, MutableAttributeMap attributes) {
		Element secured = DomUtils.getChildElementByTagName(element, "secured");
		if (secured != null) {
			SecurityRule rule = new SecurityRule();
			rule.setAttributes(SecurityRule.convertAttributesFromCommaSeparatedString(secured
					.getAttribute("attributes")));
			String comparisonType = secured.getAttribute("match");
			if ("any".equals(comparisonType)) {
				rule.setComparisonType(SecurityRule.COMPARISON_ANY);
			} else if ("all".equals(comparisonType)) {
				rule.setComparisonType(SecurityRule.COMPARISON_ALL);
			} else {
				// default to any
				rule.setComparisonType(SecurityRule.COMPARISON_ANY);
			}
			attributes.put(SecurityRule.SECURITY_ATTRIBUTE_NAME, rule);
		}
	}

	private ConversionExecutor fromStringTo(Class targetType) throws ConversionException {
		return getConversionService().getConversionExecutor(String.class, targetType);
	}

	private ExpressionParser getExpressionParser() {
		return getLocalContext().getExpressionParser();
	}

	private ConversionService getConversionService() {
		return getLocalContext().getConversionService();
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

	private static class SubflowExpression implements Expression {

		private Expression subflowId;

		private FlowDefinitionLocator flowDefinitionLocator;

		public SubflowExpression(Expression subflowId, FlowDefinitionLocator flowDefinitionLocator) {
			this.subflowId = subflowId;
			this.flowDefinitionLocator = flowDefinitionLocator;
		}

		public Object getValue(Object context) throws EvaluationException {
			String subflowId = (String) this.subflowId.getValue(context);
			return flowDefinitionLocator.getFlowDefinition(subflowId);
		}

		public void setValue(Object context, Object value) throws EvaluationException {
			throw new UnsupportedOperationException("Cannot set a subflow expression");
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("location", resource).toString();
	}
}