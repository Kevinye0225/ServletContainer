package edu.upenn.cis.cis455.webserver;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * This class implements the ServletConfig interface and contains
 * information about the servlet
 * @author cis555
 *
 */

public class MyServletConfig implements ServletConfig {

	private String name;
	private MyServletContext context;
	private HashMap<String,String> initParams;
	
	public MyServletConfig(String name, MyServletContext context) {
		this.name = name;
		this.context = context;
		initParams = new HashMap<String,String>();
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
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return this.context;
	}

	@Override
	public String getServletName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	public void setInitParam(String name, String value){
		this.initParams.put(name, value);
	}

}
