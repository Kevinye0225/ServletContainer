package edu.upenn.cis.cis455.webserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class HttpServer {
	

	int numOfThreads = 0;

	/**
	 * Logger for this particular class
	 */
	static Logger log = Logger.getLogger(HttpServer.class);
	static ServerSocket socket = null;
	static MyServletContext context;
	static ErrorLog logs;
	public static int maxInterval;
	public static int port;
	static HashMap<String, String> urlMapping = new HashMap<String, String>();
    static HashMap<String, HttpServlet> servlets;
	
	public static void main(String args[]) throws InterruptedException {
		BasicConfigurator.configure();
		log.info("Start of Http Server");

		if (args.length != 3) {
//			usage();
			System.exit(-1);
		}
		String rootDir = "";
		port = Integer.parseInt(args[0]);
		rootDir = args[1];
		int threadSize = 100;
		int queueSize = 30;
		TaskBlockingQueue queue = new TaskBlockingQueue(queueSize);
		ThreadPool pool = new ThreadPool(queue, threadSize);
		
		logs = ErrorLog.getInstance(rootDir + "/errorLogs.txt");
		
		Handler h = null;
		try {
			h = parseWebdotxml(args[2]);
			urlMapping = h.m_urlPattern;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logs.write(e.toString());
		}
		context = createContext(h);
		
		try {
			servlets = createServlets(h, context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logs.write(e.toString());
		}
		
		SessionPool sessions = SessionPool.getInstance();
		System.out.println(maxInterval);
		
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info(e);
			logs.write(e.toString());
		}
		
		while (!socket.isClosed()) {
			Socket client;
			try {
				if (socket.isClosed()) break;
				client = socket.accept();
				Task task = new Task(client, rootDir, pool);
				pool.addTask(task);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.info(e);
				logs.write(e.toString());
			}
			
		}

		log.info("Http Server terminating");
		System.exit(-1);
	}
	
	private static Handler parseWebdotxml(String webdotxml) throws Exception {
		Handler h = new Handler();
		File file = new File(webdotxml);
		if (file.exists() == false) {
			System.err.println("error: cannot find " + file.getPath());
			System.exit(-1);
		}
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, h);
		
		return h;
	}
	
	private static MyServletContext createContext(Handler h) {
		MyServletContext fc = new MyServletContext();
		for (String param : h.m_contextParams.keySet()) {
			fc.setInitParam(param, h.m_contextParams.get(param));
		}
		return fc;
	}
	
	private static HashMap<String,HttpServlet> createServlets(Handler h, MyServletContext fc) throws Exception {
		HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
		for (String servletName : h.m_servlets.keySet()) {
			MyServletConfig config = new MyServletConfig(servletName, fc);
			String className = h.m_servlets.get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String,String> servletParams = h.m_servletParams.get(servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(servletName, servlet);
		}
		return servlets;
	}
	
	static class Handler extends DefaultHandler {
		private int m_state = 0;
		public String m_servletName;
		public String m_paramName;
		public String m_servletMappingName;
		HashMap<String, String> m_urlPattern = new HashMap<String, String>();
		HashMap<String,String> m_servlets = new HashMap<String,String>();
		HashMap<String,String> m_contextParams = new HashMap<String,String>();
		HashMap<String,HashMap<String,String>> m_servletParams = new HashMap<String,HashMap<String,String>>();
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.compareTo("servlet") == 0) {
				m_state = 1;
			} else if (qName.compareTo("servlet-mapping") == 0) {
				m_state = 2;
			} else if (qName.compareTo("context-param") == 0) {
				m_state = 3;
			} else if (qName.compareTo("init-param") == 0) {
				m_state = 4;
			} else if (qName.compareTo("param-name") == 0) {
				m_state = (m_state == 3) ? 10 : 20;
			} else if (qName.compareTo("param-value") == 0) {
				m_state = (m_state == 10) ? 11 : 21;
			} else if (qName.compareTo("servlet-name") == 0) {
				m_state = (m_state == 1) ? 100 : 200;
			} else if (qName.compareTo("servlet-class") == 0){
				m_state = 30;
			} else if (qName.compareTo("url-pattern") == 0){
				m_state = 40;
			} else if (qName.compareTo("session-config") == 0){
				m_state = 500;
			} else if (qName.compareTo("session-timeout") == 0){
				m_state = 1000;
			}
		}
		
		public void characters(char[] ch, int start, int length) {
			String value = new String(ch, start, length);
			if (m_state == 100) {
				m_servletName = value;
				m_state = 0;
			} else if (m_state == 30) {
				m_servlets.put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 200){
				m_servletMappingName = value;
				m_state = 0;
			} else if (m_state == 40) {
				if (this.m_servletMappingName == null){
					System.err.println("urlPattern '" + value + "' without mapping");
					System.exit(-1);
				}
				this.m_urlPattern.put(value, m_servletMappingName);
				m_state = 0;
			} else if (m_state == 10 || m_state == 20) {
				if (m_paramName == null){
					m_paramName = value;
				}
			} else if (m_state == 11) {
				if (m_paramName == null) {
					System.err.println("Context parameter value '" + value + "' without name");
					System.exit(-1);
				}
				m_contextParams.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 21) {
				if (m_paramName == null) {
					System.err.println("Servlet parameter value '" + value + "' without name");
					System.exit(-1);
				}
				HashMap<String,String> p = m_servletParams.get(m_servletName);
				if (p == null) {
					p = new HashMap<String,String>();
					m_servletParams.put(m_servletName, p);
				}
				p.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 1000){
				maxInterval = Integer.parseInt(value);
				m_state = 0;
			}
		}
		
	}

}
