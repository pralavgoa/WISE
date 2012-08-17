package edu.ucla.wise.client.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.WISE_Application;


/*
Load a new survey and set up its Data tables. 
(Called via URL request from load.jsp in the admin application)
*/

public class reload extends HttpServlet
{
	static final long serialVersionUID = 1000;
	public void service(HttpServletRequest req, HttpServletResponse res)
       throws ServletException, IOException
	{
        // prepare for writing
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        //Make sure local app is initialized
        String initErr = Surveyor_Application.force_init(req.getContextPath());
        if(initErr != null)
        {
            out.println( initErr +"<p> Servlet called: Application Reloader </p>" + Surveyor_Application.initErrorHtmlFoot);
            WISE_Application.log_error("WISE Surveyor Init Error: "+initErr, null);//should write to file if no email
        }
        else
        {
        	out.println("<table border=0>");
            out.println("<tr><td align=center>SURVEY Application Reload succeeded.</td></tr></table>");
        }
	}

}

