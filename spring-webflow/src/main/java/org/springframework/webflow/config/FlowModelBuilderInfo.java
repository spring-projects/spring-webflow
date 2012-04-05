package org.springframework.webflow.config;

import org.springframework.util.Assert;

/**
 * Pointer to a single flow model builder to be registered within the application.
 * @author Paul Wilson
 */
final class FlowModelBuilderInfo {

	private final String extension;
	private final String className;

	/**
	 * Construct a flow model builder, enforcing that both the extension and class name are not <code>null</code> or
	 * empty.
	 * @param extension the extension to register the flow model builder under
	 * @param className the class name of the flow model builder
	 */
	public FlowModelBuilderInfo(String extension, String className) {
		Assert.hasText(extension, "An extension is required to register a flow model builder");
		Assert.hasText(className, "The fully-qualified FlowModelBuilder class name is required");
		this.extension = extension;
		this.className = className;
	}

	/**
	 * Returns the extension under which the model builder should be registered
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Returns the class name of the model builder
	 * @return the class name of the model builder
	 */
	public String getClassName() {
		return className;
	}
}