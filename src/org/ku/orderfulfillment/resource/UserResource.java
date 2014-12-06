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
import org.ku.orderfulfillment.entity.Shipment;
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
	
	private final Response OK = Response.status(Status.OK).build();
	private final Response CONFLICT = Response.status(Status.CONFLICT).build();
	private final Response BAD_REQUEST = Response.status(Status.BAD_REQUEST).build();
	private final Response NO_CONTENT = Response.status(Status.NO_CONTENT).build();
	private final Response NOT_FOUND = Response.status(Status.NOT_FOUND).build();

	/**constructor*/
	public UserResource() {
		userDao = DaoFactory.getInstance().getUserDao();
	}
	
	/**
	 * Get all users.
	 * 
	 * @return all user(s) in the user list.
	 */
	@GET
	@RolesAllowed({"admin"})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getUsers(@HeaderParam("Accept") String accept) {

		Users users = new Users(userDao.findAll());
		
		if(accept.equals(MediaType.APPLICATION_JSON)){
			return Response.ok(toJson(users)).build();
		}
		return Response.ok(users).build();
	}

	/**
	 * Get one user by id.
	 * 
	 * @param id
	 *            id of the user
	 * @param accept
	 *            type of content
	 * @return user with specific id
	 */
	@GET
	@RolesAllowed({"admin"})
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUserById(@PathParam("id") long id, @HeaderParam("Accept") String accept) {
		User user = (User) userDao.find(id);

		if (user == null)
			return NOT_FOUND;
		if (accept.equals(MediaType.APPLICATION_JSON)) {
			return Response.ok(toJson(user)).build();
		}
		return Response.ok(user).build();
	}
	
	/**
	  * Convert users or user xml into json string
	  * @param o users or user
	  * @return converted json string
	  */
	 private String toJson(Object o){
		 JAXBContext context;
		 StringWriter sw = new StringWriter();
		 try{
			 if(o instanceof Users){
				 context = JAXBContext.newInstance(Users.class);
			 }
			 else{
				 context = JAXBContext.newInstance(User.class);
			 }
			 Marshaller marsahller = (Marshaller) context.createMarshaller();
			 marsahller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			 marsahller.marshal(o, sw);
			  
			 JSONObject xmlJSONObj = XML.toJSONObject(sw.toString());
			 
			 return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		 } catch (JAXBException e) {
			
		 }
		 
		 return "";
	 }
}
