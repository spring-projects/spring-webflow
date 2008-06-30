package org.springframework.samples.springtravel.hotel.booking;

import java.util.List;

import org.springframework.samples.springtravel.hotel.search.Hotel;

/**
 * A service interface for retrieving bookings from a backing
 * repository. Also supports the ability to cancel a booking.
 */
public interface HotelBookingAgent {

	/**
	 * Find bookings made by the given user
	 * 
	 * @param username
	 *            the user's name
	 * @return their bookings
	 */
	public List<HotelBooking> findBookings(String username);

	/**
	 * Create a new, transient hotel booking instance for the given user.
	 * 
	 * @param hotel
	 *            the hotel
	 * @param userName
	 *            the user name
	 * @return the new transient booking instance
	 */
	public HotelBooking createBooking(Hotel hotel, String userName);

	/**
	 * Cancel an existing booking.
	 * 
	 * @param id
	 *            the booking id
	 */
	public void cancelBooking(Long id);

}
