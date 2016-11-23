import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostFormServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Demo post servlet</TITLE></HEAD><BODY>");
		out.println("<form method=\"GET\" action=\"demo\"><input placeholder=\"login\" type=\"text\" name=\"login\">");
		out.println("<input type=\"text\" placeholder=\"password\" name=\"password\">");
		out.println("<input type=\"submit\" value=\"submit\">");
		out.println("</form>");
		out.println("<p><a href=\"home\">return home</a></p>");
		out.println("</BODY></HTML>");	
	}
}
