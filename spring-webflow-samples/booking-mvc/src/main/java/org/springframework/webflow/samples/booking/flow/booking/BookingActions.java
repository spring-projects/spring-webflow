package org.springframework.webflow.samples.booking.flow.booking;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.samples.booking.app.Booking;
import org.springframework.webflow.samples.booking.app.Hotel;
import org.springframework.webflow.samples.booking.app.User;

/**
 * Actions invoked by the booking flow. These actions are extensions of the flow definition, called by the flow
 * definition at the appropriate points. Actions allow an externalized flow definition to delegate out to Java code to
 * perform processing.
 */
public class BookingActions extends FormAction {

    public BookingActions() {
	setFormObjectName("booking");
	setValidator(new BookingValidator());
    }

    @Override
    protected void initBinder(RequestContext context, DataBinder binder) {
	binder.setRequiredFields(new String[] { "checkinDate", "checkoutDate", "creditCard", "creditCardName" });
	binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @Override
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
    protected Object createFormObject(RequestContext context) throws Exception {
	Hotel hotel = (Hotel) context.getFlowScope().get("hotel");
	User user = (User) context.getConversationScope().get("user");
	Booking booking = new Booking(hotel, user);
	EntityManager em = (EntityManager) context.getFlowScope().get("entityManager");
	em.persist(booking);
	return booking;
    }

    public class BookingValidator implements Validator {

	public boolean supports(Class clazz) {
	    return Booking.class.equals(clazz);
	}

	public void validate(Object object, Errors errors) {
	}

    }
}