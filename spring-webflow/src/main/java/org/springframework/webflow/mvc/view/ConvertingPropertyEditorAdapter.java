/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.webflow.mvc.view;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.springframework.binding.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>
 * A {@link PropertyEditor} that delegates to a Spring ConversionService unless a converterId is provided. When a
 * converterId is provided, conversion will be delegated to the Spring Binding ConversionService instead as Spring's
 * type conversion system does not support named converters.
 * </p>
 * 
 * @author Rossen Stoyanchev
 */
class ConvertingPropertyEditorAdapter extends PropertyEditorSupport {

	private ConversionService conversionService;

	private TypeDescriptor fieldType;

	private String converterId;

	private boolean canConvertToString;

	public ConvertingPropertyEditorAdapter(ConversionService conversionService, String converterId,
			TypeDescriptor fieldType) {
		Assert.notNull(conversionService, "A ConversionService instance is required.");
		Assert.notNull(fieldType, "The field type is required");
		this.conversionService = conversionService;
		this.fieldType = fieldType;
		this.converterId = converterId;
		this.canConvertToString = conversionService.getDelegateConversionService().canConvert(this.fieldType,
				TypeDescriptor.valueOf(String.class));
	}

	public String getAsText() {
		if (StringUtils.hasText(converterId)) {
			return (String) conversionService.executeConversion(converterId, getValue(), String.class);
		} else {
			if (canConvertToString) {
				return (String) conversionService.getDelegateConversionService().convert(getValue(), fieldType,
						TypeDescriptor.valueOf(String.class));
			} else {
				return null;
			}
		}
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(converterId)) {
			setValue(conversionService.executeConversion(converterId, text, fieldType.getType()));
		} else {
			setValue(conversionService.getDelegateConversionService().convert(text,
					TypeDescriptor.valueOf(String.class), fieldType));
		}
	}
}