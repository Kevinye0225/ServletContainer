package edu.upenn.cis.cis455.webserver;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MyHttpSession implements HttpSession {
	
	private static int idList = 0;
	private int sessionId = 0;
	private Properties m_props = new Properties();
	private boolean m_valid = true;
	private Date creationTime;
	private long lastAccessTime;
	int maxInterval = 100*100*5;
	private Cookie sessionCookie;
	
	public MyHttpSession(){
		idList++;
		sessionId = idList;
		this.creationTime = new Date();
		this.lastAccessTime = new Date().getTime();
		this.sessionCookie = new Cookie("JSESSIONID", String.valueOf(sessionId));
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return this.m_props.get(arg0);
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return this.m_props.keys();
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		if (!this.isValid()){
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return this.creationTime.getTime();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return String.valueOf(this.sessionId);
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return this.lastAccessTime;
	}

	@Override
	/*
	 * return max interval(non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return this.maxInterval;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return HttpServer.context;
	}

	/*
	 * Deprecated
	 */
	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Deprecated
	 */
	@Override
	public Object getValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Deprecated
	 */
	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		SessionPool.sessions.remove(String.valueOf(sessionId));
		
		this.m_valid = false;
	}

	@Override
	/*
	 * the server only supports cookie-enable
	 */
	public boolean isNew() {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		return false;
	}

	@Override
	/*
	 * Deprecated (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
	 */
	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.m_props.remove(arg0);
	}

	@Override
	/*
	 * Deprecated (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
	 */
	public void removeValue(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.m_props.put(arg0, arg1);
	}

	@Override
	/*
	 * Set maxInactiveInterval time(non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	public void setMaxInactiveInterval(int arg0) {
		// TODO Auto-generated method stub
		if (!this.isValid()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.maxInterval = arg0;
	}
	
	public boolean isValid(){
		long currentTime = new Date().getTime();
		if (currentTime - this.lastAccessTime > TimeUnit.MINUTES.toMillis(this.maxInterval)){
			this.m_valid = false;
		}
		
		return this.m_valid;
	}
	
	public Cookie getSessionCookie(){
		return this.sessionCookie;
	}

}
