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

/**
 * A strategy to use to populate a flow definition registry with one or more flow
 * definitions.
 * <p>
 * Flow definition registrars encapsulate the knowledge about the source of a set of flow
 * definition resources and the behavior necessary to add those resources to a
 * flow definition registry.
 * <p>
 * The typical usage pattern is as follows:
 * <ol>
 * <li>Create a new (initially empty) flow definition registry.
 * <li>Use any number of flow definition registrars to populate the registry by calling
 * {@link #registerFlowDefinitions(FlowDefinitionRegistry)}.
 * </ol>
 * <p>
 * This design where various registrars populate a generic registry was
 * inspired by Spring's GenericApplicationContext, which can use any number of
 * BeanDefinitionReaders to drive context population.
 * 
 * @see FlowDefinitionRegistry
 * 
 * @author Keith Donald
 */
public interface FlowDefinitionRegistrar {

	/**
	 * Register flow definition resources managed by this registrar in the
	 * registry provided.
	 * @param registry the registry to register flow definitions in
	 */
	public void registerFlowDefinitions(FlowDefinitionRegistry registry);
}