package edu.ucla.wise.client.interview;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Interviewer;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Surveyor_Application;

/* 
 Direct the interviewer's login   
 */
public class interview_login extends HttpServlet {
    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();

	// get the interviewer login info from the login form
	String interviewer_name = req.getParameter("interviewername");
	String interviewer_id = req.getParameter("interviewerid");
	String study_id = req.getParameter("studyid");

	HttpSession session = req.getSession(true);
	Surveyor_Application s = (Surveyor_Application) session
		.getAttribute("SurveyorInst");
	// get the study space and create the interviewer object
	Study_Space theStudy = Study_Space.get_Space(study_id);
	Interviewer inv = new Interviewer(theStudy);

	String url;
	// check the interviewer's verification and assign the attributes
	if (inv.verify_interviewer(interviewer_id, interviewer_name)) {
	    session.setAttribute("INTERVIEWER", inv);
	    url = s.shared_file_url + "interview/Show_Assignment.jsp";
	} else {
	    url = theStudy.app_urlRoot + theStudy.dir_name + "/interview/error"
		    + Surveyor_Application.html_ext;
	}

	res.sendRedirect(url);
    }

}
