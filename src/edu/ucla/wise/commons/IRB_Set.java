package edu.ucla.wise.commons;

import org.w3c.dom.Node;

/**
 * This class contains a consent form object set in the preface
 */

public class IRB_Set {
    /** Instance Variables */
    public String id;
    public String irb_name;
    public String expir_date;
    public String approval_number;
    public String irb_logo;

    public Preface preface;

    /** constructor: parse a response set node from XML */
    public IRB_Set(Node n, Preface p) {
	try {
	    preface = p;
	    // assign id (required)
	    id = n.getAttributes().getNamedItem("ID").getNodeValue();
	    // assign various attributes
	    Node node_child = n.getAttributes().getNamedItem("Name");
	    if (node_child != null)
		irb_name = node_child.getNodeValue();
	    else
		irb_name = "";
	    node_child = n.getAttributes().getNamedItem("Expiration_Date");
	    if (node_child != null)
		expir_date = node_child.getNodeValue();
	    else
		expir_date = "";
	    node_child = n.getAttributes().getNamedItem("IRB_Approval_Number");
	    if (node_child != null)
		approval_number = node_child.getNodeValue();
	    else
		approval_number = "";
	    node_child = n.getAttributes().getNamedItem("Logo_File");
	    if (node_child != null)
		irb_logo = node_child.getNodeValue();
	    else
		irb_logo = "";

	} catch (Exception e) {
	    WISE_Application.log_error("WISE - IRB SET : ID = " + id
		    + "; Preface = " + p.project_name + " --> " + e.toString(),
		    null);
	    return;
	}
    }
}
