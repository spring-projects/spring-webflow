/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.model.converter;

import jakarta.faces.model.DataModel;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

/**
 * Convenient {@link org.springframework.binding.convert.ConversionService} implementation for JSF that composes
 * JSF-specific converters with the standard Web Flow converters.
 *
 * <p>
 * In addition to the standard Web Flow conversion, this service provide conversion from a list into a
 * {@link OneSelectionTrackingListDataModel} using a "dataModel" alias for the type.
 * </p>
 *
 * @author Jeremy Grelle
 */
public class FacesConversionService extends DefaultConversionService {

	public FacesConversionService() {
		addFacesConverters();
	}

	public FacesConversionService(ConversionService delegateConversionService) {
		super(delegateConversionService);
	}

	protected void addFacesConverters() {
		addConverter(new DataModelConverter());
		addAlias("dataModel", DataModel.class);
	}
}
