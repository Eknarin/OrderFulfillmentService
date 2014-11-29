package org.ku.orderfulfillment.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.OrderDao;

/**
 * 
 * 
 */
public class JpaDaoFactory extends DaoFactory {
	private static final String PERSISTENCE_UNIT = "orders";
	/** instance of the entity DAO */
	private OrderDao orderDao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(JpaDaoFactory.class.getName());
	}
	
	public JpaDaoFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
		orderDao = new JpaOrderDao( em );
	}
	
	@Override
	public OrderDao getOrderDao() {
		return orderDao;
	}
	
	@Override
	public void shutdown() {
		try {
			if (em != null && em.isOpen()) em.close();
			if (emf != null && emf.isOpen()) emf.close();
		} catch (IllegalStateException ex) {
			logger.log(Level.SEVERE, ex.toString());
		}
	}
}
