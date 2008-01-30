package org.springframework.faces.model.converter;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.CompositeConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.convert.support.GenericConversionService;

/**
 * Convenient {@link ConversionService} implementation for JSF that composes JSF-specific converters with the standard
 * Web Flow converters.
 * 
 * @author Jeremy Grelle
 */
public class FacesConversionService extends CompositeConversionService {

	private static ConversionService[] conversionServices = new ConversionService[] { new DefaultConversionService(),
			new InternalFacesConversionService() };

	public FacesConversionService() {
		super(conversionServices);
	}

	private static class InternalFacesConversionService extends GenericConversionService {

		/**
		 * Creates a new default conversion service, installing the default converters.
		 */
		public InternalFacesConversionService() {
			addFacesConverters();
		}

		/**
		 * Add all default converters to the conversion service.
		 */
		protected void addFacesConverters() {
			addConverter(new DataModelConverter());
		}
	}
}
