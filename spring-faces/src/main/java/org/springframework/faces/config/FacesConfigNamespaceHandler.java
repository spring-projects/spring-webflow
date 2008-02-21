package org.springframework.faces.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class FacesConfigNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("flow-builder-services", new FacesFlowBuilderServicesBeanDefinitionParser());
	}

}
