package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.User_DB_Connection;

//Sample request expected ?request_type=DELETE&table_name=repeat_set_project&instance_name=hi&invitee_id=31
public class RepeatingItemHttpHandler extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String DELETE_REQUEST = "DELETE";
    public static final String REQUEST_TYPE = "request_type";
    public static final String TABLE_NAME = "table_name";
    public static final String INSTANCE_NAME = "instance_name";
    public static final String INVITEE_ID = "invitee_id";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	res.setContentType("text");
	PrintWriter out = res.getWriter();

	if (!BrowserRequestChecker.checkRequest(req, res, out)) {
	    out.close();
	    return;
	}

	User user = BrowserRequestChecker.getUserFromSession(req, res, out);

	if (user == null) {
	    out.close();
	    return;
	}

	String requestType = req.getParameter(REQUEST_TYPE);

	if (DELETE_REQUEST.equalsIgnoreCase(requestType)) {

	    String inviteeId = req.getParameter(INVITEE_ID);
	    String tableName = req.getParameter(TABLE_NAME);
	    String instanceName = req.getParameter(INSTANCE_NAME);

	    // get database connection
	    User_DB_Connection userDbConnection = user.getMyDataBank();

	    if (userDbConnection.deleteRowFromTable(inviteeId, tableName,
		    instanceName)) {
		out.println(SUCCESS);
	    } else {
		out.println(FAILURE);
	    }

	} else {
	    out.println("Please specify a request type");
	    return;
	}

	out.close();

    }

}
