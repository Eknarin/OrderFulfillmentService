package org.ku.orderfulfillment.entity;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Sarathit
 */
@Entity
@Table(name = "orders")
@XmlRootElement(name="order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private long externalID; 
	private List<Long> itemIDList;
	private String orderDate;
	private String fulfillDate;
	private String status;
	private String orderURI;
	
	public Order() { }
	
	public Order(Long exID, List<Long> list, String uri) {
		externalID = exID;
		itemIDList = list;
		orderDate = (new Date()).toString();
		status = "Waiting";
		orderURI = uri;
	}

	public Order(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getExternalID() {
		return externalID;
	}

	public void setExternalID(long externalID) {
		this.externalID = externalID;
	}

	public List<Long> getItemIDList() {
		return itemIDList;
	}

	public void setItemIDList(List<Long> itemIDList) {
		this.itemIDList = itemIDList;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getFulfillDate() {
		return fulfillDate;
	}

	public void setFulfillDate(String fulfillDate) {
		this.fulfillDate = fulfillDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderURI() {
		return orderURI;
	}

	public void setOrderURI(String orderURI) {
		this.orderURI = orderURI;
	}
	
	//TODO
	@Override
	public String toString() {
		return String.format("[%ld] %s (%s)", id, orderDate, status);
	}
	
	/** Two orders are equal if they have the same id,
	 * even if other attributes differ.
	 * @param other another order to compare to this one.
	 */
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass()) return false;
		Order order = (Order) other;
		return order.getId() == this.getId();
	}
	
	/**
	 * Update this order's data from another Order.
	 * The id field of the update must either be 0 or the same value as this order!
	 * @param update the source of update values
	 */
	public void applyUpdate(Order update) {
		if (update == null) return;
		if (update.getId() != 0 && update.getId() != this.getId() )
			throw new IllegalArgumentException("Update order must have same id as contact to update");
		// Since title is used to display contacts, don't allow empty title
		if(update.getExternalID() >= 0) this.setExternalID(update.getExternalID());
		if(update.getItemIDList() != null) this.setItemIDList(update.getItemIDList());
		if(update.getOrderDate() != null) this.setOrderDate(update.getOrderDate());
		if(update.getOrderURI() != null) this.setOrderURI(update.getOrderURI()); 
		if(update.getStatus() != null) this.setStatus(update.getStatus());
		
//		if (! isEmpty( update.getTitle()) ) this.setTitle(update.getTitle()); // empty nickname is ok
//		// other attributes: allow an empty string as a way of deleting an attribute in update (this is hacky)
//		if (update.getName() != null ) this.setName(update.getName()); 
//		if (update.getEmail() != null) this.setEmail(update.getEmail());
//		if (update.getPhoneNumber() != null) this.setPhoneNumber(update.getPhoneNumber());
	}
	
	
	/**
	 * Test if a string is null or only whitespace.
	 * @param arg the string to test
	 * @return true if string variable is null or contains only whitespace
	 */
	private static boolean isEmpty(String arg) {
		return arg == null || arg.matches("\\s*") ;
	}
}