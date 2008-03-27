package org.springframework.webflow.samples.booking.jsf;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.webflow.samples.booking.Booking;
import org.springframework.webflow.samples.booking.BookingService;
import org.springframework.webflow.samples.booking.Hotel;

public class BookingController {

    private BookingService bookingService;

    private Long hotelId;

    private Hotel hotel = new Hotel();

    private Booking booking;

    private boolean initialized = false;

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public Long getHotelId() {
	return hotelId;
    }

    public void setHotelId(Long hotelId) {
	if (hotelId != null && hotelId != 0 && !initialized) {
	    booking = bookingService.createBooking(hotelId, getCurrentUser());
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("booking", booking);
	    initialized = true;
	}
	this.hotelId = hotelId;
    }

    public Booking getBooking() {
	return booking;
    }

    public void setBooking(Booking booking) {
	this.booking = booking;
    }

    public Hotel getHotel() {
	return hotel;
    }

    public void setHotel(Hotel hotel) {
	this.hotel = hotel;
    }

    public String processBooking() {
	FacesContext context = FacesContext.getCurrentInstance();
	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.DAY_OF_MONTH, -1);
	boolean valid = true;
	if (booking.getCheckinDate().before(calendar.getTime())) {
	    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Check in date must be a future date.", "Check in date must be a future date."));
	    valid = false;
	} else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
	    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Check out date must be later than check in date",
		    "Check out date must be later than check in date"));
	    valid = false;
	}
	if (valid) {
	    return "reviewBooking";
	} else {
	    return null;
	}
    }

    public String confirm() {
	bookingService.saveBooking(booking);
	removeBookingFromSession();
	return "confirm";
    }

    public String cancel() {
	removeBookingFromSession();
	return "cancel";
    }

    private String getCurrentUser() {
	return "jeremy";
    }

    private void removeBookingFromSession() {
	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("booking");
    }

}
