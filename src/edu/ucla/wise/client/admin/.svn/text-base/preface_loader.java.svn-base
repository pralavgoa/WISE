package edu.ucla.wise.client.admin;

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
Load the preface, which includes both welcome page and consent form (optional)
(continue running the URL request from the admin - load.jsp)
*/

public class preface_loader extends HttpServlet
{
	static final long serialVersionUID = 1000;
	public void service(HttpServletRequest req, HttpServletResponse res)
       throws ServletException, IOException
	{

        // prepare for writing
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        //Make sure local app is initialized
        String initErr = Surveyor_Application.check_init(req.getContextPath());
        if(initErr != null)
        {
            out.println( initErr +"<p> Servlet called: Preface Loader </p>" + Surveyor_Application.initErrorHtmlFoot);
            WISE_Application.log_error("WISE Surveyor Init Error: "+initErr, null);//should write to file if no email
            return;
        }

        out.println("<table border=0>");

        //get the survey name and study ID
        String study_id = (String) req.getParameter("SID");
        if(study_id == null )
        {
            out.println("<tr><td align=center>PREFACE LOADER ERROR: can't get the preface name or study id from URL</td></tr></table>");
            return;
        }

        //get the study space
        Study_Space study_space = Study_Space.get_Space(study_id);
        if(study_space == null)
        {
            out.println("<tr><td align=center>SURVEY LOADER ERROR: can't create study space</td></tr></table>");
            return;
        }

        //get the preface
        if(study_space.load_preface())
          out.println("<tr><td align=center>The preface has been successfully loaded for the study space.<td></tr>");
        else
          out.println("<tr><td align=center>Failed to load the preface for the study space.<td></tr>");
        out.println("</table>");
        return;
	}

}

