package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.CommonUtils;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.commons.WiseConstants.STATES;

/*
 Direct the user after browser check to appropriate next step or page
 */

public class triage extends HttpServlet {
	static final long serialVersionUID = 1000;

	public String pageReplace_html(String new_page) {
		return "<html>" + "<head><script LANGUAGE='javascript'>"
				+ "top.location.replace('" + new_page + "');"
				+ "</script></head>" + "<body></body>" + "</html>";
	}

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();

		HttpSession session = req.getSession(true);

		if (session.isNew()) {
			System.out.println("Session is new?");
			res.sendRedirect(Surveyor_Application.shared_file_url + "error"
					+ Surveyor_Application.html_ext);
			return;
		}

		// decode study space ID
		User theUser = (User) session.getAttribute("USER");

		// if the user can't be created, send error info
		if (theUser == null) {
			out.println("<HTML><HEAD><TITLE>Begin Page</TITLE>"
					+ "<LINK href='"+ Surveyor_Application.shared_file_url + "style.css' type=text/css rel=stylesheet>"
					+ "<body><center><table>"
					//+ "<body text=#000000 bgColor=#ffffcc><center><table>"
					+ "<tr><td>Error: WISE can't seem to store your identity in the browser. You may have disabled cookies.</td></tr>"
					+ "</table></center></body></html>");
			WISE_Application.log_error(
					"WISE BEGIN - Error: Can't create the user.", null);
			return;
		}

		String interview_begin = (String) session.getAttribute("INTERVIEW");
		String main_url = "";

		// check if user already completed the survey
		if (theUser.completed_survey()) {
			if (theUser.getMyDataBank().get_userState().equalsIgnoreCase(STATES.incompleter.name()))	{
				theUser.getMyDataBank().set_userState(STATES.started.name());
			}
			if (interview_begin != null) // then IS an interview, always direct
											// interviewer to the survey page.
			{
				// This previously *just* recorded the current page in the db;
				// not sure why if interviewing and done
				main_url = Surveyor_Application.servlet_url
						+ "wise_outer_frame";
			} else // not an interview
			{
				// forward to another application's URL, if specified in survey
				// xml file
				if (theUser.currentSurvey.forward_url != null
						&& !theUser.currentSurvey.forward_url
								.equalsIgnoreCase("")) {
					main_url = theUser.currentSurvey.forward_url;
					// if an educational module ID is specified in the survey
					// xml, then add it to the URL
					if (!CommonUtils.isEmpty(theUser.currentSurvey.edu_module))
						main_url += "/"
								+ theUser.currentSurvey.study_space.dir_name
								+ "/survey?t="
								+ WISE_Application
										.encode(theUser.currentSurvey.edu_module)
								+ "&r=" + WISE_Application.encode(theUser.id);
					// otherwise the link will be the URL plus the user ID
					else {
						// Added Study Space ID and Survey ID, was sending just
						// the UserID earlier
						main_url = main_url
								+ "?s="
								+ WISE_Application.encode(theUser.id)
								+ "&si="
								+ theUser.currentSurvey.id
								+ "&ss="
								+ WISE_Application
										.encode(theUser.currentSurvey.study_space.id);
					}
				}
				// if the min completers is not set in survey xml, then direct
				// to Thank You page
				else if (theUser.currentSurvey.min_completers == -1) {
					main_url = Surveyor_Application.shared_file_url + "thank_you";
				} else if (theUser.currentSurvey.min_completers != -1) {
					// this link may come from the invitation email for results
					// review or user reclicked the old invitation link
					// check if the number of completers has reached the minimum
					// number set in survey xml,
					// then redirect the user to the review result page
					if (theUser.check_completion_number() < theUser.currentSurvey.min_completers)
						main_url = Surveyor_Application.shared_file_url
								+ "thank_you"
								+ "?review=false";
					else
						main_url = Surveyor_Application.servlet_url
								+ "view_results";
				}

			}
		} else if (theUser.started_survey()) {
			// for either user or interviewer, redirect to start the current
			// page.
			main_url = Surveyor_Application.servlet_url
					+ "wise_outer_frame";
		} else // forward to the welcome page
		{
			// main_url =
			// WISE_Application.retrieveAppInstance(session).servlet_url +
			// "welcome_generate";
			main_url = Surveyor_Application.servlet_url + "welcome";
		}

		// output javascript to forward
		out.println(pageReplace_html(main_url));
		out.close();
	}

}