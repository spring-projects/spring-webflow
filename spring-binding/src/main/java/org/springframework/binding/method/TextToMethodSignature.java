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

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.ConversionServiceAwareConverter;
import org.springframework.util.StringUtils;

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
 * alias. The name is the logical name of the argument, which is used during
 * data binding to retrieve the argument value. </li>
 * </ul>
 * 
 * @see MethodSignature
 * 
 * @author Keith Donald
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
		String encodedMethodKey = (String)source;
		encodedMethodKey = encodedMethodKey.trim();
		int openParan = encodedMethodKey.indexOf('(');
		if (openParan == -1) {
			return new MethodSignature(encodedMethodKey);
		}
		else {
			String methodName = encodedMethodKey.substring(0, openParan);
			int closeParan = encodedMethodKey.lastIndexOf(')');
			if (closeParan == -1) {
				throw new ConversionException(encodedMethodKey, MethodSignature.class,
						"Syntax error: No close parenthesis specified for method parameter list", null);
			}
			String delimParamList = encodedMethodKey.substring(openParan + 1, closeParan);
			String[] paramArray = StringUtils.commaDelimitedListToStringArray(delimParamList);
			Parameters params = new Parameters(paramArray.length);
			for (int i = 0; i < paramArray.length; i++) {
				String param = paramArray[i].trim();
				String[] typeAndName = StringUtils.split(param, " ");
				if (typeAndName != null && typeAndName.length == 2) {
					Class type = (Class)converterFor(String.class, Class.class).execute(typeAndName[0]);
					params.add(new Parameter(type, parseExpression(typeAndName[1].trim())));
				}
				else {
					params.add(new Parameter(null, parseExpression(param)));
				}
			}
			return new MethodSignature(methodName, params);
		}
	}
}