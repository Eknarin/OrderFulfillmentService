package org.ku.orderfulfillment.service.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.ku.orderfulfillment.entity.Item;
import org.ku.orderfulfillment.entity.Items;
import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.service.OrderDao;

/**
 * Data access object for saving and retrieving orders,
 * using JPA.
 * To get an instance of this class use:
 * dao = DaoFactory.getInstance().getOrderDao()
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public class JpaOrderDao implements OrderDao {
	
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
			List<Item> list = new ArrayList<Item>();
			list.add(new Item(1,"Pig", 80, "Piggy", 30, 2));
			list.add(new Item(2,"Fish", 10, "Fishy", 5, 10));
			list.add(new Item(3,"Dog", 20, "Doggy", 34.5, 3));
			Items items = new Items(list);
			Order test = new Order(1234L, items, "EMS", "BB", "BB-HOME", "Kyuuri", "Kyuuri-Home");
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
			
			order.applyUpdate(update);
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
