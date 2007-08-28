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
package org.springframework.webflow.definition.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.engine.builder.FlowServiceLocator;

/**
 * A flow definition registrar that populates a flow definition registry from flow definitions defined within
 * externalized resources. Encapsulates registration behavior common to all externalized registrars and is not tied to a
 * specific flow definition format (e.g. xml).
 * <p>
 * Concrete subclasses are expected to derive from this class to provide knowledge about a particular kind of definition
 * format by implementing the abstract template methods in this class.
 * 
 * @see org.springframework.webflow.definition.registry.FlowDefinitionResource
 * @see org.springframework.webflow.definition.registry.FlowDefinitionRegistry
 * 
 * @author Keith Donald
 * @author Ben Hale
 */
public abstract class ExternalizedFlowDefinitionRegistrar implements FlowDefinitionRegistrar {

	/**
	 * The locator of services needed by flow definitions.
	 */
	private FlowServiceLocator flowServiceLocator;

	/**
	 * A set of mappings between a namespace and a set of externalized flow definitions. A map of Strings to Sets
	 * containing {@link FlowDefinitionResource}s
	 */
	private Map namespaceFlowMappings;

	/**
	 * The default namespace for flows registered without an explicit namespace.
	 */
	private String defaultNamespace = "";

	/**
	 * Creates a new registrar with an empty initial set of namespace to flow mappings.
	 */
	public ExternalizedFlowDefinitionRegistrar() {
		this(new HashMap());
	}

	/**
	 * Creates a new registrar with an initial set of namespace to flow mappings.
	 * @param namespaceFlowMappings the initial set of namespace to flow mappings
	 */
	public ExternalizedFlowDefinitionRegistrar(Map namespaceFlowMappings) {
		this.namespaceFlowMappings = namespaceFlowMappings;
	}

	/**
	 * Sets the default namespace to register flows in. If not set the default namespace is "" (an empty string).
	 * @param defaultNamespace the default namespace
	 */
	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	public void setFlowServiceLocator(FlowServiceLocator flowServiceLocator) {
		this.flowServiceLocator = flowServiceLocator;
	}

	/**
	 * Returns the flow service locator for use by subclasses.
	 */
	protected FlowServiceLocator getFlowServiceLocator() {
		return flowServiceLocator;
	}

	/**
	 * Adds an externalized XML flow definition to be registered in the default namespace.
	 * @param location the resource to register
	 * @see #addLocation(Resource, String)
	 * @see #setDefaultNamespace(String)
	 */
	public boolean addLocation(Resource location) {
		return addLocation(location, defaultNamespace);
	}

	/**
	 * Adds an externalized XML flow definition to be registered. The resource will be assigned a registry identifier
	 * equal to the filename of the resource, minus the filename extension. For example, an XML-based flow definition
	 * defined in the file <code>flow1.xml</code> will be identified as <code>flow1</code> in the registry created
	 * by this factory bean.
	 * @param location the resource to register
	 * @param namespace the namespace to register the flow definition in
	 */
	public boolean addLocation(Resource location, String namespace) {
		return getFlows(namespace).add(new FlowDefinitionResource(location));
	}

	/**
	 * Adds an externalized XML flow definition resource to be registered in the default namespace.
	 * @param resource the flow definition resource to be registered
	 * @see #addResource(FlowDefinitionResource, String)
	 * @see #setDefaultNamespace(String)
	 */
	public boolean addResource(FlowDefinitionResource resource) {
		return addResource(resource, defaultNamespace);
	}

	/**
	 * Adds an externalized XML flow definition resource to be registered.
	 * @param resource the flow definition resource to be registered
	 * @param namespace the namespace to register the flow definition in
	 */
	public boolean addResource(FlowDefinitionResource resource, String namespace) {
		return getFlows(namespace).add(resource);
	}

	public void registerFlowDefinitions(FlowDefinitionRegistry registry) {
		for (Iterator mappings = namespaceFlowMappings.entrySet().iterator(); mappings.hasNext();) {
			Map.Entry mapping = (Map.Entry) mappings.next();
			String namespace = (String) mapping.getKey();
			for (Iterator resources = ((Set) mapping.getValue()).iterator(); resources.hasNext();) {
				FlowDefinitionResource resource = (FlowDefinitionResource) resources.next();
				register(resource, namespace, registry);
			}
		}
	}

	/**
	 * Registers a flow definition resource in a given namespace.
	 * @param resource the resource to register
	 * @param namespace the namespace to register in
	 * @param registry the registry
	 */
	private void register(FlowDefinitionResource resource, String namespace, FlowDefinitionRegistry registry) {
		registry.registerFlowDefinition(createFlowDefinitionHolder(resource), namespace);
	}

	/**
	 * Returns the set of flows to be registered in a namespace.
	 * @param namespace The namespace for the collection to be returned
	 */
	private Set getFlows(String namespace) {
		if (!namespaceFlowMappings.containsKey(namespace)) {
			namespaceFlowMappings.put(namespace, new HashSet());
		}
		return (Set) namespaceFlowMappings.get(namespace);
	}

	// sub-classing hooks

	/**
	 * Template factory method subclasses must override to return the holder for the flow definition to be registered
	 * loaded from the specified resource.
	 * @param resource the externalized resource
	 * @return the flow definition holder
	 */
	protected abstract FlowDefinitionHolder createFlowDefinitionHolder(FlowDefinitionResource resource);

	public String toString() {
		return new ToStringCreator(this).append("namespaceFlowMappings", namespaceFlowMappings).toString();
	}
}