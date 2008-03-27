package org.springframework.webflow.samples.booking;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class BookingController {

    private static final String REVIEW_BOOKING_OUTCOME = "reviewBooking";

    private BookingService bookingService;

    private Long hotelId;

    private Hotel hotel = new Hotel();

    private Booking booking = new Booking(hotel, new User());

    private boolean initialized = false;

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
	    return REVIEW_BOOKING_OUTCOME;
	} else {
	    return null;
	}
    }

    public String confirm() {
	bookingService.saveBooking(booking);
	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("booking");
	return "confirm";
    }

    public String cancel() {
	FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("booking");
	return "cancel";
    }

    public Long getHotelId() {
	return hotelId;
    }

    public void setHotelId(Long hotelId) {
	if (hotelId != null && hotelId != 0 && !initialized) {
	    booking = bookingService.createBooking(hotelId, "jeremy");
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("booking", booking);
	    initialized = true;
	}
	this.hotelId = hotelId;
    }

    public Hotel getHotel() {
	return hotel;
    }

    public void setHotel(Hotel hotel) {
	this.hotel = hotel;
    }

    public Booking getBooking() {
	return booking;
    }

    public void setBooking(Booking booking) {
	this.booking = booking;
    }

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }
}
