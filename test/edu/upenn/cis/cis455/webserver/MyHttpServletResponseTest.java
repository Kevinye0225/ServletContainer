package edu.upenn.cis.cis455.webserver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

public class MyHttpServletResponseTest extends TestCase {

	private MyHttpServletResponse response;

	public void testGetter() {
		response = new MyHttpServletResponse();
		assertEquals(response.getBufferSize(), 5000);
		assertEquals(response.getCharacterEncoding(), "ISO-8859-1");
		assertFalse(response.isCommitted());
		response.setContentType("text/html");
		assertEquals(response.getContentType(), "text/html");
		
	}
	
	public void testHeaders() {
		response = new MyHttpServletResponse();
		String dateHeader = "date";
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateString = dateFormat.format(date);
		long dateVal = date.getTime();
		response.addDateHeader(dateHeader, dateVal);
		assertEquals(dateString, response.headers.get(dateHeader).get(0));
		
		String header = "test";
		String headerVal = "val";
		response.setHeader(header, headerVal);
		assertEquals(response.headers.get(header).get(0), headerVal);
		
		int intHeader = 5;
		response.setIntHeader(header, intHeader);
		
		assertEquals(response.headers.get(header).get(0), Integer.toString(intHeader));
	}
	
	public void testWriter() throws IOException {

	}

}
