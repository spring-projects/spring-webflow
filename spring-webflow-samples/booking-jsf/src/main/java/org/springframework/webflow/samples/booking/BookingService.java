package org.springframework.webflow.samples.booking;

import java.util.List;

public interface BookingService {

    public List<Booking> findBookings(User user);

    public List<Hotel> findHotels(String searchString, int pageSize, int page);

    public Hotel readHotelById(Long id);

    public Booking bookHotel(Hotel hotel, User user);
}
