package org.springframework.webflow.samples.booking.flow.booking;

import java.util.Calendar;

import javax.persistence.EntityManager;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.samples.booking.app.Booking;
import org.springframework.webflow.samples.booking.app.Hotel;
import org.springframework.webflow.samples.booking.app.User;

/**
 * Actions invoked by the booking flow. These actions are extensions of the flow definition, called by the flow
 * definition at the appropriate points. Actions allow an externalized flow definition to delegate out to Java code to
 * perform processing.
 */
public class BookingActions extends MultiAction {

    /**
     * Create a new booking object and register it with the flow-managed entity manager. The booking is not actually
     * flushed to the database at this time; that only occurs when the booking flow reaches its "bookingAuthorized"
     * end-state, which is a "commit" state.
     * 
     * It is expected a future milestone of Spring Web Flow 2.0 will support Flows being defined fully in Java and
     * Groovy, allowing logic like this to be defined with the flow definition and without the attribute lookup code you
     * see here.
     * @param context the current flow execution request context
     * @return success if the booking was created successfully.
     */
    public Event createBooking(RequestContext context) {
	Hotel hotel = (Hotel) context.getFlowScope().get("hotel");
	User user = (User) context.getConversationScope().get("user");
	Booking booking = new Booking(hotel, user);
	EntityManager em = (EntityManager) context.getFlowScope().get("entityManager");
	em.persist(booking);
	context.getFlowScope().put("booking", booking);
	return success();
    }

    /**
     * Perform some custom server-side validation on the flow-scoped Booking object updated by the booking form.
     * 
     * It is expected a future milestone of Spring Web Flow 2.0 will add a Messages abstraction that decouples SWF
     * artifacts from environment-specific constructs like the FacesContext.
     * @param context the current flow execution request context
     * @return success if validation was successful, error if not
     */
    public Event validateBooking(RequestContext context) {
	Booking booking = (Booking) context.getFlowScope().get("booking");
	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.DAY_OF_MONTH, -1);
	if (booking.getCheckinDate().before(calendar.getTime())) {
	    context.getMessageContext().addMessage(
		    new MessageBuilder().source("checkinDate").defaultText("Check in date must be a future date")
			    .error().build());
	    return error();
	} else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
	    context.getMessageContext().addMessage(
		    new MessageBuilder().source("checkoutDate").defaultText(
			    "Check out date must be later than check in date").error().build());
	    return error();
	}
	return success();
    }
}
