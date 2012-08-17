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


/*
Print an overview list of the pages in a survey
*/

public class progress extends HttpServlet
{
	static final long serialVersionUID = 1000;
	public void service(HttpServletRequest req, HttpServletResponse res)
       throws ServletException, IOException
	{

        // prepare for writing
        PrintWriter out;
        res.setContentType("text/html");
        out = res.getWriter();

        HttpSession session = req.getSession(true);
        Surveyor_Application s = (Surveyor_Application)session.getAttribute("SurveyorInst");
        
        //if session is new, then show the session expired info
        if (session.isNew())
        {
            res.sendRedirect(s.shared_file_url + "error" + Surveyor_Application.html_ext);
            return;
        }

        //get the user from session
        User theUser = (User) session.getAttribute("USER");
        if(theUser==null || theUser.id == null)
        {
          out.println("<p>Error: Can't find the user info.</p>");
          return;
        }

        Hashtable completed_pages = theUser.get_completed_pages();

        //get the interviewer if it is on the interview status
        Interviewer inv = (Interviewer) session.getAttribute("INTERVIEWER");
        //for interviewer, he can always browse any pages
        if(inv!=null)
           theUser.currentSurvey.allow_goback=true;

        //check if the allow goback setting is ture, then user could go back to
        //view the pages that he has went through
        if(theUser.currentSurvey.allow_goback)
          out.println(theUser.currentSurvey.print_progress(theUser.currentPage));
        //otherwise, print out the page list without linkages to prevent user from going back
        else
          out.println(theUser.currentSurvey.print_progress(theUser.currentPage, completed_pages));

        out.close();
	}
}



