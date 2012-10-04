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
import edu.ucla.wise.commons.User_DB_Connection;
import edu.ucla.wise.commons.WISE_Application;

//Will handle retrieving/saving survey page values sent through AJAX calls
//currently implemented only for the repeating item set
public class repeating_item_set_io extends HttpServlet {

    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	// prepare for writing

	PrintWriter out;
	res.setContentType("application/json");
	out = res.getWriter();
	String initErr = Surveyor_Application.check_init(req.getContextPath());
	HttpSession session = req.getSession(true);

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
	    return;
	}

	// if session is new, then it must have expired since begin; show the
	// session expired info
	if (session.isNew()) {
	    res.sendRedirect(Surveyor_Application.shared_file_url + "error"
		    + Surveyor_Application.html_ext);
	    return;
	}
	// get the user from session
	User theUser = (User) session.getAttribute("USER");
	if (theUser == null || theUser.id == null) // latter signals an
						   // improperly-initialized
						   // User
	{
	    out.println("<p>Error: Can't find the user info.</p>");
	    return;
	}

	// get database connection
	User_DB_Connection user_db_connection = theUser.getMyDataBank();

	// get the table name from request
	String repeat_table_name = req.getParameter("repeat_table_name");

	// get the table values as a string
	String repeat_table_values = user_db_connection
		.get_all_data_for_repeating_set(repeat_table_name);

	out.println(repeat_table_values);

    }

}
