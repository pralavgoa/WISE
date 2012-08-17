package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Page;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;

/**
 * View the survey results (with the summary of data) by page (viewed by user
 * through the Surveyor Application). URL: /survey/view_results
 */
public class view_results extends HttpServlet {
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

		// get the current page info
		String pageId = req.getParameter("page");

		// get the user from session
		User theUser = (User) session.getAttribute("USER");
		if (theUser == null) {
			out.println("<p>Error: Can't find the user info.</p>");
			return;
		} else {
			// if no page info, set the 1st page as the current page
			if (pageId == null || pageId.equalsIgnoreCase(""))
				pageId = theUser.currentSurvey.pages[0].id;
		}

		// get the page obj
		Page p = theUser.currentSurvey.get_page(pageId);

		// view results of all invitees
		//String whereStr = "";

		// put the whereclause in session
		//session.removeAttribute("WHERECLAUSE");
		//session.setAttribute("WHERECLAUSE", whereStr);

		// display the results
		// out.println(p.render_results(theUser, whereStr));
		out.println(p.toString());

		out.close();

	}

}
