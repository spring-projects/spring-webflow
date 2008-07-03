package org.springframework.samples.springtravel.hotel.search.impl;

import java.util.List;

import org.springframework.samples.springtravel.hotel.search.Hotel;
import org.springframework.samples.springtravel.hotel.search.HotelSearchAgent;
import org.springframework.samples.springtravel.hotel.search.SearchCriteria;
import org.springframework.test.jpa.AbstractJpaTests;

public class JpaHotelSearchAgentTests extends AbstractJpaTests {
	
	private HotelSearchAgent hotelSearchAgent;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:/test-jpa-context.xml" };
	}
	
	public void setHotelSearchAgent(HotelSearchAgent hotelSearchAgent) {
		this.hotelSearchAgent = hotelSearchAgent;
	}
	
	public void testFindHotelsWithEmptyCriteria() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setPage(0);
		criteria.setPageSize(Integer.MAX_VALUE);
		criteria.setSearchString("");
		List<Hotel> hotels = hotelSearchAgent.findHotels(criteria);
		assertEquals(23, hotels.size());
	}

	public void testFindHotelsWithSearchTerm() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setPage(0);
		criteria.setPageSize(Integer.MAX_VALUE);
		criteria.setSearchString("westin");
		List<Hotel> hotels = hotelSearchAgent.findHotels(criteria);
		assertEquals(1, hotels.size());
		assertTrue(((Hotel)hotels.get(0)).getName().contains("Westin"));
	}
	
	public void testFindHotelsWithPageSize() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setPage(0);
		criteria.setPageSize(5);
		criteria.setSearchString("");
		List<Hotel> hotels = hotelSearchAgent.findHotels(criteria);
		assertEquals(5, hotels.size());
	}
	
}
