package org.springframework.webflow.samples.booking;

import org.springframework.binding.convert.converters.StringToDate;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component("conversionService")
public class ApplicationConversionService extends DefaultConversionService {

    @Override
    protected void addDefaultConverters() {
	super.addDefaultConverters();
	StringToDate dateConverter = new StringToDate();
	dateConverter.setPattern("MM-dd-yyyy");
	addConverter("shortDate", dateConverter);
    }

}