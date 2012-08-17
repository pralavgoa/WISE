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


/*
View the survey page (without data fill in) by page (viewed by admin)
*/

public class view_form extends HttpServlet
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
            out.println( initErr +"<p> Servlet called: View Form </p>" + Surveyor_Application.initErrorHtmlFoot);
            WISE_Application.log_error("WISE Surveyor Init Error: "+initErr, null);//should write to file if no email
            return;
        }

        HttpSession session = req.getSession(true);
        
        Surveyor_Application sa = (Surveyor_Application)session.getAttribute("SurveyorInst");

        String study_id, survey_id;
        
        //check if it is the first link
        String a = req.getParameter("a");
        //create session info from the first URL link
        if (a!=null && a.equalsIgnoreCase("FIRSTPAGE"))
        {
            //get the study id
            study_id = (String) req.getParameter("SID");
            //get the survey id
            survey_id = (String) req.getParameter("s");
            
            //get the current study space
            Study_Space ss = Study_Space.get_Space(study_id);
            if(ss==null)
            {
                out.println("<p>ADMIN VIEW FORM Error: can't get the study space ID "+study_id+".</p>");
                return;              
            }
            //set the study space in the session
            session.setAttribute("STUDYSPACE", ss);
            
            //get the current survey
            Survey sy = ss.get_Survey(survey_id);
            if(sy==null)
            {
                out.println("<p>ADMIN VIEW FORM Error: can't get the survey ID "+survey_id+".</p>");
                return;              
            }
           //set the survey in the session
            session.setAttribute("SURVEY", sy);
            
            //set the first page id
            String page_id = sy.pages[0].id;
            //set the page id in the session as the current page id
            session.setAttribute("PAGEID", page_id);
            //call itself to display the page
            res.sendRedirect("admin_view_form");
        }
        else
        {
            //get the survey from the session
            Survey survey = (Survey) session.getAttribute("SURVEY");
            //get the page id from the session
            String pageid = (String) session.getAttribute("PAGEID");
            //get the study space from the session
            Study_Space study_space = (Study_Space) session.getAttribute("STUDYSPACE");            
            if(survey==null || study_space==null || pageid==null)
            {
                out.println("<p>ADMIN VIEW FORM Error: Your session has expired; please go back and resume from the admin page.</p>");
                return;              
            }
                      
            //get the current page
            Page pg= survey.get_page(pageid);
            //update the page id to be the next page
            session.removeAttribute("PAGEID");
            if(!survey.is_last_page(pageid))
              session.setAttribute("PAGEID", survey.next_page(pageid).id);         
            
            //display the current page
            //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            String admin_page = pg.render_admin_page(study_space);
            if( (admin_page.contains("#SHAREDFILEURL#") == true) ||
            		(admin_page.contains("#SHAREDIMAGEURL#") == true) )
            {
                //admin_page.replaceAll("#SHAREDFILEURL#", sa.shared_file_url);
                admin_page.replaceAll("#SHAREDFILEURL#", Surveyor_Application.shared_file_url);
                
                //admin_page.replaceAll("#SHAREDIMAGEURL#", sa.shared_image_url);
                admin_page.replaceAll("#SHAREDIMAGEURL#", Surveyor_Application.shared_image_url);
            }

            //out.println(pg.render_admin_page(study_space));
            out.println(admin_page);
        }
        out.close();
	}

}

