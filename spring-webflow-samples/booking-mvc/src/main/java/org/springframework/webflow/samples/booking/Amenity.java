package org.springframework.webflow.samples.booking;

import java.io.Serializable;

public class Amenity implements Serializable {

    private String name;

    public Amenity(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Amenity)) {
	    return false;
	}
	Amenity a = (Amenity) obj;
	return name.equals(a.name);
    }

    @Override
    public int hashCode() {
	return name.hashCode();
    }

}