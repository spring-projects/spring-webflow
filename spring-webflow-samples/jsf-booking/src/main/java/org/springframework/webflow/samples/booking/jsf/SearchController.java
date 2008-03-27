package org.springframework.webflow.samples.booking.jsf;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.webflow.samples.booking.Booking;
import org.springframework.webflow.samples.booking.BookingService;
import org.springframework.webflow.samples.booking.SearchCriteria;

public class SearchController {

    private BookingService bookingService;

    private SearchCriteria searchCriteria = new SearchCriteria();

    private DataModel hotels;

    private DataModel bookings;

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public SearchCriteria getSearchCriteria() {
	return searchCriteria;
    }

    public DataModel getHotels() {
	return hotels;
    }

    public DataModel getBookings() {
	if (bookings == null) {
	    // load the current user's bookings from the database
	    bookings = new ListDataModel(bookingService.findBookings(getCurrentUser()));
	}
	return bookings;
    }

    public String search() {
	searchCriteria.resetPage();
	executeSearch();
	return "reviewHotels";
    }

    public void nextListener(ActionEvent event) {
	searchCriteria.nextPage();
	executeSearch();
    }

    public void prevListener(ActionEvent event) {
	searchCriteria.previousPage();
	executeSearch();
    }

    public void cancelBookingListener(ActionEvent event) {
	Booking booking = (Booking) bookings.getRowData();
	bookingService.cancelBooking(booking);
	((List) bookings.getWrappedData()).remove(booking);
    }

    private void executeSearch() {
	hotels = new ListDataModel();
	hotels.setWrappedData(bookingService.findHotels(searchCriteria));
    }

    private String getCurrentUser() {
	return "jeremy";
    }

}
