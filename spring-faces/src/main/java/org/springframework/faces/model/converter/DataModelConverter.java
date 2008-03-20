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
package org.springframework.faces.model.converter;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.util.ClassUtils;

public class DataModelConverter extends AbstractConverter {

	protected Object doConvert(Object source, Class targetClass, Object context) throws Exception {
		Constructor emptyConstructor = ClassUtils.getConstructorIfAvailable(targetClass, new Class[] {});
		DataModel model = (DataModel) emptyConstructor.newInstance(new Object[] {});
		model.setWrappedData(source);
		return model;
	}

	public Class[] getSourceClasses() {
		return new Class[] { Object[].class, List.class, Object.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { DataModel.class };
	}

}
