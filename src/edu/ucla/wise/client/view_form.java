package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Interviewer;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.WISE_Application;

/*
 Display a single survey page as a form to be filled out
 */

public class view_form extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();

	HttpSession session = req.getSession(true);
	// Surveyor_Application s =
	// (Surveyor_Application)session.getAttribute("SurveyorInst");

	// if session is new, then show the session expired info
	if (session.isNew()) {
	    res.sendRedirect(Surveyor_Application.shared_file_url + "error"
		    + WISE_Application.html_ext);
	    return;
	}

	// get the user from session
	User theUser = (User) session.getAttribute("USER");
	if (theUser == null) {
	    out.println("<p>Error: Can't find the user info.</p>");
	    return;
	}

	// check if it is an interview process
	Interviewer inv = (Interviewer) session.getAttribute("INTERVIEWER");
	if (inv != null) {
	    // get the current page
	    String pageid = req.getParameter("p");
	    // set the current page
	    theUser.currentPage = theUser.currentSurvey.get_page(pageid);
	}
	/*
	 * //get the output string of the current page String p_output =
	 * theUser.currentPage.render_page(theUser);
	 * 
	 * // display the current page only if it returns output
	 * if(p_output!=null && !p_output.equalsIgnoreCase("")) {
	 * out.println("<html><head>" + "<link rel='stylesheet' href="+
	 * "'styleRender?css=style.css' type='text/css'>\n"+
	 * "<script type='text/javascript' language='JavaScript1.1' src='"+
	 * Surveyor_Application.shared_file_url +"survey.js'></script>" +
	 * "<script type='text/javascript' src='"
	 * +Surveyor_Application.shared_file_url
	 * +"jquery-1.7.1.min.js'></script>"); out.println(p_output); } //
	 * otherwise, skip the current page else { //redirect to the next page
	 * by outputting hidden field values and running JS submit()
	 * out.println("<html>"); out.println("<head></head>");
	 * out.println("<body>");
	 * out.println("<form name='mainform' method='post' action='readform'>"
	 * ); out.println("<input type='hidden' name='action' value=''>"); if (
	 * (theUser.currentSurvey.is_last_page(theUser.currentPage.id)) ||
	 * (theUser.currentPage.final_page) )
	 * out.println("<input type='hidden' name='nextPage' value='DONE'>");
	 * else { //if the id of the next page is not set in the survey xml file
	 * (with value=NONE), //then get its id from the page hash table in the
	 * survey class. if (
	 * theUser.currentPage.next_page.equalsIgnoreCase("NONE"))
	 * out.println("<input type='hidden' name='nextPage' value='"+
	 * theUser.currentSurvey.next_page(theUser.currentPage.id).id+"'>");
	 * else //otherwise, assign the page id directly to the form
	 * out.println("<input type='hidden' name='nextPage' value='"+
	 * theUser.currentPage.next_page+"'>"); } out.println("</form>");
	 * out.println
	 * ("<script LANGUAGE='JavaScript1.1'>document.mainform.submit();</script>"
	 * ); out.println("</body></html>"); } out.close();
	 */

	// Pralav code for printing page
	StringBuffer html_content = new StringBuffer("");

	// html_content.append("<!DOCTYPE html>");
	html_content
		.append("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/strict.dtd'>");
	html_content.append("<html>");
	html_content.append("<head>");
	html_content
		.append("<title>Web-based Interactive Survey Environment (WISE)</title>");
	html_content
		.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>"
			+ "<script type='text/javascript' language='javascript' src='"
			+ Surveyor_Application.shared_file_url
			+ "main.js'></script>"
			+ "<script type='text/javascript' language='javascript' SRC='"
			+ Surveyor_Application.shared_file_url
			+ "survey.js'></script>"
			+ "<script type='text/javascript' language='javascript'>"
			+ "	top.fieldVals = null;"
			+ "	top.requiredFields = null;"
			+ "     var userId = "
			+ theUser.id + ";" + "</script>");
	html_content
		.append("<link type='text/css' href='styleRender?css=style.css' rel='stylesheet'>");
	html_content.append("</head>");
	html_content
		.append("<body onload='javascript: setFields();check_preconditions();'>");
	html_content.append("<div id = 'progress_bar'>");
	html_content.append(getProgressDivContent(theUser, session));
	html_content.append("</div>");
	html_content.append("<div id='content'>");
	html_content.append(getPageHTML(theUser));
	html_content.append("</div>");
	html_content
		.append("<div class='modal'><!-- Place at bottom of page --></div>");
	html_content.append("</body>");
	html_content.append("</html>");

	out.println(html_content.toString());
    }

    // Method returns the elements to go inside the progress div
    public String getProgressDivContent(User theUser, HttpSession session) {
	StringBuffer progress_bar = new StringBuffer("");
	Hashtable completed_pages = theUser.get_completed_pages();

	// get the interviewer if it is on the interview status
	Interviewer intv = (Interviewer) session.getAttribute("INTERVIEWER");

	// Interviewer can always browse any pages
	if (intv != null)
	    theUser.currentSurvey.allow_goback = true;

	if (theUser.currentSurvey.allow_goback)
	    progress_bar.append(theUser.currentSurvey
		    .print_progress(theUser.currentPage));
	else
	    progress_bar.append(theUser.currentSurvey.print_progress(
		    theUser.currentPage, completed_pages));

	return progress_bar.toString();
    }

    public String getPageHTML(User theUser) {
	StringBuffer page_html = new StringBuffer("");

	// get the output string for the current page
	String p_output = theUser.currentPage.render_page(theUser);

	if (p_output != null && !p_output.equalsIgnoreCase("")) {
	    page_html
		    .append("<script type='text/javascript' language='JavaScript1.1' src='"
			    + Surveyor_Application.shared_file_url
			    + "survey.js'></script>");
	    page_html
		    .append("<script type='text/javascript' src='"
			    + Surveyor_Application.shared_file_url
			    + "jquery-1.7.1.min.js'></script>"
			    + "<script type='text/javascript' language='javascript' SRC='"
			    + Surveyor_Application.shared_file_url
			    + "survey_form_values_handler.js'></script>");
	    page_html.append(p_output);
	} else {
	    // redirect to the next page by outputting hidden field values and
	    // running JS submit()
	    page_html
		    .append("<form name='mainform' method='post' action='readform'>");
	    page_html.append("<input type='hidden' name='action' value=''>");
	    if ((theUser.currentSurvey.is_last_page(theUser.currentPage.id))
		    || (theUser.currentPage.final_page))
		page_html
			.append("<input type='hidden' name='nextPage' value='DONE'>");
	    else {
		// if the id of the next page is not set in the survey xml file
		// (with value=NONE),
		// then get its id from the page hash table in the survey class.
		if (theUser.currentPage.next_page.equalsIgnoreCase("NONE"))
		    page_html
			    .append("<input type='hidden' name='nextPage' value='"
				    + theUser.currentSurvey
					    .next_page(theUser.currentPage.id).id
				    + "'>");
		else
		    // otherwise, assign the page id directly to the form
		    page_html
			    .append("<input type='hidden' name='nextPage' value='"
				    + theUser.currentPage.next_page + "'>");
	    }
	    page_html.append("</form>");
	    page_html
		    .append("<script LANGUAGE='JavaScript1.1'>document.mainform.submit();</script>");
	}

	return page_html.toString();
    }

}