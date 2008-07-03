package org.springframework.samples.springtravel.hotel.search.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.samples.springtravel.hotel.search.Hotel;
import org.springframework.samples.springtravel.hotel.search.HotelSearchAgent;
import org.springframework.samples.springtravel.hotel.search.SearchCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A JPA-based implementation of the Search Agent. Delegates to a JPA entity
 * manager to issue data access calls against the backing repository. The
 * EntityManager reference is provided by the managing container (Spring)
 * automatically.
 */
@Repository
public class JpaHotelSearchAgent implements HotelSearchAgent {

	private EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Hotel> findHotels(SearchCriteria criteria) {
		String pattern = "%" + criteria.getSearchString() + "%";
		List<Hotel> hotels = em.createQuery(
				"select h from Hotel h where lower(h.name) like :pattern"
						+ " or lower(h.city) like :pattern"
						+ " or lower(h.zip) like :pattern"
						+ " or lower(h.address) like :pattern")
				.setParameter("pattern", pattern)
				.setMaxResults(criteria.getPageSize()).setFirstResult(
						criteria.getPage() * criteria.getPageSize())
				.getResultList();
		return hotels;
	}

}