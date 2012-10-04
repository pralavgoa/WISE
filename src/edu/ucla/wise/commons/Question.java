package edu.ucla.wise.commons;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a subclass of Page_Item and represents a question on the page
 */

public class Question extends Page_Item {
    /** Instance Variables */
    public String stem;
    public String requiredField;
    public boolean oneLine;

    /**
     * constructor: to fill out the stem of the question and parse the
     * precondition
     */
    public Question(Node n) {
	// get the attributes of page item
	super(n);
	try {
	    // if there is a translation node, display the translated stem
	    if (this.translation_id != null) {
		stem = question_translated.stem;
	    }
	    // otherwise, display the stem transformed through jaxp parser
	    else {
		NodeList nodelist = n.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
		    if (nodelist.item(i).getNodeName().equalsIgnoreCase("Stem")) {
			Node node = nodelist.item(i);
			Transformer transformer = TransformerFactory
				.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(node),
				new StreamResult(sw));
			stem = sw.toString();
		    }
		}
	    }
	    // //parse the precondition
	    // NodeList nodelist = n.getChildNodes();
	    // for (int i=0; i < nodelist.getLength();i++)
	    // {
	    // if
	    // (nodelist.item(i).getNodeName().equalsIgnoreCase("Precondition"))
	    // {
	    // //hasPrecondition = true;
	    // //create the condition object
	    // cond = new Condition(nodelist.item(i));
	    // }
	    // }
	    // assign other attributes
	    NamedNodeMap nnm = n.getAttributes();
	    // if the question has the required field to fillup
	    Node n1 = nnm.getNamedItem("requiredField");
	    if (n1 != null)
		requiredField = n1.getNodeValue();
	    else
		requiredField = "false";
	    // if the question requests one-line presence/layout
	    n1 = nnm.getNamedItem("oneLine");
	    if (n1 != null)
		oneLine = new Boolean(n1.getNodeValue()).booleanValue();
	    else
		oneLine = false;
	} catch (Exception e) {
	    WISE_Application
		    .log_error("WISE - QUESTION: " + e.toString(), null);
	    return;
	}
    }

    /** Default field count for question */
    public int countFields() {
	return 1;
    }

    public String[] listFieldNames() {
	// default is to wrap item name in an array
	String[] fieldNames = new String[1];
	fieldNames[0] = name;
	return fieldNames;
    }

    /** check if the question field is required to be filled out */
    public boolean isRequired() {
	if (requiredField.equalsIgnoreCase("true"))
	    return true;
	else
	    return false;
    }

    /**
     * return the stem for indication purpose if the required field is not
     * filled out
     */
    public String get_required_stem() {
	// assign value "A" to the unfilled required field to let the JavaScript
	// distinguish
	String s = "A";
	return s;
    }

    /** get the average results of this question from survey data table */
    public float get_avg(Page page, String whereclause) {
	float avg = 0;
	try {
	    // connect to the database
	    Connection conn = page.survey.getDBConnection();
	    Statement stmt = conn.createStatement();
	    // get the average answer of the question from data table
	    String sql = "select round(avg(" + name + "),1) from "
		    + page.survey.id + "_data as s where s.invitee in "
		    + "(select distinct(invitee) from page_submit where page='"
		    + page.id + "' and survey='" + page.survey.id + "')";
	    if (!whereclause.equalsIgnoreCase(""))
		sql += " and s." + whereclause;
	    boolean dbtype = stmt.execute(sql);
	    ResultSet rs = stmt.getResultSet();
	    if (rs.next())
		avg = rs.getFloat(1);
	    rs.close();
	    stmt.close();
	    conn.close();
	} catch (Exception e) {
	    AdminInfo.log_error("WISE - QUESTION GET AVG: " + e.toString(), e);
	}
	return avg;
    }

    /**
     * return table row for the question stem (partial, or complete if not
     * oneLine)
     */
    public String make_stem_html() {
	String s = "<tr><td width=10>&nbsp;</td>";
	// display the question
	// start from a new line if it is not requested by one-line layout
	if (requiredField.equalsIgnoreCase("true")) {
	    if (!oneLine)
		s += "<td colspan='2'>" + stem + " <b>(required)</b></td></tr>";
	    else
		s += "<td align=left>" + stem + " <b>(required)</b>";
	} else {
	    if (!oneLine)
		s += "<td colspan='2'>" + stem + "</td></tr>";
	    else
		s += "<td align=left>" + stem;
	}
	// if oneLine, leave both row and cell open for fields/responses
	return s;
    }

    /**
     * renders the question form by printing the stem - used for admin tool:
     * view survey
     */
    // public String render_form()
    // {
    //
    // String s =
    // "<table cellspacing='0' cellpadding='0' width=600' border='0'>";
    // s += "<tr>";
    // s += "<td width=10>&nbsp;</td>";
    // //display the question stem
    // //start from a new line if it is not requested by one-line layout
    // if (requiredField.equalsIgnoreCase("true"))
    // {
    // if(!oneLine)
    // s += "<td colspan='2'>"+stem+" <b>(required)</b>";
    // else
    // s += "<td align=left>"+stem+" <b>(required)</b>";
    // }
    // else
    // {
    // if(!oneLine)
    // s += "<td colspan='2'>"+stem;
    // else
    // s += "<td align=left>"+stem;
    // }
    // if(!oneLine)
    // s += "</td></tr>";
    // return s;
    // }

    /** print out the information about a question */
    public String toString() {
	String s = super.toString();
	s += "Stem: " + stem + "<br>";
	s += "Required: " + isRequired() + "<br>";
	if (cond != null)
	    s += cond.toString();
	return s;
    }
}
