package org.ku.orderfulfillment.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for filtering returned response from the server.
 * Allow access control for GET POST PUT DELETE method
 * so that the client can call those method without an error.
 * 
 * @author Sarathit, Eknarin, Natcha, Natchanon
 */
public class ResponseFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
		FilterChain chain) throws IOException, ServletException {
		//System.out.println("LoginFilter : doFilter : Start");
		//System.out.println(req.toString());

		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, PUT, GET, DELETE");
		response.setHeader("Access-Control-Allow-Headers",
				"X-Requested-With, Content-Type , Accept, Location");
		response.setHeader("Access-Control-Expose-Headers", "Location");

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

}
