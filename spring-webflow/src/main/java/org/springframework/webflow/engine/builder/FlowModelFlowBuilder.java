package org.springframework.webflow.engine.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.support.RuntimeBindingConversionExecutor;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;
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
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.support.ActionExecutingViewFactory;
import org.springframework.webflow.engine.model.AbstractActionModel;
import org.springframework.webflow.engine.model.AbstractMappingModel;
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
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
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

public class FlowModelFlowBuilder extends AbstractFlowBuilder implements ResourceHolder {

	private FlowModelHolder flowModelHolder;
	private FlowModel flowModel;
	private LocalFlowBuilderContext localFlowBuilderContext;
	private Resource resource;

	public FlowModelFlowBuilder(FlowModelHolder flowModelHolder) {
		this.flowModelHolder = flowModelHolder;
	}

	public FlowModelFlowBuilder(FlowModelHolder flowModelHolder, Resource resource) {
		this.flowModelHolder = flowModelHolder;
		this.resource = resource;
	}

	/**
	 * Initialize this builder. This could cause the builder to open a stream to an externalized resource representing
	 * the flow definition, for example.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void doInit() throws FlowBuilderException {
		flowModel = flowModelHolder.getFlowModel();
		initLocalFlowContext();
	}

	/**
	 * Builds any variables initialized by the flow when it starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildVariables() throws FlowBuilderException {
		if (flowModel.getVars() != null) {
			for (Iterator varIt = flowModel.getVars().iterator(); varIt.hasNext();) {
				getFlow().addVariable(convertFlowVariable((VarModel) varIt.next()));
			}
		}
	}

	/**
	 * Builds the input mapper responsible for mapping flow input on start.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildInputMapper() throws FlowBuilderException {
		if (flowModel.getInputs() != null) {
			getFlow().setInputMapper(convertFlowInputMapper(flowModel.getInputs()));
		}
	}

	/**
	 * Builds any start actions to execute when the flow starts.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStartActions() throws FlowBuilderException {
		if (flowModel.getOnStartActions() != null) {
			getFlow().getStartActionList().addAll(convertActions(flowModel.getOnStartActions()));
		}
	}

	/**
	 * Builds the states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildStates() throws FlowBuilderException {
		if (flowModel.getStates() == null) {
			throw new FlowBuilderException("At least one state is required to build a flow definition");
		}
		for (Iterator stateIt = flowModel.getStates().iterator(); stateIt.hasNext();) {
			AbstractStateModel state = (AbstractStateModel) stateIt.next();
			if (state instanceof ActionStateModel) {
				convertActionState((ActionStateModel) state, getFlow());
			} else if (state instanceof ViewStateModel) {
				convertViewState((ViewStateModel) state, getFlow());
			} else if (state instanceof DecisionStateModel) {
				convertDecisionState((DecisionStateModel) state, getFlow());
			} else if (state instanceof SubflowStateModel) {
				convertSubflowState((SubflowStateModel) state, getFlow());
			} else if (state instanceof EndStateModel) {
				convertEndState((EndStateModel) state, getFlow());
			}
		}
		if (flowModel.getStartStateId() != null) {
			getFlow().setStartState(flowModel.getStartStateId());
		} else {
			// default to the identifier of the first state in the flow model
			getFlow().setStartState(((AbstractStateModel) flowModel.getStates().get(0)).getId());
		}
	}

	/**
	 * Builds any transitions shared by all states of the flow.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildGlobalTransitions() throws FlowBuilderException {
		if (flowModel.getGlobalTransitions() != null) {
			getFlow().getGlobalTransitionSet().addAll(convertTransitions(flowModel.getGlobalTransitions()));
		}
	}

	/**
	 * Builds any end actions to execute when the flow ends.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildEndActions() throws FlowBuilderException {
		if (flowModel.getOnEndActions() != null) {
			getFlow().getEndActionList().addAll(convertActions(flowModel.getOnEndActions()));
		}
	}

	/**
	 * Builds the output mapper responsible for mapping flow output on end.
	 * @throws FlowBuilderException an exception occurred building the flow
	 */
	public void buildOutputMapper() throws FlowBuilderException {
		if (flowModel.getOutputs() != null) {
			getFlow().setOutputMapper(convertFlowOutputMapper(flowModel.getOutputs()));
		}
	}

	/**
	 * Creates and adds all exception handlers to the flow built by this builder.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public void buildExceptionHandlers() throws FlowBuilderException {
		getFlow().getExceptionHandlerSet().addAll(
				convertExceptionHandlers(flowModel.getExceptionHandlers(), flowModel.getGlobalTransitions()));
	}

	/**
	 * Shutdown the builder, releasing any resources it holds. A new flow construction process should start with another
	 * call to the {@link #init(FlowBuilderContext)} method.
	 * @throws FlowBuilderException an exception occurred building this flow
	 */
	public void doDispose() throws FlowBuilderException {
		flowModel = null;
		setLocalContext(null);
	}

	private void initLocalFlowContext() {
		List resources = new LinkedList();
		if (getFlowModel().getBeanImports() != null) {
			if (getResource() == null) {
				throw new FlowBuilderException("A resource must be defined in order to load bean-imports");
			}
			for (Iterator beanImportIt = getFlowModel().getBeanImports().iterator(); beanImportIt.hasNext();) {
				BeanImportModel beanImport = (BeanImportModel) beanImportIt.next();
				try {
					resources.add(getResource().createRelative(beanImport.getResource()));
				} catch (IOException e) {
					throw new FlowBuilderException("Could not access flow-relative artifact resource '"
							+ beanImport.getResource() + "'", e);
				}
			}
		}
		setLocalContext(new LocalFlowBuilderContext(getContext(), createFlowApplicationContext((Resource[]) resources
				.toArray(new Resource[resources.size()]))));
	}

	protected Flow createFlow() {
		Flow flow = convertFlow(flowModel);
		flow.setBeanFactory(getLocalContext().getBeanFactory());
		flow.setResourceLoader(getLocalContext().getResourceLoader());
		return flow;
	}

	private Flow convertFlow(FlowModel flow) {
		String flowId = getLocalContext().getFlowId();
		AttributeMap externallyAssignedAttributes = getLocalContext().getFlowAttributes();
		MutableAttributeMap flowAttributes = convertMetaAttributes(flow.getAttributes());
		convertPersistenceContext(flow.getPersistenceContext(), flowAttributes);
		convertSecured(flow.getSecured(), flowAttributes);
		return this.getLocalContext().getFlowArtifactFactory().createFlow(flowId,
				flowAttributes.union(externallyAssignedAttributes));
	}

	protected GenericApplicationContext createFlowApplicationContext(Resource[] resources) {
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

	private FlowVariable convertFlowVariable(VarModel var) {
		Class clazz = (Class) fromStringTo(Class.class).execute(var.getClassName());
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz,
				(AutowireCapableBeanFactory) getFlow().getBeanFactory());
		ScopeType scope = convertScopeType(var.getScope(), ScopeType.FLOW);
		if (!(scope == ScopeType.FLOW || scope == ScopeType.CONVERSATION)) {
			throw new IllegalArgumentException("Only " + ScopeType.FLOW + " or " + ScopeType.CONVERSATION
					+ " scope is allowed for flow variables");
		}
		return new FlowVariable(var.getName(), valueFactory, scope == ScopeType.FLOW ? true : false);
	}

	private Mapper convertFlowInputMapper(List inputs) {
		DefaultMapper inputMapper = new DefaultMapper();
		if (inputs != null) {
			for (Iterator inputIt = inputs.iterator(); inputIt.hasNext();) {
				inputMapper.addMapping(convertFlowInputMapping((InputModel) inputIt.next()));
			}
		}
		return inputMapper;
	}

	private DefaultMapping convertFlowInputMapping(InputModel input) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = input.getName();
		String value = null;
		if (StringUtils.hasText(input.getValue())) {
			value = input.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		convertMappingConversionExecutor(input, mapping);
		convertMappingRequired(input, mapping);
		return mapping;
	}

	private Mapper convertSubflowInputMapper(List inputs) {
		DefaultMapper inputMapper = new DefaultMapper();
		if (inputs != null) {
			for (Iterator inputIt = inputs.iterator(); inputIt.hasNext();) {
				inputMapper.addMapping(convertSubflowInputMapping((InputModel) inputIt.next()));
			}
		}
		return inputMapper;
	}

	private DefaultMapping convertSubflowInputMapping(InputModel input) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = input.getName();
		String value = null;
		if (StringUtils.hasText(input.getValue())) {
			value = input.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		Expression target = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		convertMappingConversionExecutor(input, mapping);
		convertMappingRequired(input, mapping);
		return mapping;
	}

	private Mapper convertFlowOutputMapper(List outputs) {
		DefaultMapper outputMapper = new DefaultMapper();
		if (outputs != null) {
			for (Iterator outputIt = outputs.iterator(); outputIt.hasNext();) {
				outputMapper.addMapping(convertFlowOutputMapping((OutputModel) outputIt.next()));
			}
		}
		return outputMapper;
	}

	private DefaultMapping convertFlowOutputMapping(OutputModel output) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = output.getName();
		String value = null;
		if (StringUtils.hasText(output.getValue())) {
			value = output.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		Expression target = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		convertMappingConversionExecutor(output, mapping);
		convertMappingRequired(output, mapping);
		return mapping;
	}

	private Mapper convertSubflowOutputMapper(List outputs) {
		DefaultMapper outputMapper = new DefaultMapper();
		if (outputs != null) {
			for (Iterator outputIt = outputs.iterator(); outputIt.hasNext();) {
				outputMapper.addMapping(convertSubflowOutputMapping((OutputModel) outputIt.next()));
			}
		}
		return outputMapper;
	}

	private DefaultMapping convertSubflowOutputMapping(OutputModel output) {
		ExpressionParser parser = getLocalContext().getExpressionParser();
		String name = output.getName();
		String value = null;
		if (StringUtils.hasText(output.getValue())) {
			value = output.getValue();
		} else {
			value = name;
		}
		Expression source = parser.parseExpression(name, new ParserContextImpl().eval(MutableAttributeMap.class));
		Expression target = parser.parseExpression(value, new ParserContextImpl().eval(RequestContext.class));
		DefaultMapping mapping = new DefaultMapping(source, target);
		convertMappingConversionExecutor(output, mapping);
		convertMappingRequired(output, mapping);
		return mapping;
	}

	private void convertMappingConversionExecutor(AbstractMappingModel model, DefaultMapping mapping) {
		if (StringUtils.hasText(model.getType())) {
			Class type = (Class) fromStringTo(Class.class).execute(model.getType());
			ConversionExecutor typeConverter = new RuntimeBindingConversionExecutor(type, getLocalContext()
					.getConversionService());
			mapping.setTypeConverter(typeConverter);
		}
	}

	private void convertMappingRequired(AbstractMappingModel model, DefaultMapping mapping) {
		if (StringUtils.hasText(model.getRequired())) {
			boolean required = ((Boolean) fromStringTo(Boolean.class).execute(model.getRequired())).booleanValue();
			mapping.setRequired(required);
		}
	}

	private void convertActionState(ActionStateModel state, Flow flow) {
		MutableAttributeMap attributes = convertMetaAttributes(state.getAttributes());
		convertSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createActionState(state.getId(), flow,
				convertActions(state.getOnEntryActions()), convertActions(state.getActions()),
				convertTransitions(state.getTransitions()),
				convertExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				convertActions(state.getOnExitActions()), attributes);
	}

	private void convertViewState(ViewStateModel state, Flow flow) {
		ViewFactory viewFactory = convertViewFactory(state.getView(), state.getId(), false);
		Boolean redirect = null;
		if (StringUtils.hasText(state.getRedirect())) {
			redirect = (Boolean) fromStringTo(Boolean.class).execute(state.getRedirect());
		}
		boolean popup = false;
		if (StringUtils.hasText(state.getPopup())) {
			popup = ((Boolean) fromStringTo(Boolean.class).execute(state.getPopup())).booleanValue();
		}
		MutableAttributeMap attributes = convertMetaAttributes(state.getAttributes());
		if (state.getModel() != null) {
			attributes.put("model", getLocalContext().getExpressionParser().parseExpression(state.getModel(),
					new ParserContextImpl().eval(RequestContext.class)));
		}
		convertSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createViewState(state.getId(), flow,
				convertViewVariables(state.getVars()), convertActions(state.getOnEntryActions()), viewFactory,
				redirect, popup, convertActions(state.getOnRenderActions()),
				convertTransitions(state.getTransitions()),
				convertExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				convertActions(state.getOnExitActions()), attributes);
	}

	private void convertDecisionState(DecisionStateModel state, Flow flow) {
		MutableAttributeMap attributes = convertMetaAttributes(state.getAttributes());
		convertSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createDecisionState(state.getId(), flow,
				convertActions(state.getOnEntryActions()), convertIfs(state.getIfs()),
				convertExceptionHandlers(state.getExceptionHandlers(), null), convertActions(state.getOnExitActions()),
				attributes);
	}

	private void convertSubflowState(SubflowStateModel state, Flow flow) {
		MutableAttributeMap attributes = convertMetaAttributes(state.getAttributes());
		convertSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createSubflowState(state.getId(), flow,
				convertActions(state.getOnEntryActions()), convertSubflowExpression(state.getSubflow()),
				convertSubflowAttributeMapper(state), convertTransitions(state.getTransitions()),
				convertExceptionHandlers(state.getExceptionHandlers(), state.getTransitions()),
				convertActions(state.getOnExitActions()), attributes);
	}

	private Expression convertSubflowExpression(String subflow) {
		Expression subflowId = getLocalContext().getExpressionParser().parseExpression(subflow,
				new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
		return new SubflowExpression(subflowId, getLocalContext().getFlowDefinitionLocator());
	}

	private SubflowAttributeMapper convertSubflowAttributeMapper(SubflowStateModel state) {
		if (StringUtils.hasText(state.getSubflowAttributeMapper())) {
			String attributeMapperBeanId = state.getSubflowAttributeMapper();
			return (SubflowAttributeMapper) getLocalContext().getBeanFactory().getBean(attributeMapperBeanId,
					SubflowAttributeMapper.class);
		} else {
			Mapper inputMapper = convertSubflowInputMapper(state.getInputs());
			Mapper outputMapper = convertSubflowOutputMapper(state.getOutputs());
			return new GenericSubflowAttributeMapper(inputMapper, outputMapper);
		}
	}

	private void convertEndState(EndStateModel state, Flow flow) {
		MutableAttributeMap attributes = convertMetaAttributes(state.getAttributes());
		if (StringUtils.hasText(state.getCommit())) {
			attributes.put("commit", fromStringTo(Boolean.class).execute(state.getCommit()));
		}
		convertSecured(state.getSecured(), attributes);
		getLocalContext().getFlowArtifactFactory().createEndState(state.getId(), flow,
				convertActions(state.getOnEntryActions()),
				new ViewFactoryActionAdapter(convertViewFactory(state.getView(), state.getId(), true)),
				convertFlowOutputMapper(state.getOutputs()),
				convertExceptionHandlers(state.getExceptionHandlers(), null), attributes);
	}

	private ViewFactory convertViewFactory(String view, String stateId, boolean endState) {
		if (!StringUtils.hasText(view)) {
			if (endState) {
				return null;
			} else {
				view = getLocalContext().getViewFactoryCreator().getViewIdByConvention(stateId);
				Expression viewId = getLocalContext().getExpressionParser().parseExpression(view,
						new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
				return createViewFactory(viewId);
			}
		} else if (view.startsWith("externalRedirect:")) {
			String encodedUrl = view.substring("externalRedirect:".length());
			Expression externalUrl = getLocalContext().getExpressionParser().parseExpression(encodedUrl,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return new ActionExecutingViewFactory(new ExternalRedirectAction(externalUrl));
		} else if (view.startsWith("flowRedirect:")) {
			String flowRedirect = view.substring("flowRedirect:".length());
			Expression expression = getLocalContext().getExpressionParser().parseExpression(flowRedirect,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return new ActionExecutingViewFactory(new FlowDefinitionRedirectAction(expression));
		} else {
			Expression viewId = getLocalContext().getExpressionParser().parseExpression(view,
					new ParserContextImpl().template().eval(RequestContext.class).expect(String.class));
			return createViewFactory(viewId);
		}
	}

	private ViewFactory createViewFactory(Expression viewId) {
		return getLocalContext().getViewFactoryCreator().createViewFactory(viewId,
				getLocalContext().getExpressionParser(), getLocalContext().getFormatterRegistry(),
				getLocalContext().getResourceLoader());
	}

	private ViewVariable[] convertViewVariables(List vars) {
		List variables = new LinkedList();
		if (vars != null) {
			for (Iterator varIt = vars.iterator(); varIt.hasNext();) {
				variables.add(convertViewVariable((VarModel) varIt.next()));
			}
		}
		return (ViewVariable[]) variables.toArray(new ViewVariable[variables.size()]);
	}

	private ViewVariable convertViewVariable(VarModel var) {
		Class clazz = (Class) fromStringTo(Class.class).execute(var.getClassName());
		VariableValueFactory valueFactory = new BeanFactoryVariableValueFactory(clazz,
				(AutowireCapableBeanFactory) getFlow().getBeanFactory());
		return new ViewVariable(var.getName(), valueFactory);
	}

	private Transition[] convertIfs(List ifs) {
		List transitions = new LinkedList();
		if (ifs != null) {
			for (Iterator ifIt = ifs.iterator(); ifIt.hasNext();) {
				transitions.addAll(Arrays.asList(convertIf((IfModel) ifIt.next())));
			}
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition[] convertIf(IfModel conditional) {
		Transition thenTransition = convertThen(conditional);
		if (StringUtils.hasText(conditional.getElse())) {
			Transition elseTransition = convertElse(conditional);
			return new Transition[] { thenTransition, elseTransition };
		} else {
			return new Transition[] { thenTransition };
		}
	}

	private Transition convertThen(IfModel conditional) {
		Expression expression = getLocalContext().getExpressionParser().parseExpression(conditional.getTest(),
				new ParserContextImpl().eval(RequestContext.class).expect(Boolean.class));
		TransitionCriteria matchingCriteria = new DefaultTransitionCriteria(expression);
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(conditional.getThen());
		return getLocalContext().getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria, null,
				null);
	}

	private Transition convertElse(IfModel conditional) {
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(conditional.getElse());
		return getLocalContext().getFlowArtifactFactory().createTransition(targetStateResolver, null, null, null);
	}

	private FlowExecutionExceptionHandler[] convertExceptionHandlers(List modelExceptionHandlers, List modelTransitions) {
		FlowExecutionExceptionHandler[] transitionExecutingHandlers = convertTransitionExecutingExceptionHandlers(modelTransitions);
		FlowExecutionExceptionHandler[] customHandlers = convertCustomExceptionHandlers(modelExceptionHandlers);
		FlowExecutionExceptionHandler[] exceptionHandlers = new FlowExecutionExceptionHandler[transitionExecutingHandlers.length
				+ customHandlers.length];
		System.arraycopy(transitionExecutingHandlers, 0, exceptionHandlers, 0, transitionExecutingHandlers.length);
		System.arraycopy(customHandlers, 0, exceptionHandlers, transitionExecutingHandlers.length,
				customHandlers.length);
		return exceptionHandlers;
	}

	private FlowExecutionExceptionHandler[] convertTransitionExecutingExceptionHandlers(List transitions) {
		List exceptionHandlers = new LinkedList();
		if (transitions != null) {
			for (Iterator transitionIt = transitions.iterator(); transitionIt.hasNext();) {
				TransitionModel transition = (TransitionModel) transitionIt.next();
				if (StringUtils.hasText(transition.getOnException())) {
					if (transition.getSecured() != null) {
						throw new FlowBuilderException("Exception based transitions cannot be secured");
					}
					exceptionHandlers.add(convertTransitionExecutingExceptionHandler(transition));
				}
			}
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler convertTransitionExecutingExceptionHandler(TransitionModel transition) {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		Class exceptionClass = (Class) fromStringTo(Class.class).execute(transition.getOnException());
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(transition.getTo());
		handler.add(exceptionClass, targetStateResolver);
		handler.getActionList().addAll(convertActions(transition.getActions()));
		return handler;
	}

	private FlowExecutionExceptionHandler[] convertCustomExceptionHandlers(List modelExceptionHandlers) {
		List exceptionHandlers = new LinkedList();
		if (modelExceptionHandlers != null) {
			for (Iterator exceptionHandlerIt = modelExceptionHandlers.iterator(); exceptionHandlerIt.hasNext();) {
				exceptionHandlers.add(convertCustomExceptionHandler((ExceptionHandlerModel) exceptionHandlerIt.next()));
			}
		}
		return (FlowExecutionExceptionHandler[]) exceptionHandlers
				.toArray(new FlowExecutionExceptionHandler[exceptionHandlers.size()]);
	}

	private FlowExecutionExceptionHandler convertCustomExceptionHandler(ExceptionHandlerModel exceptionHandler) {
		return (FlowExecutionExceptionHandler) getLocalContext().getBeanFactory().getBean(
				exceptionHandler.getBeanName(), FlowExecutionExceptionHandler.class);
	}

	private Transition[] convertTransitions(List modelTransactions) {
		List transitions = new LinkedList();
		if (modelTransactions != null) {
			for (Iterator modelTransactionIt = modelTransactions.iterator(); modelTransactionIt.hasNext();) {
				TransitionModel transition = (TransitionModel) modelTransactionIt.next();
				if (!StringUtils.hasText(transition.getOnException())) {
					transitions.add(convertTransition(transition));
				}
			}
		}
		return (Transition[]) transitions.toArray(new Transition[transitions.size()]);
	}

	private Transition convertTransition(TransitionModel transition) {
		TransitionCriteria matchingCriteria = (TransitionCriteria) fromStringTo(TransitionCriteria.class).execute(
				transition.getOn());
		TargetStateResolver targetStateResolver = (TargetStateResolver) fromStringTo(TargetStateResolver.class)
				.execute(transition.getTo());
		TransitionCriteria executionCriteria = TransitionCriteriaChain.criteriaChainFor(convertActions(transition
				.getActions()));
		MutableAttributeMap attributes = convertMetaAttributes(transition.getAttributes());
		if (transition.getBind() != null) {
			attributes.put("bind", transition.getBind());
		}
		convertSecured(transition.getSecured(), attributes);
		return getLocalContext().getFlowArtifactFactory().createTransition(targetStateResolver, matchingCriteria,
				executionCriteria, attributes);
	}

	private Action[] convertActions(List modelActions) {
		List actions = new LinkedList();
		if (modelActions != null) {
			for (Iterator modelActionIt = modelActions.iterator(); modelActionIt.hasNext();) {
				AbstractActionModel action = (AbstractActionModel) modelActionIt.next();
				if (action instanceof EvaluateModel) {
					actions.add(convertEvaluateAction((EvaluateModel) action));
				} else if (action instanceof RenderModel) {
					actions.add(convertRenderAction((RenderModel) action));
				} else if (action instanceof SetModel) {
					actions.add(convertSetAction((SetModel) action));
				}
			}
		}
		return (Action[]) actions.toArray(new Action[actions.size()]);
	}

	private Action convertEvaluateAction(EvaluateModel evaluate) {
		String expressionString = evaluate.getExpression();
		Expression expression = getLocalContext().getExpressionParser().parseExpression(expressionString,
				new ParserContextImpl().eval(RequestContext.class));
		return new EvaluateAction(expression, convertEvaluationActionResultExposer(evaluate));
	}

	private ActionResultExposer convertEvaluationActionResultExposer(EvaluateModel evaluate) {
		if (StringUtils.hasText(evaluate.getResult())) {
			Expression resultExpression = getLocalContext().getExpressionParser().parseExpression(evaluate.getResult(),
					new ParserContextImpl().eval(RequestContext.class));
			Class expectedResultType = null;
			if (StringUtils.hasText(evaluate.getResultType())) {
				expectedResultType = (Class) fromStringTo(Class.class).execute(evaluate.getResultType());
			}
			return new ActionResultExposer(resultExpression, expectedResultType, getLocalContext()
					.getConversionService());
		} else {
			return null;
		}
	}

	private Action convertRenderAction(RenderModel render) {
		String[] fragmentExpressionStrings = StringUtils.commaDelimitedListToStringArray(render.getFragments());
		fragmentExpressionStrings = StringUtils.trimArrayElements(fragmentExpressionStrings);
		ParserContext context = new ParserContextImpl().template().eval(RequestContext.class).expect(String.class);
		Expression[] fragments = new Expression[fragmentExpressionStrings.length];
		for (int i = 0; i < fragmentExpressionStrings.length; i++) {
			String fragment = fragmentExpressionStrings[i];
			fragments[i] = getLocalContext().getExpressionParser().parseExpression(fragment, context);
		}
		return new RenderAction(fragments);
	}

	private Action convertSetAction(SetModel set) {
		Expression nameExpression = getLocalContext().getExpressionParser().parseExpression(set.getName(),
				new ParserContextImpl().eval(RequestContext.class));
		Expression valueExpression = getLocalContext().getExpressionParser().parseExpression(set.getValue(),
				new ParserContextImpl().eval(RequestContext.class));
		Class expectedType = null;
		if (StringUtils.hasText(set.getType())) {
			expectedType = (Class) fromStringTo(Class.class).execute(set.getType());
		}
		return new SetAction(nameExpression, valueExpression, expectedType, getLocalContext().getConversionService());
	}

	private MutableAttributeMap convertMetaAttributes(List modelAttributes) {
		LocalAttributeMap attributes = new LocalAttributeMap();
		if (modelAttributes != null) {
			for (Iterator modelAttributeIt = modelAttributes.iterator(); modelAttributeIt.hasNext();) {
				convertMetaAttribute((AttributeModel) modelAttributeIt.next(), attributes);
			}
		}
		return attributes;
	}

	private void convertMetaAttribute(AttributeModel attribute, MutableAttributeMap attributes) {
		String name = attribute.getName();
		String value = attribute.getValue();
		attributes.put(name, convertAttributeValueIfNecessary(attribute, value));
	}

	private Object convertAttributeValueIfNecessary(AttributeModel attribute, String stringValue) {
		if (StringUtils.hasText(attribute.getType())) {
			Class targetClass = (Class) fromStringTo(Class.class).execute(attribute.getType());
			return fromStringTo(targetClass).execute(stringValue);
		} else {
			return stringValue;
		}
	}

	private void convertPersistenceContext(PersistenceContextModel persistenceContext, MutableAttributeMap attributes) {
		if (persistenceContext != null) {
			attributes.put("persistenceContext", Boolean.TRUE);
		}
	}

	private void convertSecured(SecuredModel secured, MutableAttributeMap attributes) {
		if (secured != null) {
			SecurityRule rule = new SecurityRule();
			rule.setAttributes(SecurityRule.convertAttributesFromCommaSeparatedString(secured.getAttributes()));
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

	private ScopeType convertScopeType(String scope, ScopeType defaultScope) {
		if (StringUtils.hasText(scope)) {
			return (ScopeType) fromStringTo(ScopeType.class).execute(scope);
		} else {
			return defaultScope;
		}
	}

	private ConversionExecutor fromStringTo(Class targetType) throws ConversionException {
		return getLocalContext().getConversionService().getConversionExecutor(String.class, targetType);
	}

	protected FlowModel getFlowModel() {
		return flowModel;
	}

	protected LocalFlowBuilderContext getLocalContext() {
		return localFlowBuilderContext;
	}

	public Resource getResource() {
		return resource;
	}

	protected void setLocalContext(LocalFlowBuilderContext localFlowBuilderContext) {
		this.localFlowBuilderContext = localFlowBuilderContext;
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

		public Class getValueType(Object context) {
			return null;
		}

		public String getExpressionString() {
			return null;
		}
	}

	public String toString() {
		return new ToStringCreator(this).toString();
	}

}
