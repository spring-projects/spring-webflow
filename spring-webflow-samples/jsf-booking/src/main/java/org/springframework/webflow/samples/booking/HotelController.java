package org.springframework.webflow.samples.booking;

public class HotelController {

    private BookingService bookingService;

    private Long hotelId;

    private Hotel hotel;

    public Hotel getHotel() {
	if (hotel == null && hotelId != null) {
	    hotel = bookingService.findHotelById(hotelId);
	}
	return hotel;
    }

    public void setBookingService(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    public void setHotelId(Long hotelId) {
	this.hotelId = hotelId;
    }

    public Long getHotelId() {
	return hotelId;
    }
}
