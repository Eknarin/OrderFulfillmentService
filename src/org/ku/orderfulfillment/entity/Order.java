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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An order will be placed from an e-commerce, the order has
 * externalID, id(auto-gen), List of ordered item id, order date,
 * fulfill date, status and the url.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
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
	private String orderDate;
	private String fulfillDate;
	private String status;
	private String orderURI;
	private String itemIDList;
	
	/**constructor*/
	public Order() { }
	
	/**constructor*/
	public Order(Long exID, List<Long> list, String uri) {
		externalID = exID;
		itemIDList = listToString(list);
		orderDate = (new Date()).toString();
		fulfillDate = "-";
		status = "Waiting";
		orderURI = uri;
	}

	/**constructor*/
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

	public String getItemIDList() {
		return itemIDList;
	}

	public void setItemIDList(String itemIDList) {
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
	
	@Override
	public String toString() {
		return String.format("[%ld] %s (%s)", id, orderDate, status);
	}
	
	/** 
	 * Two orders are equal if they have the same id,
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
	 * The id field of the update must either be 0 or the same value as this order.
	 * @param update the source of update values
	 */
	public void applyUpdate(Order update) {
		if (update == null) return;
		if (update.getId() != 0 && update.getId() != this.getId() )
			throw new IllegalArgumentException("Update order must have same id as order to update");

		if(update.getExternalID() >= 0) this.setExternalID(update.getExternalID());
		if(update.getItemIDList() != null) this.setItemIDList(update.getItemIDList());
		if(update.getOrderURI() != null) this.setOrderURI(update.getOrderURI()); 
	}
	
	/**
	 * Cancel the order.
	 */
	public void cancelOrder(){
		this.setStatus("Canceled");
	}
	

	/**
	 * Update the status of the order.
	 * @param status status to be updated. (Waiting, In Process, Fulfilled, Canceled)
	 */
	public void updateStatus(String status){
		this.setStatus(status);
	}
	
	public String listToString(List<Long> list){
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < list.size() ; i++){
			System.out.println(list.get(i));
			sb.append(list.get(i).longValue());
			if(i != list.size() - 1){
				sb.append(",");
			}
		}
		return sb.toString();
	}
}