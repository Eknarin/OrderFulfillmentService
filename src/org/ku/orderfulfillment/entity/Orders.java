package org.ku.orderfulfillment.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * Wrapper class for Order
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Orders {
	@XmlElement(name = "order")
	private List<Order> orders;
	
	public Orders(){
		this(new ArrayList());
	}
	
	public Orders(Order o){
		orders = new ArrayList<Order>();
		orders.add(o);
	}
	
	public Orders(List<Order> list){
		orders = list;
	}
}
