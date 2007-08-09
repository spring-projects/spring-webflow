package org.springframework.webflow.samples.booking;

import java.util.List;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.samples.booking.model.Booking;
import org.springframework.webflow.samples.booking.model.Hotel;
import org.springframework.webflow.samples.booking.model.User;
import org.springframework.webflow.samples.booking.service.BookingService;

public class SearchAction extends MultiAction {

    BookingService bookingService;

    public Event findHotels(RequestContext context) {
	HotelSearch search = (HotelSearch) context.getFlowScope().get("hotelSearch");
	List<Hotel> hotels = bookingService
		.findHotels(search.getSearchString(), search.getPageSize(), search.getPage());
	if (hotels != null) {
	    SerializableListDataModel model = new SerializableListDataModel(hotels);
	    int test = model.getRowCount();
	    context.getFlowScope().put("hotels", new SerializableListDataModel(hotels));
	} else {
	    context.getFlowScope().put("hotels", null);
	}
	return success();
    }

    public Event findBookings(RequestContext context) {
	User user = (User) context.getConversationScope().get("user");
	List<Booking> bookings = bookingService.findBookings(user);
	if (bookings != null) {
	    context.getFlowScope().put("bookings", new SerializableListDataModel(bookings));
	} else {
	    context.getFlowScope().put("bookings", null);
	}
	return success();
    }

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

}
