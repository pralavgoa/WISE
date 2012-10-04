package edu.ucla.wise.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.WISE_Application;

/*
 Load a new survey and set up its Data tables. 
 (Called via URL request from load.jsp in the admin application)
 */

public class AdminMonitor extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	HttpSession session = req.getSession(true);
	// check if the session is still valid
	// AdminInfo admin_info = (AdminInfo)
	// session.getAttribute("ADMIN_INFO");
	// //if the session is invalid
	// if (admin_info==null )
	// {
	// out.println("Wise Admin - Reload Error: Can't get your Admin Info");
	// return;
	// }
	out.println("<HTML><HEAD><TITLE>WISE Admin Reloader</TITLE>"
		+ "<LINK href='../file_product/style.css' type=text/css rel=stylesheet>"
		+ "<body text=#000000 bgColor=#ffffcc><center><table>");
	String initErr = AdminInfo.check_init(req.getContextPath());
	if (initErr != null) {
	    out.println("<tr><td>Sorry, the WISE Administration application failed to initialize. "
		    + "Please contact the system administrator with the following information."
		    + "<P>" + initErr + "</td></tr>");
	    WISE_Application.log_error("WISE Admin Init Error: " + initErr,
		    null);// should write to file if no email
	} else {
	    out.println("<tr><td align=center>WISE Admin Application Currently being used by:");
	    String theList = AdminInfo.listAdminsOnNow();
	    if (theList.equals(""))
		out.println("Nobody");
	    else
		out.println(theList);
	    out.println("</td></tr></table>");
	}
	out.println("</table></center></body></html>");
    }

}
