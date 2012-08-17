package edu.ucla.wise.admin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.ucla.wise.commons.*;

/*import org.w3c.dom.*;
 import java.net.*;
 import sun.net.smtp.SmtpClient;
 import java.text.*;
 import java.lang.*;
 import java.util.*;
 */

public class print_survey extends HttpServlet {
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();
		HttpSession session = req.getSession(true);
		// check if the session is still valid
		String survey_id = req.getParameter("s");
		AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");
		if (admin_info == null || survey_id == null) // if session does not
														// exists
		{
			out.println("Wise Admin - Print Survey Error: Can't get the Admin Info");
			return;
		}

		try {
			// Changing the URL pattern
			String new_url = admin_info.getStudyServerPath() + "/"
					+ WiseConstants.ADMIN_APP + "/" + "admin_print_survey?SID="
					+ admin_info.study_id + "&a=FIRSTPAGE&s=" + survey_id;
			res.sendRedirect(new_url);

		} catch (Exception e) {
			out.println("Wise Admin - Print Survey Error: " + e);
		}
		out.close();
	}

}
