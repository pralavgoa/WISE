package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Surveyor_Application;

/*
 Produces the outer survey frameset -- with appropriately-localized servlet refs
 */

public class wise_outer_frame extends HttpServlet {
    static final long serialVersionUID = 1000;
    static String html;

    // =
    // "<html><head><title>Web-based Interactive Survey Environment (WISE)</title>"
    // +
    // "<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>"
    // +
    // "<script type='text/javascript' language='javascript' src='"+
    // Surveyor_Application.shared_file_url +"main.js'></script>" +
    // "<script type='text/javascript' language='javascript' SRC='"+
    // Surveyor_Application.shared_file_url +"survey.js'></script>" +
    // "<script type='text/javascript' language='javascript'>" +
    // "	top.fieldVals = null;" +
    // "	top.requiredFields = null;" +
    // "</script></head>" +
    // "<frameset onLoad='javascript:no_right_click()' rows='1,*' frameborder='NO' border='0' framespacing='0'>"
    // +
    // "  <frame name='topFrame' scrolling='NO' noresize src='"+
    // Surveyor_Application.shared_file_url +"begin_fix.htm' >" +
    // "  <frame name='mainFrame' src='wise_frame'>" +
    // "</frameset><noframes></noframes>" +
    // "</html>";

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	HttpSession session = req.getSession(true);
	Surveyor_Application s = (Surveyor_Application) session
		.getAttribute("SurveyorInst");
	html = "<html><head><title>Web-based Interactive Survey Environment (WISE)</title>"
		+ "<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>"
		+ "<script type='text/javascript' language='javascript' src='"
		+ Surveyor_Application.shared_file_url
		+ "main.js'></script>"
		+ "<script type='text/javascript' language='javascript' SRC='"
		+ Surveyor_Application.shared_file_url
		+ "survey.js'></script>"
		+ "<script type='text/javascript' language='javascript'>"
		+ "	top.fieldVals = null;"
		+ "	top.requiredFields = null;"
		+ "</script></head>"
		+ "<frameset onLoad='javascript:no_right_click()' rows='1,*' frameborder='NO' border='0' framespacing='0'>"
		+ "  <frame name='topFrame' scrolling='NO' noresize src='"
		+ Surveyor_Application.shared_file_url
		+ "begin_fix.htm' >"
		+ "  <frame name='mainFrame' src='wise_frame'>"
		+ "</frameset><noframes></noframes>" + "</html>";

	out.println(html);
	out.close();
    }

}
