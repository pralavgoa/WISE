package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WISE_Application;

/*
 Print a study space, which should force a load
 */

public class print_study_space extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();
	String initErr = Surveyor_Application.check_init(req.getContextPath());
	if (initErr != null) {
	    out.println(initErr + "<p> Servlet called: Preface Loader </p>"
		    + Surveyor_Application.initErrorHtmlFoot);
	    WISE_Application.log_error("WISE Surveyor Init Error: " + initErr,
		    null);// should write to file if no email
	    return;
	}

	// get requested study space ID
	String spaceid = req.getParameter("ss");
	out.println("<p>Requested study space is: " + spaceid + "</p>\n");
	Study_Space theStudy = Study_Space.get_Space(spaceid);
	if (theStudy != null)
	    out.println(theStudy.toString());
	else
	    out.println("Retrieve of study failed.\n");
	out.println("<br>Done.");
	out.close();

    }

}
