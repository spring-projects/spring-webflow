/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.binding.method;

import java.util.LinkedList;
import java.util.List;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.ConversionServiceAwareConverter;
import org.springframework.binding.expression.Expression;

/**
 * Converter that takes an encoded string representation and produces a
 * corresponding <code>MethodSignature</code> object.
 * <p>
 * This converter supports the following encoded forms:
 * <ul>
 * <li> "methodName" - the name of the method to invoke, where the method is
 * expected to have no arguments. </li>
 * <li> "methodName(param1Type param1Name, paramNType paramNName)" - the name of
 * the method to invoke, where the method is expected to have parameters
 * delimited by a comma. In this example, the method has two parameters. The
 * type is either the fully-qualified class of the argument OR a known type
 * alias OR left out althogether. The name is the logical name of the argument,
 * which is used during data binding to retrieve the argument value
 * (typically an expression). </li>
 * </ul>
 * 
 * @see MethodSignature
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class TextToMethodSignature extends ConversionServiceAwareConverter {

	/**
	 * Create a new converter that converts strings to MethodSignature objects.
	 */
	public TextToMethodSignature() {
	}

	/**
	 * Create a new converter that converts strings to MethodSignature objects.
	 * @param conversionService the conversion service to use
	 */
	public TextToMethodSignature(ConversionService conversionService) {
		super(conversionService);
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { MethodSignature.class };
	}

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		String encodedMethodSignature = (String)source;
		encodedMethodSignature = encodedMethodSignature.trim();
		int openParan = encodedMethodSignature.indexOf('(');
		if (openParan == -1) {
			// form "foo"
			return new MethodSignature(encodedMethodSignature);
		}
		else {
			// form "foo(...)"
			String methodName = encodedMethodSignature.substring(0, openParan);
			int closeParan = encodedMethodSignature.lastIndexOf(')');
			if (closeParan != (encodedMethodSignature.length() - 1)) {
				throw new ConversionException(encodedMethodSignature, MethodSignature.class,
						"Syntax error: No close parenthesis specified for method parameter list", null);
			}
			String delimitedParams = encodedMethodSignature.substring(openParan + 1, closeParan);
			String[] paramArray = splitParameters(encodedMethodSignature, delimitedParams);
			Parameters params = new Parameters(paramArray.length);
			for (int i = 0; i < paramArray.length; i++) {
				// param could be of the form "type name", "name", "type ${name}" or "${name}"
				String param = paramArray[i].trim();
				int space = param.indexOf(' ');
				int expr = param.indexOf('{');
				if (space == -1 || (expr != -1 &&  space > expr)) {
					// "name" or "${name}"
					params.add(new Parameter(null, parseExpression(param)));
				}
				else {
					// "type name" or "type ${name}"
					Class type = (Class)fromStringTo(Class.class).execute(param.substring(0, space).trim());
					Expression name = parseExpression(param.substring(space + 1).trim());
					params.add(new Parameter(type, name));
				}
			}
			return new MethodSignature(methodName, params);
		}
	}
	
	/**
	 * Split given parameter string into individual parameter definitions.
	 */
	private String[] splitParameters(String encodedMethodSignature, String parameters) {
		List res = new LinkedList();
		
		int paramStart = 0;
		int blockNestingCount = 0;
		for (int i = 0; i < parameters.length(); i++) {
			switch (parameters.charAt(i)) {
				case '{':
					blockNestingCount++;
					break;
				case '}':
					blockNestingCount--;
					break;
				case ',':
					if (blockNestingCount == 0) {
						// only take comma delimiter into account when not inside
						// a block
						res.add(parameters.substring(paramStart, i));
						paramStart = i + 1;
					}
					break;
			}
		}
		if (blockNestingCount != 0) {
			throw new ConversionException(encodedMethodSignature, MethodSignature.class,
					"Syntax error: Curly braces do not match", null);
		}
		if (paramStart < parameters.length()) {
			res.add(parameters.substring(paramStart));
		}
		
		return (String[])res.toArray(new String[res.size()]);
	}
}