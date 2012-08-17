package edu.ucla.wise.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.WiseConstants;

/*
 create web page to select who to invite to what
 */

public class logout extends HttpServlet implements SingleThreadModel {
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(true);

		// prepare for writing
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();

		session.invalidate();
		res.sendRedirect(req.getContextPath() + "/" + WiseConstants.ADMIN_APP
				+ "/index.html");
	}
}
