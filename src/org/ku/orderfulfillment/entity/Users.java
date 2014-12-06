package org.ku.orderfulfillment.entity;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class for user.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Users {
	
	@XmlElement(name = "user")
	private List<User> users = new ArrayList<User>();	
	public Users() {
		
	}
	
	/**constructor*/
	public Users(User user) {
		users = new ArrayList<>();
		users.add(user);
	}
	
	/**constructor*/
	public Users(List<User> list){
		users = list;
	}
}
