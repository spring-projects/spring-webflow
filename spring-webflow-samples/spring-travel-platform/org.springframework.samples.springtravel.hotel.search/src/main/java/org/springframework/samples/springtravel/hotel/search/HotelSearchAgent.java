package org.springframework.samples.springtravel.hotel.search;

import java.util.List;

/**
 * A service interface for retrieving hotels from a backing repository.
 */
public interface HotelSearchAgent {

	/**
	 * Find hotels available for booking by some criteria.
	 * 
	 * @param criteria
	 *            the search criteria
	 * @return a list of hotels meeting the criteria
	 */
	public List<Hotel> findHotels(SearchCriteria criteria);

	/**
	 * Find hotels by their identifier.
	 * 
	 * @param id
	 *            the hotel id
	 * @return the hotel
	 */
	public Hotel findHotelById(Long id);

}
