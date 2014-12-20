package org.ku.orderfulfillment.service.jpa;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.ku.orderfulfillment.entity.Link;
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
	
	/** add orders for testing only. */
	private void createTestOrder( ) {
		long id = 1003; // usually we should let JPA set the id
		while(true){
			if (find(id) == null) {
				List<Item> list = new ArrayList<Item>();
				list.add(new Item(1,"Tent", 80, "Tent for testing", 30, 2));
				list.add(new Item(2,"Sleeping Bag", 10, "Sleeping bag for testing", 5, 10));
				list.add(new Item(3,"Lighter", 20, "Lighter for testing", 34.5, 3));
				Items items = new Items(list);
				Link l = null;
				try {
					l = new Link("self",new URI("128.199.175.223/fulfillment/orders/" + id));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				Order test = new Order(1234L, items, "EMS", "Sabaii-Test", "KU", "Kyuuri", "Kyuuri-Home",320,l);
				test.setId(id);
				if(id < 1006) test.setStatus(Order.CANCELED);
				else if(id < 1009) test.setStatus(Order.IN_PROGRESS);
				else if(id < 10012) test.setStatus(Order.FULLFILLED);
				save(test);
			}
			id++;
			if(id == 1015) break;
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
