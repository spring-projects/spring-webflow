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
package org.springframework.webflow.upgrade;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

/**
 * Converts Web Flow 1 flow definitions to the version 2 syntax. To use, invoke as a Java application, passing the
 * file-system path to the flow definition you wish to convert as a program argument. The converted flow definition is
 * printed to standard out.
 * 
 * This class requires a XSLT transformer to run. Saxon is recommended to preserve flow definition formatting and line
 * breaks.
 * 
 * @author Scott Andrews
 */
public class WebFlowUpgrader {

	private static final String XSL_NAME = "spring-webflow-1.0-to-2.0.xsl";

	private Transformer transformer;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("The file path to the flow to convert is required");
			System.exit(-1);
		}
		WebFlowUpgrader converter = new WebFlowUpgrader();
		String result = converter.convert(new FileSystemResource(args[0]));
		System.out.println(result);
	}

	public String convert(Resource flowResource) {
		StringWriter output = new StringWriter();
		try {
			Source source = new StreamSource(flowResource.getInputStream());
			Result result = new StreamResult(output);
			transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public synchronized void transform(Source source, Result result) throws TransformerConfigurationException,
			TransformerException, IOException {
		getTransformer().transform(source, result);
	}

	private Transformer getTransformer() throws TransformerConfigurationException, IOException {
		if (transformer == null) {
			Resource xslResource = new ClassPathResource(XSL_NAME, getClass());
			TransformerFactory factory = TransformerFactory.newInstance();
			Source source = new StreamSource(xslResource.getInputStream());
			transformer = factory.newTransformer(source);
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
		}
		return transformer;
	}
}
