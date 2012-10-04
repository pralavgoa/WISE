package edu.ucla.wise.client.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Page;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Survey;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WISE_Application;

/**
 * View the survey results (with the summary of data) by page (viewed by admin
 * on behalf of Admin Application). URL: /survey/admin_view_results
 */

public class view_results extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	// Make sure local app is initialized
	String initErr = Surveyor_Application.check_init(req.getContextPath());
	if (initErr != null) {
	    out.println(initErr + "<p> Servlet called: View_results </p>"
		    + Surveyor_Application.initErrorHtmlFoot);
	    WISE_Application.log_error("WISE Surveyor Init Error: " + initErr,
		    null);// should
			  // write to
			  // file if
			  // no email
	    return;
	}

	HttpSession session = req.getSession(true);

	String study_id, survey_id;

	// check if it is the first link
	String a = req.getParameter("a");
	// create session info from the first URL link
	if (a != null && a.equalsIgnoreCase("FIRSTPAGE")) {
	    // get the study id
	    study_id = (String) req.getParameter("SID");
	    // get the survey id
	    survey_id = (String) req.getParameter("s");

	    // get the current study space
	    Study_Space ss = Study_Space.get_Space(study_id);
	    // save the study space in the session
	    session.setAttribute("STUDYSPACE", ss);
	    // get the current survey
	    Survey sy = ss.get_Survey(survey_id);
	    // save the survey in the session
	    session.setAttribute("SURVEY", sy);

	    // set the first page id
	    String page_id = sy.pages[0].id;
	    // set the page id in the session as the current page id
	    session.setAttribute("PAGEID", page_id);

	    // get the user or the user group whose results will be presented
	    String whereStr = req.getParameter("whereclause");
	    if (whereStr == null)
		whereStr = "";
	    if (whereStr.equals("")) {
		// check if specific users selected
		String allUser = req.getParameter("alluser");
		if (allUser == null || allUser.equals("")
			|| allUser.equalsIgnoreCase("null")) {
		    String user = req.getParameter("user");
		    // get the specified user list
		    if (user == null) {
			out.println("Please select at least one invitee");
			return;
		    }
		    whereStr = "invitee in (" + user + ")";
		}
	    }

	    // update whereclause in a session
	    session.removeAttribute("whereStr");
	    session.setAttribute("WHERECLAUSE", whereStr);

	    // call itself to display the page
	    res.sendRedirect("admin_view_results");
	} else {
	    // get the survey from the session
	    Survey survey = (Survey) session.getAttribute("SURVEY");
	    // get the page id from the session
	    String pageid = (String) session.getAttribute("PAGEID");
	    // get the where string from the session
	    String where_clause = (String) session.getAttribute("WHERECLAUSE");

	    if (survey == null || where_clause == null || pageid == null) {
		out.println("<p>ADMIN VIEW RESULTS Error: can't get the study where string/survey/page info.</p>");
		return;
	    }

	    // get the current page
	    Page pg = survey.get_page(pageid);
	    // update the page id to be the next page
	    session.removeAttribute("PAGEID");
	    if (!survey.is_last_page(pageid))
		session.setAttribute("PAGEID", survey.next_page(pageid).id);

	    // display the result on current page
	    out.println(pg.render_admin_results(where_clause));
	}
	out.close();

    }

}
