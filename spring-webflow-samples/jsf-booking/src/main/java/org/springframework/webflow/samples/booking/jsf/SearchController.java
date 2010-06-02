package org.springframework.webflow.samples.booking.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.webflow.samples.booking.Booking;
import org.springframework.webflow.samples.booking.BookingService;
import org.springframework.webflow.samples.booking.SearchCriteria;

@ManagedBean
@RequestScoped
public class SearchController {

    private SearchCriteria searchCriteria;

    private DataModel bookings;

    private DataModel hotels;

    @ManagedProperty("#{bookingService}")
    private BookingService bookingService;

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public SearchCriteria getSearchCriteria() {
	if (searchCriteria == null) {
	    searchCriteria = new SearchCriteria();
	}
	return searchCriteria;
    }

    public DataModel getBookings() {
	if (bookings == null) {
	    // load the current user's bookings from the database
	    bookings = new ListDataModel(bookingService.findBookings(getCurrentUser()));
	}
	return bookings;
    }

    public DataModel getHotels() {
	return hotels;
    }

    // from enterSearchCriteria.xhtml

    public String search() {
	searchCriteria.resetPage();
	executeSearch();
	return "reviewHotels";
    }

    public void cancelBookingListener(ActionEvent event) {
	Booking booking = (Booking) bookings.getRowData();
	bookingService.cancelBooking(booking);
	((List) bookings.getWrappedData()).remove(booking);
    }

    // from reviewHotels.xhtml

    public void nextListener(ActionEvent event) {
	searchCriteria.nextPage();
	executeSearch();
    }

    public void prevListener(ActionEvent event) {
	searchCriteria.previousPage();
	executeSearch();
    }

    // internal helpers

    private void executeSearch() {
	hotels = new ListDataModel();
	hotels.setWrappedData(bookingService.findHotels(searchCriteria));
    }

    private String getCurrentUser() {
	return "jeremy";
    }

}
