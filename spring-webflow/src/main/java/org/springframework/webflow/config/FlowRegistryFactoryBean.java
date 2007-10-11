package org.springframework.webflow.config;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.RefreshableFlowDefinitionHolder;
import org.springframework.webflow.engine.builder.support.FlowBuilderContextImpl;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilder;

/**
 * A factory for a flow definition registry. Is a Spring FactoryBean, for provision by the flow definition registry bean
 * definition parser. Is package-private, as people should not be using this class directly, but rather through the
 * higher-level webflow-config Spring 2.x configuration namespace.
 * 
 * @author Keith Donald
 */
class FlowRegistryFactoryBean implements FactoryBean, ResourceLoaderAware, BeanFactoryAware, InitializingBean {

	/**
	 * The definition registry produced by this factory bean.
	 */
	private FlowDefinitionRegistry flowRegistry;

	/**
	 * Flow definitions defined in external files that should be registered in the registry produced by this factory
	 * bean.
	 */
	private FlowLocation[] flowLocations;

	/**
	 * Java {@link FlowBuilder flow builder} classes that should be registered in the registry produced by this factory
	 * bean.
	 */
	private FlowBuilderInfo[] flowBuilders;

	/**
	 * The holder for services needed to build flow definitions registered in this registry.
	 */
	private FlowBuilderServices flowBuilderServices;

	/**
	 * A helper for creating abstract representation of externalized flow definition resources.
	 */
	private FlowDefinitionResourceFactory flowResourceFactory;

	/**
	 * The container's resource loader.
	 */
	private ResourceLoader resourceLoader;

	/**
	 * The containing bean factory this factory bean was deployed in.
	 */
	private BeanFactory beanFactory;

	public void setFlowLocations(FlowLocation[] flowLocations) {
		this.flowLocations = flowLocations;
	}

	public void setFlowBuilders(FlowBuilderInfo[] flowBuilders) {
		this.flowBuilders = flowBuilders;
	}

	public void setFlowBuilderServices(FlowBuilderServices flowBuilderServices) {
		this.flowBuilderServices = flowBuilderServices;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void afterPropertiesSet() throws Exception {
		if (flowBuilderServices == null) {
			initFlowBuilderServices();
		}
		flowResourceFactory = new FlowDefinitionResourceFactory(resourceLoader);
		flowRegistry = new FlowDefinitionRegistryImpl();
		registerFlowLocations();
		registerFlowBuilders();
	}

	public Object getObject() throws Exception {
		return flowRegistry;
	}

	public Class getObjectType() {
		return FlowDefinitionRegistry.class;
	}

	public boolean isSingleton() {
		return true;
	}

	private void registerFlowLocations() {
		if (flowLocations != null) {
			for (int i = 0; i < flowLocations.length; i++) {
				FlowLocation location = flowLocations[i];
				flowRegistry.registerFlowDefinition(createFlowDefinitionHolder(location));
			}
		}
	}

	private void registerFlowBuilders() {
		if (flowBuilders != null) {
			for (int i = 0; i < flowBuilders.length; i++) {
				FlowBuilderInfo builder = flowBuilders[i];
				flowRegistry.registerFlowDefinition(createFlowDefinitionHolder(builder));
			}
		}
	}

	private FlowDefinitionHolder createFlowDefinitionHolder(FlowLocation location) {
		FlowDefinitionResource flowResource = createResource(location);
		FlowBuilder builder = createFlowBuilder(flowResource);
		FlowBuilderContext builderContext = new FlowBuilderContextImpl(flowResource.getId(), flowResource
				.getAttributes(), flowRegistry, flowBuilderServices);
		FlowAssembler assembler = new FlowAssembler(builder, builderContext);
		return new RefreshableFlowDefinitionHolder(assembler);
	}

	private FlowDefinitionResource createResource(FlowLocation location) {
		AttributeMap flowAttributes = getFlowAttributes(location.getAttributes());
		return flowResourceFactory.createResource(location.getPath(), flowAttributes, location.getId());
	}

	private AttributeMap getFlowAttributes(Set attributes) {
		MutableAttributeMap flowAttributes = null;
		if (!attributes.isEmpty()) {
			flowAttributes = new LocalAttributeMap();
			for (Iterator it = attributes.iterator(); it.hasNext();) {
				FlowElementAttribute attribute = (FlowElementAttribute) it.next();
				flowAttributes.put(attribute.getName(), getConvertedValue(attribute));
			}
		}
		return flowAttributes;
	}

	private FlowBuilder createFlowBuilder(FlowDefinitionResource resource) {
		if (isXml(resource.getPath())) {
			return new XmlFlowBuilder(resource.getPath());
		} else {
			throw new IllegalArgumentException(resource
					+ " is not a supported resource type; supported types are [.xml]");
		}
	}

	private boolean isXml(Resource flowResource) {
		return flowResource.getFilename().endsWith(".xml");
	}

	private Object getConvertedValue(FlowElementAttribute attribute) {
		if (attribute.needsTypeConversion()) {
			ConversionExecutor converter = flowBuilderServices.getConversionService()
					.getConversionExecutorByTargetAlias(String.class, attribute.getType());
			return converter.execute(attribute.getValue());
		} else {
			return attribute.getValue();
		}
	}

	private FlowDefinitionHolder createFlowDefinitionHolder(FlowBuilderInfo builder) {
		ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
		AttributeMap flowAttributes = getFlowAttributes(builder.getAttributes());
		FlowBuilderContext builderContext = new FlowBuilderContextImpl(builder.getId(), flowAttributes, flowRegistry,
				flowBuilderServices);
		return new FlowBuilderCreatingFlowDefinitionHolder(builder.getClassName(), classLoader, builderContext);
	}

	private void initFlowBuilderServices() {
		flowBuilderServices = new FlowBuilderServices();
		flowBuilderServices.setResourceLoader(resourceLoader);
		flowBuilderServices.setBeanFactory(beanFactory);
	}

	class FlowBuilderCreatingFlowDefinitionHolder implements FlowDefinitionHolder {

		private String flowBuilderClassName;

		private ClassLoader classLoader;

		private FlowBuilderContext builderContext;

		public FlowBuilderCreatingFlowDefinitionHolder(String flowBuilderClassName, ClassLoader classLoader,
				FlowBuilderContext builderContext) {
			this.flowBuilderClassName = flowBuilderClassName;
			this.classLoader = classLoader;
			this.builderContext = builderContext;
		}

		public String getFlowDefinitionId() {
			return builderContext.getFlowId();
		}

		public FlowDefinition getFlowDefinition() throws FlowDefinitionConstructionException {
			try {
				Class flowBuilderClass = classLoader.loadClass(flowBuilderClassName);
				FlowBuilder builder = (FlowBuilder) flowBuilderClass.newInstance();
				FlowAssembler assembler = new FlowAssembler(builder, builderContext);
				return assembler.assembleFlow();
			} catch (ClassNotFoundException e) {
				throw new FlowDefinitionConstructionException(getFlowDefinitionId(), e);
			} catch (InstantiationException e) {
				throw new FlowDefinitionConstructionException(getFlowDefinitionId(), e);
			} catch (IllegalAccessException e) {
				throw new FlowDefinitionConstructionException(getFlowDefinitionId(), e);
			}
		}

		public void refresh() throws FlowDefinitionConstructionException {
		}

	}
}