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
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * The default document loader strategy for XSD-based XML documents with validation enabled by default.
 * <p>
 * Note: full XSD support requires JDK 5.0 or a capable parser such as Xerces 2.0. JDK 1.4 or < do not fully support XSD
 * out of the box. To use this implementation on JDK 1.4 make sure Xerces is available in your classpath or disable XSD
 * validation by {@link #setValidating(boolean) setting the validating property to false}.
 * 
 * @author Keith Donald
 */
public class DefaultDocumentLoader implements DocumentLoader {

	private static final Log logger = LogFactory.getLog(DefaultDocumentLoader.class);

	/**
	 * JAXP attribute used to configure the schema language for validation.
	 */
	private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/**
	 * JAXP attribute value indicating the XSD schema language.
	 */
	private static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

	/**
	 * Flag indicating if the XML document parser will perform schema validation.
	 */
	private boolean validating = true;

	/**
	 * The spring-webflow schema resolution strategy.
	 */
	private EntityResolver entityResolver = new WebFlowEntityResolver();

	/**
	 * Returns whether or not the XML parser will validate the document.
	 */
	public boolean isValidating() {
		return validating;
	}

	/**
	 * Set if the XML parser should validate the document and thus enforce a schema. Defaults to true.
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	/**
	 * Returns the SAX entity resolver used by the XML parser.
	 */
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	/**
	 * Set a SAX entity resolver to be used for parsing. Can be overridden for custom entity resolution, for example
	 * relative to some specific base path.
	 */
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public Document loadDocument(Resource resource) throws IOException, ParserConfigurationException, SAXException {
		InputStream is = null;
		try {
			is = resource.getInputStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(isValidating());
			factory.setNamespaceAware(true);
			try {
				factory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE, XSD_SCHEMA_LANGUAGE);
			} catch (IllegalArgumentException ex) {
				throw new IllegalStateException("Unable to validate using XSD: Your JAXP provider [" + factory
						+ "] does not support XML Schema. "
						+ "Are you running on Java 1.4 or below with Apache Crimson? "
						+ "If so you must upgrade to Apache Xerces (or Java 5 or >) for full XSD support.");
			}
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			docBuilder.setErrorHandler(new SimpleSaxErrorHandler(logger));
			docBuilder.setEntityResolver(getEntityResolver());
			return docBuilder.parse(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}