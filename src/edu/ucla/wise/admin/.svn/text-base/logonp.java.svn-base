package edu.ucla.wise.admin;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import edu.ucla.wise.admin.healthmon.HealthMonitoringManager;
import edu.ucla.wise.commons.*;

/*
 logon to the wise admin system, set up admin info in the admin user's session
 */

public class logonp extends HttpServlet {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(this.getClass());

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		res.setContentType("text/html");

		String path = req.getContextPath();
		String userName = req.getParameter("username");

		if (userName != null && !userName.equalsIgnoreCase("")) {
			userName = userName.toLowerCase();
		} else {
			log.error("Empty UserName field:" + userName);
			res.sendRedirect(path + WiseConstants.ADMIN_APP + "/error.htm");
		}

		String password = req.getParameter("password");
		HttpSession session = req.getSession(true);

		// Initialize AdminInfo class (application)
		AdminInfo.check_init(path);

		// initialize AdminInfo instance and store in session
		AdminInfo admin_info = new AdminInfo(userName, password);
		HealthMonitoringManager.monitor(admin_info);

		// verify the username and password
		if (!admin_info.pw_valid) {
			log.error("Incorrect input: Username or password was entered wrong.");
			res.sendRedirect(path + "/" + WiseConstants.ADMIN_APP
					+ "/error.htm");
		} else {

			session.setAttribute("ADMIN_INFO", admin_info);
			// send HTTP request to create study space and admin user
			res.sendRedirect(path + "/" + WiseConstants.ADMIN_APP + "/tool.jsp");
			log.info("Login Success!");
		}
	}

}
