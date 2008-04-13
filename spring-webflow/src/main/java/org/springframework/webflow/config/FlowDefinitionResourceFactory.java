/*
 * Copyright 2004-2008 the original author or authors.
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

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;

/**
 * A factory for creating flow definition resources that serve as pointers to external Flow definition files.
 * 
 * @author Keith Donald
 */
public class FlowDefinitionResourceFactory {

	private ResourceLoader resourceLoader;

	/**
	 * Creates a new flow definition resource factory using a default resource loader.
	 */
	public FlowDefinitionResourceFactory() {
		this.resourceLoader = new DefaultResourceLoader();
	}

	/**
	 * Creates a new flow definition resource factory using the specified resource loader.
	 * @param resourceLoader the resource loader
	 */
	public FlowDefinitionResourceFactory(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "The resource loader cannot be null");
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Create a flow definition resource from the path location provided.
	 * @param path the encoded {@link Resource} path.
	 * @return the flow definition resource
	 */
	public FlowDefinitionResource createResource(String path) {
		return createResource(path, null, null);
	}

	/**
	 * Create a flow definition resource from the path location provided. The returned resource will be configured with
	 * the provided attributes.
	 * @param path the encoded {@link Resource} path.
	 * @param attributes the flow definition meta attributes to configure
	 * @return the flow definition resource
	 */
	public FlowDefinitionResource createResource(String path, AttributeMap attributes) {
		return createResource(path, attributes, null);
	}

	/**
	 * Create a flow definition resource from the path location provided. The returned resource will be configured with
	 * the provided attributes and flow id.
	 * @param path the encoded {@link Resource} path.
	 * @param attributes the flow definition meta attributes to configure
	 * @param flowId the flow definition id to configure
	 * @return the flow definition resource
	 */
	public FlowDefinitionResource createResource(String path, AttributeMap attributes, String flowId) {
		Resource resource = resourceLoader.getResource(path);
		if (flowId == null || flowId.length() == 0) {
			flowId = getFlowId(resource);
		}
		return new FlowDefinitionResource(flowId, resource, attributes);
	}

	/**
	 * Create an array of flow definition resources from the path pattern location provided.
	 * @param pattern the encoded {@link Resource} path pattern.
	 * @return the flow definition resources
	 */
	public FlowDefinitionResource[] createResources(String pattern) throws IOException {
		if (resourceLoader instanceof ResourcePatternResolver) {
			ResourcePatternResolver resolver = (ResourcePatternResolver) resourceLoader;
			Resource[] resources = resolver.getResources(pattern);
			FlowDefinitionResource[] flowResources = new FlowDefinitionResource[resources.length];
			for (int i = 0; i < resources.length; i++) {
				Resource resource = resources[i];
				flowResources[i] = new FlowDefinitionResource(getFlowId(resource), resource, null);
			}
			return flowResources;
		} else {
			throw new IllegalStateException(
					"Cannot create flow definition resources from patterns without a ResourceLoader configured that is a ResourcePatternResolver");
		}
	}

	/**
	 * Create a file-based based resource from the file path provided.
	 * @param path the {@link FileSystemResource} path
	 * @return the file-based flow definition resource
	 */
	public FlowDefinitionResource createFileResource(String path) {
		Resource resource = new FileSystemResource(new File(path));
		return new FlowDefinitionResource(getFlowId(resource), resource, null);
	}

	/**
	 * Create a classpath-based resource from the path provided.
	 * @param path the {@link ClassPathResource} path
	 * @param clazz to specify if the path should be relative to another class
	 * @return the classpath-based flow definition resource
	 */
	public FlowDefinitionResource createClassPathResource(String path, Class clazz) {
		Resource resource = new ClassPathResource(path, clazz);
		return new FlowDefinitionResource(getFlowId(resource), resource, null);
	}

	// subclassing hooks

	/**
	 * Obtains the flow id from the flow resource. By default, the flow id becomes the filename of the resource minus
	 * the extension. Subclasses may override.
	 * @param flowResource the flow resource
	 * @return the flow id
	 */
	protected String getFlowId(Resource flowResource) {
		String fileName = flowResource.getFilename();
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex != -1) {
			return fileName.substring(0, extensionIndex);
		} else {
			return fileName;
		}
	}

}
