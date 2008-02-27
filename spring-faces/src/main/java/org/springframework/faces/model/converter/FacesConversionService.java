package org.springframework.faces.model.converter;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

/**
 * Convenient {@link ConversionService} implementation for JSF that composes JSF-specific converters with the standard
 * Web Flow converters.
 * 
 * @author Jeremy Grelle
 */
public class FacesConversionService extends DefaultConversionService {

	public FacesConversionService() {
		super();
		addFacesConverters();
	}

	protected void addFacesConverters() {
		addConverter(new DataModelConverter());

		addAlias("dataModel", OneSelectionTrackingListDataModel.class);
	}
}
