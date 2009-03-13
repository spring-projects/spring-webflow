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
package org.springframework.webflow.engine.model.builder.xml;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * EntityResolver implementation for the Spring Web Flow 2.0 XML Schema. This will load the XSD from the classpath.
 * <p>
 * The xmlns of the XSD expected to be resolved:
 * 
 * <pre>
 *     &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *     &lt;flow xmlns=&quot;http://www.springframework.org/schema/webflow&quot;
 *           xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *           xsi:schemaLocation=&quot;http://www.springframework.org/schema/webflow
 *                               http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd&quot;&gt;
 * </pre>
 * 
 * @author Erwin Vervaet
 * @author Ben Hale
 */
class WebFlowEntityResolver implements EntityResolver {

	private static final String WEBFLOW_ELEMENT = "spring-webflow-2.0";

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (systemId != null && systemId.indexOf(WEBFLOW_ELEMENT) > systemId.lastIndexOf("/")) {
			String filename = systemId.substring(systemId.indexOf(WEBFLOW_ELEMENT));
			try {
				Resource resource = new ClassPathResource(filename, getClass());
				InputSource source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			} catch (IOException ex) {
				// fall through below
			}
		}
		// let the parser handle it
		return null;
	}
}