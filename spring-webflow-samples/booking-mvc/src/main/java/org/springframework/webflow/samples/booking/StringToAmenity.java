package org.springframework.webflow.samples.booking;

import org.springframework.binding.convert.converters.StringToObject;

public class StringToAmenity extends StringToObject {

    public StringToAmenity() {
	super(Amenity.class);
    }

    @Override
    protected Object toObject(String string, Class tagetClass) throws Exception {
	return new Amenity(string);
    }

    @Override
    protected String toString(Object value) throws Exception {
	return ((Amenity) value).getName();
    }

}
