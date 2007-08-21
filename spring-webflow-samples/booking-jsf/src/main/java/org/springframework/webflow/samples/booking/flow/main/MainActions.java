package org.springframework.webflow.samples.booking.flow.main;

import java.util.List;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.samples.booking.app.Booking;
import org.springframework.webflow.samples.booking.app.BookingService;
import org.springframework.webflow.samples.booking.app.Hotel;
import org.springframework.webflow.samples.booking.app.User;
import org.springframework.webflow.samples.booking.web.util.SerializableListDataModel;

public class MainActions extends MultiAction {

    private BookingService bookingService;

    public MainActions(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public Event initCurrentUser(RequestContext context) {
	User user = new User("springer", "springrocks", "Springer");
	context.getConversationScope().put("user", user);
	return success();
    }

    public Event findCurrentUserBookings(RequestContext context) {
	User user = (User) context.getConversationScope().get("user");
	List<Booking> bookings = bookingService.findBookings(user.getUsername());
	context.getFlowScope().put("bookings", new SerializableListDataModel(bookings));
	return success();
    }

    public Event findHotels(RequestContext context) {
	SearchCriteria search = (SearchCriteria) context.getFlowScope().get("searchCriteria");
	List<Hotel> hotels = bookingService
		.findHotels(search.getSearchString(), search.getPageSize(), search.getPage());
	context.getFlowScope().put("hotels", new SerializableListDataModel(hotels));
	return success();
    }

    public Event removeBooking(RequestContext context) {
	return success();
    }

}
