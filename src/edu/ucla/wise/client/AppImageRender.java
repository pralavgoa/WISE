package edu.ucla.wise.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
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
    Logger log = Logger.getLogger(AppImageRender.class);

    /** Process the quiz request */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	String app_name = request.getParameter("app");
	if (app_name == null)
	    app_name = "";
	String image_name = request.getParameter("img");
	/*
	 * not using app name if (!CommonUtils.isEmpty(app_name)) { image_name =
	 * app_name + System.getProperty("file.separator") + image_name; }
	 */
	int buffer_size = 2 << 12;// 16kb buffer
	byte[] byte_buffer = new byte[buffer_size];

	response.setContentType("text/html");

	HttpSession session = request.getSession(true);
	// if session is new, then show the session expired info
	if (session.isNew()) {
	    getImageFromFileSystem(response, image_name, app_name);
	    return;
	}
	Study_Space study_space = (Study_Space) session
		.getAttribute("STUDYSPACE");
	if (study_space == null) {
	    // retrieve image from directory [duplicated code]
	    WISE_Application.log_info("Fetching image from file system");
	    getImageFromFileSystem(response, image_name, app_name);
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
		response.setContentType("image/jpg");
		int count = 1;// initializing to a value > 0
		while (count > 0) {
		    count = imageStream.read(byte_buffer, 0, buffer_size);
		    response.getOutputStream().write(byte_buffer, 0,
			    buffer_size);
		}
		response.getOutputStream().flush();
	    } else {
		WISE_Application.log_info("Fetching image from file system");
		getImageFromFileSystem(response, image_name, app_name);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    log.error("File not found", e);
	} finally {
	    if (imageStream != null) {
		imageStream.close();
	    }
	}
	/********************************************************/
	/** Original non database code **/
	/*
	 * // Retrieve the image from the correct directory String
	 * path_to_images = WISE_Application.images_path; // check if
	 * (path_to_images + project_name + image_name) exists if not // jjust
	 * retrieve image_name // uploaded images are projname + _ + image_name
	 * String path_with_study_name = path_to_images +
	 * System.getProperty("file.separator") + image_name; InputStream
	 * imgStream = null; try { imgStream =
	 * CommonUtils.loadResource(path_with_study_name); if (imgStream ==
	 * null) { // trying to load the file, will 100% fail! imgStream = new
	 * FileInputStream(path_with_study_name); } imgStream.read(rb, 0, len);
	 * response.reset(); response.setContentType("image/jpg");
	 * response.getOutputStream().write(rb, 0, len);
	 * response.getOutputStream().flush(); } catch (IOException e) {
	 * log.error("File not found", e); } finally { if (imgStream != null) {
	 * imgStream.close(); }
	 * 
	 * }
	 */// end of original non database code
    }

    public void getImageFromFileSystem(HttpServletResponse response,
	    String image_name, String appName) {
	int buffer_size = 2 << 12;// 16kb buffer
	byte[] byte_buffer = new byte[buffer_size];

	InputStream imageStream = null;
	try {
	    // Retrieve the image from the correct directory
	    String path_to_images = WISE_Application.images_path;
	    // check if (path_to_images + project_name + image_name) exists if
	    // not
	    // jjust retrieve image_name
	    // uploaded images are projname + _ + image_name

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
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

    }
}
