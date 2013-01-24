package edu.ucla.wise.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.CommonUtils;
import edu.ucla.wise.commons.Data_Bank;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.WISE_Application;

/**
 * processes the results of a question and determines where to go next
 */

public class AppImageRender extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger(AppImageRender.class);

    /** Process the quiz request */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

	String app_name = request.getParameter("app");
	if (app_name == null)
	    app_name = "";
	String image_name = request.getParameter("img");

	int buffer_size = 2 << 12;// 16kb buffer

	byte[] byte_buffer = new byte[buffer_size];

	response.setContentType("text/html");

	HttpSession session = request.getSession(true);

	// if session is new, then show the session expired info
	if (session.isNew()) {
	    getFromFileSystem(response, image_name, app_name, true);
	    return;
	}
	Study_Space study_space = (Study_Space) session
		.getAttribute("STUDYSPACE");
	if (study_space == null) {
	    // retrieve image from directory [duplicated code]
	    WISE_Application.log_info("Fetching image from file system");
	    getFromFileSystem(response, image_name, app_name, true);
	    // P
	    // out.println("<p>Error: Can't find the user & study space.</p>");
	    return;
	}

	Data_Bank db = study_space.getDB();
	InputStream imageStream = null;
	try {
	    imageStream = db.getFileFromDatabase(image_name, app_name);
	    if (imageStream != null) {
		response.reset();
		if (image_name.contains("gif")) {
		    response.setContentType("image/gif");
		} else {
		    response.setContentType("image/jpg");
		}

		int count = 1;// initializing to a value > 0
		while (count > 0) {
		    count = imageStream.read(byte_buffer, 0, buffer_size);
		    response.getOutputStream().write(byte_buffer, 0,
			    buffer_size);
		}
		response.getOutputStream().flush();
	    } else {
		log.info("Fetching image from file system: " + image_name);
		getFromFileSystem(response, image_name, app_name, true);
	    }

	    if (imageStream != null) {
		imageStream.close();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    log.error("File not found", e);
	}
    }

    public static void getFromFileSystem(HttpServletResponse response,
	    String image_name, String appName, boolean isImage) {
	int buffer_size = 2 << 12;// 16kb buffer
	byte[] byte_buffer = new byte[buffer_size];

	InputStream imageStream = null;
	try {
	    // Retrieve the image from the correct directory
	    String path_to_images = WISE_Application.images_path;

	    String path_with_study_name;
	    if ("".equals(appName)) {
		path_with_study_name = path_to_images
			+ System.getProperty("file.separator") + image_name;
	    } else {
		path_with_study_name = path_to_images
			+ System.getProperty("file.separator") + appName
			+ System.getProperty("file.separator") + image_name;
	    }

	    imageStream = CommonUtils.loadResource(path_with_study_name);
	    if (imageStream == null) {
		// trying to load the file, will 100% fail!
		imageStream = new FileInputStream(path_with_study_name);
	    }
	    response.reset();
	    response.setContentType("image/jpg");
	    int count = 1;// initializing to a value > 0
	    while (count > 0) {
		count = imageStream.read(byte_buffer, 0, buffer_size);
		response.getOutputStream().write(byte_buffer, 0, buffer_size);
	    }
	    response.getOutputStream().flush();
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error("File not found", e);
	} finally {
	    if (imageStream != null) {
		try {
		    imageStream.close();
		} catch (IOException e) {
		    log.error(e);
		}
	    }
	}

    }
}
