package org.ku.orderfulfillment.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {
	private static final long serialVersionUID = -2938168483793196372L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private String username;
	private String password;
	private String role;
	private String company;

	public User() {

	}
	
	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}

	public User(long id) {
		this.id = id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) throws Exception {
		// String hash = byteArrayToHexString(computeHash(password));
		this.password = password;
	}
	
	/**
	 * hash the password
	 */
	// private String byteArrayToHexString(byte[] txt) {
	// StringBuffer buffer = new StringBuffer(txt.length * 2);
	// for (int i = 0; i < txt.length; i++){
	// int x = txt[i] & 0xff;
	// if (x < 16) {
	// buffer.append('0');
	// }
	// buffer.append(Integer.toHexString(x));
	// }
	// return buffer.toString().toUpperCase();
	// }
	//
	// public byte[] computeHash(String password2) throws Exception {
	// java.security.MessageDigest message = null;
	// message = java.security.MessageDigest.getInstance("SHA-1");
	// message.reset();
	// message.update(password2.getBytes());
	// return message.digest();
	// }

	@Override
	public String toString() {
		return String.format("[%ld] %s (%s)", username, password);
	}

	/**
	 * Update this user's data from another user. The id field of the update
	 * must either be 0 or the same value as this user.
	 * 
	 * @param update
	 *            the source of update values
	 * @throws Exception
	 */
	public void applyUpdate(User update) throws Exception {
		if (update == null)
			return;
		if (update.getId() != 0 && update.getId() != this.getId())
			throw new IllegalArgumentException("Update order must have same id as user to update");

		if (update.getUsername() != null)
			this.setUsername(username);
		if (update.getPassword() != null)
			this.setPassword(password);
	}

}
