package edu.ucla.wise.client;

//package ucla.merg.LOFTS;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import edu.ucla.wise.commons.Data_Bank;
import edu.ucla.wise.commons.Study_Space;

/**
 * processes the results of a question and determines where to go next
 */

public class AppStyleRender extends HttpServlet {

    private static final long serialVersionUID = 1L;
    Logger log = Logger.getLogger(AppStyleRender.class);

    /** Process the quiz request */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	String css_name = request.getParameter("css");
	String app_name = request.getParameter("app");

	log.info("Fetching css for " + app_name + " for file " + css_name);

	if (Strings.isNullOrEmpty(app_name)) {
	    log.error("App name is null or empty");
	    return;
	}

	HttpSession session = request.getSession(true);
	// if session is new, then show the session expired info
	if (session.isNew()) {
	    log.info("Session is new");
	    return;
	}

	Study_Space study_space = (Study_Space) session
		.getAttribute("STUDYSPACE");

	if (study_space == null) {
	    log.info("Study space name is null");
	    return;
	}

	Data_Bank db = study_space.getDB();
	InputStream cssStream = null;
	int buffer_size = 2 << 12;// 16kb buffer
	byte[] byte_buffer;

	try {
	    cssStream = db.getFileFromDatabase(css_name, app_name);
	    if (cssStream != null) {
		response.reset();
		response.setContentType("text/css;charset=UTF-8");

		int count = 1;// initializing to a value > 0
		while (count > 0) {
		    byte_buffer = new byte[buffer_size];
		    count = cssStream.read(byte_buffer, 0, buffer_size);
		    response.getOutputStream().write(byte_buffer, 0,
			    buffer_size);
		}
		response.getOutputStream().flush();
	    } else {
		return;
	    }
	} catch (IOException e) {
	    log.error("File not found", e);
	}




    }

}
