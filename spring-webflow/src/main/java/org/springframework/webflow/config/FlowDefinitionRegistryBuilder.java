/*
 * Copyright 2004-2014 the original author or authors.
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
package org.springframework.webflow.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.DefaultFlowHolder;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.model.FlowModelFlowBuilder;
import org.springframework.webflow.engine.builder.support.FlowBuilderContextImpl;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.engine.model.builder.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;

/**
 * A builder for creating {@link FlowDefinitionRegistry} instances designed for programmatic
 * use in {@code @Bean} factory methods. For XML configuration consider using the
 * {@code webflow-config} XML namespace.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class FlowDefinitionRegistryBuilder {

	private final List<FlowLocation> flowLocations = new ArrayList<>();

	private final List<String> flowLocationPatterns = new ArrayList<>();

	private final List<FlowBuilderInfo> flowBuilderInfos = new ArrayList<>();

	private FlowBuilderServices flowBuilderServices;

	private FlowDefinitionRegistry parent;

	private FlowDefinitionResourceFactory flowResourceFactory;


	/**
	 * Create a new instance with the given ApplicationContext.
	 *
	 * @param appContext the ApplicationContext to use for initializing the
	 * 	FlowDefinitionResourceFactory and FlowBuilderServices instances with
	 */
	public FlowDefinitionRegistryBuilder(ApplicationContext appContext) {
		this(appContext, null);
	}

	/**
	 * Create a new instance with the given ApplicationContext and {@link FlowBuilderServices}.
	 *
	 * @param appContext the ApplicationContext to use for initializing the
	 * 	FlowDefinitionResourceFactory and FlowBuilderServices instances with
	 * @param builderServices a {@link FlowBuilderServices} instance to configure
	 * 	on the FlowDefinitionRegistry
	 */
	public FlowDefinitionRegistryBuilder(ApplicationContext appContext, FlowBuilderServices builderServices) {
		Assert.notNull(appContext, "applicationContext is required");
		this.flowResourceFactory = new FlowDefinitionResourceFactory(appContext);
		if (builderServices != null) {
			this.flowBuilderServices = builderServices;
		}
		else {
			this.flowBuilderServices = new FlowBuilderServicesBuilder().build();
			this.flowBuilderServices.setApplicationContext(appContext);
		}
	}


	/**
	 * Configure the base path where flow definitions are found. When specified, all
	 * flow locations are relative to this path. Also when specified, by default flows
	 * are assigned an id equal to the the path segment between their base path and
	 * file name.
	 * <p>
	 * For example, if a flow definition is located at
	 * '/WEB-INF/hotels/booking/booking-flow.xml' and the base path is '/WEB-INF', the
	 * remaining path to this flow is 'hotels/booking' which then becomes the flow id.
	 * <p>
	 * If a flow definition is found directly on the base path, the file name minus
	 * its extension is used as the flow id.
	 * @param basePath the base path to use
	 */
	public FlowDefinitionRegistryBuilder setBasePath(String basePath) {
		if (basePath != null) {
			this.flowResourceFactory.setBasePath(basePath);
		}
		return this;
	}

	/**
	 * Register a flow defined at the following location as an .xml file.
	 * This may be a path to a single resource or a ANT-style path expression that
	 * matches multiple resources.
	 * @param path the resource path to the externalized flow definition resource.
	 */
	public FlowDefinitionRegistryBuilder addFlowLocation(String path) {
		this.addFlowLocation(path, null, null);
		return this;
	}

	/**
	 * Register a flow defined at the following location as an .xml file.
	 * This may be a path to a single resource or a ANT-style path expression that
	 * matches multiple resources.
	 * @param path the resource path to the externalized flow definition resource.
	 * @param id the unique id to assign to the added flow definition in the registry
	 * 	Specify only if you wish to provide a custom flow definition identifier.
	 */
	public FlowDefinitionRegistryBuilder addFlowLocation(String path, String id) {
		this.flowLocations.add(new FlowLocation(path, id, null));
		return this;
	}

	/**
	 * Register a flow defined at the following location as an .xml file.
	 * This may be a path to a single resource or a ANT-style path expression that
	 * matches multiple resources.
	 * @param path the resource path to the externalized flow definition resource.
	 * @param id the unique id to assign to the added flow definition in the registry
	 * 	Specify only if you wish to provide a custom flow definition identifier.
	 * @param attributes meta-attributes to assign to the flow definition
	 */
	public FlowDefinitionRegistryBuilder addFlowLocation(String path, String id, Map<String, Object> attributes) {
		this.flowLocations.add(new FlowLocation(path, id, attributes));
		return this;
	}

	/**
	 * Registers a set of flows resolved from a resource location pattern.
	 * @param pattern the pattern to use
	 */
	public FlowDefinitionRegistryBuilder addFlowLocationPattern(String pattern) {
		this.flowLocationPatterns.add(pattern);
		return this;
	}

	/**
	 * Set the {@link FlowBuilderServices} to use for defining custom services needed
	 * to build the flows registered in this registry.
	 * @param flowBuilderServices the {@link FlowBuilderServices} instance
	 */
	public FlowDefinitionRegistryBuilder setFlowBuilderServices(FlowBuilderServices flowBuilderServices) {
		this.flowBuilderServices = flowBuilderServices;
		return this;
	}

	/**
	 * Register a custom {@link FlowBuilder} instance.
	 * @param builder the FlowBuilder to configure
	 */
	public FlowDefinitionRegistryBuilder addFlowBuilder(FlowBuilder builder) {
		addFlowBuilder(builder, null, null);
		return this;
	}

	/**
	 * Register a custom {@link FlowBuilder} instance with the given flow id.
	 * @param builder the FlowBuilder to configure
	 * @param id the id assign to the flow definition in this registry.
	 * 	Specify when you wish to provide a custom flow definition identifier.
	 */
	public FlowDefinitionRegistryBuilder addFlowBuilder(FlowBuilder builder, String id) {
		addFlowBuilder(builder, id, null);
		return this;
	}

	/**
	 * Register a custom {@link FlowBuilder} instance with the given flow id.
	 * @param builder the FlowBuilder to configure
	 * @param id the id assign to the flow definition in this registry.
	 * 	Specify when you wish to provide a custom flow definition identifier.
	 * @param attributes attributes to assign to the flow definition.
	 */
	public FlowDefinitionRegistryBuilder addFlowBuilder(FlowBuilder builder, String id, Map<String, Object> attributes) {
		if (!StringUtils.hasText(id)) {
			id = StringUtils.uncapitalize(StringUtils.delete(
					ClassUtils.getShortName(builder.getClass()), "FlowBuilder"));
		}
		this.flowBuilderInfos.add(new FlowBuilderInfo(builder, id, attributes));
		return this;
	}

	/**
	 * Configure a parent registry. Registries can be organized in a hierarchy.
	 * If a child registry does not contain a flow, its parent registry is queried.
	 * @param parent the parent registry
	 */
	public FlowDefinitionRegistryBuilder setParent(FlowDefinitionRegistry parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * Create and return a {@link FlowDefinitionRegistry} instance.
	 */
	public FlowDefinitionRegistry build() {

		DefaultFlowRegistry flowRegistry = new DefaultFlowRegistry();
		flowRegistry.setParent(this.parent);

		registerFlowLocations(flowRegistry);
		registerFlowLocationPatterns(flowRegistry);
		registerFlowBuilders(flowRegistry);

		return flowRegistry;
	}

	private void registerFlowLocations(DefaultFlowRegistry flowRegistry) {
		for (FlowLocation location : this.flowLocations) {
			String path = location.getPath();
			String id = location.getId();
			AttributeMap<Object> attributes = location.getAttributes();
			updateFlowAttributes(attributes);
			FlowDefinitionResource resource = this.flowResourceFactory.createResource(path, attributes, id);
			registerFlow(resource, flowRegistry);
		}
	}

	private void registerFlowLocationPatterns(DefaultFlowRegistry flowRegistry) {
		for (String pattern : this.flowLocationPatterns) {
			AttributeMap<Object> attributes = new LocalAttributeMap<>();
			updateFlowAttributes(attributes);
			FlowDefinitionResource[] resources;
			try {
				resources = this.flowResourceFactory.createResources(pattern, attributes);
			} catch (IOException e) {
				IllegalStateException ise = new IllegalStateException(
						"An I/O Exception occurred resolving the flow location pattern '" + pattern + "'");
				ise.initCause(e);
				throw ise;
			}
			for (FlowDefinitionResource resource : resources) {
				registerFlow(resource, flowRegistry);
			}
		}
	}

	private void registerFlow(FlowDefinitionResource resource, DefaultFlowRegistry flowRegistry) {
		FlowModelBuilder flowModelBuilder;
		if (resource.getPath().getFilename().endsWith(".xml")) {
			flowModelBuilder = new XmlFlowModelBuilder(resource.getPath(), flowRegistry.getFlowModelRegistry());
		} else {
			throw new IllegalArgumentException(resource
					+ " is not a supported resource type; supported types are [.xml]");
		}
		FlowModelHolder flowModelHolder = new DefaultFlowModelHolder(flowModelBuilder);
		FlowBuilder flowBuilder = new FlowModelFlowBuilder(flowModelHolder);
		FlowBuilderContext builderContext = new FlowBuilderContextImpl(
				resource.getId(), resource.getAttributes(), flowRegistry, this.flowBuilderServices);
		FlowAssembler assembler = new FlowAssembler(flowBuilder, builderContext);
		DefaultFlowHolder flowHolder = new DefaultFlowHolder(assembler);

		flowRegistry.getFlowModelRegistry().registerFlowModel(resource.getId(), flowModelHolder);
		flowRegistry.registerFlowDefinition(flowHolder);
	}

	private void registerFlowBuilders(DefaultFlowRegistry flowRegistry) {
		for (FlowBuilderInfo info : this.flowBuilderInfos) {
			AttributeMap<Object> attributes = info.getAttributes();
			updateFlowAttributes(attributes);
			FlowBuilderContext builderContext = new FlowBuilderContextImpl(
					info.getId(), attributes, flowRegistry, this.flowBuilderServices);
			FlowAssembler assembler = new FlowAssembler(info.getBuilder(), builderContext);
			flowRegistry.registerFlowDefinition(assembler.assembleFlow());
		}
	}

	private void updateFlowAttributes(AttributeMap<Object> attributes) {
		if (this.flowBuilderServices.getDevelopment()) {
			attributes.asMap().put("development", true);
		}
	}


	private static class FlowLocation {

		private final String path;

		private final String id;

		private final AttributeMap<Object> attributes;


		public FlowLocation(String path, String id, Map<String, Object> attributes) {
			this.path = path;
			this.id = id;
			this.attributes = (attributes != null) ?
					new LocalAttributeMap<>(attributes) :
					new LocalAttributeMap<>(new HashMap<>());
		}

		public String getPath() {
			return this.path;
		}

		public String getId() {
			return this.id;
		}

		public AttributeMap<Object> getAttributes() {
			return this.attributes;
		}
	}

	private static class FlowBuilderInfo {

		private final FlowBuilder builder;

		private final String id;

		private final AttributeMap<Object> attributes;

		public FlowBuilderInfo(FlowBuilder builder, String id, Map<String, Object> attributes) {
			this.builder = builder;
			this.id = id;
			this.attributes = (attributes != null) ?
					new LocalAttributeMap<>(attributes) :
					new LocalAttributeMap<>(new HashMap<>());
		}

		public FlowBuilder getBuilder() {
			return this.builder;
		}

		public String getId() {
			return this.id;
		}

		public AttributeMap<Object> getAttributes() {
			return this.attributes;
		}
	}


}
