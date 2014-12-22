package org.ku.orderfulfillment.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Item class represents the products or the items in the order list.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item implements Serializable{

	//@XmlAttribute(name = "id")
	private long id;
	private String name;
	private double price;
	private String description;
	private double weight;
	private long quantity;

	
	/**constructor*/
	public Item() { }
	
	/**constructor*/
	public Item(long id,String name, double price, String desc, double weight, long quantity){
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = desc;
		this.weight =  weight;
		this.quantity = quantity;
	}

	/**constructor*/
	public Item(long id) {
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
