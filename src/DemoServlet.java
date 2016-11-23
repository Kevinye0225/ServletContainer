

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

public class DemoServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Demo post servlet</TITLE></HEAD><BODY>");
		out.println("<P>The value of 'login' is: " + 
				login + "</P>");
		out.println("<P>The value of 'password' is: " + 
				password + "</P>");
		out.println("<p><a href=\"home\">return home</a></p>");
		out.println("</BODY></HTML>");	
	}
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Demo post servlet</TITLE></HEAD><BODY>");
		out.println("<P>The value of 'login' is: " + 
				login + "</P>");
		out.println("<P>The value of 'password' is: " + 
				password + "</P>");
		out.println("</BODY></HTML>");	
	}
}
		
