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
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.AttributeMap;

/**
 * A factory for creating flow definition resources that serve as pointers to external Flow definition files.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class FlowDefinitionResourceFactory {

	private static final String CLASSPATH_SCHEME = "classpath:";

	private static final String SLASH = "/";

	private ResourceLoader resourceLoader;

	private String basePath;

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
	 * Sets the base removed from the flow path when determining the default flow id.
	 * <p>
	 * '/WEB-INF' by default
	 * @param basePath the flow's base path
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
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
		Resource resource;
		if (basePath == null) {
			resource = resourceLoader.getResource(path);
		} else {
			try {
				String basePath = this.basePath;
				if (!basePath.endsWith(SLASH)) {
					// the basePath must end with a slash to create a relative resource
					basePath = basePath + SLASH;
				}
				resource = resourceLoader.getResource(basePath).createRelative(path);
			} catch (IOException e) {
				throw new IllegalStateException("The base path cannot be resolved from '" + basePath + "': "
						+ e.getMessage());
			}
		}
		if (flowId == null || flowId.length() == 0) {
			flowId = getFlowId(resource);
		}
		return new FlowDefinitionResource(flowId, resource, attributes);
	}

	/**
	 * Create an array of flow definition resources from the path pattern location provided.
	 * @param pattern the encoded {@link Resource} path pattern.
	 * @param attributes meta attributes to apply to each flow definition resource
	 * @return the flow definition resources
	 */
	public FlowDefinitionResource[] createResources(String pattern, AttributeMap attributes) throws IOException {
		if (resourceLoader instanceof ResourcePatternResolver) {
			ResourcePatternResolver resolver = (ResourcePatternResolver) resourceLoader;
			Resource[] resources;
			if (basePath == null) {
				resources = resolver.getResources(pattern);
			} else {
				if (basePath.endsWith(SLASH) || pattern.startsWith(SLASH)) {
					resources = resolver.getResources(basePath + pattern);
				} else {
					resources = resolver.getResources(basePath + SLASH + pattern);
				}
			}
			FlowDefinitionResource[] flowResources = new FlowDefinitionResource[resources.length];
			for (int i = 0; i < resources.length; i++) {
				Resource resource = resources[i];
				flowResources[i] = new FlowDefinitionResource(getFlowId(resource), resource, attributes);
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
	 * Obtains the flow id from the flow resource. By default, the flow id becomes the portion of the path between the
	 * basePath and the filename. If no directory structure is available then the filename without the extension is
	 * used. Subclasses may override.
	 * <p>
	 * For example, '${basePath}/booking.xml' becomes 'booking' and '${basePath}/hotels/booking/booking.xml' becomes
	 * 'hotels/booking'
	 * @param flowResource the flow resource
	 * @return the flow id
	 */
	protected String getFlowId(Resource flowResource) {
		if (basePath == null) {
			return getFlowIdFromFileName(flowResource);
		}
		String basePath = this.basePath;
		String filePath;
		if (flowResource instanceof ContextResource) {
			filePath = ((ContextResource) flowResource).getPathWithinContext();
		} else if (flowResource instanceof ClassPathResource) {
			basePath = removeClasspathScheme(basePath);
			filePath = ((ClassPathResource) flowResource).getPath();
		} else if (flowResource instanceof FileSystemResource) {
			basePath = removeClasspathScheme(basePath);
			filePath = truncateFilePath(((FileSystemResource) flowResource).getPath(), basePath);
		} else if (flowResource instanceof UrlResource) {
			basePath = removeClasspathScheme(basePath);
			try {
				filePath = truncateFilePath(((UrlResource) flowResource).getURL().getPath(), basePath);
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to obtain path: " + e.getMessage());
			}
		} else {
			// default to the filename
			return getFlowIdFromFileName(flowResource);
		}

		int beginIndex = 0;
		int endIndex = filePath.length();
		if (filePath.startsWith(basePath)) {
			beginIndex = basePath.length();
		} else if (filePath.startsWith(SLASH + basePath)) {
			beginIndex = basePath.length() + 1;
		}
		if (filePath.startsWith(SLASH, beginIndex)) {
			// ignore a leading slash
			beginIndex++;
		}
		if (filePath.lastIndexOf(SLASH) >= beginIndex) {
			// ignore the filename
			endIndex = filePath.lastIndexOf(SLASH);
		} else {
			// there is no path info, default to the filename
			return getFlowIdFromFileName(flowResource);
		}
		return filePath.substring(beginIndex, endIndex);
	}

	private String getFlowIdFromFileName(Resource flowResource) {
		return StringUtils.stripFilenameExtension(flowResource.getFilename());
	}

	private String truncateFilePath(String filePath, String basePath) {
		int basePathIndex = filePath.lastIndexOf(basePath);
		if (basePathIndex != -1) {
			return filePath.substring(basePathIndex);
		} else {
			return filePath;
		}
	}

	private String removeClasspathScheme(String basePath) {
		if (basePath.startsWith(CLASSPATH_SCHEME)) {
			return basePath.substring(CLASSPATH_SCHEME.length());
		} else {
			return basePath;
		}
	}

}
