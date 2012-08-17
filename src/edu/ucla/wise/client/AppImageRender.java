package edu.ucla.wise.client;

//package ucla.merg.LOFTS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.CommonUtils;
import edu.ucla.wise.commons.WISE_Application;

/**
 * processes the results of a question and determines where to go next
 */

public class AppImageRender extends HttpServlet {

	Logger log = Logger.getLogger(AppImageRender.class);

	/** Process the quiz request */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String app_name = request.getParameter("app");
		String image_name = request.getParameter("img");
		if (!CommonUtils.isEmpty(app_name)) {
			image_name = app_name + System.getProperty("file.separator")
					+ image_name;
		}

		int len = 2 << 16;// imgLen.length();
		byte[] rb = new byte[len];

		// Retrieve the image from the correct directory
		String path_to_images = WISE_Application.images_path;
		// check if (path_to_images + project_name + image_name) exists if not
		// jjust retrieve image_name
		// uploaded images are projname + _ + image_name
		String path_with_study_name = path_to_images
				+ System.getProperty("file.separator") + image_name;
		InputStream imgStream = null;
		try {
			imgStream = CommonUtils.loadResource(path_with_study_name);
			if (imgStream == null) {
				// trying to load the file, will 100% fail!
				imgStream = new FileInputStream(path_with_study_name);
			}
			imgStream.read(rb, 0, len);
			response.reset();
			response.setContentType("image/jpg");
			response.getOutputStream().write(rb, 0, len);
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("File not found", e);
		} finally {
			if (imgStream != null) {
				imgStream.close();
			}
		}
	}

}
