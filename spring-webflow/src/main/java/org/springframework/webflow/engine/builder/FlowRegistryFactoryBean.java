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
package org.springframework.webflow.engine.builder;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistrar;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.xml.XmlFlowRegistrar;

/**
 * A factory bean that accepts an arbitrary list of {@link FlowDefinitionRegistrar}s and uses them to register flows.
 * This implementation should not be used programmatically but rather from the spring-webflow-config XML namespace.
 * <p>
 * Example Usage:
 * 
 * <pre>
 * &lt;flow:registry id=&quot;flowRegistry&quot;&gt;
 *     &lt;flow:location path=&quot;flow1.xml&quot;/&gt;
 *     &lt;flow:location id=&quot;foo&quot; path=&quot;flow2.xml&quot;/&gt;
 *     &lt;flow:class name=&quot;BarClass&quot;/&gt;
 *     &lt;flow:crud entity=&quot;Account&quot;/&gt;
 *     &lt;flow:namespace name=&quot;baz&quot;&gt;
 *         &lt;flow:location path=&quot;flow3.xml&quot;/&gt;
 *         &lt;flow:class name=&quot;XyzClass&quot;/&gt;
 *     &lt;/flow:namespace&gt;
 * &lt;/flow:registry&gt;
 * </pre>
 * 
 * @author Ben Hale
 */
public class FlowRegistryFactoryBean implements FactoryBean, InitializingBean, BeanFactoryAware {

	private FlowDefinitionRegistry registry;

	private BeanFactory beanFactory;

	private FlowServiceLocator flowServiceLocator;

	private Map xmlNamespaceFlowMappings;

	public void setFlowServiceLocator(FlowServiceLocator flowServiceLocator) {
		this.flowServiceLocator = flowServiceLocator;
	}

	public void setXmlNamespaceFlowMappings(Map xmlNamespaceFlowMappings) {
		this.xmlNamespaceFlowMappings = xmlNamespaceFlowMappings;
	}

	public void afterPropertiesSet() throws Exception {
		this.registry = new FlowDefinitionRegistryImpl();
		initXml(registry);
	}

	public Object getObject() throws Exception {
		return registry;
	}

	public Class getObjectType() {
		return FlowDefinitionRegistry.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private void initXml(FlowDefinitionRegistry registry) {
		XmlFlowRegistrar registrar = new XmlFlowRegistrar(getFlowServiceLocator(registry));
		for (Iterator mappings = xmlNamespaceFlowMappings.entrySet().iterator(); mappings.hasNext();) {
			Map.Entry mapping = (Map.Entry) mappings.next();
			String namespace = (String) mapping.getKey();
			for (Iterator resources = ((Set) mapping.getValue()).iterator(); resources.hasNext();) {
				FlowDefinitionResource resource = (FlowDefinitionResource) resources.next();
				registrar.addResource(resource, namespace);
			}
		}
		registrar.registerFlowDefinitions(registry);
	}

	private FlowServiceLocator getFlowServiceLocator(FlowDefinitionRegistry registry) {
		if (flowServiceLocator == null) {
			this.flowServiceLocator = new DefaultFlowServiceLocator(registry, beanFactory);
		}
		return flowServiceLocator;
	}
}
