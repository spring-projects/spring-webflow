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
package org.springframework.webflow.definition.registry;

/**
 * A management interface for managing flow definition registries at runtime.
 * Provides the ability to query the size and state of the registry, as well as
 * refresh registered flow definitions at runtime.
 * <p>
 * Flow registries that implement this interface may be exposed for management
 * over the JMX protocol. The following is an example of using Spring's JMX
 * <code>MBeanExporter</code> to export a flow registry to an MBeanServer:
 * <pre class="code">
 *     &lt;!-- Creates the registry of flow definitions for this application --&gt;
 *     &lt;bean name=&quot;flowRegistry&quot; class=&quot;org.springframework.webflow...XmlFlowRegistryFactoryBean&quot;&gt;
 *         &lt;property name=&quot;locations&quot; value=&quot;/WEB-INF/flow1.xml&quot;/&gt;
 *     &lt;/bean&gt;
 *  
 *     &lt;!-- Automatically exports the created flowRegistry as an MBean --&gt;
 *     &lt;bean id=&quot;mbeanExporter&quot; class=&quot;org.springframework.jmx.export.MBeanExporter&quot;&gt;
 *         &lt;property name=&quot;beans&quot;&gt;
 *             &lt;map&gt;
 *                 &lt;entry key=&quot;spring-webflow:name=flowRegistry&quot; value-ref=&quot;flowRegistry&quot;/&gt;
 *             &lt;/map&gt;
 *         &lt;/property&gt;
 *         &lt;property name=&quot;assembler&quot;&gt;
 *             &lt;bean class=&quot;org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler&quot;&gt;
 *                 &lt;property name=&quot;managedInterfaces&quot;
 *                     value=&quot;org.springframework.webflow.definition.registry.FlowDefinitionRegistryMBean&quot;/&gt;
 *             &lt;/bean&gt;
 *         &lt;/property&gt;
 *     &lt;/bean&gt;
 * </pre>
 * With the above configuration, you may then use any JMX client (such as Sun's
 * jConsole which ships with JDK 1.5) to refresh flow definitions at runtime.
 * 
 * @author Keith Donald
 */
public interface FlowDefinitionRegistryMBean {

	/**
	 * Returns the ids of the flow definitions registered in this registry.
	 * @return the flow definition ids
	 */
	public String[] getFlowDefinitionIds();

	/**
	 * Return the number of flow definitions registered in this registry.
	 * @return the flow definition count
	 */
	public int getFlowDefinitionCount();

	/**
	 * Queries this registry to determine if a specific flow is contained within
	 * it.
	 * @param id the flow definition id
	 * @return true if a flow definition is contained in this registry with the
	 * id provided
	 */
	public boolean containsFlowDefinition(String id);

	/**
	 * Refresh this flow definition registry, reloading all Flow definitions
	 * from their externalized representations.
	 */
	public void refresh() throws FlowDefinitionConstructionException;

	/**
	 * Refresh the Flow definition in this registry with the <code>id</code>
	 * provided, reloading it from it's externalized representation
	 * @param flowDefinitionId the id of the flow definition to refresh
	 * @throws NoSuchFlowDefinitionException if a flow with the id provided is not
	 * stored in this registry
	 */
	public void refresh(String flowDefinitionId)
			throws NoSuchFlowDefinitionException, FlowDefinitionConstructionException;

}