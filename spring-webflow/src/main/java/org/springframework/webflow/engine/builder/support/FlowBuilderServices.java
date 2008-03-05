package org.springframework.webflow.engine.builder.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;

/**
 * A simple holder for configuring the services used by flow builders. These services are exposed to a builder in a
 * {@link FlowBuilderContext}.
 * 
 * Note this class does not attempt to default any service implementations other than the {@link FlowArtifactFactory}
 * and {@link BeanInvokingActionFactory}, which are more like builder helper objects than services. It is expected
 * clients inject non-null references to concrete service implementations appropriate for their environment.
 * 
 * @see FlowBuilderContextImpl
 * 
 * @author Keith Donald
 */
public class FlowBuilderServices implements ResourceLoaderAware, BeanFactoryAware, InitializingBean {

	/**
	 * The factory encapsulating the creation of central Flow artifacts such as {@link Flow flows} and
	 * {@link State states}.
	 */
	private FlowArtifactFactory flowArtifactFactory = new FlowArtifactFactory();

	/**
	 * The factory encapsulating the creation of bean invoking actions, actions that adapt methods on objects to the
	 * {@link Action} interface.
	 */
	private BeanInvokingActionFactory beanInvokingActionFactory = new BeanInvokingActionFactory();

	/**
	 * The view factory creator for creating views to render during flow execution. The default is <code>null</code>
	 * and this service must be configured externally.
	 */
	private ViewFactoryCreator viewFactoryCreator;

	/**
	 * The conversion service for converting from one object type to another.
	 */
	private ConversionService conversionService;

	/**
	 * The parser for parsing expression strings into expression objects. The default is Web Flow's default expression
	 * parser implementation.
	 */
	private ExpressionParser expressionParser;

	/**
	 * A resource loader that can load resources.
	 */
	private ResourceLoader resourceLoader;

	/**
	 * The Spring bean factory that provides access to the services of the user application.
	 */
	private BeanFactory beanFactory;

	public FlowArtifactFactory getFlowArtifactFactory() {
		return flowArtifactFactory;
	}

	public void setFlowArtifactFactory(FlowArtifactFactory flowArtifactFactory) {
		this.flowArtifactFactory = flowArtifactFactory;
	}

	public BeanInvokingActionFactory getBeanInvokingActionFactory() {
		return beanInvokingActionFactory;
	}

	public void setBeanInvokingActionFactory(BeanInvokingActionFactory beanInvokingActionFactory) {
		this.beanInvokingActionFactory = beanInvokingActionFactory;
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		return viewFactoryCreator;
	}

	public void setViewFactoryCreator(ViewFactoryCreator viewFactoryCreator) {
		this.viewFactoryCreator = viewFactoryCreator;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	// implementing InitializingBean

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowArtifactFactory, "The flow artifact factory is required");
		Assert.notNull(beanInvokingActionFactory, "The bean invoking action factory is required");
		Assert.notNull(viewFactoryCreator, "The view factory creator is required");
		Assert.notNull(conversionService, "The type conversion service is required");
		Assert.notNull(expressionParser, "The expression parser is required");
		Assert.notNull(resourceLoader, "The resource loader is required");
		Assert.notNull(beanFactory, "The bean factory is required");
	}

}