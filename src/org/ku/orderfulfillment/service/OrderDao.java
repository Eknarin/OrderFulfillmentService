package org.ku.orderfulfillment.service;

import java.util.List;

import org.ku.orderfulfillment.entity.Order;
/**
 * Interface defines the operations required by 
 * a DAO for Order.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public interface OrderDao {

	
	/** Find a order by ID in orders.
	 * @param the id of order to find
	 * @return the matching order or null if the id is not found
	 */
	public abstract Order find(long id);

	/**
	 * Return all the persisted orders as a List.
	 * There is no guarantee what implementation of
	 * List is returned, so caller should use only
	 * List methods (not, say ArrayList).
	 * @return list of all orders in persistent storage.
	 *   If no order, returns an empty list.
	 */
	public abstract List<Order> findAll();

	/**
	 * Delete a saved order by id.
	 * @param id the id of order to delete. Should be positive.
	 * @return true if order is deleted, false otherwise.
	 */
	public abstract boolean delete(long id);

	/**
	 * Save or replace a order.
	 * If the order.id is 0 then it is assumed to be a
	 * new (not saved) order.  In this case a unique id
	 * is assigned to the order.  
	 * If the order id is not zero and there is a saved
	 * order with same id, then the old order is replaced.
	 * @param order the order to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(Order order);

	/**
	 * Update an order.  If the order with same id
	 * as the update is already in persistent storage,
	 * then all fields of the order are replaced with
	 * values in the update (excluding null values).
	 * The id of the update must match the id of a order
	 * already persisted.  If not, false is returned.
	 * @param update update info for the order.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(Order update);


}