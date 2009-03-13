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

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A generic strategy interface encapsulating the logic to load an XML-based document.
 * 
 * @author Keith Donald
 */
public interface DocumentLoader {

	/**
	 * Load the XML-based document from the external resource.
	 * @param resource the document resource
	 * @return the loaded (parsed) document
	 * @throws IOException an exception occured accessing the resource input stream
	 * @throws ParserConfigurationException an exception occured building the document parser
	 * @throws SAXException a error occured during document parsing
	 */
	public Document loadDocument(Resource resource) throws IOException, ParserConfigurationException, SAXException;
}