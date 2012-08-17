package edu.ucla.wise.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.WISE_Application;

/**
 * @author Ka Cheung Sia A simple servlet to accept a hidden request from LOFTS
 *         to declare completion
 * 
 */
public class complete extends HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out;
		Statement stmt = null;
		Connection conn = null;
		res.setContentType("text/html");
		out = res.getWriter();

		String userID = req.getParameter("u");
		String surveyID = req.getParameter("si");
		String studySpaceID = req.getParameter("ss");

		if (userID != null && surveyID != null & studySpaceID != null) {
			AdminInfo.check_init(req.getContextPath());
			String user = WISE_Application.decode(userID);
			String ss = WISE_Application.decode(studySpaceID);
			Study_Space studySpace = Study_Space.get_Space(ss);

			try {
				conn = studySpace.getDBConnection();
				stmt = conn.createStatement();
				stmt.executeUpdate("update survey_user_state set state='completed', state_count=1, entry_time=now() where invitee="
						+ user + " AND survey='" + surveyID + "'");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}

			}
		}
		out.println("OK");
		out.close();
		return;

	}
}