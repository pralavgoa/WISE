package edu.ucla.wise.admin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.ucla.wise.commons.*;

public class view_survey extends HttpServlet {
	static final long serialVersionUID = 1000;

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		// Make sure local app is initialized
		String initErr = Surveyor_Application.check_init(req.getContextPath());
		if (initErr != null) {
			out.println("<HTML><HEAD><TITLE>WISE survey system -- Can't identify you</TITLE>"
					+ "<LINK href='../file_product/style.css' type=text/css rel=stylesheet>"
					+ "<body text=#000000 bgColor=#ffffcc><center><table>"
					+ "<tr><td>Sorry, the WISE Surveyor application failed to initialize. "
					+ "Please contact the system administrator with the following information."
					+ "<P>"
					+ initErr
					+ "</td></tr>"
					+ "</table></center></body></html>");
			WISE_Application.log_error(
					"WISE Surveyor Init Error: " + initErr, null);// should
																	// write to
																	// file if
																	// no email
			return;
		}
		HttpSession session = req.getSession(true);
		// check if the session is still valid
		String survey_id = req.getParameter("s");
		AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");
		// if the session is invalid
		if (admin_info == null || survey_id == null) {
			out.println("Wise Admin - View Survey Error: Can't get the Admin Info");
			return;
		}

		try {
			// Changing URL pattern
			String new_url = admin_info.getStudyServerPath()
					+ WiseConstants.ADMIN_APP + "/admin_view_form?SID="
					+ admin_info.study_id + "&a=FIRSTPAGE&s=" + survey_id;
			System.out.println(new_url);
			res.sendRedirect(new_url);

		} catch (Exception e) {
			out.println("Wise Admin - View Survey Error: " + e);
		}
		out.close();
	}

}
