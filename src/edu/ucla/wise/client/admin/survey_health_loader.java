package edu.ucla.wise.client.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.wise.client.healthmon.SurveyHealth;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WISE_Application;

/*
 Load a new survey and set up its Data tables. 
 (Called via URL request from load.jsp in the admin application)
 */

public class survey_health_loader extends HttpServlet {
	static final long serialVersionUID = 1000;

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// prepare for writing
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		// Make sure local app is initialized
		String initErr = Surveyor_Application.check_init(req.getContextPath());
		if (initErr != null) {
			out.println(initErr + "<p> Servlet called: Survey Loader </p>"
					+ Surveyor_Application.initErrorHtmlFoot);
			WISE_Application.log_error("WISE Surveyor Init Error: " + initErr,
					null);// should write to file if no email
			return;
		}

		// get session
		// HttpSession session = req.getSession(true);

		// get the survey name and study ID
		String survey_name = (String) req.getParameter("SurveyName");
		String study_id = (String) req.getParameter("SID");
		if (survey_name == null || study_id == null) {
			out.println("<tr><td align=center>SURVEY LOADER ERROR: can't get the survey name or study id from URL</td></tr></table>");
			return;
		}
		// get the study space
		Study_Space study_space = Study_Space.get_Space(study_id);
		if (study_space == null) {
			out.println("<tr><td align=center>SURVEY LOADER ERROR: can't create study space</td></tr></table>");
			return;
		}
		SurveyHealth.monitor(study_space);
		return;
	}

}