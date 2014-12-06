package org.ku.orderfulfillment.service;

import java.util.List;

import org.ku.orderfulfillment.entity.User;

/**
 * Interface defines the operations required by 
 * a DAO for User.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public interface UserDao {
	/**
	 * Find a user by ID in users.
	 * 
	 * @param the
	 *            id of user to find
	 * @return the matching user or null if the id is not found
	 */
	public abstract User find(long id);

	/**
	 * Return all the persisted users as a List. There is no guarantee what
	 * implementation of List is returned, so caller should use only List
	 * methods (not, say ArrayList).
	 * 
	 * @return list of all users in persistent storage. If no user, returns an
	 *         empty list.
	 */
	public abstract List<User> findAll();

	/**
	 * Delete a saved user by id.
	 * 
	 * @param id
	 *            the id of user to delete. Should be positive.
	 * @return true if user is deleted, false otherwise.
	 */
	public abstract boolean delete(long id);

	/**
	 * Save or replace a user. If the user.id is 0 then it is assumed to be a
	 * new (not saved) user. In this case a unique id is assigned to the user.
	 * If the user id is not zero and there is a saved user with same id, then
	 * the old user is replaced.
	 * 
	 * @param user
	 *            the user to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(User user);

	/**
	 * Update an user. If the user with same id as the update is already in
	 * persistent storage, then all fields of the user are replaced with values
	 * in the update (excluding null values). The id of the update must match
	 * the id of a user already persisted. If not, false is returned.
	 * 
	 * @param update
	 *            update info for the user.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(User user);

}
