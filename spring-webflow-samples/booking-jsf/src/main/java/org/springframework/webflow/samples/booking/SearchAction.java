package org.springframework.webflow.samples.booking;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

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

    public String nextPage() {
	HotelSearch search = (HotelSearch) FacesContext.getCurrentInstance().getApplication().getVariableResolver()
		.resolveVariable(FacesContext.getCurrentInstance(), "hotelSearch");

	search.setPage(search.getPage() + 1);

	return "findHotels";
    }

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

}
