package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MyHttpServletRequest implements HttpServletRequest {
	private Properties params = new Properties();
	private Properties props = new Properties();
	private MyHttpSession session = null;
	private String method;
	private String characterEncoding = "ISO-8859-1";
	private HttpRequest request;
	private Locale locale;
	
	/*
	 * pass in request and session
	 */
	public MyHttpServletRequest(HttpRequest request, MyHttpSession session){
		this.request = request;
		this.method = this.request.getMethod();
		this.session = session;
		
	}
	
	public MyHttpServletRequest(MyHttpSession session){
		this.session = session;
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return props.get(arg0);
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return props.keys();
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return this.characterEncoding;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		if (this.method != null && this.method.equals("POST")){
			String body = this.request.headers.get("body");
			return body.length();
		}
		return 0;
	}

	@Override
	/*
	 * Return the MIME type(non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	public String getContentType() {
		// TODO Auto-generated method stub
		String contentType = "text/html";
		return contentType;
	}

	@Override
	/*
	 * Not required(non-Javadoc)
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * return local address of socket
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return this.request.socket.getLocalAddress().toString();
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return this.request.headers.get("host");
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return this.request.socket.getPort();
	}

	@Override
	/*
	 * return null if locale is not set(non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return this.locale;
	}
	
	/*
	 * set locale
	 */
	public void setLocale(Locale locale){
		this.locale = locale;
	}

	@Override
	/*
	 * Not required(non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return this.params.getProperty(arg0);
	}

	@Override
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return this.params;
	}

	@Override
	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		return this.params.keys();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return (String[]) this.params.get(arg0);
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		String version = this.request.getVersion();
		
		String res = "HTTP/" + version;
		return res;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return this.request.bf;
	}

	@Override
	/*
	 * Deprecated(non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return this.request.socket.getInetAddress().toString();
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return this.request.socket.getInetAddress().getCanonicalHostName();
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return this.request.socket.getPort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return "http";
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return this.request.headers.get("host");
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return HttpServer.port;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		props.remove(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		props.put(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		this.characterEncoding = arg0;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return "BASIC";
	}

	@Override
	/*
	 * do not handle context path at this point
	 */
	public String getContextPath() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	/*
	 * get cookies from request(non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getCookies()
	 */
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		ArrayList<Cookie> cookies = HttpRequest.getCookieList();
		return cookies.toArray(new Cookie[cookies.size()]);
	}

	@Override
	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		String dateHeader = this.request.headers.get(arg0.toLowerCase());
		if (dateHeader == null) return -1;
		Date date = this.convertDate(dateHeader);
		
		return date.getTime();
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return this.request.headers.get(arg0.toLowerCase());
	}

	@Override
	public Enumeration getHeaderNames() {
		// TODO Auto-generated method stub
		Vector<String> vector = new Vector<String>();
		Set<String> headers = this.request.headers.keySet();
		for (String s: headers){
			vector.add(s);
		}
		return vector.elements();
	}

	@Override
	public Enumeration getHeaders(String arg0) {
		// TODO Auto-generated method stub
		String headers = this.request.headers.get(arg0);
		String[] headerVals = null;
		if (headers.contains(";;")){
			headerVals = headers.split(";;");
		} else if (headers.contains(";")) {
			headerVals = headers.split(";");
		} else {
			headerVals = headers.split(",");
		}
		
		Vector<String> vector = new Vector<String>();
		for (String s: headerVals){
			vector.add(s.trim());
		}
		
		return vector.elements();
	}

	@Override
	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		String value = this.request.headers.get(arg0);
		int res = Integer.parseInt(value);
		return res;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return this.method;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		String path = this.request.getPath();
		if (path.indexOf("?") != -1){
			String pathWithoutQueryString = path.substring(0, path.lastIndexOf("?"));
			String[] pathArray = pathWithoutQueryString.split("/");
			String res = "/";
			res += pathArray[2];
			return res;
		}
		
		return "";
	}

	@Override
	/*
	 * Not required to implement
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		String path = this.request.getPath();
		if (path.indexOf("?") != -1){
			int index = path.lastIndexOf("?");
			return path.substring(index+1);
		}
		
		return "";
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		String uri = this.request.getPath();
		String[] pathArray = uri.split("?");
		return pathArray[0];
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return new StringBuffer(this.request.getPath());
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		if (session != null) {
			return session.getId();
		}
		for (Cookie c: HttpRequest.getCookieList()){
			if (c.getName().equals("JSESSIONID")){
				return c.getValue();
			}
		}
		return null;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		// TODO Auto-generated method stub
		return this.request.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		if (arg0){
			if (!this.hasSession()){
				for (Cookie c: this.getCookies()){
					if (c.getName().equalsIgnoreCase("JSESSIONID")){
						if (SessionPool.sessions.containsKey(c.getValue())){
							this.session = SessionPool.sessions.get(c.getValue());
							return this.session;
						}
					}
				}
				MyHttpSession newSession = new MyHttpSession();
				this.session = newSession;
				HttpRequest.addCookie(newSession.getSessionCookie());
				SessionPool.addSession(newSession);
			}
		} else {
			if (!this.hasSession()){
				return null;
			}
		}
		return this.session;
	}

	@Override
	/*
	 * Not required to implement
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		for (Cookie c: HttpRequest.getCookieList()){
			if (c.getName().equals("JSESSIONID")){
				return true;
			}
		}
		return false;
	}

	@Override
	/*
	 * always return false (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setParameter(String key, String value) {
		this.params.setProperty(key, value);
	}
	
	public void clearParameters() {
		this.params.clear();
	}
	
	public boolean hasSession() {
		return ((this.session != null) && this.session.isValid());
	}
	
	/*
	 * Convert string to date
	 */
	private Date convertDate(String line) {
		SimpleDateFormat df = null;
		Date date = null;
		if (!line.contains(",")) {
			df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		} else if (line.contains("-")) {
			df = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz");
		} else {
			df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		}

		try {
			date = df.parse(line);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			HttpServer.logs.write(e.toString());
		}
		
		return date;
	}

}
