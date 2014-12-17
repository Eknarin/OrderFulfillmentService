package org.ku.orderfulfillment.resource;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.security.RolesAllowed;
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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
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
import org.eclipse.jetty.http.HttpHeader;
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
 * OrderResource provides RESTful web resources using JAX-RS annotations to map
 * requests to request handling code, and to inject resources into code.
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
	private final Response BAD_REQUEST = Response.status(Status.BAD_REQUEST)
			.build();
	private final Response NO_CONTENT = Response.status(Status.NO_CONTENT)
			.build();
	private final Response NOT_FOUND = Response.status(Status.NOT_FOUND)
			.build();

	private String shipmentService = "http://track-trace.tk:8080";

	/** client for sending a request */
	private static HttpClient client;

	private ShipmentConverter shipConverter;

	private CacheControl cache;

	/** constructor */
	public OrderResource() {
		TimeZone.setDefault(TimeZone.getTimeZone("ICT"));
		dao = DaoFactory.getInstance().getOrderDao();
		logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		shipConverter = new ShipmentConverter();
		client = new HttpClient();
		cache = new CacheControl();
		cache.setMaxAge(-1);
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get all orders.
	 * 
	 * @param request
	 *            request
	 * @return all order(s) in the order list.
	 */
	@GET
	@RolesAllowed({ "admin", "fulfiller" })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getOrders(@HeaderParam("Accept") String accept,
			@Context javax.ws.rs.core.Request request) {
		logger.info("GETALL accept = " + accept);
		Orders orders = new Orders(dao.findAll());

		return returnWithETagGET(orders, request, accept, Orders.class);
	}

	/**
	 * Get one order by id.
	 * 
	 * @param id
	 *            id of the order
	 * @param accept
	 *            type of content
	 * @param request
	 *            request
	 * @return order with specific id
	 */
	@GET
	@Path("{id}")
	@RolesAllowed({ "admin", "fulfiller", "e-commerce" })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getOrderById(@PathParam("id") long id,
			@HeaderParam("Accept") String accept,
			@Context javax.ws.rs.core.Request request) {
		Order order = dao.find(id);
		logger.info("GET id = " + id + "accept + " + accept);

		if (order == null)
			return NOT_FOUND;

		return returnWithETagGET(order, request, accept, Order.class);
	}

	/**
	 * (For e-commerce) Ask for a shipment cost of an order.
	 * 
	 * @param order
	 *            order
	 * @param type
	 *            type of shipment
	 * @param request
	 *            request
	 * @return shipment cost
	 */
	@POST
	@Path("/shipmentcost")
	@RolesAllowed({ "admin", "e-commerce" })
	public Response checkOrderShipmentCost(String order,
			@HeaderParam("Content-Type") String type,
			@HeaderParam("Accept") String accept,
			@Context javax.ws.rs.core.Request request) {

		logger.info("POST COST type = " + type);
		Request req = client.newRequest(shipmentService
				+ "/shipments/calculate");
		Order o;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			o = stringJSONtoOrder(order);
		} else {
			o = stringXMLtoOrder(order);
		}

		Shipment shm = shipConverter.orderToShipment(o);
		String shipmentXML = shipConverter.shipmentToStringXML(shm);

		StringContentProvider content = new StringContentProvider(shipmentXML);
		req.method(HttpMethod.POST);
		req.content(content, MediaType.APPLICATION_XML);
		req.accept(MediaType.APPLICATION_XML);
		ContentResponse res;
		try {
			res = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			logger.info(e.toString());
			return BAD_REQUEST;
		}

		if (res.getStatus() == Response.Status.OK.getStatusCode()) {
			Shipment shipment = shipConverter.stringXMLtoShipment(res
					.getContentAsString());

			return returnWithETagGET(shipment, request, accept, Shipment.class);
		} else {
			return BAD_REQUEST;
		}
	}

	/**
	 * Create a new order. The server will assign a unique ID and return it as
	 * the location header. This method will be called only when the payment is
	 * successfully created The created order will be return for getting the
	 * order id from the server.
	 */

	/**
	 * (For E-Commerce) Create a new order. The server will assign a unique ID
	 * and return it as the location header. This method will be called only
	 * when the payment is successfully.
	 * 
	 * @param ord
	 *            placed order
	 * @return 201 created if the order is placed successfully
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@RolesAllowed({ "admin", "e-commerce" })
	public Response placeOrder(String ord,
			@HeaderParam("Content-Type") String type) {
		logger.info("Place order");

		Order order;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			order = stringJSONtoOrder(ord);
		} else {
			order = stringXMLtoOrder(ord);
		}

		Order o = new Order();

		if (checkOrder(order)) {
			o.seteCommerceOrderID(order.geteCommerceOrderID());
			o.setShipmentID(-1); // does not have yet
			o.setShipmentURI("-"); // does not have yet
			o.setOrderDate((new Date()).toString());
			o.setFulfillDate("-"); // does not have yet
			o.setStatus(Order.WAITING);
			o.setType(order.getType());
			o.setCourier_name(order.getCourier_name());
			o.setCourier_address(order.getCourier_address());
			o.setReceive_name(order.getReceive_name());
			o.setReceive_address(order.getReceive_address());
			o.setAmount(order.getAmount());
			o.setItems(order.getItems());

			boolean success = dao.save(o);
			if (success) {
				try {
					return Response
							.created(
									new URI(uriInfo.getAbsolutePath() + ""
											+ o.getId())).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return BAD_REQUEST;
	}

	/**
	 * (For E-Commerce) Cancel an order, possible when it's in waiting state.
	 * 
	 * @param id
	 *            internal id
	 * @param request
	 *            request
	 * @return URI location or no content if the updating order is null.
	 */
	@PUT
	@RolesAllowed({ "admin", "e-commerce" })
	@Path("{id}/cancel")
	public Response cancelOrder(@PathParam("id") long id,
			@Context javax.ws.rs.core.Request request) {
		logger.info("PUT CANCEL id = " + id);

		Order o = dao.find(id);

		if (o != null) {
			if (o.getStatus().equals(Order.WAITING)) {
				o.cancelOrder();
				dao.update(o);
				return returnWithETagPUT(o, request);
			}
			return BAD_REQUEST;
		}
		return NOT_FOUND;
	}

	/**
	 * (For Fulfiller) Grab the order, update an order status from waiting to in
	 * progress
	 * 
	 * @param id
	 *            internal id
	 * @param request
	 *            request
	 * @return URI location or no content if the updating order is null.
	 */
	@PUT
	@RolesAllowed({ "admin", "fulfiller" })
	@Path("{id}/grab")
	public Response grabOrder(@PathParam("id") long id,
			@Context javax.ws.rs.core.Request request) {
		logger.info("PUT GRAB id = " + id);

		Order o = dao.find(id);

		if (o != null) {
			if (o.getStatus().equals(Order.WAITING)) {
				o.updateStatus(Order.IN_PROGRESS);
				dao.update(o);
				// return Response.ok(uriInfo.getAbsolutePath()+"").build();
				return returnWithETagPUT(o, request);
			}
			return BAD_REQUEST;
		}
		return NOT_FOUND;
	}

	/**
	 * (For Fulfiller) Fulfill the order, update an order status from in
	 * progress to fulfilled.
	 * 
	 * @param id
	 *            internal id
	 * @param request
	 *            request
	 * @return URI location or no content if the updating order is null.
	 */
	@PUT
	@RolesAllowed({ "admin", "fulfiller" })
	@Path("{id}/fulfill")
	public Response fulfillOrder(@PathParam("id") long id,
			@Context javax.ws.rs.core.Request request) {
		logger.info("PUT FULFILL id = " + id);

		Order o = dao.find(id);
		if (o != null) {
			if (o.getStatus().equals(Order.IN_PROGRESS)) {
				o.updateStatus(Order.FULLFILLED);
				o.setFulfillDate((new Date()).toString());
				dao.update(o);
				// return Response.ok(uriInfo.getAbsolutePath()+"").build();
				return returnWithETagPUT(o, request);
			}
			return BAD_REQUEST;
		}
		return NOT_FOUND;
	}

	/**
	 * (For Fulfiller) Revert the order status back 1 step.
	 * 
	 * @param id
	 *            internal id
	 * @param request
	 *            request
	 * @return URI location or no content if the updating order is null.
	 */
	@PUT
	@RolesAllowed({ "admin", "fulfiller" })
	@Path("{id}/undo")
	public Response undoOrder(@PathParam("id") long id,
			@Context javax.ws.rs.core.Request request) {
		logger.info("PUT UNDO id = " + id);

		Order o = dao.find(id);
		if (o != null) {
			String status = o.getStatus();
			if (!status.equals(Order.WAITING)) {
				if (status.equals(Order.IN_PROGRESS)) {
					o.setStatus(Order.WAITING);
				} else if (status.equals(Order.FULLFILLED)) {
					o.setFulfillDate("-");
					o.setStatus(Order.IN_PROGRESS);
				} else if (status.equals(Order.SHIPPING)) {
					o.setStatus(Order.FULLFILLED);
				}

				dao.update(o);
				return returnWithETagPUT(o, request);
			}
			return BAD_REQUEST;
		}
		return NOT_FOUND;
	}

	/**
	 * Ship the order, update an order status from fulfilled to shipping. Will
	 * be used again if it was failed at the first request.
	 * 
	 * @param id
	 * @param request
	 *            request
	 * @return uri of the order
	 */
	@PUT
	@RolesAllowed({ "admin", "fulfiller" })
	@Path("{id}/ship")
	public Response shipOrder(@PathParam("id") long id,
			@Context javax.ws.rs.core.Request request) {
		logger.info("PUT SHIP id = " + id);

		Order o = dao.find(id);
		if (o != null) {
			if (o.getStatus().equals(Order.FULLFILLED)) {

				String location = sendShipmentOrder(o);
				if (location.length() > 0) {
					o.updateStatus(Order.SHIPPING);
					o.setShipmentURI(location);
					o.setShipmentID(splitID(location));
					dao.update(o);
					// return Response.ok(uriInfo.getAbsolutePath()+"").build();
					return returnWithETagPUT(o, request);
				}
			}
			return BAD_REQUEST;
		}
		return NOT_FOUND;
	}

	/**
	 * Send the request to shipment service for creating shipment.
	 * 
	 * @param order
	 *            order
	 * @return location of the created Shipment from Shipment Service
	 */
	public String sendShipmentOrder(Order order) {
		Request request = client.newRequest(shipmentService + "/shipments");
		Shipment shipment = shipConverter.orderToShipment(order);
		String shipmentXML = shipConverter.shipmentToStringXML(shipment);
		System.out.println(shipmentXML);

		StringContentProvider content = new StringContentProvider(shipmentXML);
		request.method(HttpMethod.POST);
		request.content(content, MediaType.APPLICATION_XML);
		request.accept(MediaType.APPLICATION_XML);

		String token = "";
		try {
			FileInputStream file = new FileInputStream(
					"/home/sb/access_token.txt");
			DataInputStream datafile = new DataInputStream(file);
			token = datafile.readLine();
			datafile.close();
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		request.header(HttpHeader.AUTHORIZATION, token);
		ContentResponse res;
		try {
			res = request.send();
			if (res.getStatus() == Status.CREATED.getStatusCode()) {
				String location = res.getHeaders().get(HttpHeader.LOCATION);
				return location;
			}

		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			logger.info(e.toString());
		}

		return "";
	}

	/**
	 * (For Fulfiller) Delete an order with the matching id. Delete will be
	 * possible when the order is in "Canceled" state.
	 * 
	 * @param id
	 *            id
	 * @return message for deleted id.
	 */
	@DELETE
	@RolesAllowed({ "admin", "fulfiller" })
	@Path("{id}")
	public Response deleteOrder(@PathParam("id") long id) {
		logger.info("DELETE id = " + id);
		Order o = dao.find(id);
		boolean success = false;

		if (o != null) {
			if (o.getStatus().equals(Order.CANCELED)) {
				success = dao.delete(id);
				if (success) {
					return OK;
				}
			}
			return BAD_REQUEST;
		}
		return NO_CONTENT;
	}

	/**
	 * Convert orders or order xml into json string
	 * 
	 * @param o
	 *            orders or order
	 * @return converted json string
	 */
	private String toJson(Object o, Class c) {
		JAXBContext context;
		StringWriter sw = new StringWriter();
		try {
			context = JAXBContext.newInstance(c);

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
	 * 
	 * @param s
	 *            xml string
	 * @return converted order
	 */
	private Order stringXMLtoOrder(String s) {
		JAXBContext context;
		try {
			StringReader sr = new StringReader(s);
			context = JAXBContext.newInstance(Order.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Order order = (Order) unmarshaller.unmarshal(sr);
			return order;
		} catch (JAXBException e) {
			logger.info("Cannot convert String XML to Order.");
		}
		return null;
	}

	/**
	 * Convert json string to payment object.
	 * 
	 * @param s
	 *            json string
	 * @return converted payment.
	 */
	private Order stringJSONtoOrder(String s) {
		JSONObject json = new JSONObject(s);
		String xml = XML.toString(json);
		return stringXMLtoOrder(xml);
	}

	/**
	 * Split the id from the uri The id should be after the last slash ("/") of
	 * the uri.
	 * 
	 * @param uri
	 *            uri
	 * @return id splitted id
	 */
	private long splitID(String uri) {
		char[] ch = uri.toCharArray();
		String id = "";
		for (int i = ch.length - 1; i >= 0; i--) {
			if (ch[i] == '/') {
				if (id.length() == 0) {
					continue;
				} else {
					break;
				}
			} else if (ch[i] >= '0' && ch[i] <= '9') {
				id = ch[i] + id;
			}
		}
		long splittedID = -1;
		try {
			splittedID = Long.parseLong(id);
		} catch (NumberFormatException e) {
			logger.info(e.toString());
		}
		return splittedID;
	}

	/**
	 * Evaluate precondition for the etag for GET method
	 * 
	 * @param ob
	 *            object (either Order, Orders, or Shipment)
	 * @param request
	 *            request
	 * @param accept
	 *            type of content
	 * @param c
	 *            class of the object
	 * @return response with the etag
	 */
	public Response returnWithETagGET(Object ob,
			javax.ws.rs.core.Request request, String accept, Class c) {
		EntityTag etag = new EntityTag((ob.hashCode() + accept).hashCode() + "");
		Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			if (accept.equals(MediaType.APPLICATION_JSON)) {

				builder = Response.ok(toJson(ob, c)).tag(etag);
			} else {
				builder = Response.ok(ob).tag(etag);
			}
		}
		builder.cacheControl(cache);

		return builder.build();
	}

	/**
	 * Evaluate precondition for the etag for PUT method
	 * 
	 * @param ob
	 *            object (either Order, Orders, or Shipment)
	 * @param request
	 *            request
	 * @return response with the etag
	 */
	public Response returnWithETagPUT(Object ob,
			javax.ws.rs.core.Request request) {

		EntityTag etag = new EntityTag(Integer.toString(ob.hashCode()));
		Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
		if (builder == null) {
			builder = Response.ok().header("Location",
					uriInfo.getAbsolutePath() + "");
		}
		builder.cacheControl(cache);

		return builder.build();
	}

	/**
	 * Check the validation of an order
	 * 
	 * @param order
	 *            order
	 * @return true if the order is valid otherwise false
	 */
	private boolean checkOrder(Order order) {
		if (order.geteCommerceOrderID() <= 0) {
			return false;
		}
		if (order.getType().trim().isEmpty()) {
			return false;
		}
		if (order.getReceive_address().trim().isEmpty()
				|| order.getReceive_name().trim().isEmpty()) {
			return false;
		}
		if (order.getCourier_address().trim().isEmpty()
				|| order.getCourier_name().trim().isEmpty()) {
			return false;
		}
		if (order.getAmount() <= 0) {
			return false;
		}
		if (order.getItems().getItemList().isEmpty()) {
			return false;
		}
		return true;
	}

	/** getters and setters */
	public String getShipmentService() {
		return shipmentService;
	}

	public void setShipmentService(String shipmentService) {
		this.shipmentService = shipmentService;
	}

}
