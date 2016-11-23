package edu.upenn.cis.cis455.webserver;

import java.util.Enumeration;

import junit.framework.TestCase;

public class MyServletConfigTest extends TestCase {

	private MyServletConfig config;
	private final String name = "Kevin";
	
	public void testInitParam() {
		MyServletContext context = new MyServletContext();
		config = new MyServletConfig(name, context);
		String arg0 = "test";
		String arg1 = "value";
		config.setInitParam(arg0, arg1);
		assertEquals(config.getInitParameter(arg0), arg1);
		
		Enumeration vec = config.getInitParameterNames();
		
		assertEquals(vec.nextElement(), arg0);
	}
	
	public void testGetter() {
		MyServletContext context = new MyServletContext();
		config = new MyServletConfig(name, context);
		assertEquals(config.getServletName(), this.name);
	}

}
