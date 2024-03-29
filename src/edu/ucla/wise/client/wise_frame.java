package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 Produces the inner survey frameset -- with appropriately-localized servlet refs
 */

public class wise_frame extends HttpServlet {
    static final long serialVersionUID = 1000;
    static final String html = "<html><head><title>Web-based Interactive Survey Environment (WISE)</title></head>"
	    + "<frameset cols='140,*' frameborder='NO' border='0' framespacing='0' rows='*'>"
	    +
	    // "	<frame name='instruct' noresize src='progress'>" +
	    "	<frame name='form' src='setup_survey'>"
	    + "</frameset>"
	    + "<noframes><body bgcolor='#FFFFFF' text='#000000'>"
	    + "WISE requires frames. Please use a browser that can support frames.</body></noframes>"
	    + "</html>";

    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	out.println(html);
	out.close();
    }

}
