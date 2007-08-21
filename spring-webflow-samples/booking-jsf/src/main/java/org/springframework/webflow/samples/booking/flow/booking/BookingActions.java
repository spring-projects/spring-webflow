package org.springframework.webflow.samples.booking.flow.booking;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.samples.booking.app.Booking;
import org.springframework.webflow.samples.booking.app.Hotel;
import org.springframework.webflow.samples.booking.app.User;

public class BookingActions extends MultiAction {

    public Event createBooking(RequestContext context) {
	Hotel hotel = (Hotel) context.getFlowScope().get("hotel");
	User user = (User) context.getFlowScope().get("user");
	Booking booking = new Booking(hotel, user);
	EntityManager em = (EntityManager) context.getFlowScope().get("entityManager");
	em.persist(booking);
	context.getFlowScope().put("booking", booking);
	return success();
    }

    public Event validateBooking(RequestContext context) throws Exception {
	Booking booking = (Booking) context.getFlowScope().get("booking");
	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.DAY_OF_MONTH, -1);
	if (booking.getCheckinDate().before(calendar.getTime())) {
	    FacesContext.getCurrentInstance().addMessage("checkinDate",
		    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Check in date must be a future date", ""));
	    return error();
	} else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
	    FacesContext.getCurrentInstance()
		    .addMessage(
			    "checkoutDate",
			    new FacesMessage(FacesMessage.SEVERITY_ERROR,
				    "Check out date must be later than check in date", ""));
	    return error();
	}
	return success();
    }
}
