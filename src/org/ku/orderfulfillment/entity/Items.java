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
 * Wrapper class for Item
 * @author Sarathit
 *
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Items implements Serializable{

	@XmlElement(name = "item")
	private List<Item> items;
	
	public Items(){
		this(new ArrayList());
	}
	
	public Items(Item i){
		items = new ArrayList<>();
		items.add(i);
	}
	
	public Items(List<Item> list){
		items = list;
	}
	
	public List<Item> getItemList(){
		return items;
	}
}
