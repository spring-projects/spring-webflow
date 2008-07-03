package org.springframework.samples.springtravel.hotel.booking.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.samples.springtravel.hotel.booking.HotelBooking;
import org.springframework.samples.springtravel.hotel.booking.HotelBookingAgent;
import org.springframework.samples.springtravel.hotel.booking.User;
import org.springframework.samples.springtravel.hotel.search.Hotel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A JPA-based implementation of the Hotel Booking Agent. Delegates to a JPA entity
 * manager to issue data access calls against the backing repository. The
 * EntityManager reference is provided by the managing container (Spring)
 * automatically.
 */
@Repository
public class JpaHotelBookingAgent implements HotelBookingAgent {

	private EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<HotelBooking> findBookings(String username) {
		if (username != null) {
			return em
					.createQuery(
							"select b from HotelBooking b where b.user.username = :username order by b.checkinDate")
					.setParameter("username", username).getResultList();
		} else {
			return null;
		}
	}

	@Transactional
	public void cancelBooking(Long id) {
		HotelBooking booking = em.find(HotelBooking.class, id);
		if (booking != null) {
			em.remove(booking);
		}
	}

	// flow helper
	
	@Transactional(readOnly = true)
	public HotelBooking createBooking(Long hotelId, String username) {
		User user = findUser(username);
		Hotel hotel = findHotel(hotelId);
		HotelBooking booking = new HotelBooking(hotel, user);
		em.persist(booking);
		return booking;
	}

	// helpers

	private User findUser(String username) {
		return (User) em.createQuery(
				"select u from User u where u.username = :username")
				.setParameter("username", username).getSingleResult();
	}
	
	private Hotel findHotel(Long hotelId) {
		return (Hotel) em.createQuery(
				"select h from Hotel h where h.id = :hotelId")
				.setParameter("hotelId", hotelId).getSingleResult();
	}

}