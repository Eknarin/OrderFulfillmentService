package org.ku.orderfulfillment.resource;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;
import org.json.XML;
import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.entity.Orders;
import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.OrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OrderResource provides RESTful web resources using JAX-RS annotations to
 * map requests to request handling code, and to inject resources into code.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
@Singleton
@Path("/orders")
public class OrderResource {
	private final Logger logger;

	@Context
	UriInfo uriInfo;

	private OrderDao dao;
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	public OrderResource() {
		dao = DaoFactory.getInstance().getOrderDao();
		logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	}

	/**
	 * Get all orders.
	 * 
	 * @return all order(s) in the order list.
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getOrders(@HeaderParam("Accept") String accept) {
//		System.out.print("GETALL ");
		logger.debug("accept="+accept);
		
		Orders orders = new Orders(dao.findAll());
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			System.out.println("JSON");
			return Response.ok(toJson(orders)).header("Access-Control-Allow-Origin", "*").build();
		}
		System.out.println("XML");
		return Response.ok(orders).header("Access-Control-Allow-Origin", "*").build();
	}

	/**
	 * Get one order by id.
	 * 
	 * @param id id of the order
	 * @param accept type of content
	 * @return order with specific id
	 */
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getOrderById(@PathParam("id") long id, @HeaderParam("Accept") String accept) {
		Order order = dao.find(id);
//		System.out.print("GET BY ID ");
		logger.debug("id="+id);
		if (order == null)
			return Response.status(Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			System.out.println("JSON");
			return Response.ok(toJson(order)).header("Access-Control-Allow-Origin", "*").build();
		}
		System.out.println("XML");
		return Response.ok(order).header("Access-Control-Allow-Origin", "*").build();
	}

	/**
	 * Create a new order. If order id is omitted or 0, the server will
	 * assign a unique ID and return it as the location header.
	 * 
	 * @param order order
	 * @param type content-type
	 * @return URI location
	 */
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response postOrder(String order, @HeaderParam("Content-Type") String type){	
		
		System.out.print("POST ");
		Order o;
		if(type.equals(MediaType.APPLICATION_JSON)){
			System.out.println("JSON");
			o = stringJSONtoOrder(order); 
		}
		else{
			System.out.println("XML");
			o = stringXMLtoOrder(order);
		}
		
		if (dao.find(o.getId()) == null ){//&& checkItemList(o)) {
			
			o.setOrderDate((new Date()).toString());
			o.setStatus("Waiting"); //TODO enum/static  
			o.setFulfillDate("-");

			boolean success = dao.save(o);
			if (success) {
				try {
					return Response.created(new URI(uriInfo.getAbsolutePath()+""+o.getId())).header("Access-Control-Allow-Origin", "*").build();
				} catch (URISyntaxException e) {
					System.out.println("Error-POST");
				}
			}
			return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();	
		}
		else {
			Response.status(Status.CONFLICT).header("Access-Control-Allow-Origin", "*").build();
		}
		return Response.status(Status.CONFLICT).header("Access-Control-Allow-Origin", "*").build();

	}
	
	 /**
	  * (For E-Commerce)
	  * Update an order. Only update the attributes supplied in request body.
	  * @param id internal id
	  * @param order order
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 @Path("{id}")
	 @Consumes(MediaType.APPLICATION_XML)
	 public Response updateOrder(@PathParam("id") long id, JAXBElement<Order> order){
		 System.out.println("PUT-UPDATE");
		 
		 Order o = dao.find(id);
		 Order update = (Order)order.getValue();
		 boolean success = false;	
		
		 if(o != null){
			 if(o.getStatus().equals("Waiting")){
				 o.applyUpdate(update);
				 if(id == update.getId()){
					 success = dao.update(o);
				 }
				 if(success){
					 return Response.ok(uriInfo.getAbsolutePath()+"").header("Access-Control-Allow-Origin", "*").build();
				 }
			 }
			 return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		 } 
		 return Response.status(Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	 }
	 
	 /**
	  * (For E-Commerce)
	  * Cancel an order
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 @Path("{id}/cancel")
	 public Response cancelOrder(@PathParam("id") long id){
		 System.out.println("PUT-CANCEL");
		 
		 Order o = dao.find(id);	
		
		 if(o != null){
			 if(o.getStatus().equals("Waiting")){
				 o.cancelOrder();
				 dao.update(o);
				 return Response.ok(uriInfo.getAbsolutePath()+"").header("Access-Control-Allow-Origin", "*").build();
			 }
			 return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		 } 
		 return Response.status(Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	 }
	 
	 /**
	  * (For Fulfiller)
	  * Update an order status
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 @Path("fulfiller/grab/{id}")
	 public Response grabOrder(@PathParam("id") long id){
		 System.out.println("PUT-UPDATE-GRAB");
		 
		 Order o = dao.find(id);
		
		 if(o != null){
			 if(o.getStatus().equals("Waiting")){
				 o.updateStatus("In Process");
				 dao.update(o);
				 return Response.ok(uriInfo.getAbsolutePath()+"").header("Access-Control-Allow-Origin", "*").build();
			 }
			 return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		 } 
		 return Response.status(Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	 }
	 
	 /**
	  * (For Fulfiller)
	  * Update an order status
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 // resource hierarchy: /fulfill/{id}/grab
	 //                     /fulfill/{id}/fullfill
	 @Path("fulfiller/fulfill/{id}")
	 
	 public Response fulfillOrder(@PathParam("id") long id){
		 System.out.println("PUT-UPDATE-FULFILL");		 
		 Order o = dao.find(id);		
		 if(o != null){
			 if(o.getStatus().equals("In Process")){
				 o.updateStatus("Fullfilled");
				 o.setFulfillDate((new Date()).toString());	 
				 dao.update(o);
				 
				 return Response.ok(uriInfo.getAbsolutePath()+"").header("Access-Control-Allow-Origin", "*").build();
			 }
			 return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		 } 
		 return Response.status(Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	 }
	 
	
	 /**
	  * (For Fulfiller)
	  * Delete an order with the matching id.
	  * Delete will be possible when the order is in "Canceled" state.
	  * @param id id
	  * @return message for deleted id.
	  */
	 @DELETE
	 @Path("fulfiller/delete/{id}")
	 public Response deleteOrder(@PathParam("id") long id){
		 Order o = dao.find(id);
		 boolean success = false;
		
		 if(o != null){
			 if(o.getStatus().equals("Canceled")){
				 success = dao.delete(id);
				 if(success){
					 return Response.ok().header("Access-Control-Allow-Origin", "*").build();
				 }
			 }
			 return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		 }
		 return Response.status(Status.NO_CONTENT).header("Access-Control-Allow-Origin", "*").build();
	 }
	 
//	 /**
//	  * Verify the validity of the order item.
//	  * @param o order
//	  * @return true if the order is valid; otherwise false.
//	  */
//	 private boolean checkItemList(Order o){
//		 String items = o.getItemIDList();
//		 if(items == null || items.length() == 0){
//			 return false;
//		 }
//		 
//		 char[] itemsArray = items.toCharArray();
//		 for(char c : itemsArray){
//			 if(( c >= '0' && c <= '9' ) || c == ','){
//				 continue;
//			 }
//			 else{
//				 return false;
//			 }
//		 }
//		 return true;
//	 }
	 
	 private String toJson(Orders o){
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 context = JAXBContext.newInstance(Orders.class);
			 Marshaller marsahller = (Marshaller) context.createMarshaller();
			 marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 marsahller.marshal(o, sw);
			  
			 JSONObject xmlJSONObj = XML.toJSONObject(sw.toString());
			 
			 return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert XML to JSON.");
		 }
		 
		 return "";
	 }
	 
	 private String toJson(Order o){
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 context = JAXBContext.newInstance(Orders.class);
			 Marshaller marsahller = (Marshaller) context.createMarshaller();
			 marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 marsahller.marshal(o, sw);
			  
			 JSONObject xmlJSONObj = XML.toJSONObject(sw.toString());
			 
			 return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert XML to JSON.");
		 }
		 
		 return "";
	 }
	 
	 private Order stringXMLtoOrder(String s){
		 JAXBContext context;
		 try {
			 StringReader sr = new StringReader(s);
			 context = JAXBContext.newInstance(Order.class);
			 Unmarshaller unmarshaller = context.createUnmarshaller();
			 Order order = (Order) unmarshaller.unmarshal(sr);
			 return order;
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert String XML to Order.");
		 }	 
		 return null;
	 }
	 
	 private Order stringJSONtoOrder(String s){
		 JSONObject json = new JSONObject(s);
		 String xml = XML.toString(json);
		 return stringXMLtoOrder(xml);
	 }
}
