package org.ku.orderfulfillment.server;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.JDBCLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.glassfish.jersey.server.ServerProperties;
import org.ku.orderfulfillment.service.DaoFactory;
import org.ku.orderfulfillment.service.jpa.JpaDaoFactory;

/**
 * Main class for running the application and set path of the url
 * which link to the resource.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public class JettyMain {
	
	/** 
	 * The default port to listen on.
	 */
	static final int PORT = 5722;
	private static Server server;

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * 
	 * @param args not used
	 * @throws Exception if Jetty server encounters any problem
	 */
	public static void main(String[] args) throws Exception {
		startServer(PORT);
		waitForExit();
	}
	
	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * @param port port of the server.
	 * @return the url for connecting to the server.
	 * @throws Exception 
	 */
	public static String startServer(int port) throws Exception{
		server = new Server( port );
		
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		context.setContextPath("/fulfillment");
		
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.ku.orderfulfillment.resource");
		context.addFilter(ResponseFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
		context.addServlet( holder, "/*" );

		server.setHandler( getSecurityHandler( context ) );
		
		DaoFactory.setFactory(new JpaDaoFactory());
		
		System.out.println("Starting Jetty server on port " + port);
		try {
			server.start();
			return server.getURI().toString();
		} catch (Exception e) {}
		
		return "";
	}
	
	/**
	 * Set the context to be a security context.
	 * @param handler handler to be set.
	 * @return security handler
	 * @throws IOException 
	 */
	private static Handler getSecurityHandler(ServletContextHandler handler) throws IOException {
		// params to LoginService are realm name and properties file.

		 LoginService loginService = new JDBCLoginService("myrealm", "src/OrdfRealm.properties");
		 server.addBean( loginService );

		 Constraint constraint = new Constraint();
		 constraint.setName("auth");
		 constraint.setAuthenticate( true );
		 // Only allow users that have these roles.
		 // It is more appropriate to specify this in the resource
		 // itself using annotations.
		 // But if I comment this out, Jetty returns 403 Forbidden
		 // instead of 401 Unauthorized.
		 constraint.setRoles( new String[] {"fulfiller", "admin","e-commerce"} );

		 // A mapping of resource paths to constraints
		 ConstraintMapping mapping = new ConstraintMapping();
		 mapping.setPathSpec("/*");
		 mapping.setConstraint( constraint );
		 ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		 // setConstraintMappings requires an array or List as argument
		 securityHandler.setConstraintMappings( new ConstraintMapping[] { mapping } );
		 securityHandler.setAuthenticator( new DigestAuthenticator());
		 securityHandler.setLoginService(loginService);

		 // finally: wrap the parameter (Handler) in securityHandler
		 securityHandler.setHandler(handler);
		 return securityHandler;
	}
	
	/**
	 * Wait for stopping the server by pressing enter.
	 */
	public static void waitForExit() {
		try {
			System.out.println("Server started.  Press ENTER to exit.");
			System.in.read();
			System.out.println("Stopping server.");
			stopServer();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Stop the server.
	 */
	public static void stopServer(){
		try {
			JpaDaoFactory.getInstance().shutdown();
			server.stop();
		} catch (Exception e) {}
	}
	
}


