/*
 * Copyright 2002-2006 the original author or authors.
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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.AbstractFlowBuildingFlowRegistryFactoryBean;
import org.springframework.webflow.engine.builder.DefaultFlowServiceLocator;
import org.springframework.webflow.engine.builder.FlowServiceLocator;

/**
 * A factory bean that produces a populated flow registry using an
 * {@link XmlFlowRegistrar}. This is the simplest implementation to use when
 * using a Spring BeanFactory to deploy an explicit registry of XML-based Flow
 * definitions for execution.
 * <p>
 * By default, a configured flow definition will be assigned a registry
 * identifier equal to the filename of the underlying definition resource, minus
 * the filename extension. For example, an XML-based flow definition defined in
 * the file <code>flow1.xml</code> will be identified as <code>flow1</code>
 * in the registry created by this factory bean.
 * <p>
 * This class is also <code>BeanFactoryAware</code> and when used with Spring
 * will automatically create a configured {@link DefaultFlowServiceLocator} for
 * loading Flow artifacts like Actions from the Spring bean factory during the
 * Flow registration process.
 * <p>
 * This class is also <code>ResourceLoaderAware</code>; when an instance is
 * created by a Spring BeanFactory the factory will automatically configure the
 * XmlFlowRegistrar with a context-relative resource loader for accessing other
 * resources during Flow assembly.
 * 
 * Usage example:
 * 
 * <pre>
 *     &lt;bean id=&quot;flowRegistry&quot; class=&quot;org.springframework.webflow.engine.builder.registry.XmlFlowRegistryFactoryBean&quot;&gt;
 *         &lt;property name=&quot;flowLocations&quot;&gt; value=&quot;/WEB-INF/flows/*-flow.xml&quot;/&gt; 
 *     &lt;/bean&gt;
 * </pre>
 * 
 * @author Keith Donald
 */
public class XmlFlowRegistryFactoryBean extends AbstractFlowBuildingFlowRegistryFactoryBean {

	/**
	 * The flow registrar that will perform the definition registrations.
	 */
	private XmlFlowRegistrar flowRegistrar = new XmlFlowRegistrar();
	
	/**
	 * Temporary holder for flow definition locations.
	 */
	private Resource[] locations; 

	/**
	 * Temporary holder for flow definitions configured using a property map.
	 */
	private Properties flowDefinitions;
	
	/**
	 * A map that contains a map (java.util.Map) of flow attributes keyed by flow id (String).
	 */
	private Map flowAttributes;

	/**
	 * Returns the configured externalized XML flow registrar.
	 */
	protected XmlFlowRegistrar getXmlFlowRegistrar() {
		return flowRegistrar;
	}

	/**
	 * Sets the locations (resource file paths) pointing to XML-based flow
	 * definitions.
	 * <p>
	 * When configuring as a Spring bean definition, ANT-style resource
	 * patterns/wildcards are also supported, taking advantage of Spring's built
	 * in ResourceArrayPropertyEditor machinery.
	 * <p>
	 * For example:
	 * 
	 * <pre>
	 *     &lt;bean id=&quot;flowRegistry&quot; class=&quot;org.springframework.webflow.engine.builder.xml.XmlFlowRegistryFactoryBean&quot;&gt;
	 *         &lt;property name=&quot;flowLocations&quot;&gt; value=&quot;/WEB-INF/flows/*-flow.xml&quot;/&gt; 
	 *     &lt;/bean&gt;
	 * </pre>
	 * 
	 * Another example:
	 * 
	 * <pre>
	 *    &lt;bean id=&quot;flowRegistry&quot; class=&quot;org.springframework.webflow.engine.builder.xml.XmlFlowRegistryFactoryBean&quot;&gt;
	 *          &lt;property name=&quot;flowLocations&quot;&gt; value=&quot;classpath*:/example/flows/*-flow.xml&quot;/&gt; 
	 *    &lt;/bean&gt;
	 * </pre>
	 * 
	 * Flows registered from this set will be automatically assigned an id based
	 * on the filename of the matched XML resource.
	 * @param locations the resource locations
	 */
	public void setFlowLocations(Resource[] locations) {
		this.locations = locations;
	}

	/**
	 * Convenience method for setting externalized flow definitions
	 * from a <code>java.util.Properties</code> map. Allows for more control
	 * over the definition, including which <code>flowId</code> is assigned.
	 * <p>
	 * Each property key is the <code>flowId</code> and each property value is
	 * the string encoded location of the externalized flow definition resource.
	 * <p>
	 * Here is the exact format:
	 * 
	 * <pre>
	 *      flowId=resource
	 * </pre>
	 * 
	 * For example:
	 * 
	 * <pre>
	 *     &lt;bean id=&quot;flowRegistry&quot; class=&quot;org.springframework.webflow.engine.builder.xml.XmlFlowRegistryFactoryBean&quot;&gt;
	 *         &lt;property name=&quot;flowDefinitions&quot;&gt;
	 *             &lt;value&gt;
	 *                 searchFlow=/WEB-INF/flows/search-flow.xml
	 *                 detailFlow=/WEB-INF/flows/detail-flow.xml
	 *             &lt;/value&gt;
	 *         &lt;/property&gt;
	 *     &lt;/bean&gt;
	 * </pre>
	 * @param flowDefinitions the flow definitions, defined within a properties
	 * map
	 */
	public void setFlowDefinitions(Properties flowDefinitions) {
		this.flowDefinitions = flowDefinitions;
	}
	
	/**
	 * Sets flow attributes from an externalized <code>java.util.Map</code>. The keys in the
	 * map are String flow ids. The corresponding values should be <code>java.util.Map</code>
	 * maps containing flow attributes to be assigned to the flow. A flow with an id not
	 * contained in the provided map will get not externally defined flow attributes assigned.
	 * <p>
	 * Can be used in conjunction with both {@link #setFlowLocations(Resource[])}
	 * and {@link #setFlowDefinitions(Properties)}.
	 * @param flowAttributes the flow attributes, keyed by flow id
	 */
	public void setFlowAttributes(Map flowAttributes) {
		this.flowAttributes = flowAttributes;
	}

	/**
	 * Sets the loader to load XML-based flow definition documents during flow
	 * definition assembly. Allows for customization over how flow definition 
	 * documents are loaded. Optional.
	 * @param documentLoader the document loader
	 */
	public void setDocumentLoader(DocumentLoader documentLoader) {
		getXmlFlowRegistrar().setDocumentLoader(documentLoader);
	}

	protected void init(FlowServiceLocator flowServiceLocator) {
		// simply wire in the locator to the registrar 
		flowRegistrar.setFlowServiceLocator(flowServiceLocator);
	}

	protected void doPopulate(FlowDefinitionRegistry registry) {
		addFlowDefinitionLocations();
		addFlowDefinitionsFromProperties();
		getXmlFlowRegistrar().registerFlowDefinitions(registry);
	}
	
	/**
	 * Add configured flow definition locations to the flow definition
	 * registrar. 
	 */
	private void addFlowDefinitionLocations() {
		if (locations != null) {
			for (int i = 0; i < locations.length; i++) {
				String flowId = FlowDefinitionResource.conventionalFlowId(locations[i]);
				getXmlFlowRegistrar().addResource(
						new FlowDefinitionResource(flowId, locations[i], getFlowAttributes(flowId)));
			}
		}
	}

	/**
	 * Add flow definitions configured using a property map to 
	 * the flow definition registrar.
	 */
	private void addFlowDefinitionsFromProperties() {
		if (flowDefinitions != null) {
			Iterator it = flowDefinitions.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String flowId = (String)entry.getKey();
				String location = (String)entry.getValue();
				Resource resource = getFlowServiceLocator().getResourceLoader().getResource(location);
				getXmlFlowRegistrar().addResource(
						new FlowDefinitionResource(flowId, resource, getFlowAttributes(flowId)));
			}
		}
	}
	
	/**
	 * Returns the flow attributes to be assigned to the flow with given id. Returns
	 * null if no attributes should be assigned. 
	 */
	private AttributeMap getFlowAttributes(String flowId) {
		if (flowAttributes != null) {
			Map attributes = (Map)flowAttributes.get(flowId);
			if (attributes != null) {
				return new LocalAttributeMap(attributes);
			}
		}
		return null;
	}
}