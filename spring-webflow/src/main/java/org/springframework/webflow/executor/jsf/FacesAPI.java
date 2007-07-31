package org.springframework.webflow.executor.jsf;

import javax.faces.application.Application;

public class FacesAPI {

    private static final int version = specifyVersion();

    private FacesAPI() {

    }

    private final static int specifyVersion() {
	try {
	    Application.class.getMethod("getExpressionFactory", null);
	} catch (NoSuchMethodException e) {
	    return 11;
	}
	return 12;
    }

    public final static int getVersion() {
	return version;
    }
}
