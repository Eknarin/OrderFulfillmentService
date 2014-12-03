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
	
	public static final String WAITING = "Waiting";
	public static final String IN_PROGRESS = "In Progress";
	public static final String FULLFILLED = "Fulfilled";
	public static final String SHIPPED = "Shipped";
	public static final String CANCELED = "Canceled";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private long eCommerceOrderID;
	private long shipmentID;
	private long paymentID;
	private String orderURI;
	private String shipmentURI;
	private String orderDate;
	private String shipDate;
	private String status;
	@XmlElement
	private Items itemIDList;

	
	/**constructor*/
	public Order() { }
	
	/**constructor*/
	public Order(Long exID, List<Long> list, String uri) {
		eCommerceOrderID = exID;
		shipmentID = -1;
		paymentID = -1;
		orderURI = uri;
		shipmentURI = "-";
		orderDate = (new Date()).toString();
		shipDate = "-";
		status = Order.WAITING;
		itemIDList = new Items(list);
	}

	/**constructor*/
	public Order(long id) {
		this.id = id;
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

		if(update.geteCommerceOrderID() > 0) this.seteCommerceOrderID(update.geteCommerceOrderID());
		if(update.getItemIDList() != null) this.setItemIDList(update.getItemIDList());
		if(update.getOrderURI() != null) this.setOrderURI(update.getOrderURI()); 
	}
	
	/**
	 * Cancel the order.
	 */
	public void cancelOrder(){
		this.setStatus(Order.CANCELED);
	}
	

	/**
	 * Update the status of the order.
	 * @param status status to be updated. (Waiting, In Process, Fulfilled, Canceled, Shipped)
	 */
	public void updateStatus(String status){
		this.setStatus(status);
	}

	/**Getters and Setters*/
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long geteCommerceOrderID() {
		return eCommerceOrderID;
	}

	public void seteCommerceOrderID(long eCommerceOrderID) {
		this.eCommerceOrderID = eCommerceOrderID;
	}

	public long getShipmentID() {
		return shipmentID;
	}

	public void setShipmentID(long shipmentID) {
		this.shipmentID = shipmentID;
	}

	public long getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(long paymentID) {
		this.paymentID = paymentID;
	}

	public String getOrderURI() {
		return orderURI;
	}

	public void setOrderURI(String orderURI) {
		this.orderURI = orderURI;
	}

	public String getShipmentURI() {
		return shipmentURI;
	}

	public void setShipmentURI(String shipmentURI) {
		this.shipmentURI = shipmentURI;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getShipDate() {
		return shipDate;
	}

	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Items getItemIDList() {
		return itemIDList;
	}

	public void setItemIDList(Items itemIDList) {
		this.itemIDList = itemIDList;
	}
	
	
}