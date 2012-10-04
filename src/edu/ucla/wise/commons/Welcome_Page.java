package edu.ucla.wise.commons;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains a welcome page object set in the preface
 */

public class Welcome_Page {
    /** Instance Variables */
    public String id;
    public String title;
    public String banner;
    public String logo;
    public String survey_id;
    public String irb_id;
    public String page_contents;

    public Preface preface;

    /** constructor: parse a response set node from XML */
    public Welcome_Page(Node n, Preface p) {
	try {
	    preface = p;
	    // assign id & survey id (required)
	    id = n.getAttributes().getNamedItem("ID").getNodeValue();
	    survey_id = n.getAttributes().getNamedItem("Survey_ID")
		    .getNodeValue();

	    // assign various attributes
	    Node node_child = n.getAttributes().getNamedItem("Title");
	    if (node_child != null)
		title = node_child.getNodeValue();
	    else
		title = "";
	    node_child = n.getAttributes().getNamedItem("BannerFileName");
	    if (node_child != null)
		banner = node_child.getNodeValue();
	    else
		banner = "title.gif";
	    node_child = n.getAttributes().getNamedItem("LogoFileName");
	    if (node_child != null)
		logo = node_child.getNodeValue();
	    else
		logo = "proj_logo.gif";

	    node_child = n.getAttributes().getNamedItem("IRB_ID");
	    if (node_child != null)
		irb_id = node_child.getNodeValue();
	    else
		irb_id = "";

	    NodeList node_p = n.getChildNodes();
	    page_contents = "";
	    for (int j = 0; j < node_p.getLength(); j++) {
		if (node_p.item(j).getNodeName().equalsIgnoreCase("p"))
		    page_contents += "<p>"
			    + node_p.item(j).getFirstChild().getNodeValue()
			    + "</p>";
		if (node_p.item(j).getNodeName()
			.equalsIgnoreCase("html_content")) {
		    NodeList node_n = node_p.item(j).getChildNodes();
		    for (int k = 0; k < node_n.getLength(); k++) {
			if (node_n.item(k).getNodeName()
				.equalsIgnoreCase("#cdata-section"))
			    page_contents += node_n.item(k).getNodeValue();
		    }
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("WISE - WELCOME PAGE : ID = " + id
		    + "; Preface Project name = " + p.project_name + "; --> "
		    + e.toString(), null);
	    return;
	}
    }
}
