package org.springframework.webflow.samples.booking;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
public class JpaBookingService implements BookingService {

    private EntityManager em;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Booking> findBookings(User user) {
	return em.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
		.setParameter("username", user.getUsername()).getResultList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Hotel> findHotels(String searchString, int pageSize, int page) {
	String pattern = !StringUtils.hasText(searchString) ? "'%'" : "'%"
		+ searchString.toLowerCase().replace('*', '%') + "%'";
	return em.createQuery(
		"select h from Hotel h where lower(h.name) like " + pattern + " or lower(h.city) like " + pattern
			+ " or lower(h.zip) like " + pattern + " or lower(h.address) like " + pattern).setMaxResults(
		pageSize).setFirstResult(page * pageSize).getResultList();
    }

    @Transactional(readOnly = true)
    public Hotel readHotelById(Long id) {
	return em.find(Hotel.class, id);
    }

    @Transactional(readOnly = true)
    public Booking bookHotel(Hotel hotel, User user) {
	Booking booking = new Booking(hotel, user);
	em.persist(booking);
	return booking;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

}
