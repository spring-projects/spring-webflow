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
package org.springframework.webflow.engine.builder.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.service.RuntimeBindingConversionExecutor;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestScope;
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
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.FlowVariable;
import org.springframework.webflow.engine.History;
import org.springframework.webflow.engine.SubflowAttributeMapper;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.VariableValueFactory;
import org.springframework.webflow.engine.ViewVariable;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.engine.model.AbstractActionModel;
import org.springframework.webflow.engine.model.AbstractMappingModel;
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
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
import org.springframework.webflow.engine.support.ActionExecutingViewFactory;
import org.springframework.webflow.engine.support.BeanFactoryVariableValueFactory;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.engine.support.GenericSubflowAttributeMapper;
import org.springframework.webflow.engine.support.TransitionCriteriaChain;
import org.springframework.webflow.engine.support.TransitionExecutingFlowExecutionExceptionHandler;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.AnnotatedAction;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.scope.ConversationScope;
import org.springframework.webflow.scope.FlashScope;
import org.springframework.webflow.scope.FlowScope;
import org.springframework.webflow.scope.ViewScope;
import org.springframework.webflow.security.SecurityRule;

/**
 * Builds a runtime {@link Flow} definition object from a {@link FlowModel}.
 * 
 * @author Keith Donald
 */
public class FlowModelFlowBuilder extends AbstractFlowBuilder {

	private FlowModelHolder flowModelHolder;

	private FlowModel flowModel;

	private LocalFlowBuilderContext localFlowBuilderContext;

	/**
	 * Creates a flow builder that can build a {@link Flow} from a {@link FlowModel}.
	 * @param flowModelHolder the flow model holder
	 */
	public FlowModelFlowBuilder(FlowModelHolder flowModelHolder) {
		this.flowModelHolder = flowModelHolder;
	}

	/**
	 * Initialize this builder. This could cause the builder to open a stream to an externalized resource representing
	 * the flow definition, for example.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	protected void doInit() throws FlowBuilderException {
		try {
			flowModel = flowModelHolder.getFlowModel();
		} catch (FlowModelBuilderException e) {
			throw new FlowBuilderException("Unable to get the model for this flow", e);
		}
		if ("true".equals(flowModel.getAbstract())) {
			throw new FlowBuilderException("Abstract flow models cannot be instantiated.");
		}
		initLocalFlowContext();
	}

	/**
	 * Builds any variables initialized by the flow when it starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildVariables() throws FlowBuilderException {
		if (flowModel.getVars() != null) {
			for (Iterator it = flowModel.getVars().iterator(); it.hasNext();) {
				getFlow().addVariable(parseFlowVariable((VarModel) it.next()));
			}
		}
	}

	/**
	 * Builds the input mapper responsible for mapping flow input on start.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildInputMapper() throws FlowBuilderException {
		getFlow().setInputMapper(parseFlowInputMapper(flowModel.getInputs()));
	}

	/**
	 * Builds any start actions to execute when the flow starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStartActions() throws FlowBuilderException {
		getFlow().getStartActionList().addAll(parseActions(flowModel.getOnStartActions()));
	}

	/**
	 * Builds the states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStates() throws FlowBuilderException {
		if (flowModel.getStates() == null) {
			throw new FlowBuilderException("At least one state is required to build a Flow");
		}
		for (Iterator it = flowModel.getStates().iterator(); it.hasNext();) {
			AbstractStateModel state = (AbstractStateModel) it.next();
			if (state instanceof ActionStateModel) {
				parseAndAddActionState((ActionStateModel) state, getFlow());
			} else if (state instanceof ViewStateModel) {
				parseAndAddViewState((ViewStateModel) state, getFlow());
			} else if (state instanceof DecisionStateModel) {
				parseAndAddDecisionState((DecisionStateModel) state, getFlow());
			} else if (state instanceof SubflowStateModel) {
				parseAndAddSubflowState((SubflowStateModel) state, getFlow());
			} else if (state instanceof EndStateModel) {
				parseAndAddEndState((EndStateModel) state, getFlow());
			}
		}
		if (flowModel.getStartStateId() != null) {
			getFlow().setStartState(flowModel.getStartStateId());
		}
	}

	/**
	 * Builds any transitions shared by all states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildGlobalTransitions() throws FlowBuilderException {
		getFlow().getGlobalTransitionSet().addAll(parseTransitions(flowModel.getGlobalTransitions()));
	}

	/**
	 * Builds any end actions to execute when the flow ends.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildEndActions() throws FlowBuilderException {
		getFlow().getEndActionList().addAll(parseActions(flowModel.getOnEndActions()));
	}

	/**
	 * Builds the output mapper responsible for mapping flow output on end.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildOutputMapper() throws FlowBuilderException {
		if (flowModel.getOutputs() != null) {
			getFlow().setOutputMapper(parseFlowOutputMapper(flowModel.getOutputs()));
		}
	}

	/**
	 * Creates and adds all exception handlers to the flow built by this builder.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public void buildExceptionHandlers() throws FlowBuilderException {
		getFlow().getExceptionHandlerSet().addAll(
				parseExceptionHandlers(flowModel.getExceptionHandlers(), flowModel.getGlobalTransitions()));
	}

	public boolean hasFlowChanged() {
		return flowModelHolder.hasFlowModelChanged();
	}

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow construction process should start with another
	 * call to the {@link #init(FlowBuilderContext)} method.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	protected void doDispose() throws FlowBuilderException {
		flowModel = null;
		setLocalContext(null);
	}

	// subclassing hooks

	protected Flow createFlow() {
		Flow flow = parseFlow(flowModel);
		flow.setApplicationContext(getLocalContext().getApplicationContext());
		return flow;
	}

	protected FlowModel getFlowModel() {
		return flowModel;
	}

	protected LocalFlowBuilderContext getLocalContext() {
		return localFlowBuilderContext;
	}

	protected void setLocalContext(LocalFlowBuilderContext localFlowBuilderContext) {
		this.localFlowBuilderContext = localFlowBuilderContext;
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

	// internal helpers

	private void initLocalFlowContext() {
		Resource[] contextResources = parseContextResources(getFlowModel().getBeanImports());
		GenericApplicationContext flowContext = createFlowApplicationContext(contextResources);
		setLocalContext(new LocalFlowBuilderContext(getContext(), flowContext));
	}

	private Resource[] parseContextResources(List beanImports) {
		if (beanImports != null && !beanImports.isEmpty()) {
			Resource flowResource = flowModelHolder.getFlowModelResource();
			if (flowResource == null) {
				throw new FlowBuilderException("The FlowModel must be Resource in order to load bean-imports");
			}
			List resources = new ArrayList(beanImports.size());
			for (Iterator it = getFlowModel().getBeanImports().iterator(); it.hasNext();) {
				BeanImportModel beanImport = (BeanImportModel) it.next();
				try {
					resources.add(flowResource.createRelative(beanImport.getResource()));
				} catch (IOException e) {
					throw new FlowBuilderException("Could not access flow-relative artifact resource '"
							+ beanImport.getResource() + "'", e);
				}
			}
			return (Resource[]) resources.toArray(new Resource[resources.size()]);
		} else {
			return new Resource[0];
		}
	}

	private GenericApplicationContext createFlowApplicationContext(Resource[] resources) {
		ApplicationContext parent = getContext().getApplicationContext();
		GenericApplicationContext flowContext;
		if (parent instanceof WebApplicationContext) {
			GenericWebApplicationContext webContext = new GenericWebApplicationContext();
			webContext.setServletContext(((WebApplicationContext) parent).getServletContext());
			flowContext = webContext;
		} else {
			flowContext = new GenericApplicationContext();
		}
		flowContext.setParent(parent);
		flowContext.getBeanFactory().registerScope("request", new RequestScope());
		flowContext.getBeanFactory().registerScope("flash", new FlashScope());
		flowContext.getBeanFactory().registerScope("view", new ViewScope());
		flowContext.getBeanFactory().registerScope("flow", new FlowScope());
		flowContext.getBeanFactory().registerScope("conversation", new ConversationScope());
		Resource flowResource = flowModelHolder.getFlowModelResource();
		if (flowResource != null) {
			flowContext.setResourceLoader(new FlowRelativeResourceLoader(flowResource));
		}
		if (JdkVersion.isAtLeastJava15()) {
			AnnotationConfigUtils.registerAnnotationConfigProcessors(flowContext);
		}
		new XmlBeanDefinitionReader(flowContext).loadBeanDefinitions(resources);
		registerFlowBeans(flowContext.getBeanFactory());
		registerMessageSource(flowContext, flowResource);
		flowContext.refresh();
		return flowContext;
	}

	private void registerMessageSource(GenericApplicationContext flowContext, Resource flowResource) {
		boolean localMessageSourcePresent = flowContext
				.containsLocalBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME);
		if (flowResource != null && !localMessageSourcePresent) {
			Resource messageBundle;
			try {
				messageBundle = flowResource.createRelative("messages.properties");
			} catch (IOException e) {
				messageBundle = null;
			}
			if (messageBundle != null && messageBundle.exists()) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder
						.rootBeanDefinition(ReloadableResourceBundleMessageSource.class);
				builder.addPropertyValue("basename", "messages");
				flowContext.registerBeanDefinition(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, builder
						.getBeanDefinition());
			}
		}
	}

	private Flow parseFlow(FlowModel flow) {
		String flowId = getLocalContext().getFlowId();
		AttributeMap externallyAssignedAttributes = getLocalContext().getFlowAttributes();
		MutableAttributeMap flowAttributes = parseMetaAttributes(flow.getAttributes());
		parseAndPutPersistenceContext(flow.getPersistenceContext(), flowAttributes);
		parseAndPutSecured(flow.getSecured(), flowAttributes);
		return this.getLocalContext().getFlowArtifactFactory().createFlow(flowId,
				flowAttributes.union(externallyAssignedAttributes));
	}

	private FlowVariable parseFlowVariable(VarModel var) {
		Class clazz = toClass(var.getClassName());
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz, getFlow()
				.getApplicationContext().getAutowireCapableBeanFactory());
		return new FlowVariable(var.getName(), valueFactory);
	}

	private Mapper parseFlowInputMapper(List inputs) {
		if (inputs != null && !inputs.isEmpty()) {
			DefaultMapper inputMapper = new DefaultMapper();
			for (Iterator it = inputs.iterator(); it.hasNext();) {
				inputMapper.addMapping(parseFlowInputMapping((InputModel) it.next()));
			}
			return inputMapper;
		} else {
			return null;
		}
	}

	private DefaultMapping parseFlowInputMapping(InputModel input) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = input.getName();
		String value = null;
		if (StringUtils.hasText(input.getValue())) {
			value = input.getValue();
		} else {
			value = "flowScope." + name;
		}
		Expression source = parser.parseExpression(name, new FluentParserContext().evaluate(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new FluentParserContext().evaluate(RequestContext.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		parseAndSetMappingConversionExecutor(input, mapping);
		parseAndSetMappingRequired(input, mapping);
		return mapping;
	}

	private Mapper parseSubflowInputMapper(List inputs) {
		if (inputs != null && !inputs.isEmpty()) {
			DefaultMapper inputMapper = new DefaultMapper();
			for (Iterator it = inputs.iterator(); it.hasNext();) {
				inputMapper.addMapping(parseSubflowInputMapping((InputModel) it.next()));
			}
			return inputMapper;
		} else {
			return null;
		}
	}

	private DefaultMapping parseSubflowInputMapping(InputModel input) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = input.getName();
		String value = null;
		if (StringUtils.hasText(input.getValue())) {
			value = input.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new FluentParserContext().evaluate(RequestContext.class));
		Expression target = parser.parseExpression(name, new FluentParserContext().evaluate(MutableAttributeMap.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		parseAndSetMappingConversionExecutor(input, mapping);
		parseAndSetMappingRequired(input, mapping);
		return mapping;
	}

	private Mapper parseFlowOutputMapper(List outputs) {
		if (outputs != null && !outputs.isEmpty()) {
			DefaultMapper outputMapper = new DefaultMapper();
			for (Iterator it = outputs.iterator(); it.hasNext();) {
				outputMapper.addMapping(parseFlowOutputMapping((OutputModel) it.next()));
			}
			return outputMapper;
		} else {
			return null;
		}
	}

	private DefaultMapping parseFlowOutputMapping(OutputModel output) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = output.getName();
		String value = null;
		if (StringUtils.hasText(output.getValue())) {
			value = output.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new FluentParserContext().evaluate(RequestContext.class));
		Expression target = parser.parseExpression(name, new FluentParserContext().evaluate(MutableAttributeMap.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		parseAndSetMappingConversionExecutor(output, mapping);
		parseAndSetMappingRequired(output, mapping);
		return mapping;
	}

	private Mapper parseSubflowOutputMapper(List outputs) {
		if (outputs != null && !outputs.isEmpty()) {
			DefaultMapper outputMapper = new DefaultMapper();
			for (Iterator it = outputs.iterator(); it.hasNext();) {
				outputMapper.addMapping(parseSubflowOutputMapping((OutputModel) it.next()));
			}
			return outputMapper;
		} else {
			return null;
		}
	}

	private DefaultMapping parseSubflowOutputMapping(OutputModel output) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = output.getName();
		String value = null;
		if (StringUtils.hasText(output.getValue())) {
			value = output.getValue();
		} else {
			value = "flowScope." + name;
		}
		Expression source = parser.parseExpression(name, new FluentParserContext().evaluate(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new FluentParserContext().evaluate(RequestContext.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		parseAndSetMappingConversionExecutor(output, mapping);
		parseAndSetMappingRequired(output, mapping);
		return mapping;
	}

	private void parseAndSetMappingConversionExecutor(AbstractMappingModel mappingModel, DefaultMapping mapping) {
		if (StringUtils.hasText(mappingModel.getType())) {
			Class type = toClass(mappingModel.getType());
			ConversionExecutor typeConverter = new RuntimeBindingConversionExecutor(type, getLocalContext()
					.getConversionService());
			mapping.setTypeConverter(typeConverter);
		}
	}

	private void parseAndSetMappingRequired(AbstractMappingModel mappingModel, DefaultMapping mapping) {
		if (StringUtils.hasText(mappingModel.getRequired())) {
			boolean required = ((Boolean) fromStringTo(Boolean.class).execute(mappingModel.getRequired()))
					.booleanValue();
			mapping.setRequired(required);
		}
	}

	private void parseAndAddViewState(ViewStateModel state, Flow flow) {
		ViewFactory viewFactory = parseViewFactory(state.getView(), state.getId(), false, state.getBinder());
		Boolean redirect = null;
		if (StringUtils.hasText(state.getRedirect())) {
			redirect = (Boolean) fromStringTo(Boolean.class).execute(state.getRedirect());
		}
		boolean popup = false;
		if (StringUtils.hasText(state.getPopup())) {
			popup = ((Boolean) fromStringTo(Boolean.class).execute(state.getPopup())).booleanValue();
		}
		MutableAttributeMap attributes = parseMetaAttributes(state.getAttributes());
		if (state.getModel() != null) {
			attributes.put("model", getLocalContext().getExpressionParser().parseExpression(state.getModel(),
					new FluentParserContext().evaluate(RequestContext.class)));
		}
		parseAndPutSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createViewState(state.getId(), flow,
				parseViewVariables(state.getVars()), parseActions(state.getOnEntryActions()), viewFactory, redirect,
				popup, parseActions(state.getOnRenderActions()), parseTransitions(state.getTransitions()),
				parseExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				parseActions(state.getOnExitActions()), attributes);
	}

	private void parseAndAddActionState(ActionStateModel state, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(state.getAttributes());
		parseAndPutSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createActionState(state.getId(), flow,
				parseActions(state.getOnEntryActions()), parseActions(state.getActions()),
				parseTransitions(state.getTransitions()),
				parseExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				parseActions(state.getOnExitActions()), attributes);
	}

	private void parseAndAddDecisionState(DecisionStateModel state, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(state.getAttributes());
		parseAndPutSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createDecisionState(state.getId(), flow,
				parseActions(state.getOnEntryActions()), parseIfs(state.getIfs()),
				parseExceptionHandlers(state.getExceptionHandlers(), null), parseActions(state.getOnExitActions()),
				attributes);
	}

	private void parseAndAddSubflowState(SubflowStateModel state, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(state.getAttributes());
		parseAndPutSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createSubflowState(state.getId(), flow,
				parseActions(state.getOnEntryActions()), parseSubflowExpression(state.getSubflow()),
				parseSubflowAttributeMapper(state), parseTransitions(state.getTransitions()),
				parseExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				parseActions(state.getOnExitActions()), attributes);
	}

	private void parseAndAddEndState(EndStateModel state, Flow flow) {
		MutableAttributeMap attributes = parseMetaAttributes(state.getAttributes());
		if (StringUtils.hasText(state.getCommit())) {
			attributes.put("commit", fromStringTo(Boolean.class).execute(state.getCommit()));
		}
		parseAndPutSecured(state.getSecured(), attributes);
		Action finalResponseAction;
		ViewFactory viewFactory = parseViewFactory(state.getView(), state.getId(), true, null);
		if (viewFactory != null) {
			finalResponseAction = new ViewFactoryActionAdapter(viewFactory);
		} else {
			finalResponseAction = null;
		}
		getLocalContext().getFlowArtifactFactory().createEndState(state.getId(), flow,
				parseActions(state.getOnEntryActions()), finalResponseAction,
				parseFlowOutputMapper(state.getOutputs()), parseExceptionHandlers(state.getExceptionHandlers(), null),
				attributes);
	}

	private ViewFactory parseViewFactory(String view, String stateId, boolean endState, BinderModel binderModel) {
		if (!StringUtils.hasText(view)) {
			if (endState) {
				return null;
			} else {
				view = getLocalContext().getViewFactoryCreator().getViewIdByConvention(stateId);
				Expression viewId = getLocalContext().getExpressionParser().parseExpression(view,
						new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class));
				return createViewFactory(viewId, binderModel);
			}
		} else if (view.startsWith("externalRedirect:")) {
			String encodedUrl = view.substring("externalRedirect:".length());
			Expression externalUrl = getLocalContext().getExpressionParser().parseExpression(encodedUrl,
					new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class));
			return new ActionExecutingViewFactory(new ExternalRedirectAction(externalUrl));
		} else if (view.startsWith("flowRedirect:")) {
			String flowRedirect = view.substring("flowRedirect:".length());
			Expression expression = getLocalContext().getExpressionParser().parseExpression(flowRedirect,
					new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class));
			return new ActionExecutingViewFactory(new FlowDefinitionRedirectAction(expression));
		} else {
			Expression viewId = getLocalContext().getExpressionParser().parseExpression(view,
					new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class));
			return createViewFactory(viewId, binderModel);
		}
	}

	private ViewFactory createViewFactory(Expression viewId, BinderModel binderModel) {
		BinderConfiguration binderConfiguration = createBinderConfiguration(binderModel);
		return getLocalContext().getViewFactoryCreator().createViewFactory(viewId,
				getLocalContext().getExpressionParser(), getLocalContext().getConversionService(), binderConfiguration);
	}

	private BinderConfiguration createBinderConfiguration(BinderModel binderModel) {
		if (binderModel == null) {
			return null;
		}
		BinderConfiguration binderConfiguration = new BinderConfiguration();
		List bindings = binderModel.getBindings();
		for (Iterator it = bindings.iterator(); it.hasNext();) {
			BindingModel bindingModel = (BindingModel) it.next();
			boolean required;
			if (StringUtils.hasText(bindingModel.getRequired())) {
				required = ((Boolean) fromStringTo(Boolean.class).execute(bindingModel.getRequired())).booleanValue();
			} else {
				required = false;
			}
			Binding binding = new Binding(bindingModel.getProperty(), bindingModel.getConverter(), required);
			binderConfiguration.addBinding(binding);
		}
		return binderConfiguration;
	}

	private ViewVariable[] parseViewVariables(List vars) {
		if (vars != null && !vars.isEmpty()) {
			List variables = new ArrayList(vars.size());
			for (Iterator it = vars.iterator(); it.hasNext();) {
				variables.add(parseViewVariable((VarModel) it.next()));
			}
			return (ViewVariable[]) variables.toArray(new ViewVariable[variables.size()]);
		} else {
			return new ViewVariable[0];
		}
	}

	private ViewVariable parseViewVariable(VarModel var) {
		Class clazz = toClass(var.getClassName());
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz, getFlow()
				.getApplicationContext().getAutowireCapableBeanFactory());
		return new ViewVariable(var.getName(), valueFactory);
	}

	private Transition[] parseIfs(List ifModels) {
		if (ifModels != null && !ifModels.isEmpty()) {
			List transitions = new ArrayList(ifModels.size());
			for (Iterator it = ifModels.iterator(); it.hasNext();) {
				transitions.addAll(Arrays.asList(parseIf((IfModel) it.next())));
			}
			return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
		} else {
			return new Transition[0];
		}
	}

	private Transition[] parseIf(IfModel ifModel) {
		Transition thenTransition = parseThen(ifModel);
		if (StringUtils.hasText(ifModel.getElse())) {
			Transition elseTransition = parseElse(ifModel);
			return new Transition[] { thenTransition, elseTransition };
		} else {
			return new Transition[] { thenTransition };
		}
	}

	private Transition parseThen(IfModel ifModel) {
		Expression test = getLocalContext().getExpressionParser().parseExpression(ifModel.getTest(),
				new FluentParserContext().evaluate(RequestContext.class).expectResult(Boolean.class));
		TransitionCriteria matchingCriteria = new DefaultTransitionCriteria(test);
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(ifModel.getThen());
		return getLocalContext().getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null,
				null);
	}

	private Transition parseElse(IfModel ifModel) {
		TargetStateResolver stateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class).execute(
				ifModel.getElse());
		return getLocalContext().getFlowArtifactFactory().createTransition(stateResolver, null, null, null);
	}

	private Expression parseSubflowExpression(String subflow) {
		Expression subflowId = getLocalContext().getExpressionParser().parseExpression(subflow,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class));
		return new SubflowExpression(subflowId, getLocalContext().getFlowDefinitionLocator());
	}

	private SubflowAttributeMapper parseSubflowAttributeMapper(SubflowStateModel state) {
		if (StringUtils.hasText(state.getSubflowAttributeMapper())) {
			String beanId = state.getSubflowAttributeMapper();
			return (SubflowAttributeMapper) getLocalContext().getApplicationContext().getBean(beanId,
					SubflowAttributeMapper.class);
		} else {
			Mapper inputMapper = parseSubflowInputMapper(state.getInputs());
			Mapper outputMapper = parseSubflowOutputMapper(state.getOutputs());
			return new GenericSubflowAttributeMapper(inputMapper, outputMapper);
		}
	}

	private FlowExecutionExceptionHandler[] parseExceptionHandlers(List modelExceptionHandlers, List modelTransitions) {
		FlowExecutionExceptionHandler[] transitionExecutingHandlers = parseTransitionExecutingExceptionHandlers(modelTransitions);
		FlowExecutionExceptionHandler[] customHandlers = parseCustomExceptionHandlers(modelExceptionHandlers);
		FlowExecutionExceptionHandler[] exceptionHandlers = new FlowExecutionExceptionHandler[transitionExecutingHandlers.length
				+ customHandlers.length];
		System.arraycopy(transitionExecutingHandlers, 0, exceptionHandlers, 0, transitionExecutingHandlers.length);
		System.arraycopy(customHandlers, 0, exceptionHandlers, transitionExecutingHandlers.length,
				customHandlers.length);
		return exceptionHandlers;
	}

	private FlowExecutionExceptionHandler[] parseTransitionExecutingExceptionHandlers(List transitionModels) {
		if (transitionModels != null && !transitionModels.isEmpty()) {
			List exceptionHandlers = new ArrayList(transitionModels.size());
			for (Iterator it = transitionModels.iterator(); it.hasNext();) {
				TransitionModel model = (TransitionModel) it.next();
				if (StringUtils.hasText(model.getOnException())) {
					if (model.getSecured() != null) {
						throw new FlowBuilderException("Exception based transitions cannot be secured");
					}
					exceptionHandlers.add(parseTransitionExecutingExceptionHandler(model));
				}
			}
			return (FlowExecutionExceptionHandler[]) exceptionHandlers
					.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
		} else {
			return new FlowExecutionExceptionHandler[0];
		}
	}

	private FlowExecutionExceptionHandler parseTransitionExecutingExceptionHandler(TransitionModel transition) {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		Class exceptionClass = toClass(transition.getOnException());
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(transition.getTo());
		handler.add(exceptionClass, targetStateResolver);
		handler.getActionList().addAll(parseActions(transition.getActions()));
		return handler;
	}

	private FlowExecutionExceptionHandler[] parseCustomExceptionHandlers(List exceptionHandlerModels) {
		if (exceptionHandlerModels != null && !exceptionHandlerModels.isEmpty()) {
			List exceptionHandlers = new ArrayList(exceptionHandlerModels.size());
			for (Iterator it = exceptionHandlerModels.iterator(); it.hasNext();) {
				exceptionHandlers.add(parseCustomExceptionHandler((ExceptionHandlerModel) it.next()));
			}
			return (FlowExecutionExceptionHandler[]) exceptionHandlers
					.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
		} else {
			return new FlowExecutionExceptionHandler[0];
		}
	}

	private FlowExecutionExceptionHandler parseCustomExceptionHandler(ExceptionHandlerModel exceptionHandler) {
		return (FlowExecutionExceptionHandler) getLocalContext().getApplicationContext().getBean(
				exceptionHandler.getBean(), FlowExecutionExceptionHandler.class);
	}

	private Transition[] parseTransitions(List transitionModels) {
		if (transitionModels != null && !transitionModels.isEmpty()) {
			List transitions = new ArrayList(transitionModels.size());
			if (transitionModels != null) {
				for (Iterator it = transitionModels.iterator(); it.hasNext();) {
					TransitionModel transition = (TransitionModel) it.next();
					if (!StringUtils.hasText(transition.getOnException())) {
						transitions.add(parseTransition(transition));
					}
				}
			}
			return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
		} else {
			return new Transition[0];
		}
	}

	private Transition parseTransition(TransitionModel transition) {
		TransitionCriteria matchingCriteria = (TransitionCriteria) fromStringTo(TransitionCriteria.class).execute(
				transition.getOn());
		TargetStateResolver stateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class).execute(
				transition.getTo());
		TransitionCriteria executionCriteria = TransitionCriteriaChain.criteriaChainFor(parseActions(transition
				.getActions()));
		MutableAttributeMap attributes = parseMetaAttributes(transition.getAttributes());
		if (StringUtils.hasText(transition.getBind())) {
			attributes.put("bind", fromStringTo(Boolean.class).execute(transition.getBind()));
		}
		if (StringUtils.hasText(transition.getValidate())) {
			attributes.put("validate", fromStringTo(Boolean.class).execute(transition.getValidate()));
		}
		if (StringUtils.hasText(transition.getHistory())) {
			attributes.put("history", fromStringTo(History.class).execute(transition.getHistory()));
		}
		parseAndPutSecured(transition.getSecured(), attributes);
		return getLocalContext().getFlowArtifactFactory().createTransition(stateResolver, matchingCriteria,
				executionCriteria, attributes);
	}

	private Action[] parseActions(List actionModels) {
		if (actionModels != null && !actionModels.isEmpty()) {
			List actions = new ArrayList(actionModels.size());
			for (Iterator it = actionModels.iterator(); it.hasNext();) {
				AbstractActionModel actionModel = (AbstractActionModel) it.next();
				Action action;
				if (actionModel instanceof EvaluateModel) {
					action = parseEvaluateAction((EvaluateModel) actionModel);
				} else if (actionModel instanceof RenderModel) {
					action = parseRenderAction((RenderModel) actionModel);
				} else if (actionModel instanceof SetModel) {
					action = parseSetAction((SetModel) actionModel);
				} else {
					action = null;
				}
				if (action != null) {
					AnnotatedAction annotatedAction = new AnnotatedAction(action);
					annotatedAction.getAttributes().putAll(parseMetaAttributes(actionModel.getAttributes()));
					actions.add(annotatedAction);
				}
			}
			return (Action[]) actions.toArray(new Action[actions.size()]);
		} else {
			return new Action[0];
		}
	}

	private Action parseEvaluateAction(EvaluateModel evaluate) {
		String expressionString = evaluate.getExpression();
		Expression expression = getLocalContext().getExpressionParser().parseExpression(expressionString,
				new FluentParserContext().evaluate(RequestContext.class));
		return new EvaluateAction(expression, parseEvaluationActionResultExposer(evaluate));
	}

	private ActionResultExposer parseEvaluationActionResultExposer(EvaluateModel evaluate) {
		if (StringUtils.hasText(evaluate.getResult())) {
			Expression resultExpression = getLocalContext().getExpressionParser().parseExpression(evaluate.getResult(),
					new FluentParserContext().evaluate(RequestContext.class));
			Class expectedResultType = null;
			if (StringUtils.hasText(evaluate.getResultType())) {
				expectedResultType = toClass(evaluate.getResultType());
			}
			return new ActionResultExposer(resultExpression, expectedResultType, getLocalContext()
					.getConversionService());
		} else {
			return null;
		}
	}

	private Action parseRenderAction(RenderModel render) {
		String[] fragmentExpressionStrings = StringUtils.commaDelimitedListToStringArray(render.getFragments());
		fragmentExpressionStrings = StringUtils.trimArrayElements(fragmentExpressionStrings);
		ParserContext context = new FluentParserContext().template().evaluate(RequestContext.class).expectResult(
				String.class);
		Expression[] fragments = new Expression[fragmentExpressionStrings.length];
		for (int i = 0; i < fragmentExpressionStrings.length; i++) {
			String fragment = fragmentExpressionStrings[i];
			fragments[i] = getLocalContext().getExpressionParser().parseExpression(fragment, context);
		}
		return new RenderAction(fragments);
	}

	private Action parseSetAction(SetModel set) {
		Expression nameExpression = getLocalContext().getExpressionParser().parseExpression(set.getName(),
				new FluentParserContext().evaluate(RequestContext.class));
		Expression valueExpression = getLocalContext().getExpressionParser().parseExpression(set.getValue(),
				new FluentParserContext().evaluate(RequestContext.class));
		Class expectedType = null;
		if (StringUtils.hasText(set.getType())) {
			expectedType = toClass(set.getType());
		}
		return new SetAction(nameExpression, valueExpression, expectedType, getLocalContext().getConversionService());
	}

	private MutableAttributeMap parseMetaAttributes(List attributeModels) {
		if (attributeModels != null && !attributeModels.isEmpty()) {
			LocalAttributeMap attributes = new LocalAttributeMap();
			for (Iterator it = attributeModels.iterator(); it.hasNext();) {
				parseAndPutMetaAttribute((AttributeModel) it.next(), attributes);
			}
			return attributes;
		} else {
			return new LocalAttributeMap();
		}
	}

	private void parseAndPutMetaAttribute(AttributeModel attribute, MutableAttributeMap attributes) {
		String name = attribute.getName();
		String value = attribute.getValue();
		attributes.put(name, parseAttributeValueIfNecessary(attribute, value));
	}

	private Object parseAttributeValueIfNecessary(AttributeModel attribute, String stringValue) {
		if (StringUtils.hasText(attribute.getType())) {
			Class targetClass = toClass(attribute.getType());
			return fromStringTo(targetClass).execute(stringValue);
		} else {
			return stringValue;
		}
	}

	private void parseAndPutPersistenceContext(PersistenceContextModel persistenceContext,
			MutableAttributeMap attributes) {
		if (persistenceContext != null) {
			attributes.put("persistenceContext", Boolean.TRUE);
		}
	}

	private void parseAndPutSecured(SecuredModel secured, MutableAttributeMap attributes) {
		if (secured != null) {
			SecurityRule rule = new SecurityRule();
			rule.setAttributes(SecurityRule.commaDelimitedListToSecurityAttributes(secured.getAttributes()));
			String comparisonType = secured.getMatch();
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

	private ConversionExecutor fromStringTo(Class targetType) throws ConversionExecutionException {
		return getLocalContext().getConversionService().getConversionExecutor(String.class, targetType);
	}

	private Class toClass(String name) {
		Class clazz = getLocalContext().getConversionService().getClassForAlias(name);
		if (clazz != null) {
			return clazz;
		} else {
			try {
				ClassLoader classLoader = getLocalContext().getApplicationContext().getClassLoader();
				return ClassUtils.forName(name, classLoader);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Unable to load class '" + name + "'");
			}
		}
	}

}
