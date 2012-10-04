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
 Record declining consent reason
 */

public class consent_decline extends HttpServlet {
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

	// save the decline comments
	theUser.set_decline_reason(req.getParameter("reason"));

	// then show the thank you page to user
	String new_page = s.shared_file_url + "decline_thanks"
		+ Surveyor_Application.html_ext;
	out.println("<html><head>");
	out.println("<script LANGUAGE='JavaScript1.1'>");
	out.println("top.location.replace('" + new_page + "');");
	out.println("</script></head>");
	out.println("<body></body>");
	out.println("</html>");
	out.close();
    }
}
