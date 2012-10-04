package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Page;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Survey;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.WISE_Application;

/*
 Create a summary report for each individual open question 
 */

public class view_open_results extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare to write
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

	// get the user or the user group whose results will get presented
	String whereclause = (String) session.getAttribute("WHERECLAUSE");
	if (whereclause == null)
	    whereclause = "";

	// get the unanswered question number
	String unanswered = req.getParameter("u");
	// get the question id
	String question = req.getParameter("q");
	// get the page id
	String page = req.getParameter("t");

	Study_Space study_space;
	Survey survey;

	// get the user from session
	User theUser = (User) session.getAttribute("USER");
	if (theUser == null) {
	    // theUser is null means this view came from admin
	    study_space = (Study_Space) session.getAttribute("STUDYSPACE");
	    survey = (Survey) session.getAttribute("SURVEY");
	} else {
	    study_space = theUser.currentSurvey.study_space;
	    survey = theUser.currentSurvey;
	}

	// get the question stem
	String q_stem = "";
	Page pg = survey.get_page(page);
	if (pg != null)
	    q_stem = pg.title;

	// find the question stem
	// for(int i=0; i<pg.items.length; i++)
	// {
	// if(pg.items[i].name!=null &&
	// pg.items[i].name.equalsIgnoreCase(question))
	// {
	// Question theQ = (Question) pg.items[i];
	// q_stem = theQ.stem;
	// break;
	// }
	// }

	// display the report
	out.println("<html><head>");
	out.println("<title>VIEW RESULTS - QUESTION:" + question.toUpperCase()
		+ "</title>");
	out.println("<LINK href='" + s.shared_file_url
		+ "style.css' rel=stylesheet>");
	out.println("<style>");
	out.println(".tth {	border-color: #CC9933;}");
	out.println(".sfon{	font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; font-weight: bold; color: #996633;}");
	out.println("</style>");
	out.println("<script type='text/javascript' language='javascript' src=''></script>");
	out.println("</head><body text=#333333><center>");
	// out.println("</head><body text=#333333 bgcolor=#FFFFCC><center>");
	out.println("<table class=tth border=1 cellpadding=2 cellspacing=2 bgcolor=#FFFFF5>");
	out.println("<tr bgcolor=#BA5D5D>");
	out.println("<td align=left><font color=white>");
	out.println("<b>Question:</b> " + q_stem + " <font size=-2><i>("
		+ question + ")</i></font>");
	out.println("</font>");
	out.println("</tr><tr>");
	out.println("<th width=200 class=sfon align=left><b>Answer:</b></th>");
	out.println("</tr>");

	try {
	    // open database connection
	    Connection conn = study_space.getDBConnection();
	    Statement stmt = conn.createStatement();

	    if (page != null) {
		// get all the answers from data table regarding to this
		// question
		String sql = "select invitee, firstname, lastname, status, "
			+ question + " from " + survey.id
			+ "_data, invitee where ";
		sql += "id=invitee and (status not in (";

		for (int k = 0; k < survey.pages.length; k++) {
		    if (!page.equalsIgnoreCase(survey.pages[k].id))
			sql += "'" + survey.pages[k].id + "', ";
		    else
			break;
		}
		sql += "'" + page + "') or status is null) and " + question
			+ " is not null and " + question + " !=''";
		if (!whereclause.equalsIgnoreCase(""))
		    sql += " and " + whereclause;

		boolean dbtype = stmt.execute(sql);
		ResultSet rs = stmt.getResultSet();

		String text;
		while (rs.next()) {
		    text = rs.getString(question);
		    if (text == null || text.equalsIgnoreCase(""))
			text = "null";
		    out.println("<tr>");
		    out.println("<td align=left>" + text + "</td>");
		    out.println("</tr>");
		}
	    } // end of if

	    // display unanswered question number
	    if (unanswered != null && !unanswered.equalsIgnoreCase(""))
		out.println("<tr><td align=left>Number of unanswered:"
			+ unanswered + "</td></tr>");
	    out.println("</table></center><br><br>");
	    stmt.close();
	    conn.close();
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - VIEW OPEN RESULT: " + e.toString(), null);
	    return;
	}

	out.println("<center><a href='javascript: history.go(-1)'>");
	out.println("<img src='" + "imageRender?img=back.gif' /></a></center>");
	out.close();
    }

}
