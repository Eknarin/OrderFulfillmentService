package org.ku.orderfulfillment.service;


/**
 * DaoFactory defines methods for obtaining instance of data access objects.
 * To create DAO you first get an instance of a concrete factory by invoking
 * <p>
 * <tt>DaoFactory factory = DaoFactory.getInstance(); </tt>
 * <p>
 * Then use the <tt>factory</tt> object to get instances of actual DAO.
 * This factory is an abstract class.  There are concrete subclasses for
 * each persistence mechanism.  You can add your own factory by subclassing
 * this factory.
 * 
 * @author Eknarin, Natcha, Natchanon, Sarathit
 */
public abstract class DaoFactory {
	// singleton instance of this factory
	private static DaoFactory factory;
	
	/** this class shouldn't be instantiated, but constructor must be visible to subclasses. */
	protected DaoFactory() {
		// nothing to do
	}
	
	/**
	 * Get a singleton instance of the DaoFactory.
	 * @return instance of a concrete DaoFactory
	 */
	public static DaoFactory getInstance() {
		return factory;
	}
	
	/**
	 * Get an instance of a data access object for Contact objects.
	 * Subclasses of the base DaoFactory class must provide a concrete
	 * instance of this method that returns a ContactDao suitable
	 * for their persistence framework.
	 * @return instance of Contact's DAO
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
	 * @param factory a DaoFactory
	 */
	public static void setFactory(DaoFactory factory){
		DaoFactory.factory = factory;
	}
}
