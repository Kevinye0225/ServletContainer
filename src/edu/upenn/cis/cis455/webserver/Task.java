package edu.upenn.cis.cis455.webserver;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * This class is created to coordinate request and response
 * 
 * @author kevin
 *
 */
public class Task implements Runnable {

	static Logger log = Logger.getLogger(Task.class);

	private String root;
	private String url;
	private ThreadPool pool;
	private HttpRequest request;
	private boolean isShutDown = false;
	private Socket socket;
	private InputStreamReader in = null;

	public Task(Socket socket, String root, ThreadPool pool) {
		this.socket = socket;
		this.root = root;
		this.pool = pool;
	}

	/*
	 * retrieve the url before running it to store 
	 */
	public void retrieveUrl() {
		try {
			in = new InputStreamReader(socket.getInputStream());
			request = new HttpRequest(socket, in, root);

			request.parseRequest();
			this.setUrl(request.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info(e);
		}
	}

	/*
	 * Invoke servlet or render static content here(non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedOutputStream out = null;
		DataOutputStream dataOut = null;
		try {
			if (!request.isStatic()) {
				MyHttpSession session = null;
				
				MyHttpServletRequest servletRequest = new MyHttpServletRequest(this.request, session);
				dataOut = new DataOutputStream(socket.getOutputStream());
				MyHttpServletResponse servletResponse = new MyHttpServletResponse(dataOut);
				
				// servletRequest.getSession();
				String servletName = HttpServer.urlMapping.get(request.getServletPath());
				HttpServlet servlet = HttpServer.servlets.get(servletName);
				
				String messages = null;
				if (request.getMethod().equals("POST")){
					messages = request.headers.get("body");
				} else if (servletRequest.getQueryString() != null){
					messages = servletRequest.getQueryString();
				}
				
				if (messages != null){
					String[] params = messages.split("&");
					for (String inputs: params){
						if (!inputs.contains("=")) continue;
						String[] keyValue = inputs.split("=");
						servletRequest.setParameter(keyValue[0], keyValue[1]);
					}
				}
				
				servlet.service(servletRequest, servletResponse);
				if (!servletResponse.isCommitted()){
					servletResponse.flushBuffer();
				}
				
				dataOut.close();
				in.close();
				socket.close();
				
			} else {
				out = new BufferedOutputStream(socket.getOutputStream());
				HttpResponse response = new HttpResponse(request, out, root, pool);

				response.createResponse();
			}
			
			if (request.isShutDown()) {
				this.isShutDown = true;
				LinkedList<WorkerThread> threads = this.pool.getThreads();
				for (WorkerThread w : threads) {
					w.shutDown();
				}
				
				for (String key: HttpServer.servlets.keySet()){
					HttpServer.servlets.get(key).destroy();
				}
				HttpServer.socket.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info(e);
			HttpServer.logs.write(e.toString());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			log.info(e);
		} finally {
			try {
				in.close();
				if (out != null) {
					out.close();
				}
				if (dataOut != null){
					dataOut.close();
				}
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.info(e);
				HttpServer.logs.write(e.toString());
			}
		}

	}

	public synchronized void setUrl(String url) {
		notifyAll();
		this.url = url;
	}

	public synchronized String getUrl() {
		return this.url;
	}

	public synchronized boolean isShutDown() {
		return this.isShutDown;
	}
}
