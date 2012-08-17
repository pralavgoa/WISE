package edu.ucla.wise.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oreilly.servlet.MultipartRequest;

import edu.ucla.wise.commons.AdminInfo;
import edu.ucla.wise.commons.CommonUtils;
import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.commons.WiseConstants;

public class load_data extends HttpServlet {

	private AdminInfo admin_info = null;

	// update the survey information in the database when uploading the survey
	// xml file
	private String process_survey_file(Document doc, PrintWriter out,
			Statement stmt) throws SQLException {
		NodeList nodelist;
		Node n, nodeOne;
		NamedNodeMap nnm;

		String id, title;
		String sql;
		boolean dbtype;
		String return_val;

		try {
			// parse the survey node
			nodelist = doc.getElementsByTagName("Survey");
			n = nodelist.item(0);
			nnm = n.getAttributes();
			// get the survey attributes
			id = nnm.getNamedItem("ID").getNodeValue();
			title = nnm.getNamedItem("Title").getNodeValue();
			nodeOne = nnm.getNamedItem("Version");
			if (nodeOne != null)
				title = title + " (v" + nodeOne.getNodeValue() + ")";
			// get the latest survey's internal ID from the table of surveys
			sql = "select max(internal_id) from surveys where id = '" + id
					+ "'";
			dbtype = stmt.execute(sql);
			ResultSet rs = stmt.getResultSet();
			boolean isn = rs.next();
			String max_id = rs.getString(1);
			// initiate the survey status as "N"
			String status = "N";
			// display processing information
			out.println("<table border=0><tr><td align=center>Processing a SURVEY (ID = "
					+ id + ")</td></tr>");
			// get the latest survey's status
			if (max_id != null) {
				sql = "select status from surveys where internal_id = "
						+ max_id;
				dbtype = stmt.execute(sql);
				rs = stmt.getResultSet();
				isn = rs.next();
				status = (rs.getString(1)).toUpperCase();
			}
			// if the survey status is in Developing or Production mode
			// NOTE this just sets up survey info in surveys table; actual read
			// of survey is handled by the Surveyor application
			if (status.equalsIgnoreCase("D") || status.equalsIgnoreCase("P")) {
				// display the processing situation about the status
				out.println("<tr><td align=center>Existing survey is in "
						+ status + " mode. </td></tr>");
				// insert a new survey record
				sql = "INSERT INTO surveys (id, title, status, archive_date) VALUES ('"
						+ id
						+ "',\""
						+ title
						+ "\", '"
						+ status
						+ "', 'current')";
				dbtype = stmt.execute(sql);
				// get the new inserted internal ID
				// sql = "SELECT LAST_INSERT_ID() from surveys";
				sql = "SELECT max(internal_id) from surveys";
				dbtype = stmt.execute(sql);
				rs = stmt.getResultSet();
				rs.next();
				String new_id = rs.getString(1);
				// use the newly created internal ID to name the file
				String filename = "file" + new_id + ".xml";
				// update the file name and uploading time in the table
				sql = "UPDATE surveys SET filename = '" + filename
						+ "', uploaded = now() WHERE internal_id = " + new_id;
				dbtype = stmt.execute(sql);
				// display the processing information about the file name
				out.println("<tr><td align=center>New version becomes the one with internal ID = "
						+ id + "</td></tr>");
				return_val = filename;
			}
			// if the survey status is in Removed or Closed mode. Or there is no
			// such survey (keep the default status as N)
			// the survey will be treated as a brand new survey with the default
			// Developing status
			else if (status.equalsIgnoreCase("N")
					|| status.equalsIgnoreCase("R")
					|| status.equalsIgnoreCase("C")) {
				out.println("<tr><td align=center>This is a NEW Survey.  Adding a new survey into DEVELOPMENT mode...</td></tr>");
				// insert the new survey record
				sql = "INSERT INTO surveys (id, title, status, archive_date) VALUES ('"
						+ id + "',\"" + title + "\",'D','current')";
				dbtype = stmt.execute(sql);
				// get the newly created internal ID
				// sql = "SELECT LAST_INSERT_ID()";
				sql = "SELECT max(internal_id) from surveys";
				dbtype = stmt.execute(sql);
				rs = stmt.getResultSet();
				rs.next();
				String new_id = rs.getString(1);
				String filename = "file" + new_id + ".xml";
				// update the file name and uploading time
				sql = "UPDATE surveys SET filename = '" + filename
						+ "', uploaded = now() WHERE internal_id = " + new_id;
				dbtype = stmt.execute(sql);
				out.println("<tr><td align=center>New version becomes the one with internal ID = "
						+ id + "</td></tr>");
				return_val = filename;
			} else {
				out.println("<tr><td align=center>ERROR!  Unknown STATUS!</td></tr>");
				out.println("<tr><td align=center>status:" + status
						+ "</td></tr>");
				return_val = "NONE";
			}
			out.println("</table>");
		} catch (Exception e) {
			AdminInfo.log_error("WISE ADMIN - PROCESS SURVEY FILE:"
					+ e.toString(), e);
			return_val = "ERROR";
		}
		return return_val;
	}

	// update the invitee information in the database when uploading the invitee
	// csv file
	// TODO: Currently, ID column should be deleted from the csv file to Handle
	// Adding
	// Invitees. In future, we want to make sure, that if ID column exists in
	// the csv file then it should be automatically handled up update if exists
	public void process_invitees_csv_file(File f, PrintWriter out,
			Statement stmt) throws SQLException {
		// declare the column array of string
		String[] col_val = new String[1000];
		int emailIndex = -999;
		try {
			// compose the sql query
			String sql = "insert into invitee(";
			// open the file reader to read the file line by line
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String oneline = new String();
			int col_numb = 0, line_count = 0;
			while (!CommonUtils.isEmpty(oneline = br.readLine())) {
				oneline = oneline.trim();
				if (oneline.length() != 0) {
					line_count++;
					// put the column names into the array
					String[] split_str = oneline.split(",");
					// at the 1st line, the size of the array is the total
					// number of the columns
					if (line_count == 1)
						col_numb = split_str.length;
					// assign the column values
					for (int i = 0, j = 0; i < split_str.length; i++, j++) {
						col_val[j] = split_str[i];
						// mark as the null string if the phrase is an empty
						// string
						if (split_str[i].length() == 0) {
							col_val[j] = "NULL";
						}
						// parse the phrase with the comma inside (has the
						// double-quotation mark)
						// this string is just part of the entire string, so
						// append with the next one
						else if (split_str[i].charAt(0) == '\"'
								&& split_str[i]
										.charAt(split_str[i].length() - 1) != '\"') {
							do {
								i++;
								col_val[j] += "," + split_str[i];
							}
							// remove the double-quotation mark at the beginnig
							// and end of the string
							while (i < split_str.length
									&& split_str[i].charAt(split_str[i]
											.length() - 1) != '\"');
							col_val[j] = col_val[j].substring(1,
									col_val[j].length() - 1);
						}
						// there could be double-quotation mark(s) (doubled by
						// csv format) inside this string
						// keep only one double-quotation mark(s)
						else if (split_str[i].charAt(0) == '\"'
								&& split_str[i]
										.charAt(split_str[i].length() - 1) == '\"') {
							if (split_str[i].indexOf("\"\"") != -1)
								col_val[j] = col_val[j]
										.replaceAll("\"\"", "\"");
						}

						// keep only one double-quotation mark(s) if there is
						// any inside the string
						if (split_str[i].indexOf("\"\"") != -1)
							col_val[j] = col_val[j].replaceAll("\"\"", "\"");

						// compose the sql query with the column values
						if (line_count == 1
								|| col_val[j].equalsIgnoreCase("null")) {
							if (col_val[j].equals("email"))
								emailIndex = j;
							sql += col_val[j] + ",";
						} else {
							if (j == emailIndex) {
								col_val[j] = "AES_ENCRYPT('"
										+ col_val[j]
										+ "','"
										+ admin_info.myStudySpace.db.email_encryption_key
										+ "')";
								sql += col_val[j] + ",";
							} else
								sql += "\"" + col_val[j] + "\",";
						}
					}
				} // end if for oneline!=null

				// compose the sql query
				if (line_count == 1)
					sql = sql.substring(0, sql.length() - 1) + ") values (";
				else
					sql = sql.substring(0, sql.length() - 1) + "),(";

			}// end while

			// delete the last "," and "("
			sql = sql.substring(0, sql.length() - 2);

			// insert into the database
			boolean dbtype = stmt.execute(sql);
			out.println("The data has been successfully uploaded and input into database");
		} catch (IOException err) {
			// catch possible io errors from readLine()
			System.out.println("CVS parsing: IOException error!");
			err.printStackTrace();
		}
	}

	// public void handle_uploaded_file(File f, JspWriter out, Statement stmt)
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException

	{
		String path = request.getContextPath() + "/" + WiseConstants.ADMIN_APP;
		String html = "<link rel=\"stylesheet\" href=\""
				+ path
				+ "/style.css\" type=\"text/css\">\n"
				+ "	<title>WISE Administration Tools - Load</title>\n"
				+ "	</head>\n"
				+ "	<body text=\"#333333\" bgcolor=\"#FFFFCC\">\n"
				+ "	<center>\n"
				+ "	<table cellpadding=\"0\" cellspacing=\"0\" border=0>\n"
				+ "		<tr>\n"
				+ "			<td width=\"160\" align=center><img src=\"admin_images/somlogo.gif\"\n"
				+ "				border=\"0\"></td>\n"
				+ "			<td width=\"400\" align=\"center\"><img src=\"admin_images/title.jpg\"\n"
				+ "				border=\"0\"></td>\n" + "		</tr>\n" + "	</table>";

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		if (session.isNew()) {
			response.sendRedirect(path + "/index.html");
			return;
		}

		// get the admin info obj
		admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");
		if (admin_info == null) {
			response.sendRedirect(path + "/error.htm");
			return;
		}

		String file_loc = admin_info.study_xml_path;
		// String xml_temp_loc = file_loc + "tmp/";
		String xml_temp_loc = CommonUtils.getAbsolutePath(file_loc);
		xml_temp_loc = System.getProperty("os.name").toLowerCase()
				.contains("window") ? xml_temp_loc.substring(1,
				xml_temp_loc.length()) : xml_temp_loc;
		System.out.println(xml_temp_loc);
		File xml_dir = new File(xml_temp_loc);
		if (!xml_dir.isDirectory())
			System.out.println("Not a directory");
		xml_temp_loc = xml_dir.getAbsolutePath()
				+ System.getProperty("file.separator");
		file_loc = xml_temp_loc;
		String css_path = admin_info.study_css_path;
		// String image_path= admin_info.study_image_path;
		String image_path = WISE_Application.images_path;
		try {
			MultipartRequest multi = new MultipartRequest(request,
					xml_temp_loc, 250 * 1024);
			File f1;
			String filename = multi.getFilesystemName("file");
			xml_temp_loc = xml_temp_loc + multi.getFilesystemName("file");
			String file_type = multi.getContentType("file");
			// out.println(file_type);

			if ((file_type.indexOf("csv") != -1)
					|| (file_type.indexOf("excel") != -1)
					|| (file_type.indexOf("plain") != -1)) {
				// open database connection
				Connection con = admin_info.getDBConnection();
				Statement stm = con.createStatement();
				out.println("<p>Processing an Invitee CSV file...</p>");
				// parse csv file and put invitees into database
				File f = multi.getFile("file");
				process_invitees_csv_file(f, out, stm);

				// delete the file
				f.delete();
				String disp_html = "<p>The CSV named " + filename
						+ " has been successfully uploaded.</p>";
				out.println(disp_html);
			} else if (file_type.indexOf("css") != -1) {
				File f = multi.getFile("file");

				if (filename.equalsIgnoreCase("print.css"))
					f1 = new File(css_path + "print.css");
				else
					f1 = new File(css_path + "style.css");

				// move the file to css_path directory
				f1.delete();
				f.renameTo(f1);
				out.println("<p>The style sheet named " + filename
						+ " has been successfully uploaded.</p>");
			} else if ((file_type.indexOf("jpg") != -1)
					|| (file_type.indexOf("jpeg") != -1)
					|| (file_type.indexOf("gif") != -1)) {
				File f = multi.getFile("file");
				String disp_html = "";
				// use it temporarily to test
				f1 = new File(CommonUtils.getAbsolutePath(image_path
						+ System.getProperty("file.separator") + filename));
				if (f1.exists() == true) {
					f1.delete();
					f.renameTo(f1);
					disp_html = "<p>The file named " + filename
							+ " has been successfully uploaded.</p>";
				} else {
					disp_html = "<p>The file named "
							+ filename
							+ " does not exist. You can only replace an existing image file.</p>";
				}
				// else
				// {
				//
				// }
				out.println(disp_html);
			} else {
				// open database connection
				Connection conn = admin_info.getDBConnection();
				Statement stmt = conn.createStatement();

				// Get parser and an XML document
				Document doc = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder()
						.parse(new FileInputStream(xml_temp_loc));

				NodeList nodelist = doc.getChildNodes();
				Node n;
				String fname = "";

				for (int i = 0; i < nodelist.getLength(); i++) {
					n = nodelist.item(i);
					if (n.getNodeName().equalsIgnoreCase("Survey")) {

						String fn = process_survey_file(doc, out, stmt);

						if (!fn.equalsIgnoreCase("NONE")) {
							File f = multi.getFile("file");
							f1 = new File(file_loc
									+ System.getProperty("file.separator") + fn);
							// f1.delete();
							if (!f.renameTo(f1)) {
								System.err.println("Renaming File changed");
								throw new Exception();
							}

							fname = fn;

							// send URL request to create study space and survey
							// in remote server 
							// commenting this out for Apache admin on same machine -- seems to be blocked
							//String remoteResult = admin_info.load_remote("survey", fname);
							//out.println(remoteResult);
							String remoteURL = admin_info.make_remoteURL("survey", fname);
							response.sendRedirect(remoteURL);
						} else {
							// delete the file
							File f = multi.getFile("file");
							f.delete();
						}

						break;
					} else if (n.getNodeName().equalsIgnoreCase("Preface")) {
						fname = "preface.xml";
						File f = multi.getFile("file");
						f1 = new File(file_loc + fname);
						f.renameTo(f1);
						String disp_html = null;
						if (admin_info.parse_message_file()) {
							disp_html = "<p>PREFACE file is uploaded with name changed to be preface.xml</p>";
						} else {
							disp_html = "<p>PREFACE file upload failed.</p>";
						}
						out.println(disp_html);
						// send URL request to create study space and survey in
						// remote server
						// commenting this out for Apache admin on same machine -- seems to be blocked
						//String remoteResult = admin_info.load_remote("preface", fname);
						//out.print(remoteResult);
						//As a workaround, passing control to the browser to call URL notifying Surveyor of preface change
						String remoteURL = admin_info.make_remoteURL("preface", fname);
						response.sendRedirect(remoteURL);
						break;
					}
				}
				stmt.close();
				conn.close();
			}// else
		}// try
		catch (Exception e) {
			WISE_Application.log_error(
					"WISE - ADMIN load_data.jsp: " + e.toString(), e);
			out.println("<h3>Invalid XML document submitted.  Please try again.</h3>");
			out.println("<p>Error: " + e.toString() + "</p>");
		}
		out.println("<p><a href= tool.jsp>Return to Administration Tools</a></p>\n"
				+ "		</center>\n" +
				// <!-- <p>AdminInfo dump:
				"		<pre>\n" +
				// file_loc [admin_info.study_xml_path]: file_loc%>
				// css_path [admin_info.study_css_path]: <%=css_path%>
				// image_path [admin_info.study_image_path]: <%=image_path%>
				"		</pre>\n" + "</p>\n" + "</body>\n" + "</html>");
	}

}

/*
 * 1/19/2012 - Fixed survey and preface upload failure bugs due to blocked HTTP calls [Doug]  
 */
