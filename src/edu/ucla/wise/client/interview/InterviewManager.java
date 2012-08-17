/**
 * 
 */
package edu.ucla.wise.client.interview;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.Interviewer;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.WISE_Application;

/**
 * This class represents functionality around Interviewer. For. ex.
 * Add/Modify/Get/Delete an interviewer. This is a singleton class.
 * 
 * @author ssakdeo
 * @author dbell
 */
public class InterviewManager {

	private static InterviewManager interviewManager = null;
	private static Logger log = Logger.getLogger(InterviewManager.class);

	private InterviewManager() {
	}

	/**
	 * 
	 * @return a singleton instance of {@link InterviewManager}
	 */
	public synchronized static InterviewManager getInstance() {
		if (interviewManager == null) {
			interviewManager = new InterviewManager();
		}
		return interviewManager;
	}

	/**
	 * This function get Maximum ID that can be assigned to new
	 * {@link Interviewer}.
	 * 
	 * @param study_space
	 * @return id string maximum ID in the database.
	 */
	public synchronized String get_newid(Study_Space study_space) {
		String id = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = study_space.getDBConnection();
			statement = conn.createStatement();

			String sql = "SELECT MAX(id) from interviewer";
			statement.execute(sql);
			ResultSet rs = statement.getResultSet();
			if (rs.next())
				id = Integer.toString(rs.getInt(1) + 1);
		} catch (SQLException e) {
			AdminInfo.log_error("GET NEW INTERVIEWER ID:" + e.toString(), e);
			log.error("SQL Error getting new ID", e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
		}
		return id;
	}

	/**
	 * Add a new interviewer by creating a new record in the interviewer table.
	 * 
	 * @return id of the newly added interviewer
	 */
	public synchronized String add_interviewer(Study_Space study_space,
			Interviewer interviewer) {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		String sql = null;
		String returnId = null;

		try {
			conn = study_space.getDBConnection();
			statement = conn.createStatement();
			// Inserted into database
			sql = "insert into interviewer(username, firstname, lastname, salutation, email, submittime)"
					+ " values('"
					+ interviewer.getUser_name()
					+ "','"
					+ interviewer.getFirst_name()
					+ "','"
					+ interviewer.getLast_name()
					+ "','"
					+ interviewer.getSalutation()
					+ "','"
					+ interviewer.getEmail() + "', now())";
			statement.execute(sql);

			// Now get the ID of the last inserted value, this needs the method
			// to be synchronized
			sql = "SELECT LAST_INSERT_ID() from interviewer";
			statement.execute(sql);
			rs = statement.getResultSet();
			if (rs != null && rs.next())
				returnId = rs.getString(1);

		} catch (SQLException e) {
			AdminInfo.log_error("Add interviewer ID:" + e.toString(), e);
			log.error("SQL Error adding new ID", e);
			return null;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
		}
		return returnId;
	}

	/**
	 * Update the profile of the interviewer
	 * 
	 * @return id of the updated interviewer
	 */
	public String save_profile(Study_Space studySpace, Interviewer interviewer) {

		Connection conn = null;
		Statement statement = null;
		String sql = null;

		try {
			conn = studySpace.getDBConnection();
			statement = conn.createStatement();
			sql = "UPDATE interviewer SET username='"
					+ interviewer.getUser_name() + "', firstname='"
					+ interviewer.getFirst_name() + "', lastname='"
					+ interviewer.getLast_name() + "', salutation='"
					+ interviewer.getSalutation() + "', email='"
					+ interviewer.getEmail() + "' WHERE id = "
					+ interviewer.getId();
			statement.execute(sql);

		} catch (SQLException e) {
			AdminInfo.log_error("GET NEW INTERVIEWER ID:" + e.toString(), e);
			log.error("SQL Error updating new ID", e);
			return null;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
		}
		return interviewer.getId();
	}

	/**
	 * Search by interviewer ID to assign the attributes
	 * 
	 * @return Interviewer object
	 */
	public Interviewer getInterviewer(Study_Space study_space,
			String interview_id) {
		Interviewer interviewer = new Interviewer(study_space);
		Connection conn = null;
		Statement statement = null;
		String sql;

		try {
			// connect to the database
			conn = study_space.getDBConnection();
			statement = conn.createStatement();
			sql = "select id, username, firstname, lastname, salutation, email, submittime from interviewer where id='"
					+ interview_id + "'";
			statement.execute(sql);
			ResultSet rs = statement.getResultSet();

			if (rs.wasNull()) {
				return null;
			}

			if (rs.next()) {
				interviewer.setId(rs.getString("id"));
				interviewer.setUser_name(rs.getString("username"));
				interviewer.setFirst_name(rs.getString("firstname"));
				interviewer.setLast_name(rs.getString("lastname"));
				interviewer.setSalutation(rs.getString("salutation"));
				interviewer.setEmail(rs.getString("email"));
				interviewer.setLogin_time(rs.getString("submittime"));
			}

		} catch (SQLException e) {
			AdminInfo.log_error("GET NEW INTERVIEWER ID:" + e.toString(), e);
			log.error("SQL Error getting new ID", e);
			return null;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("SQL Statement failure", e);
				}
			}
		}

		return interviewer;
	}
}
