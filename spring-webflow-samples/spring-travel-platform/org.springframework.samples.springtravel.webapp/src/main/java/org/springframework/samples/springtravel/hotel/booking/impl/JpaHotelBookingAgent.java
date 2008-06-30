package org.springframework.samples.springtravel.hotel.booking.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.samples.springtravel.hotel.booking.HotelBooking;
import org.springframework.samples.springtravel.hotel.booking.HotelBookingAgent;
import org.springframework.samples.springtravel.hotel.booking.User;
import org.springframework.samples.springtravel.hotel.search.Hotel;
import org.springframework.samples.springtravel.hotel.search.SearchCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

	@Transactional(readOnly = true)
	public HotelBooking createBooking(Hotel hotel, String username) {
		User user = findUser(username);
		return new HotelBooking(hotel, user);
	}

	@Transactional
	public void cancelBooking(Long id) {
		HotelBooking booking = em.find(HotelBooking.class, id);
		if (booking != null) {
			em.remove(booking);
		}
	}

	// helpers

	private User findUser(String username) {
		return (User) em.createQuery(
				"select u from User u where u.username = :username")
				.setParameter("username", username).getSingleResult();
	}

}