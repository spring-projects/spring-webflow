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
package org.springframework.binding.convert.support;

import java.beans.PropertyEditorSupport;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.util.Assert;

/**
 * Adapts a Converter to the PropertyEditor interface.
 * <p>
 * Note: with a converter, only forward conversion from-string-to-value is supported. Value-to-string conversion is not
 * supported. If you need this capability, use a Formatter with a FormatterPropertyEditor adapter.
 * 
 * @see org.springframework.binding.format.Formatter
 * @see org.springframework.binding.format.adapters.FormatterPropertyEditor
 * 
 * @author Keith Donald
 */
public class ConverterPropertyEditorAdapter extends PropertyEditorSupport {

	private ConversionExecutor conversionExecutor;

	/**
	 * Adapt given conversion executor to the PropertyEditor contract.
	 */
	public ConverterPropertyEditorAdapter(ConversionExecutor conversionExecutor) {
		Assert.notNull(conversionExecutor, "A conversion executor is required");
		if (conversionExecutor.getSourceClass() != null) {
			Assert.isTrue(conversionExecutor.getSourceClass().equals(String.class),
					"A string conversion executor is required");
		}
		this.conversionExecutor = conversionExecutor;
	}

	/**
	 * Returns the type strings will be converted to.
	 */
	public Class getTargetClass() {
		return conversionExecutor.getTargetClass();
	}

	public void setAsText(String text) throws IllegalArgumentException {
		setValue(conversionExecutor.execute(text));
	}

	public String getAsText() {
		throw new UnsupportedOperationException();
	}
}