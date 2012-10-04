package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.WISE_Application;

public class BrowserRequestChecker {

    public static boolean checkRequest(HttpServletRequest req,
	    HttpServletResponse res, PrintWriter out) throws IOException {

	String initErr = Surveyor_Application.check_init(req.getContextPath());

	if (initErr != null) {
	    out.println("<HTML><HEAD><TITLE>WISE survey system -- Can't identify you</TITLE>"
		    + "<LINK href='"
		    + Surveyor_Application.shared_file_url
		    + "style.css' type=text/css rel=stylesheet>"
		    + "<body><center><table>"
		    // + "<body text=#000000 bgColor=#ffffcc><center><table>"
		    + "<tr><td>Sorry, the WISE Surveyor application failed to initialize. "
		    + "Please contact the system administrator with the following information."
		    + "<P>"
		    + initErr
		    + "</td></tr>"
		    + "</table></center></body></html>");
	    WISE_Application.log_error("WISE Surveyor Init Error: " + initErr,
		    null);// should write to file if no email
	    return false;
	}

	return true;

    }

    public static User getUserFromSession(HttpServletRequest req,
	    HttpServletResponse res, PrintWriter out) throws IOException {

	HttpSession session = req.getSession(true);

	// if session is new, then it must have expired since begin; show the
	// session expired info
	if (session.isNew()) {
	    res.sendRedirect(Surveyor_Application.shared_file_url + "error"
		    + Surveyor_Application.html_ext);
	    return null;
	}
	// get the user from session
	User user = (User) session.getAttribute("USER");
	if (user == null || user.id == null) // latter signals an
					     // improperly-initialized User
	{
	    out.println("<p>Error: Can't find the user info.</p>");
	    return null;
	}

	return user;
    }

}
