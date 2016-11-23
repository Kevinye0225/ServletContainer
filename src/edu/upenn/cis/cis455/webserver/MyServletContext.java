package edu.upenn.cis.cis455.webserver;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MyServletContext implements ServletContext {
	private HashMap<String,Object> attributes;
	private HashMap<String,String> initParams;
	
	public MyServletContext() {
		this.attributes = new HashMap<String, Object>();
		this.initParams = new HashMap<String, String>();
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return this.attributes.get(arg0);
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		Set<String> keys = attributes.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}

	@Override
	public ServletContext getContext(String arg0) {
		// TODO Auto-generated method stub
		return HttpServer.context;
	}

	@Override
	public String getInitParameter(String arg0) {
		// TODO Auto-generated method stub
		return this.initParams.get(arg0);
	}

	@Override
	public Enumeration getInitParameterNames() {
		// TODO Auto-generated method stub
		Set<String> keys = initParams.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	/*
	 * this method should return null
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	/*
	 * this method is not required to implement
	 * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 */
	public RequestDispatcher getNamedDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * return absolute url
	 */
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		if (arg0.startsWith("http://")){
			return arg0;
		} else {
			String realPath = null;
			if (arg0.startsWith("/")){
				realPath = "http://localhost:" + HttpServer.port + arg0;
			} else {
				realPath = "http://localhost:" + HttpServer.port + "/" + arg0;
			}
			return realPath;
		}
	}

	@Override
	/*
	 * not required
	 */
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * this method is not required to implement
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String arg0) throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * this method is not required to implement
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * this method is not required to implement
	 * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	public Set getResourcePaths(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfo() {
		// TODO Auto-generated method stub
		return "JavaServer 1.0";
	}

	@Override
	/*
	 * Deprecated
	 */
	public Servlet getServlet(String arg0) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletContextName() {
		// TODO Auto-generated method stub
		return "HttpServer";
	}

	@Override
	/*
	 * Deprecated
	 */
	public Enumeration getServletNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * Deprecated
	 */
	public Enumeration getServlets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Exception arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		attributes.remove(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		attributes.put(arg0, arg1);
	}
	
	public void setInitParam(String name, String value) {
		initParams.put(name, value);
	}

}
