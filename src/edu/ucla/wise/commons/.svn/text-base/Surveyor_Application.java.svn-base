package edu.ucla.wise.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.wise.client.healthmon.SurveyHealth;

/* Class to represent common elements for *this* local installation of the wise surveyor java application
 *   NOT the possibly-remote surveyor information for a given Study_Space
 * Never instantiated; just initialize static variables */

public class Surveyor_Application extends WISE_Application {

	public static final String shared_file_url_ref = "#SHAREDFILEURL#";
	public static final String shared_image_url_ref = "#SHAREDIMAGEURL#";


	// 08-Nov-2009
	// Pushed down into the children classes like Surveyor_Application
	// and AdminInfo as static in the children
	// Make Surveyor_Application a singleton class per JBOSS
	public static String ApplicationName = "WISE";

	public static String shared_file_url;
	public static String shared_image_url;
	// public String ApplicationName;
	public static String servlet_url;

	// Default of the following is declared in Wise_Application but can override
	// for a given Surveyor app if necessary
	// public static final String html_ext = ".htm";

	public static void initStaticFields(String appContext) {
		if (ApplicationName == null) {
			Surveyor_Application.ApplicationName = appContext;
		}
		shared_file_url = WISE_Application.rootURL + "/"
				+ Surveyor_Application.ApplicationName + "/"
				+ WISE_Application.shared_files_link + "/";
		shared_image_url = Surveyor_Application.shared_file_url + "images/";
		servlet_url = WISE_Application.rootURL + "/"
				+ Surveyor_Application.ApplicationName + "/" + "survey" + "/";
	}

	public static String check_init(String appContext) throws IOException {
		if ((Surveyor_Application.ApplicationName == null)
				|| (Surveyor_Application.servlet_url == null)
				|| (Surveyor_Application.shared_file_url == null)) {
			String initErr = null;
			// Surveyor_Application.ApplicationName = appContext;

			WISE_Application.initialize();
			Surveyor_Application.initStaticFields(appContext);

			if (Surveyor_Application.ApplicationName == null)
			// *still* null means uninitialized
			{
				initErr = "Wise Surveyor Application -- uncaught initialization error";
			}
			return initErr;
		} else
			return null;
		/*
		 * String retVal = initErrorHtmlHead+"<P>"+ appContext +"</p>"; return
		 * retVal; } else {
		 */
	}

	// else
	// {
	// Surveyor_Application newApp = new Surveyor_Application(appContext);
	// /*
	// String initErr = WISE_Application.initialize(appContext);
	// newApp.ApplicationName = appContext;
	// if(newApp.ApplicationName == null)
	// {
	// initErr = "Wise Surveyor Application -- uncaught initialization error";
	// }
	// else
	// {
	// initErr = initErrorHtmlHead+"<P>"+ initErr +"</p>";
	// }
	// //Manoj
	// newApp.servlet_url = rootURL + newApp.ApplicationName + "/";
	// //Manoj
	// newApp.shared_file_url = rootURL + newApp.ApplicationName + "/" +
	// localProps.getProperty("default.sharedFiles_linkName") + "/";
	// System.out.println("Manoj:Shared File URL is initialised to [" +
	// newApp.shared_file_url + "]");
	// newApp.shared_image_url = newApp.shared_file_url + "images/";
	// */
	// WISE_Application.AppInstanceTbl.put(appContext, newApp);
	// String initErr = newApp.ApplicationName;
	// if(newApp.ApplicationName == null)
	// {
	// //initErr +=
	// "Wise Surveyor Application -- uncaught initialization error";
	// initErr += initErrorHtmlHead+"<P>"+ initErr +"</p>";
	// }
	// else
	// {
	// initErr = null;
	// }
	//
	// return initErr;
	// //String initErr = initialize(appContext);
	// }
	//
	// if()
	// return null;
	/*
	 * String initErr = null; if (ApplicationName == null) initErr =
	 * initialize(appContext); if (ApplicationName == null) // *still* null
	 * means uninitialized initErr =
	 * "Wise Surveyor Application -- uncaught initialization error"; if (initErr
	 * != null) //wrap error message, leaving page open for calling servlet to
	 * add info initErr = initErrorHtmlHead+"<P>"+ initErr +"</p>"; return
	 * initErr;
	 */
	// }

	public static String force_init(String appContext) throws IOException {
		String initErr = null;

		WISE_Application.initialize();
		Surveyor_Application.initStaticFields(appContext);
		// Surveyor_Application insApp =
		// WISE_Application.AppInstanceTbl.get(appContext);
		if (Surveyor_Application.ApplicationName == null) // *still* null means
															// uninitialized
			initErr = "Wise Surveyor Application -- uncaught initialization error";
		return initErr;
	}

	// This won't be called probably
	public Surveyor_Application(String appContext) throws IOException {

		String initErr = WISE_Application.initialize();
		Surveyor_Application.initStaticFields(appContext);
		//
		//
		this.ApplicationName = appContext;
		if (this.ApplicationName == null) {
			initErr = "Wise Surveyor Application -- uncaught initialization error";
		} else {
			initErr = initErrorHtmlHead + "<P>" + initErr + "</p>";
		}
		// Manoj
		// WISE_Application.servlet_url = rootURL + this.ApplicationName + "/";
		// Manoj
		// WISE_Application.shared_file_url = rootURL +
		// WISE_Application.ApplicationName + "/" +
		// localProps.getProperty("default.sharedFiles_linkName") + "/";
		Surveyor_Application.shared_image_url = Surveyor_Application.shared_file_url
				+ "images/";
		//
		//

	}

	/*
	 * public static String initialize(String appContext) { String initErr =
	 * WISE_Application.initialize(appContext); //Manoj servlet_url = rootURL +
	 * ApplicationName + "/"; //Manoj shared_file_url = rootURL +
	 * ApplicationName + "/" +
	 * localProps.getProperty("default.sharedFiles_linkName") + "/";
	 * System.out.println("Manoj:Shared File URL is initialised to [" +
	 * shared_file_url + "]"); shared_image_url = shared_file_url + "images/";
	 * return initErr; }
	 */

	public static String initErrorHtmlHead = "<HTML><HEAD><TITLE>WISE survey system -- Startup error</TITLE>"
			+ "<LINK href='" + Surveyor_Application.shared_file_url
			+ "style.css' type=text/css rel=stylesheet>"
			+ "<body text=#000000 bgColor=#ffffcc><center><table>"
			+ "<tr><td>Sorry, the WISE Surveyor application failed to initialize. "
			+ "Please contact the system administrator with the following information.";
	public static String initErrorHtmlFoot = "</td></tr></table></center></body></html>";

}
