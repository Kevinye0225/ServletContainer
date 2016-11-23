package edu.upenn.cis.cis455.webserver;

import java.util.Enumeration;

import junit.framework.TestCase;

public class MyServletContextTest extends TestCase {

	private MyServletContext context;
	
	public void testAttribute() {
		this.context = new MyServletContext();
		String arg0 = "test";
		int arg1 = 1;
		context.setAttribute(arg0, arg1);
		assertEquals(context.getAttribute(arg0), arg1);
		
		Enumeration vec = context.getAttributeNames();
		assertEquals(vec.nextElement(), arg0);
		
		context.removeAttribute(arg0);
		assertEquals(context.getAttribute(arg0), null);
	}
	
	public void testInitParam() {
		this.context = new MyServletContext();
		String arg0 = "test";
		String arg1 = "value";
		context.setInitParam(arg0, arg1);
		assertEquals(context.getInitParameter(arg0), arg1);
		
		Enumeration vec = context.getInitParameterNames();
		assertEquals(vec.nextElement(), arg0);
	}
	
	public void testGetRealPath() {
		this.context = new MyServletContext();
		String arg0 = "/index.html";
		HttpServer.port = 8080;
		String expected = "http://localhost:8080/index.html";
		assertEquals(context.getRealPath(arg0), expected);
		
		String arg1 = "http://localhost:8080/index.html";
		assertEquals(context.getRealPath(arg1), expected);
	}
	
	public void testGetter() {
		this.context = new MyServletContext();
		assertEquals(context.getServletContextName(), "HttpServer");
		assertEquals(context.getServerInfo(), "JavaServer 1.0");
	}

}
