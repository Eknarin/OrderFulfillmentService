package org.ku.orderfulfillment.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Shipment class represents the shipment,
 * used for sending the request to the shipment service.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement(name = "shipment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Shipment implements Serializable {
	
	@XmlAttribute
	private long id;
	private String type;
	private String courier_name;
	private String courier_address;
	private String receive_name;
	private String receive_address;
	private float total_weight;
	private float total_cost;
	@XmlElement(name = "items")
	private ShipmentItems items;

	public Shipment(){}

	/**getters and setters*/
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getRecieve_name() {
		return receive_name;
	}

	public void setRecieve_name(String recieve_name) {
		this.receive_name = recieve_name;
	}

	public String getRecieve_address() {
		return receive_address;
	}

	public void setRecieve_address(String recieve_address) {
		this.receive_address = recieve_address;
	}

	public float getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(float total_cost) {
		this.total_cost = total_cost;
	}

	public float getTotal_weight() {
		return total_weight;
	}

	public void setTotal_weight(float total_weight) {
		this.total_weight = total_weight;
	}

	public ShipmentItems getItems() {
		return items;
	}

	public void setItems(ShipmentItems items) {
		this.items = items;
	}
}
