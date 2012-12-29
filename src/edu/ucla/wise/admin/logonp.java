package edu.ucla.wise.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.ucla.wise.admin.healthmon.HealthMonitoringManager;
import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.WiseConstants;

/*
 logon to the wise admin system, set up admin info in the admin user's session
 */

public class logonp extends HttpServlet {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 1L;
    Logger log = Logger.getLogger(this.getClass());

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	res.setContentType("text/html");

	String path = req.getContextPath();

	log.debug("Context path is " + path);

	String userName = req.getParameter("username");

	log.debug("username is " + userName);

	if (userName != null && !userName.equalsIgnoreCase("")) {
	    userName = userName.toLowerCase();
	} else {
	    log.error("Empty UserName field:" + userName);
	    res.sendRedirect(path + WiseConstants.ADMIN_APP + "/error.htm");
	}

	String password = req.getParameter("password");
	HttpSession session = req.getSession(true);

	log.debug("Initializing admin info class");
	// Initialize AdminInfo class (application)
	AdminInfo.check_init(path);
	log.debug("AdminInfo object initialized");

	try {
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
	} catch (IllegalArgumentException e) {
	    log.error("Could not get the parameters for study space "
		    + userName);
	    res.sendRedirect(path + "/" + WiseConstants.ADMIN_APP
		    + "/error.htm");
	}
    }

}
