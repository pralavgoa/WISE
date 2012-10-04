package edu.ucla.wise.client.interview;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Interviewer;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WiseConstants;

/* 
 Handle the interviewer's log out
 */
public class interview_logout extends HttpServlet {
    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();

	String url = null;
	HttpSession session = req.getSession(true);
	Surveyor_Application s = (Surveyor_Application) session
		.getAttribute("SurveyorInst");

	if (session.isNew()) {
	    url = "interview/expired.htm";
	} else if (session != null) {
	    Interviewer inv = (Interviewer) session.getAttribute("INTERVIEWER");
	    // get the URL of the forwarding page
	    url = inv.study_space.app_urlRoot + WiseConstants.SURVEY_APP
		    + "/interview/expired.htm";
	    // remove the interviewer from the session
	    session.removeAttribute("INTERVIEWER");
	    // end the session
	    session.invalidate();
	}
	res.sendRedirect(url);
    }

}
