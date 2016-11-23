package edu.upenn.cis.cis455.webserver;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import junit.framework.TestCase;

public class MyHttpServletRequestTest extends TestCase {

	private MyHttpServletRequest servletRequest;
	private HttpRequest request = new HttpRequest();
	
	public void testAttribute() {
		MyHttpSession session = null;
		servletRequest = new MyHttpServletRequest(request, session);
		String arg0 = "test";
		String arg1 = "value";
		servletRequest.setAttribute(arg0, arg1);
		assertEquals(servletRequest.getAttribute(arg0), arg1);
		
		Enumeration vec = servletRequest.getAttributeNames();
		
		assertEquals(vec.nextElement(), arg0);
		servletRequest.removeAttribute(arg0);
		
		assertEquals(servletRequest.getAttribute(arg0), null);
	}
	
	public void testGetter() throws UnsupportedEncodingException{
		MyHttpSession session = null;
		servletRequest = new MyHttpServletRequest(request, session);
		assertEquals(servletRequest.getCharacterEncoding(), "ISO-8859-1");
		servletRequest.setCharacterEncoding("ISO-111");
		assertEquals(servletRequest.getCharacterEncoding(), "ISO-111");
		
		assertEquals(servletRequest.getContentLength(), 0);
		assertEquals(servletRequest.getMethod(), null);
		assertEquals(servletRequest.getContentType(), "text/html");
		
		request.headers.put("host", "head");
		assertEquals(servletRequest.getLocalName(), "head");
		
		Locale locale = new Locale("EN");
		servletRequest.setLocale(locale);
		assertEquals(servletRequest.getLocale(), locale);
		
		String key = "key";
		String value = "value";
		servletRequest.setParameter(key, value);
		assertEquals(servletRequest.getParameter(key), value);
		
		Enumeration vec = servletRequest.getParameterNames();
		assertEquals(vec.nextElement(), key);
		
		String expectedProtocol = "HTTP/1.1";
		assertEquals(servletRequest.getProtocol(), expectedProtocol);
		
		assertEquals(servletRequest.getScheme(), "http");
		
		HttpServer.port = 8080;
		assertEquals(servletRequest.getServerPort(), 8080);
		
		assertEquals(servletRequest.getAuthType(), "BASIC");
	}
	
	public void testHeaders() throws ParseException{
		MyHttpSession session = null;
		servletRequest = new MyHttpServletRequest(request, session);
		String dateHeader = "date";
		String date = "Wed DEC 01 12:00:00 2013";
		request.headers.put(dateHeader, date);
		
		SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		Date dateVal = df.parse(date);
		assertEquals(dateVal.getTime(), servletRequest.getDateHeader(dateHeader));
		assertEquals(servletRequest.getHeader(dateHeader), date);
		
		Enumeration vec = servletRequest.getHeaderNames();
		assertEquals(vec.nextElement(), dateHeader);
		
		Enumeration headersVec = servletRequest.getHeaders(dateHeader);
		assertEquals(headersVec.nextElement(), date);
		
		String intHeader = "int";
		String val = "5";
		request.headers.put(intHeader, val);
		assertEquals(servletRequest.getIntHeader(intHeader), 5);
	}

}
