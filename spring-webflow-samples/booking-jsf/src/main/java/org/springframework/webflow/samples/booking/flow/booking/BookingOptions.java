package org.springframework.webflow.samples.booking.flow.booking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class BookingOptions implements Serializable {

    private List bedOptions;

    private List smokingOptions;

    private List creditCardExpMonths;

    private List creditCardExpYears;

    public List getBedOptions() {
	if (bedOptions == null) {
	    bedOptions = new ArrayList();
	    bedOptions.add(new SelectItem(new Integer(1), "One king-size bed"));
	    bedOptions.add(new SelectItem(new Integer(2), "Two double beds"));
	    bedOptions.add(new SelectItem(new Integer(3), "Three beds"));
	}
	return bedOptions;
    }

    public List getSmokingOptions() {
	if (smokingOptions == null) {
	    smokingOptions = new ArrayList();
	    smokingOptions.add(new SelectItem(Boolean.TRUE, "Smoking"));
	    smokingOptions.add(new SelectItem(Boolean.FALSE, "Non-Smoking"));
	}
	return smokingOptions;
    }

    public List getCreditCardExpMonths() {
	if (creditCardExpMonths == null) {
	    creditCardExpMonths = new ArrayList();
	    creditCardExpMonths.add(new SelectItem(new Integer(1), "Jan"));
	    creditCardExpMonths.add(new SelectItem(new Integer(2), "Feb"));
	    creditCardExpMonths.add(new SelectItem(new Integer(3), "Mar"));
	    creditCardExpMonths.add(new SelectItem(new Integer(4), "Apr"));
	    creditCardExpMonths.add(new SelectItem(new Integer(5), "May"));
	    creditCardExpMonths.add(new SelectItem(new Integer(6), "Jun"));
	    creditCardExpMonths.add(new SelectItem(new Integer(7), "Jul"));
	    creditCardExpMonths.add(new SelectItem(new Integer(8), "Aug"));
	    creditCardExpMonths.add(new SelectItem(new Integer(9), "Sep"));
	    creditCardExpMonths.add(new SelectItem(new Integer(10), "Oct"));
	    creditCardExpMonths.add(new SelectItem(new Integer(11), "Nov"));
	    creditCardExpMonths.add(new SelectItem(new Integer(12), "Dec"));
	}
	return creditCardExpMonths;
    }

    public List getCreditCardExpYears() {
	if (creditCardExpYears == null) {
	    creditCardExpYears = new ArrayList();
	    creditCardExpYears.add(new SelectItem(new Integer(2005), "2005"));
	    creditCardExpYears.add(new SelectItem(new Integer(2006), "2006"));
	    creditCardExpYears.add(new SelectItem(new Integer(2007), "2007"));
	    creditCardExpYears.add(new SelectItem(new Integer(2008), "2008"));
	    creditCardExpYears.add(new SelectItem(new Integer(2009), "2009"));
	    creditCardExpYears.add(new SelectItem(new Integer(2010), "2010"));
	}
	return creditCardExpYears;
    }

}
