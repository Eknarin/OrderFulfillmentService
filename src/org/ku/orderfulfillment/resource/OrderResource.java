package org.ku.orderfulfillment.resource;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONObject;
import org.json.XML;
import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.entity.Orders;
import org.ku.orderfulfillment.entity.Shipment;
import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.OrderDao;
import org.ku.orderfulfillment.util.ShipmentConverter;
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
	
	private final Response OK = Response.status(Status.OK).build();
	private final Response CONFLICT = Response.status(Status.CONFLICT).build();
	private final Response BAD_REQUEST = Response.status(Status.BAD_REQUEST).build();
	private final Response NO_CONTENT = Response.status(Status.NO_CONTENT).build();
	private final Response NOT_FOUND = Response.status(Status.NOT_FOUND).build();
	
	private String shipmentService = "http://10.2.31.107:8080";
	private String paymentService;
	
	private static HttpClient client;
	
	private ShipmentConverter shipConverter;

	public OrderResource() {
		dao = DaoFactory.getInstance().getOrderDao();
		logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
		shipConverter = new ShipmentConverter();
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TOTO call set service URI
	}

	/**
	 * Get all orders.
	 * 
	 * @return all order(s) in the order list.
	 */
	@GET
	//@RolesAllowed("fulfiller")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getOrders(@HeaderParam("Accept") String accept) {
		logger.debug("accept = " + accept);
		
		Orders orders = new Orders(dao.findAll());
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			return Response.ok(toJson(orders)).build();
		}
		return Response.ok(orders).build();
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
	//@RolesAllowed({"fulfiller","e-commerce"})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getOrderById(@PathParam("id") long id, @HeaderParam("Accept") String accept) {
		Order order = dao.find(id);
		logger.debug("id = " + id + "accept + " + accept);
		
		if (order == null)
			return NOT_FOUND;		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			return Response.ok(toJson(order)).build();
		}
		return Response.ok(order).build();
	}
	
	/**
	 * (For e-commerce)
	 * Ask for a shipment cost of an order.
	 * @param order order
	 * @param type type of shipment
	 * @return shipment cost
	 */
	@POST
	@Path("/shipcost")
	//@RolesAllowed("e-commerce")
	public Response checkOrderShipmentCost(String order, @HeaderParam("Content-Type") String type){
		logger.debug("type = " + type);
		System.out.println("asdasd");
		Request request = client.newRequest(shipmentService + "/shipments/calculate");
		Order o ;
		if(type.equals(MediaType.APPLICATION_JSON)){
			o = stringJSONtoOrder(order);
		}
		else{
			o = stringXMLtoOrder(order);
		}
		Shipment shm = shipConverter.orderToShipment(o);
		String shipmentXML = shipConverter.shipmentToStringXML(shm);
		System.out.println(shipmentXML);
		StringContentProvider content = new StringContentProvider(shipmentXML);
		request.method(HttpMethod.POST);
		request.content(content, MediaType.APPLICATION_XML);
		request.accept(type);
		ContentResponse res;
		try {
			res = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			logger.debug(e.toString());
			return BAD_REQUEST;
		}
		
		if(res.getStatus() == Response.Status.OK.getStatusCode()){
			Shipment shipment = shipConverter.stringXMLtoShipment(res.getContentAsString());
			return Response.ok(shipment).build();
		}
		else{
			return BAD_REQUEST;
		}
	}
	
	//payment

	/**
	 * Create a new order. If order id is omitted or 0, the server will
	 * assign a unique ID and return it as the location header.
	 * 
	 * @param order order
	 * @param type content-type
	 * @return URI location
	 */
	@POST
	//@RolesAllowed("e-commerce")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response postOrder(String order, @HeaderParam("Content-Type") String type){//),@HeaderParam("Authorization") String auth){		
		//if() auth
		
		logger.debug("type = " + type);
		Order o;
		if(type.equals(MediaType.APPLICATION_JSON)){
			o = stringJSONtoOrder(order); 
		}
		else{
			o = stringXMLtoOrder(order);
		}
		
		if (dao.find(o.getId()) == null ){//&& checkItemList(o)) {
			
			o.setOrderDate((new Date()).toString());
			o.setStatus(Order.WAITING); 
			o.setShipDate("-");
			o.setPaymentID(-1);
			o.setShipDate("-");
			o.setShipmentID(-1);
			o.setShipmentURI("-");

			boolean success = dao.save(o);
			if (success) {
				try {
					return Response.created(new URI(uriInfo.getAbsolutePath()+""+o.getId())).build();
				} catch (URISyntaxException e) {
					System.out.println("Error-POST");
				}
			}
			return BAD_REQUEST;
		}
		else {
			return CONFLICT;
		}
	}
	
	 /**
	  * (For E-Commerce)
	  * Update an order. Only update the attributes supplied in request body.
	  * @param id internal id
	  * @param order order
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 //@RolesAllowed("e-commerce")
	 @Path("{id}")
	 @Consumes(MediaType.APPLICATION_XML)
	 public Response updateOrder(@PathParam("id") long id, JAXBElement<Order> order){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);
		 Order update = (Order)order.getValue();
		 boolean success = false;	
		
		 if(o != null){
			 if(o.getStatus().equals(Order.WAITING)){
				 o.applyUpdate(update);
				 if(id == update.getId()){
					 success = dao.update(o);
				 }
				 if(success){
					 return Response.ok(uriInfo.getAbsolutePath()+"").build();
				 }
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	 /**
	  * (For E-Commerce)
	  * Cancel an order
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 //@RolesAllowed("e-commerce")
	 @Path("{id}/cancel")
	 public Response cancelOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);	
		
		 if(o != null){
			 if(o.getStatus().equals(Order.WAITING)){
				 o.cancelOrder();
				 dao.update(o);
				 return Response.ok(uriInfo.getAbsolutePath()+"").build();
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	 /**
	  * (For Fulfiller)
	  * Update an order status
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 //@RolesAllowed("fulfiller")
	 @Path("{id}/grab")
	 public Response grabOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);
		
		 if(o != null){
			 if(o.getStatus().equals(Order.WAITING)){
				 o.updateStatus(Order.IN_PROGRESS);
				 dao.update(o);
				 return Response.ok(uriInfo.getAbsolutePath()+"").build();
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	 /**
	  * (For Fulfiller)
	  * Update an order status
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 //@RolesAllowed("fulfiller")
	 @Path("{id}/fulfill") 
	 public Response fulfillOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);		
		 if(o != null){
			 if(o.getStatus().equals(Order.IN_PROGRESS)){
				 o.updateStatus(Order.FULLFILLED);
				 o.setShipDate((new Date()).toString());
				 dao.update(o);
				 
				 return Response.ok(uriInfo.getAbsolutePath()+"").build();
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	
	 /**
	  * (For Fulfiller)
	  * Delete an order with the matching id.
	  * Delete will be possible when the order is in "Canceled" state.
	  * @param id id
	  * @return message for deleted id.
	  */
	 @DELETE
	 //@RolesAllowed("fulfiller")
	 @Path("{id}")
	 public Response deleteOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 Order o = dao.find(id);
		 boolean success = false;
		
		 if(o != null){
			 if(o.getStatus().equals(Order.CANCELED)){
				 success = dao.delete(id);
				 if(success){
					 return OK;
				 }
			 }
			 return BAD_REQUEST;
		 }
		 return NO_CONTENT;
	 }
	 
	 /**
	  * Convert orders or order xml into json string
	  * @param o orders or order
	  * @return converted json string
	  */
	 private String toJson(Object o){
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 if(o instanceof Orders){
				 context = JAXBContext.newInstance(Orders.class);
			 }
			 else{
				 context = JAXBContext.newInstance(Order.class);
			 }
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
	 
	 /**
	  * Convert xml string to order object.
	  * @param s xml string
	  * @return converted order
	  */
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
	 
	 /**
	  * Convert json string to order object.
	  * @param s json string
	  * @return converted order.
	  */
	 private Order stringJSONtoOrder(String s){
		 JSONObject json = new JSONObject(s);
		 String xml = XML.toString(json);
		 return stringXMLtoOrder(xml);
	 }
	 
	 public String getShipmentService() {
		 return shipmentService;
	 }

	 public void setShipmentService(String shipmentService) {
		 this.shipmentService = shipmentService;
	 }

	 public String getPaymentService() {
		 return paymentService;
	 }

	 public void setPaymentService(String paymentService) {
		 this.paymentService = paymentService;
	 }	 
}
