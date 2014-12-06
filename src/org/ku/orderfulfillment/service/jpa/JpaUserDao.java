package org.ku.orderfulfillment.service.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.entity.User;
import org.ku.orderfulfillment.service.UserDao;

public class JpaUserDao implements UserDao{

	/** the EntityManager for accessing JPA persistence services. */
	private final EntityManager em;
	
	public JpaUserDao(EntityManager em) throws Exception {
		this.em = em;
		createTestUser();
	}
	/** add orders for testing. 
	 * @throws Exception */
	private void createTestUser( ) throws Exception {
		long id = 9999; // usually we should let JPA set the id
		if (find(id) == null) {
			
			User testUser = new User();
			testUser.setId(id);
			testUser.setUsername("ko_kaowoat");
			testUser.setPassword("5678");
			testUser.setRole("fulfiller");
			testUser.setCompany("J-Force");
			save(testUser);
			
		}
	}


	@Override
	public User find(long id) {
		return em.find(User.class, id);
	}

//
//	@Override
//	public boolean update(User user) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public boolean save(User user) {
		if (user == null) throw new IllegalArgumentException("Can't save a null order");
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(user);
			tx.commit();
			return true;
		} catch (EntityExistsException ex) {
			Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
			if (tx.isActive()) try { tx.rollback(); } catch(Exception e) {}
			return false;
		}
	}
	@Override
	public List<User> findAll() {
		Query query = em.createQuery("SELECT u FROM User u");
		List<User> userList = query.getResultList();
		System.out.println(userList.size());
		return Collections.unmodifiableList(userList);
	}
	@Override
	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean update(User user) {
		// TODO Auto-generated method stub
		return false;
	}



}
