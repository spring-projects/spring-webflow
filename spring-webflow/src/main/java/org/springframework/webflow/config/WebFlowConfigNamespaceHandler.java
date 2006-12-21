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
package org.springframework.webflow.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <code>NamespaceHandler</code> for the <code>webflow-config</code> namespace.
 * <p>
 * Provides {@link BeanDefinitionParser bean definition parsers} for the
 * <code>&lt;executor&gt;</code> and <code>&lt;registry&gt;</code> tags. An
 * <code>executor</code> tag can include an <code>execution-listeners</code>
 * tag and a <code>registry</code> tag can include <code>location</code>
 * tags.
 * <p>
 * Using the <code>executor</code> tag you can configure a
 * {@link FlowExecutorFactoryBean} that creates a
 * {@link org.springframework.webflow.executor.FlowExecutor}. The
 * <code>executor</code> tag allows you to specify the repository type and a
 * reference to a registry.
 * 
 * <pre class="code">
 *       &lt;flow:executor id=&quot;registry&quot; registry-ref=&quot;registry&quot; repository-type=&quot;continuation&quot; &gt;
 *           &lt;flow:execution-listeners&gt;
 *               &lt;flow:listener ref=&quot;listener1&quot; /&gt;
 *               &lt;flow:listener ref=&quot;listener2&quot; ref=&quot;*&quot; /&gt;
 *               &lt;flow:listener ref=&quot;listener3&quot; ref=&quot;flow1, flow2, flow3&quot; /&gt;
 *           &lt;flow:execution-listeners /&gt;
 *       &lt;/flow:executor&gt;
 * </pre>
 * 
 * <p>
 * Using the <code>registry</code> tag you can configure an
 * {@link org.springframework.webflow.engine.builder.xml.XmlFlowRegistryFactoryBean}
 * to create a registry for use by any number of <code>executor</code>s. The
 * <code>registry</code> tag supports in-line flow definition locations.
 * 
 * <pre class="code">
 *       &lt;flow:registry id=&quot;registry&quot;&gt;
 *           &lt;flow:location path=&quot;/path/to/flow.xml&quot; /&gt;
 *           &lt;flow:location path=&quot;/path/with/wildcards/*-flow.xml&quot; /&gt;
 *       &lt;/flow:registry&gt;
 * </pre>
 * 
 * @author Ben Hale
 */
public class WebFlowConfigNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("execution-attributes", new ExecutionAttributesBeanDefinitionParser());
		registerBeanDefinitionParser("execution-listeners", new ExecutionListenersBeanDefinitionParser());
		registerBeanDefinitionParser("executor", new ExecutorBeanDefinitionParser());
		registerBeanDefinitionParser("registry", new RegistryBeanDefinitionParser());
	}
}