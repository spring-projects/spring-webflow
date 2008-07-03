package org.springframework.samples.springtravel.hotel.booking.impl;

import java.util.List;

import org.springframework.samples.springtravel.hotel.booking.HotelBooking;
import org.springframework.samples.springtravel.hotel.booking.HotelBookingAgent;
import org.springframework.test.jpa.AbstractJpaTests;


public class JpaHotelBookingAgentTests extends AbstractJpaTests {
	
	private HotelBookingAgent hotelBookingAgent;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:/test-jpa-context.xml" };
	}
	
	public void setHotelBookingAgent(HotelBookingAgent hotelBookingAgent) {
		this.hotelBookingAgent = hotelBookingAgent;
	}
	
	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		hotelBookingAgent.createBooking(1L, "scott");
	}

	public void testCancelBooking() {
		List<HotelBooking> bookings = hotelBookingAgent.findBookings("scott");
		assertEquals(1, bookings.size());
		hotelBookingAgent.cancelBooking(bookings.get(0).getId());
		bookings = hotelBookingAgent.findBookings("scott");
		assertEquals(0, bookings.size());
	}
	
	public void testCreateBooking() {
		HotelBooking booking = hotelBookingAgent.createBooking(1L, "scott");
		assertEquals("Scott", booking.getUser().getName());
		assertEquals("Westin Diplomat", booking.getHotel().getName());
	}
	
	public void testFindBookings() {
		List<HotelBooking> bookings = hotelBookingAgent.findBookings("scott");
		assertEquals(1, bookings.size());
		assertEquals("Westin Diplomat", bookings.get(0).getHotel().getName());
	}
	
}
