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
package org.springframework.binding.convert.converters;

import org.springframework.core.enums.LabeledEnum;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.core.enums.StaticLabeledEnumResolver;

/**
 * Converts from a textual representation to a {@link LabeledEnum}. The text should be the enum's label.
 * 
 * @author Keith Donald
 */
public class StringToLabeledEnum extends StringToObject {

	private LabeledEnumResolver labeledEnumResolver = StaticLabeledEnumResolver.instance();

	public StringToLabeledEnum() {
		super(LabeledEnum.class);
	}

	protected Object toObject(String string, Class targetClass) throws Exception {
		return labeledEnumResolver.getLabeledEnumByLabel(targetClass, string);
	}

	protected String toString(Object object) throws Exception {
		LabeledEnum labeledEnum = (LabeledEnum) object;
		return labeledEnum.getLabel();
	}

}