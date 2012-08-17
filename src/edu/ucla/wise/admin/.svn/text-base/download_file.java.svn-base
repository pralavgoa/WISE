package edu.ucla.wise.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.FileExtensions;

public class download_file extends HttpServlet {
	static final long serialVersionUID = 1000;

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession(true);

		// check if the session is still valid
		AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");
		// if the session is invalid
		// String path=req.getContextPath();
		// get the download file name from the request
		String filename = req.getParameter("fileName");
		// if the session is invalid, display the error
		if (admin_info == null) {
			out.println("Wise Admin - Download function can't ID you as a valid admin");
			return;
		}
		if (filename == null) {
			out.println("Wise Admin - File download Error: Can't get the file name");
			return;
		}

		String outputStr = null;
		String fileExt = null;

		try {
			String filepath = "";
			// if the file is the stylesheet
			if (filename.indexOf(".css") != -1) {
				if (filename.equalsIgnoreCase("print.css"))
					filepath = admin_info.study_css_path; // print.css
				else
					filepath = admin_info.study_css_path; // style.css
				fileExt = FileExtensions.css.name();
				outputStr = admin_info.buildXmlCssSql(filepath, filename);
			}
			// if the file is the database backup
			else if (filename.indexOf(".sql") != -1) {
				filepath = admin_info.db_backup_path; // dbase mysqldump file
				fileExt = FileExtensions.sql.name();
				outputStr = admin_info.buildXmlCssSql(filepath, filename);
			}
			// if the file is the csv file (MS Excel)
			else if (filename.indexOf(".csv") != -1) {
				// create the csv file (could be either the survey data or the
				// invitee list)
				outputStr = admin_info.buildCsvString(filename);
				fileExt = FileExtensions.csv.name();
			}
			// for else, the file should be the xml file (survey, message,
			// preface etc.)
			else {
				// the file should be the xml file (survey, message, preface
				// etc.)
				filepath = admin_info.study_xml_path; // xml file
				fileExt = FileExtensions.xml.name();
				outputStr = admin_info.buildXmlCssSql(filepath, filename);
			}

			res.setContentType("text/" + fileExt);
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			out.write(outputStr);
		} catch (Exception e) {
			out.println("Wise Admin - View Survey Error: " + e);
		}
		out.close();
	}

}
