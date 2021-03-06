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

	private String name;
	private double weight;
	private long quantity;

	
	/**constructor*/
	public ShipmentItem() { }
	
	/**constructor*/
	public ShipmentItem(String name, double weight, long quantity){
		this.name = name;
		this.weight =  weight;
		this.quantity = quantity;
	}

	/**getters and setters*/
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
