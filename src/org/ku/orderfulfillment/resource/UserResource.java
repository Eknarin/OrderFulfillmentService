package org.ku.orderfulfillment.resource;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;
import org.json.XML;
import org.ku.orderfulfillment.entity.Order;
import org.ku.orderfulfillment.entity.Orders;
import org.ku.orderfulfillment.entity.User;
import org.ku.orderfulfillment.entity.Users;
import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.UserDao;

@Singleton
@Path("/users")
public class UserResource {

	@Context
	UriInfo uriInfo;

	private UserDao userDao;
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	public UserResource() {
		userDao = DaoFactory.getInstance().getUserDao();
	}
	
	/**
	 * Get all users.
	 * 
	 * @return all order(s) in the order list.
	 */
	@GET
	@RolesAllowed({"admin"})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getOrders(@HeaderParam("Accept") String accept) {
		System.out.print("GETALL ");
		Users users = new Users(userDao.findAll());
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			System.out.println("JSON");
			return Response.ok(toJson(users)).header("Access-Control-Allow-Origin", "*").build();
		}
		System.out.println("XML");
		return Response.ok(users).header("Access-Control-Allow-Origin", "*").build();
	}

	/**
	 * Get one user by id.
	 * 
	 * @param id
	 *            id of the user
	 * @param accept
	 *            type of content
	 * @return order with specific id
	 */
	@GET
	@RolesAllowed({"admin"})
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUserById(@PathParam("id") long id,
			@HeaderParam("Accept") String accept) {
		User user = (User) userDao.find(id);
		System.out.print("GET BY ID ");
		if (user == null)
			return Response.status(Status.NOT_FOUND).build();

		if (accept.equals(MediaType.APPLICATION_JSON)) {
			System.out.println("JSON");
			return Response.ok(toJson(user))
					.header("Access-Control-Allow-Origin", "*").build();
		}
		System.out.println("XML");
		return Response.ok(user).header("Access-Control-Allow-Origin", "*")
				.build();
	}

	/**
	 * Create a new order. If order id is omitted or 0, the server will assign a
	 * unique ID and return it as the location header.
	 * 
	 * @param order
	 *            order
	 * @param type
	 *            content-type
	 * @return URI location
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postUser(String u,
			@HeaderParam("Content-Type") String type) {

		System.out.print("POST ");
		User user;
		if (type.equals(MediaType.APPLICATION_JSON)) {
			System.out.println("JSON");
			user = stringJSONtoUser(u);
		} else {
			System.out.println("XML");
			user = stringXMLtoUser(u);
		}

		if (userDao.find(user.getId()) == null) {// && checkItemList(o)) {

			boolean success = userDao.save(user);
			if (success) {
				try {
					return Response
							.created(
									new URI(uriInfo.getAbsolutePath() + ""
											+ user.getId())).build();
				} catch (URISyntaxException e) {
					System.out.println("Error-POST");
				}
			}
			return Response.status(Status.BAD_REQUEST).build();
		} else {
			Response.status(Status.CONFLICT).build();
		}
		return Response.status(Status.CONFLICT).build();

	}

	private String toJson(Users users) {
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 context = JAXBContext.newInstance(Orders.class);
			 Marshaller marsahller = (Marshaller) context.createMarshaller();
			 marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 marsahller.marshal(users, sw);
			  
			 JSONObject xmlJSONObj = XML.toJSONObject(sw.toString());
			 
			 return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert XML to JSON.");
		 }
		 
		 return "";
	}
	
	private String toJson(User user) {
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 context = JAXBContext.newInstance(Orders.class);
			 Marshaller marsahller = (Marshaller) context.createMarshaller();
			 marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 marsahller.marshal(user, sw);
			  
			 JSONObject xmlJSONObj = XML.toJSONObject(sw.toString());
			 
			 return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		 } catch (JAXBException e) {
			 System.out.println("Cannot convert XML to JSON.");
		 }
		 
		 return "";
	}

	private User stringXMLtoUser(String xml) {
		JAXBContext context;
		try {
			StringReader sr = new StringReader(xml);
			context = JAXBContext.newInstance(User.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			User user = (User) unmarshaller.unmarshal(sr);
			return user;
		} catch (JAXBException e) {
			System.out.println("Cannot convert String XML to User.");
		}
		return null;
	}
	
	private User stringJSONtoUser(String s){
		 JSONObject json = new JSONObject(s);
		 String xml = XML.toString(json);
		 return stringXMLtoUser(xml);
	 }
}
