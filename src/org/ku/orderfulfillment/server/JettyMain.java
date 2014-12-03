package org.ku.orderfulfillment.server;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
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
	
	//TODO
	/** 
	 * The default port to listen on. Typically 80 or 8080.  
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
	 */
	public static String startServer(int port){
		server = new Server( port );
		
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		context.setContextPath("/ordf");
		
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.ku.orderfulfillment.resource");
		context.addFilter(ResponseFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
		context.addServlet( holder, "/*" );

		server.setHandler( context );
		
		DaoFactory.setFactory(new JpaDaoFactory());
		
		System.out.println("Starting Jetty server on port " + port);
		try {
			server.start();
			return server.getURI().toString();
		} catch (Exception e) {}
		
		return "";
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


