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
 * Shipment converter
 * 
 * @author Sarathit
 */

public class PaymentConverter {

	/** constructor */
	public PaymentConverter() {
	}

	public Payment orderToPayment(Order o,Shipment s) {
		Payment pay = new Payment();
		pay.setRecipientID(o.getRecipientID());
		pay.setSenderID(o.getSenderID());
		pay.setAmount(o.getAmount() + s.getTotal_cost());
		return pay;
	}

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