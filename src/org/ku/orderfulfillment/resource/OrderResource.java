package org.ku.orderfulfillment.resource;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
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
import org.ku.orderfulfillment.entity.Payment;
import org.ku.orderfulfillment.entity.Shipment;
import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.OrderDao;
import org.ku.orderfulfillment.util.PaymentConverter;
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
	private String paymentService = "http://128.199.212.108:25052";
	
	private static HttpClient client;
	
	private ShipmentConverter shipConverter;
	private PaymentConverter paymentConverter;

	public OrderResource() {
		dao = DaoFactory.getInstance().getOrderDao();
		logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
		shipConverter = new ShipmentConverter();
		paymentConverter = new PaymentConverter();
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get all orders.
	 * 
	 * @return all order(s) in the order list.
	 */
	@GET
	@RolesAllowed({"admin","fulfiller"})
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
	@RolesAllowed({"admin","fulfiller","e-commerce"})
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
	@Path("/shipmentcost")
	@RolesAllowed({"admin","e-commerce"})
	public Response checkOrderShipmentCost(String order, @HeaderParam("Content-Type") String type){
		logger.debug("type = " + type);

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

		StringContentProvider content = new StringContentProvider(shipmentXML);
		request.method(HttpMethod.POST);
		request.content(content, MediaType.APPLICATION_XML);
		request.accept(MediaType.APPLICATION_XML);
		ContentResponse res;
		//TODO get this comment back
//		try {
//			res = request.send();
//		} catch (InterruptedException | TimeoutException | ExecutionException e) {
//			logger.debug(e.toString());
//			return BAD_REQUEST;
//		}
//		
//		if(res.getStatus() == Response.Status.OK.getStatusCode()){
//			Shipment shipment = shipConverter.stringXMLtoShipment(res.getContentAsString());
//			//TODO may handle JSON
//			return Response.ok(shipment).build();
//		}
//		else{
//			return BAD_REQUEST;
//		}
		shm.setTotal_cost((long)Math.random()*1000);
		return Response.ok(shm).build();
	}
	
	/**
	 * Send the request to the payment service for creating a payment from an order.
	 * @param order order
	 * @param type content type
	 * @return 201 Created if the payment is created.
	 */
	@POST
	@Path("/payment")
	@RolesAllowed({"admin","e-commerce"})
	public Response checkPayment(String order, @HeaderParam("Content-Type") String type){

		logger.debug("type = " + type);

		Request request = client.newRequest(paymentService + "/payment");
		Order o ;
		if(type.equals(MediaType.APPLICATION_JSON)){
			o = stringJSONtoOrder(order);
		}
		else{
			o = stringXMLtoOrder(order);
		}

		//TODO check validation of the order
	
		Payment payment = paymentConverter.orderToPayment(o);
		String paymentXML = paymentConverter.paymentmentToStringXML(payment);
		
		StringContentProvider content = new StringContentProvider(paymentXML);
		request.method(HttpMethod.POST);
		request.content(content, MediaType.APPLICATION_XML);
		request.accept(MediaType.APPLICATION_XML);
		ContentResponse res;
		try {
			res = request.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			logger.debug(e.toString());
			return BAD_REQUEST;
		}
		
		if(res.getStatus() == Response.Status.CREATED.getStatusCode()){
			String location = res.getHeaders().get("Location");
			try {
				long paymentID = splitID(location);
				//create order after payment is successfully created.
				Order createdOrder = createOrder(o, paymentID);
				return Response.created(new URI((uriInfo.getAbsolutePath() + "").replace("payment/","") + createdOrder.getId())).build();
				//return Response.created(new URI(location)).build();
				
				//TODO decide what should be the return value ? 
			} catch (URISyntaxException e) {
				return BAD_REQUEST;
			}
		}
		else{
			return BAD_REQUEST;
		}
	}
	
	//TODO PSUEDO code
	@GET
	@Path("{id}/paymentstatus")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RolesAllowed({"admin","fulfiller"})
	public Response getOrderPaymentStatus(@PathParam("id") long id, @HeaderParam("Accept") String accept) {
		Order order = dao.find(id);
		logger.debug("id = " + id + "accept + " + accept);
		
		if (order == null)
			return NOT_FOUND;
		
		Payment p = paymentConverter.orderToPayment(order);
		//TODO send request to ask status from payment service
		//but now it's now available so this will return randomly.
		
		double random = Math.random();
		if(random >= 0.4){
			order.setStatus(Order.WAITING);
			dao.update(order);
		}
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			return Response.ok(toJson(order)).build();
		}
		return Response.ok(order).build();
	}

	/**
	 * Create a new order. The server will
	 * assign a unique ID and return it as the location header.
	 * This method will be called only when the payment is successfully created
	 * The created order will be return for getting the order id from the server.
	 * @param order order to be saved
	 * @param paymentID paymentID
	 * @return created order
	 */
	public Order createOrder(Order order,long paymentID){		
		
		logger.debug("Create order");
		Order o = new Order();
		
		o.seteCommerceOrderID(order.geteCommerceOrderID());
		o.setShipmentID(-1); //does not have yet
		o.setShipmentURI("-"); //does not have yet
		o.setPaymentID(paymentID);
		o.setOrderDate((new Date()).toString());
		o.setShipDate("-"); //does not have yet
		o.setStatus(Order.PENDING_PAYMENT);
		o.setType(order.getType());
		o.setCourier_name(order.getCourier_name());
		o.setCourier_address(order.getCourier_address());
		o.setReceive_name(order.getReceive_name());
		o.setReceive_address(order.getReceive_address());
		o.setRecipientID(order.getRecipientID());
		o.setSenderID(order.getSenderID());
		o.setAmount(order.getAmount());
		o.setItems(order.getItems());

		dao.save(o);
		
		return o;
	}
	 
	 /**
	  * (For E-Commerce)
	  * Cancel an order
	  * @param id internal id
	  * @return URI location or no content if the updating order is null.
	  */
	 @PUT
	 @RolesAllowed({"admin","e-commerce"})
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
	 @RolesAllowed({"admin","fulfiller"})
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
	 @RolesAllowed({"admin","fulfiller"})
	 @Path("{id}/fulfill") 
	 public Response fulfillOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);		
		 if(o != null){
			 if(o.getStatus().equals(Order.IN_PROGRESS)){
				 o.updateStatus(Order.FULLFILLED);
				 shipOrder(id);
				 dao.update(o);
				 
				 return Response.ok(uriInfo.getAbsolutePath()+"").build();
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	 @PUT
	 @RolesAllowed({"admin","fulfiller"})
	 @Path("{id}/ship") 
	 public Response shipOrder(@PathParam("id") long id){
		 logger.debug("id = " + id);
		 
		 Order o = dao.find(id);		
		 if(o != null){
			 if(o.getStatus().equals(Order.FULLFILLED)){
				 
				 //TODO Integrate send shipmentOrder
				 String location = "testest/shipments/100/";//sendShipmentOrder(o);
				 if(location.length() > 0){
					 o.updateStatus(Order.SHIPPING);
					 o.setShipDate((new Date()).toString());
					 o.setShipmentURI(location);
					 o.setShipmentID(splitID(location));
					 dao.update(o);
				 }
				 dao.update(o);
				 
				 return Response.ok(uriInfo.getAbsolutePath()+"").build();
			 }
			 return BAD_REQUEST;
		 } 
		 return NOT_FOUND;
	 }
	 
	 /**
	  * Send request for creating shipment.
	  * @param order order
	  * @return location of the created Shipment from Shipment Service
	  */
	 public String sendShipmentOrder(Order order){
		 Request request = client.newRequest(shipmentService + "/shipments");
		 Shipment shipment = shipConverter.orderToShipment(order);
		 String shipmentXML = shipConverter.shipmentToStringXML(shipment);
		 
		 StringContentProvider content = new StringContentProvider(shipmentXML);
		 request.method(HttpMethod.POST);
		 request.content(content, MediaType.APPLICATION_XML);
		 request.accept(MediaType.APPLICATION_XML);
		 ContentResponse res;
		 try {
			 res = request.send();
			 if(res.getStatus() == Status.CREATED.getStatusCode()){
				 String location = res.getHeaders().get("Location");
				 return location;
			 }
			 
		 } catch (InterruptedException | TimeoutException | ExecutionException e) {
			 logger.debug(e.toString());
		 }
		 
		 return "";
	 }
	 
	
	 /**
	  * (For Fulfiller)
	  * Delete an order with the matching id.
	  * Delete will be possible when the order is in "Canceled" state.
	  * @param id id
	  * @return message for deleted id.
	  */
	 @DELETE
	 @RolesAllowed({"admin","fulfiller"})
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
	 
	 /**
	  * Split the id from the uri
	  * The id should be after the last slash ("/") of the uri.
	  * @param uri uri
	  * @return id 
	  */
	 private long splitID(String uri){
		 char[] ch = uri.toCharArray();
		 String id = "";
		 for(int i = ch.length -1 ; i >= 0 ; i--){
			 if(ch[i] == '/'){
				 if(id.length() == 0){
					 continue;
				 }
				 else{
					 break;
				 }
			 }
			 else if(ch[i] >= '0' && ch[i] <= '9'){
				 id = ch[i] + id;
			 }
		 }
		 long splittedID = -1;
		 try{
			 splittedID = Long.parseLong(id);
		 } catch(NumberFormatException e){
			 logger.debug(e.toString());
		 }
		 return splittedID;
	 }
}
