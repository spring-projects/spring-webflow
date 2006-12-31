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

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.registry.ExternalizedFlowDefinitionRegistrar;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowServiceLocator;
import org.springframework.webflow.engine.builder.RefreshableFlowDefinitionHolder;

/**
 * A flow definition registrar that populates a flow definition registry with
 * flow definitions defined in externalized XML resources. Typically used in
 * conjunction with a {@link XmlFlowRegistryFactoryBean} but may also be used
 * standalone in programmatic fashion.
 * <p>
 * By default, a flow definition registered by this registrar will be assigned a
 * registry identifier equal to the filename of the underlying definition
 * resource, minus the filename extension. For example, a XML-based flow
 * definition defined in the file "flow1.xml" will be identified as "flow1" when
 * registered in a registry.
 * <p>
 * Programmatic usage example:
 * 
 * <pre class="code">
 *     BeanFactory beanFactory = ...
 *     FlowDefinitionRegistry registry = new FlowDefinitionRegistryImpl();
 *     FlowServiceLocator flowServiceLocator =
 *         new DefaultFlowServiceLocator(registry, beanFactory);
 *     XmlFlowRegistrar registrar = new XmlFlowRegistrar(flowServiceLocator);
 *     File parent = new File(&quot;src/webapp/WEB-INF&quot;);
 *     registrar.addLocation(new FileSystemResource(new File(parent, &quot;flow1.xml&quot;));
 *     registrar.addLocation(new FileSystemResource(new File(parent, &quot;flow2.xml&quot;));
 *     registrar.registerFlowDefinitions(registry);
 * </pre>
 * 
 * @author Keith Donald
 */
public class XmlFlowRegistrar extends ExternalizedFlowDefinitionRegistrar {

	/**
	 * The xml file suffix constant.
	 */
	private static final String XML_SUFFIX = ".xml";

	/**
	 * The locator of services needed by flow definitions.
	 */
	private FlowServiceLocator flowServiceLocator;

	/**
	 * The loader of XML-based flow definition documents.
	 */
	private DocumentLoader documentLoader;

	/**
	 * Creates a new XML flow registrar. Protected constructor - if used, make
	 * sure the required {@link #flowServiceLocator} reference is set.
	 */
	protected XmlFlowRegistrar() {
	}

	/**
	 * Creates a new XML flow registrar.
	 * @param flowServiceLocator the locator needed to support flow definition
	 * assembly
	 */
	public XmlFlowRegistrar(FlowServiceLocator flowServiceLocator) {
		setFlowServiceLocator(flowServiceLocator);
	}

	/**
	 * Sets the flow service locator.
	 * @param flowServiceLocator the flow service locator (may not be null)
	 */
	protected void setFlowServiceLocator(FlowServiceLocator flowServiceLocator) {
		Assert.notNull(flowServiceLocator, "The flow service locator is required");
		this.flowServiceLocator = flowServiceLocator;
	}

	/**
	 * Returns the flow service locator.
	 */
	protected FlowServiceLocator getFlowServiceLocator() {
		return flowServiceLocator;
	}

	/**
	 * Sets the loader to load XML-based flow definition documents during flow
	 * definition assembly. Allows for customization over how documents are
	 * loaded. Optional.
	 * @param documentLoader the document loader
	 */
	public void setDocumentLoader(DocumentLoader documentLoader) {
		this.documentLoader = documentLoader;
	}
	
	/**
	 * Returns the loader of XML-based flow definition documents.
	 */
	public DocumentLoader getDocumentLoader() {
		return documentLoader;
	}

	protected boolean isFlowDefinitionResource(Resource resource) {
		return resource.getFilename().endsWith(XML_SUFFIX);
	}

	protected FlowDefinitionHolder createFlowDefinitionHolder(FlowDefinitionResource resource) {
		FlowBuilder builder = createFlowBuilder(resource.getLocation());
		FlowAssembler assembler = new FlowAssembler(resource.getId(), resource.getAttributes(), builder);
		return new RefreshableFlowDefinitionHolder(assembler);
	}
	
	// hook methods

	/**
	 * Factory method that creates and fully initializes the XML-based flow
	 * definition builder.
	 * @param location the xml-based resource
	 * @return the builder to build the flow definition from the resource.
	 */
	protected FlowBuilder createFlowBuilder(Resource location) {
		XmlFlowBuilder builder = new XmlFlowBuilder(location, getFlowServiceLocator());
		if (documentLoader != null) {
			builder.setDocumentLoader(documentLoader);
		}
		return builder;
	}
}