package org.ku.orderfulfillment.service.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import jersey.repackaged.com.google.common.collect.Lists;

import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.service.OrderDao;

/**
 * Data access object for saving and retrieving orders,
 * using JPA.
 * To get an instance of this class use:
 * dao = DaoFactory.getInstance().getContactDao()
 * 
 * @author Eknarin, Natcha, Natchanon, Sarathit
 */
public class JpaOrderDao implements OrderDao {
	//TODO
	/** the EntityManager for accessing JPA persistence services. */
	private final EntityManager em;
	
	/**
	 * constructor with injected EntityManager to use.
	 * @param em an EntityManager for accessing JPA services.
	 */
	public JpaOrderDao(EntityManager em) {
		this.em = em;
		createTestOrder( );
	}
	
	/** add orders for testing. */
	private void createTestOrder( ) {
		long id = 999; // usually we should let JPA set the id
		if (find(id) == null) {
			List<Long> list = new ArrayList<Long>();
			list.add(1L);
			list.add(2L);
			list.add(3L);
			Order test = new Order(1234L,list,"Test URI");
			test.setId(id);
			save(test);
		}
	}

	@Override
	public Order find(long id) {
		return em.find(Order.class, id);
	}

	@Override
	public List<Order> findAll() {
		Query query = em.createQuery("SELECT o FROM Order o");
		List<Order> orderList = query.getResultList();
		return Collections.unmodifiableList(orderList);
	}

	@Override
	public List<Order> findByTitle(String titlestr) {
		// LIKE does string match using patterns.
		Query query = em.createQuery("select c from Order c where LOWER(c.title) LIKE :title");
		// % is wildcard that matches anything
		query.setParameter("title", "%"+titlestr.toLowerCase()+"%");
		// now why bother to copy one list to another list?
		java.util.List<Order> result = Lists.newArrayList( query.getResultList() );
		return result;
	}

	@Override
	public boolean delete(long id) {
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			Order order = this.find(id);
			em.remove(order);
			trans.commit();
			return true;
		}catch(EntityExistsException e){
			if(trans.isActive()){
				trans.rollback();
			}
		}
		return false;
	}
	
	@Override
	public boolean save(Order order) {
		if (order == null) throw new IllegalArgumentException("Can't save a null order");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(order);
			tx.commit();
			return true;
		} catch (EntityExistsException ex) {
			Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
			if (tx.isActive()) try { tx.rollback(); } catch(Exception e) {}
			return false;
		}
	}

	@Override
	public boolean update(Order update) {
		if(update == null){
			throw new IllegalArgumentException("Can't update a null order");
		}
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Order order = this.find(update.getId());
			if(order == null){
				throw new IllegalArgumentException("Can't update a null order");
			}
			
			//contact.forceApplyUpdate(update);
			em.merge(order);
			tx.commit();
			return true;
		} catch (EntityExistsException e){
			Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
			
			if (tx.isActive()) try { tx.rollback(); } catch(Exception ex) {}
			return false;
		}
	}
}
