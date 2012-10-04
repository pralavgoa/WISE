package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;

/*
 Lead user to the survey if he accepted the consent or
 lead him to the page to ask for decline reason if he declined the consent
 */

public class consent_record extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();

	HttpSession session = req.getSession(true);
	Surveyor_Application s = (Surveyor_Application) session
		.getAttribute("SurveyorInst");

	// if session is new, then show the session expired info
	if (session.isNew()) {
	    res.sendRedirect(s.shared_file_url + "error"
		    + Surveyor_Application.html_ext);
	    return;
	}

	// get the user from session
	User theUser = (User) session.getAttribute("USER");
	if (theUser == null) {
	    out.println("<p>Error: Can't find the user info.</p>");
	    return;
	}

	// get user's consent decision
	String answer = req.getParameter("answer");

	String url = "";
	if (answer.equalsIgnoreCase("yes")) // accepted the consent
	{
	    theUser.consent();
	    // forward to setup_survey servlet
	    url = "setup_survey";
	} else if (answer.equalsIgnoreCase("no_consent")) // accepted the
							  // consent
	{
	    // forward to setup_survey servlet, which handles all other state
	    // changes
	    url = "setup_survey";
	} else // declined the consent
	{
	    theUser.decline();
	    // forward to setup_survey servlet
	    url = s.shared_file_url + "decline" + Surveyor_Application.html_ext;
	}
	res.sendRedirect(url);
	out.close();
    }

}
