package org.springframework.webflow.samples.booking.app;

import java.util.List;

public interface BookingService {

    public List<Booking> findBookings(String username);

    public List<Hotel> findHotels(String searchString, int pageSize, int page);

    public Hotel findHotelById(Long id);

    public Booking bookHotel(Hotel hotel, User user);

    public void cancelBooking(Long id);
}
