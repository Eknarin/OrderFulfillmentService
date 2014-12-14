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
 * externalID, id(auto-gen) and other need attributes for creating
 * Payment and Shipment
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@Table(name = "orders")
@XmlRootElement(name="order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String PENDING_PAYMENT = "Pending Payment"; 
	public static final String WAITING = "In Queued";
	public static final String IN_PROGRESS = "In Progress";
	public static final String FULLFILLED = "Fulfilled";
	public static final String SHIPPING = "Shipping";
	public static final String CANCELED = "Canceled";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private long eCommerceOrderID;
	private long shipmentID;
	private long paymentID;
	private String shipmentURI;
	private String paymentURI;
	private String orderDate;
	private String fulfillDate;
	private String status;
	private String type;
	private String courier_name;
	private String courier_address;
	private String receive_name;
	private String receive_address;
	private String merchant_email;
	private double amount;
	private Items items;

	
	/**constructor*/
	public Order() { }
	
	/**constructor*/
	public Order(Long exID, Items itemList,String t, String cn, String ca, String rn, String ra,String merEmail, double amt) {
		eCommerceOrderID = exID;
		shipmentID = -1;
		paymentID = -1;
		shipmentURI = "-";
		orderDate = (new Date()).toString();
		fulfillDate = "-";
		status = Order.WAITING;
		items = itemList;
		type = t;
		courier_name = cn;
		courier_address = ca;
		receive_name = rn;
		receive_address = ra;
		merchant_email = merEmail;
		amount = amt;
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

		if(update.geteCommerceOrderID() >= 0) 
			this.seteCommerceOrderID(update.geteCommerceOrderID());
		
		if(update.getItems() != null) 
			this.setItems(update.getItems());
		
		if(update.getCourier_address() != null) 
			this.setCourier_address(update.getCourier_address());
		
		if(update.getCourier_name() != null) 
			this.setCourier_name(update.getCourier_name()); 
		
		if(update.getType() != null) 
			this.setType(update.getType());
		
		if(update.getReceive_address() != null) 
			this.setReceive_address(update.getReceive_address());
		
		if(update.getReceive_name() != null) 
			this.setReceive_name(update.getReceive_name());
		
		if(update.getMerchant_email() != null) 
			this.setMerchant_email(update.getMerchant_email());
		
		if(update.getAmount() > 0) 
			this.setAmount(update.getAmount());
		
		if(update.getPaymentURI() != null)
			this.setPaymentURI(update.getPaymentURI());
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

	public String getFulfillDate() {
		return fulfillDate;
	}

	public void setFulfillDate(String shipDate) {
		this.fulfillDate = shipDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Items getItems() {
		return items;
	}

	public void setItems(Items itemList) {
		this.items = itemList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCourier_name() {
		return courier_name;
	}

	public void setCourier_name(String courier_name) {
		this.courier_name = courier_name;
	}

	public String getCourier_address() {
		return courier_address;
	}

	public void setCourier_address(String courier_address) {
		this.courier_address = courier_address;
	}

	public String getReceive_name() {
		return receive_name;
	}

	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
	}

	public String getReceive_address() {
		return receive_address;
	}

	public void setReceive_address(String receive_address) {
		this.receive_address = receive_address;
	}

	public String getMerchant_email() {
		return merchant_email;
	}

	public void setMerchant_email(String merchant_email) {
		this.merchant_email = merchant_email;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPaymentURI() {
		return paymentURI;
	}

	public void setPaymentURI(String paymentURI) {
		this.paymentURI = paymentURI;
	}
	
	@Override
	public int hashCode(){
		String s = "" + id + eCommerceOrderID + shipmentID + paymentID
				+ shipmentURI + paymentURI + orderDate + fulfillDate
				+ status + type + courier_name + courier_address
				+ receive_name + receive_address + merchant_email
				+ amount + items;
		return s.hashCode();
	}
	
}