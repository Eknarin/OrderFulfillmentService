package org.ku.orderfulfillment.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ku.orderfulfillment.entity.Item;
import org.ku.orderfulfillment.entity.Items;
import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.entity.Payment;
import org.ku.orderfulfillment.entity.Shipment;
import org.ku.orderfulfillment.entity.ShipmentItem;

/**
 * Payment converter, used for converting payment to order
 * or xml string back and forth by marshaling and unmarshaling 
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */

public class PaymentConverter {

	/** constructor */
	public PaymentConverter() {
	}

	/**
	 * Convert order object to payment object
	 * @param o order
	 * @return payment object
	 */
	public Payment orderToPayment(Order o) {
		Payment pay = new Payment();
		pay.setRecipientID(o.getRecipientID());
		pay.setSenderID(o.getSenderID());
		pay.setAmount(o.getAmount());
		return pay;
	}

	/**
	 * Convert payment object into xml string
	 * @param payment payment
	 * @return convreted xml string
	 */
	public String paymentmentToStringXML(Payment payment) {
		JAXBContext context;
		StringWriter sw = new StringWriter();
		try {
			context = JAXBContext.newInstance(Payment.class);
			Marshaller marsahller = (Marshaller) context.createMarshaller();
			marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marsahller.marshal(payment, sw);
			return sw.toString();
		} catch (JAXBException e) {
			System.out.println("Cannot marshal payment");
		}
		return null;
	}

	
}