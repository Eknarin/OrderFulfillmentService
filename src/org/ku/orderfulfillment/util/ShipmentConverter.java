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
import org.ku.orderfulfillment.entity.Shipment;
import org.ku.orderfulfillment.entity.ShipmentItem;
import org.ku.orderfulfillment.entity.ShipmentItems;

/**
 * Shipment converter, used for converting shipment to order
 * or xml string back and forth by marshaling and unmarshaling 
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */

public class ShipmentConverter {

	/** constructor */
	public ShipmentConverter() {
	}

	/**
	 * Convert order object into shipment object.
	 * @param order order
	 * @return shipment
	 */
	public Shipment orderToShipment(Order order) {
		Shipment shm = new Shipment();
		shm.setCourier_address(order.getCourier_address());
		shm.setCourier_name(order.getCourier_name());
		shm.setItems(itemsToShipmentItems(order.getItems()));
		shm.setRecieve_address(order.getReceive_address());
		shm.setRecieve_name(order.getReceive_name());
		shm.setType(order.getType());
		return shm;
	}

	/**
	 * Convert items from order into list of shipmentItem
	 * @param items items
	 * @return list of shipmentItem
	 */
	public ShipmentItems itemsToShipmentItems(Items items) {
		List<ShipmentItem> list = new ArrayList<ShipmentItem>();

		for (Item i : items.getItemList()) {
			ShipmentItem si = new ShipmentItem();
			si.setName(i.getName());
			si.setQuantity(i.getQuantity());
			si.setWeight(i.getWeight());
			list.add(si);
		}
		return new ShipmentItems(list);
	}

	/**
	 * Convert shipment object into xml string
	 * @param shipment shipment
	 * @return converted xml string
	 */
	public String shipmentToStringXML(Shipment shipment) {
		JAXBContext context;
		StringWriter sw = new StringWriter();
		try {
			context = JAXBContext.newInstance(Shipment.class);
			Marshaller marsahller = (Marshaller) context.createMarshaller();
			marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marsahller.marshal(shipment, sw);
			return sw.toString();
		} catch (JAXBException e) {
			System.out.println("Cannot marshal");
		}
		return null;
	}

	/**
	 * Convert xml string into shipment object.
	 * @param xml xml string
	 * @return shipment object
	 */
	public Shipment stringXMLtoShipment(String xml){
		JAXBContext context;
		 try {
			 StringReader sr = new StringReader(xml);
			 context = JAXBContext.newInstance(Shipment.class);
			 Unmarshaller unmarshaller = context.createUnmarshaller();
			 Shipment shm = (Shipment) unmarshaller.unmarshal(sr);
			 return shm;
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert String XML to Shipment.");
		 }	 
		 return null;
	}
}