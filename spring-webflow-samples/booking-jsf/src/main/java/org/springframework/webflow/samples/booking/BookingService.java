package org.springframework.webflow.samples.booking;

import java.util.List;

/**
 * A service interface for retrieving hotels and bookings from a backing repository. Also supports the ability to cancel
 * a booking.
 */
public interface BookingService {

    /**
     * Find bookings made by the given user
     * @param username the user's name
     * @return their bookings
     */
    public List<Booking> findBookings(String username);

    /**
     * Find hotels available for booking by some criteria.
     * @param criteria the search criteria
     * @return a list of hotels meeting the criteria
     */
    public List<Hotel> findHotels(SearchCriteria criteria);

    /**
     * Find hotels by their identifier.
     * @param id the hotel id
     * @return the hotel
     */
    public Hotel findHotelById(Long id);

    /**
     * Returns a new booking object attached to the current persistence context.
     * @param hotel the hotel being booked
     * @param user the user performing the booking
     * @return the booking
     */
    public Booking createBooking(Hotel hotel, User user);

    /**
     * Cancel an existing booking.
     * @param id the booking id
     */
    public void cancelBooking(Long id);
}
