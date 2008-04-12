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

public class WebFlowUpgrader {

	private static final String XSL_NAME = "spring-webflow-1.0-to-2.0.xsl";
	private Transformer transformer;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("The name of the file to convert is required");
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
