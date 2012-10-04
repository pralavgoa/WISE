package edu.ucla.wise.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.AdminInfo;

/*
 create web page to select who to invite to what
 */

public class dev2prod extends HttpServlet implements SingleThreadModel {
    public void service(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {
	// prepare to write
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();
	// get the server path
	String path = req.getContextPath();
	out.println("<html><head>");
	out.println("<link rel='stylesheet' href='" + path
		+ "/style.css' type='text/css'>");
	out.println("<title>WISE CHNAGE SURVEY MODE</title>");
	out.println("</head><body text=#333333 bgcolor=#FFFFCC>");
	out.println("<center><table cellpadding=2 cellpadding=0 cellspacing=0 border=0>");
	out.println("<tr><td>");
	HttpSession session = req.getSession(true);
	if (session.isNew()) {
	    out.println("<h2>Your session has timed out.</h2><p>");
	    out.println("<h3>Please return to the <a href='../'>admin logon page</a> and try again.</h3>");
	    out.println("</td></tr></table></center></body></html>");
	    out.close();
	    return;
	}
	AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");
	String internal_id = req.getParameter("s");
	if (admin_info == null || internal_id == null) // if session does not
						       // exists
	{
	    out.println("Wise Admin - Dev to Prod Error: Can't get the Admin Info");
	    return;
	}

	try {
	    // open database connection
	    Connection conn = admin_info.getDBConnection();
	    Statement stmt = conn.createStatement();

	    out.println("Changing status from DEVELOPMENT to PRODUCTION...<br>");

	    String sql = "SELECT id, filename, title FROM surveys WHERE internal_id = "
		    + internal_id;
	    boolean dbtype = stmt.execute(sql);
	    ResultSet rs = stmt.getResultSet();
	    rs.next();
	    String s_id = rs.getString(1);
	    String filename = rs.getString(2);
	    String title = rs.getString(3);

	    sql = "INSERT INTO surveys (id, filename, title, status) ";
	    sql += "VALUES ('" + s_id + "','" + filename + "',\"" + title
		    + "\", 'P')";
	    dbtype = stmt.execute(sql);

	    stmt.close();
	    conn.close();
	} catch (Exception e) {
	    AdminInfo.log_error(
		    "Wise Admin - Dev to Prod Error: " + e.toString(), e);
	    return;
	}

	out.println("<p><a href='../tool.jsp'>Return to Administration Tools</a>");
	out.println("</td></tr></table></center></body></html>");
	out.close();

    }

}
