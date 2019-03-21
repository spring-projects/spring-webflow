/*
 * Copyright 2004-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.model.builder.xml;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * EntityResolver implementation for the Spring Web Flow XML Schema. This will load the XSD from the classpath.
 * <p>
 * The xmlns of the XSD expected to be resolved:
 *
 * <pre>
 *     &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *     &lt;flow xmlns=&quot;http://www.springframework.org/schema/webflow&quot;
 *           xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *           xsi:schemaLocation=&quot;http://www.springframework.org/schema/webflow
 *                               http://www.springframework.org/schema/webflow/spring-webflow.xsd&quot;&gt;
 * </pre>
 *
 * @author Erwin Vervaet
 * @author Ben Hale
 */
class WebFlowEntityResolver implements EntityResolver {

	private static final String SPRING_WEBFLOW_XSD = "spring-webflow.xsd";

	private static final String[] WEBFLOW_VERSIONS = new String[] { "spring-webflow-2.4", "spring-webflow-2.0" };


	public InputSource resolveEntity(String publicId, String systemId) {
		if (systemId != null && systemId.contains(SPRING_WEBFLOW_XSD)) {
			return createInputSource(publicId, systemId, SPRING_WEBFLOW_XSD);
		}
		for (String element : WEBFLOW_VERSIONS) {
			if (systemId != null && systemId.indexOf(element) > systemId.lastIndexOf("/")) {
				return createInputSource(publicId, systemId, SPRING_WEBFLOW_XSD);
			}
		}
		// let the parser handle it
		return null;
	}

	private InputSource createInputSource(String publicId, String systemId, String fileName) {
		try {
			Resource resource = new ClassPathResource(fileName, getClass());
			InputSource source = new InputSource(resource.getInputStream());
			source.setPublicId(publicId);
			source.setSystemId(systemId);
			return source;
		} catch (IOException ex) {
			// fall through below
		}
		return null;
	}
}
