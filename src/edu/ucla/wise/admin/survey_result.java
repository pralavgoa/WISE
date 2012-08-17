package edu.ucla.wise.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.WiseConstants;

public class survey_result extends HttpServlet {
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();

		HttpSession session = req.getSession(true);
		// get the survey ID from request and the admin info object from session
		String survey_id = req.getParameter("s");
		AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");

		// if the session is invalid, display the error
		if (admin_info == null || survey_id == null) {
			out.println("Wise Admin - Survey Result Error: Can't get the Admin Info");
			return;
		}
		// get the selected users and the where clause of the query to select
		// users
		String whereclause_v = req.getParameter("whereclause");
		String alluser_v = req.getParameter("alluser");
		String user_v[] = req.getParameterValues("user");

		try {
			// initiate the user ID list
			String user_list = "";
			// put each user ID into the list, seperated by comma
			if (user_v != null) {
				int last_i = user_v.length - 1;
				for (int i = 0; i < last_i; i++)
					user_list += user_v[i] + ",";
				user_list += user_v[last_i];
			}
			// compose the forwarding URL to review the survey data conducted by
			// the selected users
			// TODO: is this correct
			res.sendRedirect(admin_info.getStudyServerPath()
					+ WiseConstants.SURVEY_APP + "/admin_view_results?SID="
					+ admin_info.study_id + "&a=FIRSTPAGE&s=" + survey_id
					+ "&whereclause=" + whereclause_v + "&alluser=" + alluser_v
					+ "&user=" + user_list);
		} catch (Exception e) {
			out.println("Wise Admin - Survey Result Error: " + e);
		}
		out.close();
	}

}
