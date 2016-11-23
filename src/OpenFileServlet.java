import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class OpenFileServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>OpenFile Servlet</TITLE></HEAD><BODY>");
		out.println("<p><a href=\"index.html\">Click here to open a html file</a></p>");
		out.println("<p><a href=\"image02.png\">Click here to open an image</a></p>");
		out.println("<p><a href=\"home\">return home</a></p>");
		out.println("</BODY></HTML>");		
	}
}
