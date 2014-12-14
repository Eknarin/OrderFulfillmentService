package org.ku.orderfulfillment.service;


/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * This factory is an abstract class.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public abstract class DaoFactory {
	// singleton instance of this factory
	private static DaoFactory factory;
	
	protected DaoFactory() {}
	
	/**
	 * Get a singleton instance of the DaoFactory.
	 * @return instance of a concrete DaoFactory
	 */
	public static DaoFactory getInstance() {
		return factory;
	}
	
	/**
	 * Get an instance of a data access object for Order objects.
	 * @return instance of Order's DAO
	 */
	public abstract OrderDao getOrderDao();
	
	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();
	
	/**
	 * Set DaoFactory
	 * @param factory a DaoFactory to be set
	 */
	public static void setFactory(DaoFactory factory){
		DaoFactory.factory = factory;
	}
}
