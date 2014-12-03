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
 * Wrapper class for Item ID
 * @author Sarathit
 *
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Items implements Serializable{
	@XmlElement(name = "item")
	private List<Long> itemsID;
	
	public Items(){
		this(new ArrayList());
	}
	
	public Items(Long l){
		itemsID = new ArrayList<>();
		itemsID.add(l);
	}
	
	public Items(List<Long> list){
		itemsID = list;
	}
}
