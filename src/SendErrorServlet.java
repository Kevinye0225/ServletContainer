import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class SendErrorServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.sendError(404, "Cannot find you");
	}
}