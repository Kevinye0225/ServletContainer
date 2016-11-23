package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

/**
 * This class is to parse request from browser
 * 
 * @author kevin
 *
 */
public class HttpRequest {

	static Logger log = Logger.getLogger(HttpRequest.class);

	public BufferedReader bf;
	private String method = null;
	private String path = null;
	private String httpVersion = "1.1";
	private String extension = null;
	private Date date;
	private String root;
	private boolean isValid = true;
	private boolean isHome = false;
	private boolean isStatic = true;
	private boolean isShutDown = false;
	private boolean controlPanel = false;
	private boolean isValidPath = true;
	private boolean isAbsolute = false;
	private boolean isExpectHeader = false;
	private boolean isLastModified = false;
	private boolean isLastUnmodified = false;
	public boolean isServerError = false;
	private static ArrayList<Cookie> cookieList;
	private String servletPath;
	public Socket socket;
	public HashMap<String, String> headers = new HashMap<String, String>();

	public HttpRequest(Socket socket, InputStreamReader in, String root) {
		this.bf = new BufferedReader(in);
		this.root = root;
		this.socket = socket;
		cookieList = new ArrayList<Cookie>();
	}

	public HttpRequest() {
	}

	/*
	 * go through the request and store the info
	 */
	public void parseRequest() {
		String line = null;
		try {
			line = bf.readLine();
			if (line != null) {
				parseMethod(line);
			} else {
				this.isValid = false;
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.info(e);
			HttpServer.logs.write(e.toString());
		}

		while (true) {
			try {
				String[] headerLine = null;
				line = bf.readLine();
				if (line == null || line.length() == 0 || line.equals("\n")) {
					if (this.method.equals("POST")) {
						line = bf.readLine();
						// put post body message to headers
						headers.put("body", line);
					}
					break;
				}
				headerLine = line.split(":");
				String title = headerLine[0].toLowerCase();
				String value = headerLine[1];
				for (int i = 2; i < headerLine.length; i++) {
					value = value + ":" + headerLine[i];
				}
				if (headers.containsKey(title)) {
					String str = headers.get(title);
					if (title.equals("cookie")) {
						// special parsing for cookie since it has
						// different format
						str += ";;" + value;
					} else {
						str += "," + value;
					}

					headers.put(title, str);
				} else {
					headers.put(title, value);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// log.info(e);
				HttpServer.logs.write(e.toString());
			}
		}
		String modified = "if-modified-since: ";
		String unmodified = "if-unmodified-since: ";

		// set value based on headers
		if (!headers.containsKey("host") && this.httpVersion.equals("1.1")) {
			this.isValid = false;
		}
		if (headers.containsKey("expect")) {
			this.isExpectHeader = true;
		}
		if (headers.containsKey(modified)) {
			this.isLastModified = true;
			this.convertDate(headers.get(modified));
		}
		if (headers.containsKey(unmodified)) {
			this.isLastUnmodified = true;
			this.convertDate(headers.get(unmodified));
		}
		if (headers.containsKey("cookie")) {
			String cookies = headers.get("cookie");
			this.getCookies(cookies);
		}

		if (this.isValid && this.isValidPath && this.servletPath == null) {
			if (this.checkIfServlet(this.path)) {
				this.isStatic = false;
			}
		}

	}

	/*
	 * check what request it is making and whether it is valid
	 */
	public void parseMethod(String line) {

		if (line == null || line.isEmpty()) {
			this.isValid = false;
			return;
		}

		String[] elements = line.split(" ");
		if (elements.length < 3) {
			this.isValid = false;
			return;
		}
		this.method = elements[0];
		try {
			parsePath(elements[1]);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.info(e);
			HttpServer.logs.write(e.toString());
		}

		if (elements[2].length() > 5) {
			this.httpVersion = elements[2].substring(5);
		}

	}

	/*
	 * check if the requested path is valid
	 */
	private void parsePath(String element) throws MalformedURLException {
		this.path = element;
		if (this.path == null || this.path.length() == 0) {
			this.isValid = false;
			return;
		}
		
		if (this.path.equals("/home")){
			this.isHome = true;
		}

		if (this.path.equals("/shutdown")) {
			this.isShutDown = true;
		}

		if (this.path.equals("/control")) {
			this.controlPanel = true;
		}

		// different parse scheme for absolute path
		if (this.path.startsWith("http://")) {
			this.isAbsolute = true;
			int pathIndex = this.path.indexOf("/", 9);
			if (pathIndex == -1) {
				this.isValidPath = false;
			} else if (checkIfServlet(this.path)) {
				this.isStatic = false;
			} else {
				String realPath = this.path.substring(pathIndex);

				String[] pathArray = realPath.split("/");
				int i = 0;
				while (i < pathArray.length && !pathArray[i].equals(root)) {
					i++;
				}
				if (i >= pathArray.length) {
					this.path = realPath;
				} else {
					i++;
					String actualPath = "";
					while (i < pathArray.length) {
						actualPath += "/";
						actualPath += pathArray[i];
						i++;
					}
					this.path = actualPath;
				}

			}
		}

		if (this.path.indexOf('.') != -1) {
			this.extension = this.path.substring(this.path.indexOf('.') + 1);
		}

	}

	/*
	 * check if request matches any servlet url
	 */
	private boolean checkIfServlet(String str) {
		String exactMatch = null;
		String partMatch = null;
		String localPath = str;
		if (str.contains("?")) {
			int index = str.indexOf("?");
			localPath = str.substring(0, index);
		}
		for (String url : HttpServer.urlMapping.keySet()) {
			String[] urlElement = url.split("/");
			String[] pathElement = localPath.split("/");
			boolean precise = true;
			int i = 0;
			while (i < urlElement.length) {
				if (i >= pathElement.length)
					break;
				if (urlElement[i].equals("*")) {
					precise = false;
					i++;
				} else if (urlElement[i].equals(pathElement[i])) {
					i++;
				} else {
					break;
				}
			}
			if (i == urlElement.length || (i == urlElement.length - 1 && urlElement[i].equals("*"))) {
				if (!precise) {
					partMatch = url;
				} else {
					exactMatch = url;
					break;
				}
			}

		}

		if (exactMatch != null) {
			this.servletPath = exactMatch;
			return true;
		} else {
			if (partMatch != null) {
				this.servletPath = partMatch;
				return true;
			} else {
				return false;
			}
		}
	}

	/*
	 * add cookies to cookielist
	 */
	private void getCookies(String str) {
		String[] cookies = str.split(";");
		for (String cookieInfo : cookies) {
			boolean found = false;
			String[] nameValuePair = cookieInfo.split("=");
			String name = nameValuePair[0].trim();
			String value = nameValuePair[1].trim();
			for (Cookie c : cookieList) {
				if (c.getName().equals(name) && c.getValue().equals(value)) {
					found = true;
					break;
				}
			}
			if (!found) {
				Cookie cookie = new Cookie(name, value);
				cookieList.add(cookie);
			}
		}
	}

	/*
	 * convert a string into date for if-modified-since request
	 */
	private void convertDate(String line) {
		SimpleDateFormat df = null;
		if (!line.contains(",")) {
			df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		} else if (line.contains("-")) {
			df = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz");
		} else {
			df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		}

		try {
			this.date = df.parse(line);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.info(e);
			HttpServer.logs.write(e.toString());
		}
	}

	public boolean isValid() {
		return isValid;
	}

	public boolean isShutDown() {
		return isShutDown;
	}

	public boolean isControlPanel() {
		return controlPanel;
	}

	public String getMethod() {
		return this.method;
	}

	public String getPath() {
		return this.path;
	}

	public String getVersion() {
		return this.httpVersion;
	}

	public String getExtension() {
		return this.extension;
	}

	public boolean isAbsolute() {
		return this.isAbsolute;
	}

	public boolean isExpect() {
		return this.isExpectHeader;
	}

	public boolean isValidPath() {
		return this.isValidPath;
	}

	public Date getDate() {
		return this.date;
	}

	public boolean isModified() {
		return this.isLastModified;
	}

	public boolean isUnmodified() {
		return this.isLastUnmodified;
	}

	public boolean isStatic() {
		return this.isStatic;
	}

	public String getServletPath() {
		return this.servletPath;
	}

	public static ArrayList<Cookie> getCookieList() {
		return cookieList;
	}

	public static void addCookie(Cookie c) {
		cookieList.add(c);
	}
	
	public boolean isHome() {
		return this.isHome;
	}
}
