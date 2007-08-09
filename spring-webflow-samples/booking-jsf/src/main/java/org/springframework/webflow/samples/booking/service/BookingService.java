package org.springframework.webflow.samples.booking.service;

import java.util.List;

import org.springframework.webflow.samples.booking.model.Booking;
import org.springframework.webflow.samples.booking.model.Hotel;
import org.springframework.webflow.samples.booking.model.User;

public interface BookingService {

    public List<Booking> findBookings(User user);

    public List<Hotel> findHotels(String searchString, int pageSize, int page);

    public Hotel readHotelById(Long id);

    public Booking bookHotel(Hotel hotel, User user);

    public void cancelBooking(Long id);
}
