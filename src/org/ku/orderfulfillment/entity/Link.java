package org.ku.orderfulfillment.entity;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class link for atom link.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@XmlRootElement(name="link")
@XmlAccessorType(XmlAccessType.FIELD)
public class Link implements Serializable{
	@XmlAttribute(name = "rel")
	private String rel;
	@XmlAttribute(name = "href")
	private URI href;
	
	/**constructor*/
	public Link(){
		this("",null);
	}
	
	public Link(String rel, URI href){
		this.rel = rel;
		this.href = href;
	}

	/**getters and setters*/
	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public URI getHref() {
		return href;
	}

	public void setHref(URI href) {
		this.href = href;
	}

}
