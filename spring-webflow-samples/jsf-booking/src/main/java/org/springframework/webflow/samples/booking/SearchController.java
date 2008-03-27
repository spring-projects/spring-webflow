package org.springframework.webflow.samples.booking;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class SearchController {

    private BookingService bookingService;

    private SearchCriteria searchCriteria = new SearchCriteria();

    private DataModel hotels = null;

    private DataModel bookings = null;

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

    private void executeSearch() {
	hotels = new ListDataModel();
	hotels.setWrappedData(bookingService.findHotels(searchCriteria));
    }

    public SearchCriteria getSearchCriteria() {
	return searchCriteria;
    }

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public DataModel getHotels() {
	return hotels;
    }

    public DataModel getBookings() {
	if (bookings == null) {
	    bookings = new ListDataModel(bookingService.findBookings("jeremy"));
	}
	return bookings;
    }

    public void cancelBookingListener(ActionEvent event) {
	Booking booking = (Booking) bookings.getRowData();
	bookingService.cancelBooking(booking);
	((List) bookings.getWrappedData()).remove(booking);
    }

}
