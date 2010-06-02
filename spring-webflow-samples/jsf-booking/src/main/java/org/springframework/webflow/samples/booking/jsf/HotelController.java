package org.springframework.webflow.samples.booking.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.springframework.webflow.samples.booking.BookingService;
import org.springframework.webflow.samples.booking.Hotel;

@ManagedBean
@RequestScoped
public class HotelController {

    @ManagedProperty("#{bookingService}")
    private BookingService bookingService;

    @ManagedProperty("#{param.id}")
    private Long hotelId;

    private Hotel hotel;

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public void setHotelId(Long hotelId) {
	this.hotelId = hotelId;
    }

    public Hotel getHotel() {
	if (hotel == null && hotelId != null) {
	    hotel = bookingService.findHotelById(hotelId);
	}
	return hotel;
    }

    public Long getHotelId() {
	return hotelId;
    }

}
