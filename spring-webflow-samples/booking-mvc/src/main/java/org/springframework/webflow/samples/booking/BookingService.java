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
     * @param searchCriteria the search criteria
     * @return a list of hotels not exceeding the page size
     */
    public List<Hotel> findHotels(SearchCriteria searchCriteria);

    /**
     * Find hotels by their identifier.
     * @param id the hotel id
     * @return the hotel
     */
    public Hotel findHotelById(Long id);

    /**
     * Cancel an existing booking.
     * @param id the booking id
     */
    public void cancelBooking(Long id);

    /**
     * Lookup a user based on their username
     * @param username the user's username
     * @return the user
     */
    public User findUser(String username);
}
