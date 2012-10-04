package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Message_Sequence;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Surveyor_Application;

/*
 Direct the user coming from email URL or interviewers to appropriate next step or page
 */

public class SurveyorTest extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// FIND server's default file location
	// File test_file = new File("whereAmI");
	// FileOutputStream tstout = new FileOutputStream(test_file);
	// tstout.write(100);
	// tstout.close();

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	// Initialize surveyor application if not already started
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
	    Surveyor_Application.log_error("WISE Surveyor Init Error: "
		    + initErr, null);// should write to file if no email
	    return;
	}

	HttpSession session = req.getSession(true);
	session.getServletContext();
	Surveyor_Application s = (Surveyor_Application) session
		.getAttribute("SurveyorInst");

	// get the encoded study space ID
	String spaceid_encode = req.getParameter("t");
	// get the email message ID
	String msgid_encode = req.getParameter("msg");

	Study_Space myStudySpace = null;
	String id2 = "", thesharedFile = "";
	if (spaceid_encode != null) {
	    myStudySpace = Study_Space.get_Space(spaceid_encode); // instantiates
								  // the study
	    if (myStudySpace != null) {
		id2 = myStudySpace.id;
		thesharedFile = myStudySpace.sharedFile_urlRoot;
		Message_Sequence[] msa = myStudySpace.preface
			.get_message_sequences("Enrollmt");
		for (int i = 0; i < msa.length; i++)
		    id2 += "; " + msa[1].toString();
	    }
	}

	out.println("<HTML><HEAD><TITLE>Begin Page</TITLE>"
		+ "<LINK href='../file_product/style.css' type=text/css rel=stylesheet>"
		+ "<body text=#000000 bgColor=#ffffcc><center><table>"
		+ "<tr><td>Successful test. StudySpace id [t]= "
		+ spaceid_encode + "</td></tr>"
		+ "<tr><td>Root URL= "
		+ Surveyor_Application.rootURL
		+ "</td></tr>"
		+ "<tr><td>XML path = "
		+ Surveyor_Application.xml_loc
		+ "</td></tr>"
		+ "<tr><td>SS file path = "
		+ thesharedFile
		+ "</td></tr>"
		// + "<tr><td>Image path = " +
		// Surveyor_Application.image_root_path + "</td></tr>"
		// + "<tr><td>DB backup path = " +
		// Surveyor_Application.db_backup_path + "</td></tr>"
		+ "<tr><td>Context Path= " + s.ApplicationName + "</td></tr>"
		+ "<tr><td>Servlet Path= " + s.servlet_url + "</td></tr>"
		+ "<tr><td>message id= " + msgid_encode + "</td></tr>"
		+ "</table></center></body></html>");
    }

}
