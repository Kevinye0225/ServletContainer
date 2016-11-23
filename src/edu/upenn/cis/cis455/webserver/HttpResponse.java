package edu.upenn.cis.cis455.webserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * This class is to generate response based on the request
 * 
 * @author kevin
 *
 */
public class HttpResponse {

	/**
	 * Logger for this particular class
	 */
	static Logger log = Logger.getLogger(HttpResponse.class);

	HttpRequest request;
	BufferedOutputStream out;
	String root;
	byte[] images = null;
	StringBuilder response;
	StringBuilder header;
	StringBuilder contentType;
	StringBuilder contentLength;
	StringBuilder content;
	String statusCode = "200 OK";
	String currentDate = "Date: ";
	int length = 0;
	ThreadPool pool;

	public HttpResponse(HttpRequest request, BufferedOutputStream out, String root, ThreadPool pool) {
		this.request = request;
		this.out = out;
		this.root = root;
		this.header = new StringBuilder("HTTP/");
		this.contentType = new StringBuilder("Content-Type: ");
		this.contentLength = new StringBuilder("Content-Length: ");
		this.content = new StringBuilder("");
		this.response = new StringBuilder("");
		this.pool = pool;
	}

	/*
	 * create http response
	 */
	public void createResponse() {
		String method = request.getMethod();

		if (request.isServerError){
			this.createServerError();
		} else if (!request.isValid() || 
				((request.isModified() || request.isUnmodified()) 
						&& request.getVersion().equals("1.0"))) {
			createInvalidReponse();
		} else {
			if (request.isControlPanel()) {
				showControlPanel();
			} else if(request.isHome()) {
				showHomePage();
			} else if ((method.equals("GET") && !request.isShutDown()) || method.equals("HEAD")) {
				renderPage();
				if (method.equals("HEAD")) {
					this.content = new StringBuilder("");
					this.images = null;
				}
			} else {
				this.statusCode = "501 Not Implemented";
			}
		}

		// create header based on the request
		this.header.append(request.getVersion() + " ");

		if (request.isExpect() && request.getVersion().equals("1.1")) {
			String continueHeader = "HTTP/1.1 100 Continue \n";
			continueHeader += "\r\n";
			byte[] outputArr = continueHeader.getBytes();
			try {
				out.write(outputArr);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.info(e);
				HttpServer.logs.write(e.toString());
			}
			
		}
		this.header.append(statusCode);
		this.response.append(header);
		this.response.append("\r\n");

		if (!this.statusCode.contains("500") && !this.request.isShutDown() && 
				(!this.statusCode.contains("412") && !this.statusCode.contains("501"))) {
			// get current date and time
			DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = new Date();
			this.currentDate += dateFormat.format(date);
			this.response.append(this.currentDate);
			this.response.append("\r\n");
			
			if (!this.statusCode.contains("304")) {
				this.response.append(this.contentType);
				this.response.append("\r\n");
				this.response.append(this.contentLength);
				this.response.append("\r\n");
				this.response.append("Connection: close");
				this.response.append("\r\n");
				if (images == null)
					this.response.append("\n");

				this.response.append(this.content);
				this.response.append("\r\n");
			}
		}

		byte[] temp = response.toString().getBytes();
		try {
			out.write(temp, 0, temp.length);
			if (images != null) {
				out.write(images, 0, length);
				out.write((byte) '\n');
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info(e);
			HttpServer.logs.write(e.toString());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				HttpServer.logs.write(e.toString());
			}
		}

	}

	/*
	 * generate content based on get/head request
	 */
	private void renderPage() {
		String path = request.getPath();
		try {
			File file = new File(root, path);
			if (!file.exists() || !request.isValidPath()) {
				// return correspondent error when resource not found
				this.statusCode = "404 NOT FOUND";
				this.contentType.append("text/html");
				this.content.append("This file you request is not found");
				this.contentLength.append(content.length());
			} else if (file.isDirectory()) {
				File[] subfiles = file.listFiles();
				this.contentType.append("text/html");
				this.content.append("<html>");
				this.content.append("<body>");
				this.content.append("<p>Here is list of files in this directory:</p>");
				for (File f : subfiles) {
					this.getDirectoryList(f);
				}
				this.content.append("</body>");
				this.content.append("</html>");
				this.content.append("\n");
				this.contentLength.append(this.content.length());

			} else {
				Date fileDate = new Date(file.lastModified());

				// check for if-modified-since
				if (request.isModified() && fileDate.before(request.getDate())) {
					this.statusCode = "304 Not Modified";
					return;
				} else if (request.isUnmodified() && fileDate.after(request.getDate())) {
					this.statusCode = "412 Precondition Failed";
					return;
				}

				String extension = request.getExtension();
				if (extension.equals("html") || extension.equals("txt")) {
					BufferedReader read = new BufferedReader(new FileReader(file));
					String line = read.readLine();
					if (extension.equals("html")){
						this.contentType.append("text/html");
					} else {
						this.contentType.append("text/plain");
					}
					
					while (line != null) {
						this.length += line.length();
						content.append(line);
						line = read.readLine();
					}
					content.append("\r\n");
					read.close();
				} else if (extension.equals("jpg") || extension.equals("gif") || extension.equals("png")) {
					this.contentType.append("image/" + extension);
					this.images = Files.readAllBytes(file.toPath());
					this.length = this.images.length;
				}
				this.contentLength.append(String.valueOf(length));
			}

		} catch (IOException e) {
			log.info(e);
			HttpServer.logs.write(e.toString());
			// generate a 500 response if file is not successfully open
			createServerError();
		}

	}
	
	private void showHomePage() {
		this.contentType.append("text/html");
		this.content.append("<html>");
		this.content.append("<body>");
		this.content.append("<p>Here are the available servlets: </p>");
		this.content.append("<p>For Demo Servlet, you need to either make a post request or pass in a query string</p>");
		this.content.append("<p>SendError test send error method</p>");
		this.content.append("<p>SendRedirect test send redirect method</p>");
		for (String url: HttpServer.urlMapping.keySet()){
			String servletName = HttpServer.urlMapping.get(url);
			this.content.append("<p>" + servletName + ": ");
			this.content.append("<a href=\"" + url + "\">" + url + "</a></p>");
		}
		this.content.append("</body>");
		this.content.append("</html>");
		this.contentLength.append(this.content.length());
	}
	
	public void createServerError(){
		this.statusCode = "500 Internal Server Error";
		this.contentType.append("text/html");
		this.content.append("Server error");
		this.contentLength.append(this.content.length());
	}

	/*
	 * generating response for bad request
	 */
	private void createInvalidReponse() {
		this.statusCode = "400 Bad Request";
		this.contentType.append("text/html");
		this.content.append("<html>");
		this.content.append("<body>");
		this.content.append("<h2>No Host: header received</h2>");
		this.content.append("HTTP 1.1 requests must include the Host: header.");
		this.content.append("</body>");
		this.content.append("</html>");
		this.contentLength.append(this.content.length());
	}

	/*
	 * create the control panel page
	 */
	private void showControlPanel() {
		HttpServer.logs.flush();
		this.content.append("<html>");
		this.content.append("<body>");
		this.content.append("<p>Yufei Ye, yeyufei@seas.upenn.edu</p>");
		this.content.append("<p>Here are the status of all threads</p>");
		this.content.append("<p>Click error log to see log:</p>");
		this.content.append("<a href=\"errorLogs.txt\">Error Log</a>");
		LinkedList<WorkerThread> threads = this.pool.getThreads();
		for (int i = 0; i < threads.size(); i++) {
			Thread.State state = threads.get(i).getState();
			String currentState = state.toString();
			String threadUrl = threads.get(i).getUrl();
			if (currentState.equals("WAITING")){
				this.content.append("<p>" + i + ": " + currentState + "</p>");
			} else {
				this.content.append("<p>" + i + ": " + threadUrl + "</p>");
			}
			
		}
		String shutDownLink = "shutdown";
		
		this.content.append("<a href=\"" + shutDownLink + "\">shutdown</a>");
		this.content.append("</body>");
		this.content.append("</html>");
		this.contentLength.append(this.content.length());
		this.contentType.append("text/html");
	}
	
	/*
	 * get file list and subdirectory
	 */
	private void getDirectoryList(File file){
		if (file.isDirectory()){
			File[] listFiles = file.listFiles();
			content.append("<p>" + file.getName() + "is a directory: </p>");
			for (File f: listFiles){
				getDirectoryList(f);
			}
		} else {
			content.append("<p>" + file.getName() + "</p>");
		}
	}
}
