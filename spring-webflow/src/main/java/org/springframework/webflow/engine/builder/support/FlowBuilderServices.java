package org.springframework.webflow.engine.builder.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.core.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;

/**
 * A simple holder for services needed by a flow builder. These services are typically exposed via a Flow Builder's
 * {@link FlowBuilderContext}.
 * 
 * @author Keith Donald
 */
public class FlowBuilderServices implements ResourceLoaderAware, BeanFactoryAware {

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
	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * The parser for parsing expression strings into expression objects. The default is Web Flow's default expression
	 * parser implementation.
	 */
	private ExpressionParser expressionParser = DefaultExpressionParserFactory.getExpressionParser();

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
		Assert.notNull(flowArtifactFactory, "The flow artifact factory is required");
		this.flowArtifactFactory = flowArtifactFactory;
	}

	public BeanInvokingActionFactory getBeanInvokingActionFactory() {
		return beanInvokingActionFactory;
	}

	public void setBeanInvokingActionFactory(BeanInvokingActionFactory beanInvokingActionFactory) {
		Assert.notNull(beanInvokingActionFactory, "The bean invoking action factory is required");
		this.beanInvokingActionFactory = beanInvokingActionFactory;
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		return viewFactoryCreator;
	}

	public void setViewFactoryCreator(ViewFactoryCreator viewFactoryCreator) {
		Assert.notNull("The view factory creator cannot be null");
		this.viewFactoryCreator = viewFactoryCreator;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		Assert.notNull(conversionService, "The type conversion service cannot be null");
		this.conversionService = conversionService;
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		Assert.notNull(expressionParser, "The expression parser cannot be null");
		this.expressionParser = expressionParser;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		Assert.notNull("The resource loader cannot be null");
		this.resourceLoader = resourceLoader;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.notNull("The bean factory cannot be null");
		this.beanFactory = beanFactory;
	}
}