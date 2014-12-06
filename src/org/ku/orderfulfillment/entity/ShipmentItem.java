package org.ku.orderfulfillment.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Item class for the shipment attribute
 * (Item in this service is not the same as shipment service)
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShipmentItem implements Serializable{

	@XmlAttribute
	private long id;
	private String name;
	private double weight;
	private long quantity;

	
	/**constructor*/
	public ShipmentItem() { }
	
	/**constructor*/
	public ShipmentItem(long id,String name, double weight, long quantity){
		this.id = id;
		this.name = name;
		this.weight =  weight;
		this.quantity = quantity;
	}

	/**constructor*/
	public ShipmentItem(long id) {
		this.id = id;
	}

	/**getters and setters*/
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
