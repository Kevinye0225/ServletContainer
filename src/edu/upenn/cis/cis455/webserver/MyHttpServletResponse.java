package edu.upenn.cis.cis455.webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MyHttpServletResponse implements HttpServletResponse {

	public HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
	private HashMap<Integer, String> status = new HashMap<Integer, String>();
	int bufferSize = 5000;
	private String characterEncoding = "ISO-8859-1";
	private Locale locale;
	private int statusCode = 200;
	private boolean isCommitted;
	private MyPrintWriter buffer;
	DataOutputStream output;

	public MyHttpServletResponse(DataOutputStream output) {
		this.output = output;
		ArrayList<String> list = new ArrayList<String>();
		list.add("text/html");
		this.headers.put("content-type", list);
		status.put(SC_NOT_FOUND, "Not Found");
		status.put(SC_OK, "OK");
		status.put(SC_BAD_REQUEST, "Bad Request");
		status.put(SC_NOT_MODIFIED, "Not Modified");
		status.put(SC_PRECONDITION_FAILED, "Precondition Failed");
		status.put(SC_TEMPORARY_REDIRECT, "Redirect");
		status.put(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		status.put(SC_FORBIDDEN, "Forbidden");

		this.buffer = new MyPrintWriter();
	}

	public MyHttpServletResponse() {

	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		buffer.flush();
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return this.bufferSize;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return this.characterEncoding;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return headers.get("content-type").get(0);
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return this.locale;
	}

	/*
	 * Not required
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return this.buffer;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return this.isCommitted;
	}

	@Override
	/*
	 * reset everything to its original state(non-Javadoc)
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		if (this.isCommitted) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		buffer.clear();
		headers = new HashMap<String, ArrayList<String>>();
		statusCode = 200;
	}

	@Override
	/*
	 * empty the content in the buffer(non-Javadoc)
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer() {
		// TODO Auto-generated method stub
		if (this.isCommitted) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		buffer.clear();
	}

	@Override
	/*
	 * set the maximum characters that get flush(non-Javadoc)
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub
		if (this.buffer.isWritten()) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.bufferSize = arg0;
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub
		this.characterEncoding = arg0;
	}

	@Override
	/*
	 * set content length and store it in the header map(non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add(Integer.toString(arg0));
		this.headers.put("content-length", list);
	}

	@Override
	/*
	 * set content type and store it in the header map(non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add(arg0);
		this.headers.put("content-type", list);
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		this.locale = arg0;
	}

	@Override
	/*
	 * add cookie to list (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.
	 * Cookie)
	 */
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		for (Cookie c: HttpRequest.getCookieList()){
			if (c.getName().equals(arg0.getName()) && 
					c.getValue().equals(arg0.getValue())){
				return;
			}
		}
		
		HttpRequest.addCookie(arg0);
		
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		String key = arg0.toLowerCase();
		if (headers.containsKey(key)) {
			headers.get(key).add(this.generateDate(arg1));
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(this.generateDate(arg1));
			headers.put(key, list);
		}
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		String key = arg0.toLowerCase();
		if (!headers.containsKey(key)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(arg1);
			headers.put(key, list);
		} else {
			headers.get(key).add(arg1);
		}
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		String key = arg0.toLowerCase();
		if (!headers.containsKey(key)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(Integer.toString(arg1));
			headers.put(key, list);
		} else {
			headers.get(key).add(Integer.toString(arg1));
		}
	}

	@Override
	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return headers.containsKey(arg0.toLowerCase());
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	/*
	 * Deprecated
	 */
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	/*
	 * Deprecated
	 */
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub
		if (this.isCommitted) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.statusCode = arg0;
		this.flushBuffer();

	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub
		if (this.isCommitted) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.statusCode = arg0;
		this.buffer.write(arg1);

		this.flushBuffer();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub
		if (this.isCommitted) {
			HttpServer.logs.write("IllegalStateException");
			throw new IllegalStateException();
		}
		this.statusCode = 302;
		String redirect = null;
		if (arg0.startsWith("http://")) {
			redirect = arg0;
		} else {
			if (arg0.startsWith("/")) {
				redirect = "http://localhost:" + HttpServer.port + arg0;
			} else {
				redirect = "http://localhost:" + HttpServer.port + "/" + arg0;
			}
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add(redirect);
		this.headers.put("location", list);
		this.flushBuffer();

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add(this.generateDate(arg1));
		headers.put(arg0.toLowerCase(), list);
	}

	/*
	 * generate a date variable from a long value
	 */
	public String generateDate(long date) {
		DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date newDate = new Date(date);
		String res = dateFormat.format(newDate);

		return res;
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add(arg1);
		headers.put(arg0.toLowerCase(), list);
	}

	@Override
	/*
	 * Convert int to string and store in the header map(non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		String str = Integer.toString(arg1);
		ArrayList<String> list = new ArrayList<String>();
		list.add(str);
		headers.put(arg0.toLowerCase(), list);
	}

	@Override
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub
		this.statusCode = arg0;
	}

	@Override
	/*
	 * Deprecated
	 */
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}
	

	/*
	 * The servlet would use this writer class to write output
	 */
	class MyPrintWriter extends PrintWriter {
		private StringBuffer sb;
		private ArrayList<Byte> binary;

		public MyPrintWriter() {
			super(output);
			sb = new StringBuffer(bufferSize);
			binary = new ArrayList<Byte>();
		}

		public boolean isWritten() {
			return this.sb.length() != 0;
		}
		
		public StringBuffer getStringBuffer(){
			return this.sb;
		}

		public void setBufferSize(int size) {
			sb = new StringBuffer(size);
		}

		public void clear() {
			sb = new StringBuffer(bufferSize);
		}

		@Override
		public void write(String s) {
			if (sb.length() + s.length() >= sb.capacity()) {
				flush();
			}
			sb.append(s);
		}

		@Override
		public void write(char[] buf) {
			this.write(String.valueOf(buf));
		}

		@Override
		public void write(char[] buf, int offset, int len) {
			this.write(String.valueOf(buf).substring(offset, offset + len));
		}

		@Override
		public void write(String s, int offset, int len) {
			this.write(s.substring(offset, offset + len));
		}

		@Override
		public void write(int c) {
			this.write(String.valueOf(c));
		}

		@Override
		public void print(char c) {
			this.write(Character.toString(c));
		}

		@Override
		public void print(boolean b) {
			if (b) {
				this.write("true");
			} else {
				this.write("false");
			}
		}

		@Override
		public void print(char[] s) {
			this.write(s);
		}

		@Override
		public void print(float f) {
			this.write(Float.toString(f));
		}

		@Override
		public void print(int i) {
			this.write(String.valueOf(i));
		}

		@Override
		public void print(long l) {
			this.write(String.valueOf(l));
		}

		@Override
		public void print(String s) {
			this.write(s);
		}

		public void print(Byte b) {
			this.binary.add(b);
		}

		@Override
		public void print(Object o) {
			this.write(o.toString());
		}

		@Override
		public void println() {
			this.write("\n");
		}

		@Override
		public void println(char x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(char[] x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(double x) {
			this.print(String.valueOf(x));
			sb.append("\n");
		}

		@Override
		public void println(float x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(int x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(long x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(Object x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		public void println(String x) {
			this.print(x);
			sb.append("\n");
		}

		@Override
		/*
		 * writes the response
		 */
		public void flush() {
			if (isCommitted) {
				HttpServer.logs.write("IllegalStateException");
				throw new IllegalStateException();
			}
			try {
				if (!headers.containsKey("content-length") ||
						headers.get("content-length").isEmpty()){
					int contentLength = sb.length();
					ArrayList<String> list = new ArrayList<String>();
					list.add(Integer.toString(contentLength));
					headers.put("content-length", list);
				}
				output.writeBytes("HTTP/1.1 " + Integer.toString(statusCode) + " " + status.get(statusCode) + "\r\n");
				for (String key : headers.keySet()) {
					ArrayList<String> headerVals = headers.get(key);
					output.writeBytes(key + ": ");
					output.writeBytes(headerVals.get(0));
					int i = 1;
					while (i < headerVals.size()) {
						output.writeBytes(",");
						output.writeBytes(headerVals.get(i));
					}
					output.writeBytes("\r\n");
				}
				for (Cookie c : HttpRequest.getCookieList()) {
					String str = "Set-Cookie: " + c.getName() + "=" + c.getValue();
					if (c.getDomain() != null) {
						str += "; Domain=" + c.getDomain();
					}
					if (c.getPath() != null) {
						str += "; Path=" + c.getPath();
					}
					if (c.getMaxAge() != -1) {
						str += "; Expires=" + getExpiredDate(c.getMaxAge());
					}
					output.writeBytes(str + "\r\n");
				}
				output.writeBytes("\r\n");
				output.writeBytes(sb.toString());
				if (binary.size() > 0) {
					byte[] byteArr = new byte[binary.size()];
					for (int i = 0; i < binary.size(); i++) {
						byteArr[i] = binary.get(i);
					}
					output.write(byteArr, 0, byteArr.length);
				}
				output.writeBytes("\r\n");
				output.flush();

				isCommitted = true;
				clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				HttpServer.logs.write(e.toString());
			}
		}
		
		private String getExpiredDate(int val){
			Date date = new Date();
			long maxTime = (long) val*1000;
			long res = date.getTime() + maxTime;
			
			return generateDate(res);
		}
	}

}
