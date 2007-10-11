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

public class FlowDefinitionResourceFactory {
	private ResourceLoader resourceLoader;

	public FlowDefinitionResourceFactory() {
		this.resourceLoader = new DefaultResourceLoader();
	}

	public FlowDefinitionResourceFactory(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "The resource loader cannot be null");
		this.resourceLoader = resourceLoader;
	}

	public FlowDefinitionResource createResource(String path) {
		return createResource(path, null, null);
	}

	public FlowDefinitionResource createResource(String path, AttributeMap attributes) {
		return createResource(path, attributes, null);
	}

	public FlowDefinitionResource createResource(String path, AttributeMap attributes, String flowId) {
		Resource resource = resourceLoader.getResource(path);
		if (flowId == null || flowId.length() == 0) {
			flowId = getFlowId(resource);
		}
		return new FlowDefinitionResource(flowId, resource, attributes);
	}

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

	public FlowDefinitionResource createFileResource(String path) {
		Resource resource = new FileSystemResource(new File(path));
		return new FlowDefinitionResource(getFlowId(resource), resource, null);
	}

	public FlowDefinitionResource createClassPathResource(String path, Class clazz) {
		Resource resource = new ClassPathResource(path, clazz);
		return new FlowDefinitionResource(getFlowId(resource), resource, null);
	}

	private String getFlowId(Resource flowResource) {
		String fileName = flowResource.getFilename();
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex != -1) {
			return fileName.substring(0, extensionIndex);
		} else {
			return fileName;
		}
	}

}
