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
package org.springframework.binding.convert.converters;

import org.springframework.core.enums.LabeledEnum;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.core.enums.StaticLabeledEnumResolver;

/**
 * Converts from a textual representation to a {@link LabeledEnum}. The text should be the enum's label.
 * 
 * @author Keith Donald
 */
public class TextToLabeledEnum extends AbstractConverter {

	private LabeledEnumResolver labeledEnumResolver = StaticLabeledEnumResolver.instance();

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { LabeledEnum.class };
	}

	protected Object doConvert(Object source, Class targetClass, Object context) throws Exception {
		String label = (String) source;
		return labeledEnumResolver.getLabeledEnumByLabel(targetClass, label);
	}
}