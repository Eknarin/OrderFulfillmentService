package org.ku.orderfulfillment.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * Wrapper class for ShipmentItem.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ShipmentItems implements Serializable{

	@XmlElement(name = "item")
	private List<ShipmentItem> items;
	
	public ShipmentItems(){
		this(new ArrayList());
	}
	
	public ShipmentItems(ShipmentItem i){
		items = new ArrayList<>();
		items.add(i);
	}
	
	public ShipmentItems(List<ShipmentItem> list){
		items = list;
	}
	
	public List<ShipmentItem> getItemList(){
		return items;
	}
}

