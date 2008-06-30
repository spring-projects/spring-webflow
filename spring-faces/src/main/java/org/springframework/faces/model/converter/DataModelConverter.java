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

import org.springframework.binding.convert.converters.Converter;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.util.ClassUtils;

/**
 * A {@link Converter} implementation that converts an Object, Object array, or {@link List} into a JSF
 * {@link DataModel}.
 * 
 * @author Jeremy Grelle
 */
public class DataModelConverter implements Converter {

	public Class getSourceClass() {
		return Object.class;
	}

	public Class getTargetClass() {
		return DataModel.class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (targetClass.equals(DataModel.class)) {
			targetClass = OneSelectionTrackingListDataModel.class;
		}
		Constructor emptyConstructor = ClassUtils.getConstructorIfAvailable(targetClass, new Class[] {});
		DataModel model = (DataModel) emptyConstructor.newInstance(new Object[] {});
		model.setWrappedData(source);
		return model;
	}

}
