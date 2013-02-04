package edu.ucla.wise.client;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.User_DB_Connection;

public class repeating_set_read_input extends HttpServlet {
    static final long serialVersionUID = 1000;

    static Logger log = Logger.getLogger(repeating_set_read_input.class);

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
 {
	try {
	// prepare for writing
	PrintWriter out;
	res.setContentType("text/html");
	out = res.getWriter();
	String initErr = Surveyor_Application.check_init(req.getContextPath());
	HttpSession session = req.getSession(true);

	if (initErr != null) {
	    out.print("FAILURE");
	    return;
	}

	// if session is new, then it must have expired since begin; show the
	// session expired info
	if (session.isNew()) {
	    res.sendRedirect(Surveyor_Application.shared_file_url + "error"
		    + Surveyor_Application.html_ext);
	    return;
	}
	// get the user from session
	User theUser = (User) session.getAttribute("USER");
	if (theUser == null || theUser.id == null) // latter signals an
						   // improperly-initialized
						   // User
	{
	    out.println("FAILURE");
	    return;
	}

	String repeat_table_name = req.getParameter("repeat_table_name");
	String repeat_table_row = req.getParameter("repeat_table_row");

	if (!Strings.isNullOrEmpty(repeat_table_row)) {
	    if ("null".equals(repeat_table_row)) {
		repeat_table_row = null;
	    }
	}

	String repeat_table_row_name = req
		.getParameter("repeat_table_row_name");
	// get all the fields values from the request and save them in the hash
	// table
	Hashtable<String, String> params = new Hashtable<String, String>();
	Hashtable<String, String> types = new Hashtable<String, String>();

	String name, value;
	Enumeration e = req.getParameterNames();
	while (e.hasMoreElements()) {
	    name = (String) e.nextElement();
	    value = req.getParameter(name);
	    if (!name.contains("repeat_table_name")
		    && !name.contains("repeat_table_row")
		    && !name.contains("repeat_table_row_name")) {
		// Parse out the proper name here

		// here split the value into its constituents
		String[] type_and_value = value.split(":::");

		if (type_and_value.length == 2) {
		    params.put(name, type_and_value[1]);
		    types.put(name, type_and_value[0]);
		} else {
		    if (type_and_value.length == 1) {
			params.put(name, "");
			types.put(name, type_and_value[0]);
		    }

		}
	    } else {
		;// do nothing
	    }
	}

	int generatedKeyValue = put_values_in_database(repeat_table_name,
		repeat_table_row,
		repeat_table_row_name, theUser, params, types);

	out.print(generatedKeyValue);
	out.flush();
	out.close();
	} catch (Exception e) {
	    log.error(e);
	}

    }

    private int put_values_in_database(String table_name, String row_id,
	    String row_name, User theUser, Hashtable<String, String> params,
	    Hashtable<String, String> param_types) {
	// get database connection
	User_DB_Connection user_db_connection = theUser.getMyDataBank();

	// send the table name and values to the database
	int insertedKeyValue = user_db_connection
		.insert_update_row_repeating_table(table_name,
		row_id, row_name, params, param_types);

	return insertedKeyValue;
    }
}
